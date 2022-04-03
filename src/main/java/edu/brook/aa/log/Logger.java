package edu.brook.aa.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public enum Logger {
    INSTANCE;

    private PrintWriter farmWriter, decisionWriter;
    private boolean isClosed = false;

    private int currentPeriod = 0;

    private final Map<Integer, HouseholdDecisions> householdMap = new HashMap<>();


    Logger() {
        try {
            farmWriter = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi-farms.arff");
            farmWriter.println("@relation anasazi-build-farm");
            farmWriter.println("@attribute 'nutrition need' numeric");
            farmWriter.println("@attribute 'base yield' numeric");
            farmWriter.println("@attribute 'distance to water' numeric");
            farmWriter.println("@attribute 'is available' { true, false }");
            farmWriter.println("@attribute 'build farm' { true, false }");
            farmWriter.println("@data");

            decisionWriter = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi-decisions.arff");
            decisionWriter.println("@relation anasazi-household-decision");
            decisionWriter.println("@attribute 'age' numeric");
            decisionWriter.println("@attribute 'has farm' { true, false }");
//            decisionWriter.println("@attribute 'has settlement' { true, false }");
            decisionWriter.println("@attribute 'nutrition need' numeric");
            decisionWriter.println("@attribute 'est nutrition available' numeric");
            decisionWriter.println("@attribute 'total corn stocks' numeric");
//            decisionWriter.println("@attribute 'est next year corn' numeric");
            decisionWriter.println("@attribute 'fertility' numeric");
            decisionWriter.println("@attribute 'decision' { DIE_STARVATION, DIE_OLD_AGE, DEPART, MOVE, FISSION, NONE }");
            decisionWriter.println("@data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(BuildFarmDecision event) {
        if (!isClosed) {
            farmWriter.println(event.toString());
        }
    }

    public void log(HouseholdEvent event) {
        if (!isClosed) {
            if (event.period != currentPeriod) {
                if (currentPeriod != 0) {
                    printDecisions();
                }

                currentPeriod = event.period;
                householdMap.clear();
            }

            if (!householdMap.containsKey(event.household.id)) {
                householdMap.put(event.household.id, new HouseholdDecisions(event.household));
            }

            householdMap.get(event.household.id).setDecision(event);

        }
    }

    private void printDecisions() {
        for (HouseholdDecisions decisions : householdMap.values()) {
            decisionWriter.println(decisions.toString());
        }
    }


    public void close() {
        // print final decisions
        printDecisions();

        isClosed = true;
        farmWriter.flush();
        farmWriter.close();
        decisionWriter.flush();
        decisionWriter.close();
    }


}

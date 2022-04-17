package edu.brook.aa.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public enum Logger {
    INSTANCE;

    private final Map<Integer, HouseholdDecisions> householdMap = new HashMap<>();
    private PrintWriter decisionWriter;
    private boolean isClosed = false;
    private int currentPeriod = 0;


    Logger() {
        try {
            decisionWriter = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi-decisions.arff");
            decisionWriter.println("@relation anasazi-household-decision");
            decisionWriter.println("@attribute 'age' numeric");
            decisionWriter.println("@attribute 'has farm' { true, false }");
            decisionWriter.println("@attribute 'nutrition need' numeric");
            decisionWriter.println("@attribute 'total corn stocks' numeric");
            decisionWriter.println("@attribute 'est next year corn' numeric");
            decisionWriter.println("@attribute 'fertility' numeric");
            decisionWriter.println("@attribute 'decision' { DIE_STARVATION, DIE_OLD_AGE, DEPART, MOVE, FISSION, NONE }");
            decisionWriter.println("@data");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        // print final decisions
        printDecisions();

        isClosed = true;
//        farmWriter.flush();
//        farmWriter.close();
        decisionWriter.flush();
        decisionWriter.close();
    }

    public void log(HouseholdEvent event) {
        if (!isClosed && event.id != 0) {
            if (event.period != currentPeriod) {
                if (currentPeriod != 0) {
                    printDecisions();
                }

                currentPeriod = event.period;
                householdMap.clear();
            }

            if (!householdMap.containsKey(event.id)) {
                householdMap.put(event.id, new HouseholdDecisions(event.id));
            }

            householdMap.get(event.id).setDecision(event);

        }
    }

    public void log(BuildFarmDecision event) {
        if (!isClosed) {
//            farmWriter.println(event.toString());
        }
    }

    private void printDecisions() {
        for (HouseholdDecisions decisions : householdMap.values()) {
            decisionWriter.println(decisions.toString());
        }
    }


}

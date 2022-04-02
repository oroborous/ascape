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

    private Map<Integer, HouseholdDecisions> householdMap = new HashMap<>();


    Logger() {
        try {
            farmWriter = new PrintWriter("buildFarm.csv");
            decisionWriter = new PrintWriter("decisions.csv");
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

            householdMap.get(event.household.id).setDecision(event.eventType, event.decision);

        }
    }

    private void printDecisions() {
        for(HouseholdDecisions decisions : householdMap.values()) {
            decisionWriter.println(currentPeriod + ", " + decisions.toString());
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

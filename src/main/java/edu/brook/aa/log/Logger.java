package edu.brook.aa.log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public enum Logger {
    INSTANCE;

    private final Map<Integer, HouseholdDecisions> householdMap = new HashMap<>();
    private PrintWriter trainingData, decisionHistory;
    private boolean isClosed = false;
    private int currentPeriod = 0;


    Logger() {

    }

    public void close() {
        // print final decisions
        printDecisions();

        isClosed = true;
        decisionHistory.flush();
        decisionHistory.close();
        trainingData.flush();
        trainingData.close();
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
                householdMap.put(event.id, new HouseholdDecisions());
            }

            householdMap.get(event.id).setDecision(event);

        }
    }

    public void open() {
        isClosed = false;

        try {
            trainingData = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi-decisions-train.arff");
            trainingData.println("@relation anasazi-household-decision");
            trainingData.println("@attribute 'period' numeric");
            trainingData.println("@attribute 'id' numeric");
            trainingData.println("@attribute 'age' numeric");
            trainingData.println("@attribute 'has farm' { true, false }");
            trainingData.println("@attribute 'nutrition need' numeric");
            trainingData.println("@attribute 'total corn stocks' numeric");
            trainingData.println("@attribute 'est next year corn' numeric");
            trainingData.println("@attribute 'fertility' numeric");
            trainingData.println("@attribute 'decision' { DIE_STARVATION, DIE_OLD_AGE, DEPART, MOVE, FISSION, NONE }");
            trainingData.println("@data");

            decisionHistory = new PrintWriter("C:\\Users\\moogi\\Documents\\data-weka\\anasazi-decisions-history.arff");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printDecisions() {
        for (HouseholdDecisions decisions : householdMap.values()) {
            if (decisions.hasEvents()) {
                trainingData.println(decisions.getFinalDecision());
                decisionHistory.println(decisions.getDecisionHistory());
            }
        }
    }


}

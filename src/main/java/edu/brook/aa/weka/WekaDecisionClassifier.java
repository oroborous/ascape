package edu.brook.aa.weka;

// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Apr 02 20:14:10 CDT 2022

import edu.brook.aa.log.EventType;

public class WekaDecisionClassifier {

    public static EventType classify(Object[] i) {
        int result = WekaDecisionClassifier.N825e24e81(i);
        switch (result) {
            case 0:
                return EventType.DIE_STARVATION;
            case 1:
                return EventType.DIE_OLD_AGE;
            case 2:
                return EventType.DEPART;
            case 3:
                return EventType.MOVE;
            case 4:
                return EventType.FISSION;
            default:
                return EventType.NONE;
        }
    }

    static int N825e24e81(Object[] i) {

        if (i[1] == null) {
            return 4;
        } else if (i[1].equals("true")) {
            return WekaDecisionClassifier.N6857b9f582(i);
        } else if (i[1].equals("false")) {
            return WekaDecisionClassifier.N21f7a62990(i);
        }
        return 4;
    }

    static int N6857b9f582(Object[] i) {

        if (i[5] == null) {
            return 4;
        } else if (((Double) i[5]).doubleValue() <= 0.874999) {
            return WekaDecisionClassifier.N4add712583(i);
        } else if (((Double) i[5]).doubleValue() > 0.874999) {
            return WekaDecisionClassifier.N726ee32c89(i);
        }
        return 4;
    }

    static int N4add712583(Object[] i) {

        if (i[4] == null) {
            return 4;
        } else if (((Double) i[4]).doubleValue() <= 259.0) {
            return WekaDecisionClassifier.N359710384(i);
        } else if (((Double) i[4]).doubleValue() > 259.0) {
            return 4;
        }
        return 4;
    }

    static int N359710384(Object[] i) {

        if (i[3] == null) {
            return 4;
        } else if (((Double) i[3]).doubleValue() <= 888.121561) {
            return 4;
        } else if (((Double) i[3]).doubleValue() > 888.121561) {
            return WekaDecisionClassifier.N2449c4b485(i);
        }
        return 4;
    }

    static int N2449c4b485(Object[] i) {

        if (i[0] == null) {
            return 4;
        } else if (((Double) i[0]).doubleValue() <= 0.0) {
            return 4;
        } else if (((Double) i[0]).doubleValue() > 0.0) {
            return WekaDecisionClassifier.N710459d986(i);
        }
        return 4;
    }

    static int N710459d986(Object[] i) {

        if (i[4] == null) {
            return 2;
        } else if (((Double) i[4]).doubleValue() <= 159.0) {
            return WekaDecisionClassifier.N50715a8587(i);
        } else if (((Double) i[4]).doubleValue() > 159.0) {
            return 4;
        }
        return 4;
    }

    static int N50715a8587(Object[] i) {

        if (i[4] == null) {
            return 2;
        } else if (((Double) i[4]).doubleValue() <= 67.0) {
            return 2;
        } else if (((Double) i[4]).doubleValue() > 67.0) {
            return WekaDecisionClassifier.N18e8e8e188(i);
        }
        return 4;
    }

    static int N18e8e8e188(Object[] i) {

        if (i[3] == null) {
            return 4;
        } else if (((Double) i[3]).doubleValue() <= 941.926808) {
            return 4;
        } else if (((Double) i[3]).doubleValue() > 941.926808) {
            return 2;
        }
        return 4;
    }

    static int N726ee32c89(Object[] i) {

        if (i[0] == null) {
            return 4;
        } else if (((Double) i[0]).doubleValue() <= 16.0) {
            return 4;
        } else if (((Double) i[0]).doubleValue() > 16.0) {
            return 3;
        }
        return 4;
    }

    static int N21f7a62990(Object[] i) {

        if (i[0] == null) {
            return 1;
        } else if (((Double) i[0]).doubleValue() <= 30.0) {
            return WekaDecisionClassifier.N7b8865e091(i);
        } else if (((Double) i[0]).doubleValue() > 30.0) {
            return 0;
        }
        return 4;
    }

    static int N7b8865e091(Object[] i) {

        if (i[4] == null) {
            return 0;
        } else if (((Double) i[4]).doubleValue() <= 0.0) {
            return 0;
        } else if (((Double) i[4]).doubleValue() > 0.0) {
            return 1;
        }
        return 4;
    }
}

package edu.brook.aa.weka;

// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Apr 02 20:14:10 CDT 2022

import edu.brook.aa.log.EventType;

public class WekaDecisionClassifier {

    public static EventType classify(Object[] i) {
        int result = WekaDecisionClassifier.N2c62887f0(i);
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

    static int N2c62887f0(Object[] i) {

        if (i[1] == null) {
            return 5;
        } else if (i[1].equals("true")) {
            return WekaDecisionClassifier.N725df88e1(i);
        } else if (i[1].equals("false")) {
            return WekaDecisionClassifier.N820d4969(i);
        }
        return 5;
    }

    static int N725df88e1(Object[] i) {

        if (i[5] == null) {
            return 5;
        } else if (((Double) i[5]) <= 0.874974) {
            return WekaDecisionClassifier.N4e689f362(i);
        } else if (((Double) i[5]) > 0.874974) {
            return WekaDecisionClassifier.N8432f368(i);
        }
        return 5;
    }

    static int N4e689f362(Object[] i) {

        if (i[4] == null) {
            return 5;
        } else if (((Double) i[4]) <= 298.0) {
            return WekaDecisionClassifier.N55f356bb3(i);
        } else if (((Double) i[4]) > 298.0) {
            return 5;
        }
        return 5;
    }

    static int N55f356bb3(Object[] i) {

        if (i[3] == null) {
            return 5;
        } else if (((Double) i[3]) <= 935.037488) {
            return 5;
        } else if (((Double) i[3]) > 935.037488) {
            return WekaDecisionClassifier.N6d6a0d0a4(i);
        }
        return 5;
    }

    static int N6d6a0d0a4(Object[] i) {

        if (i[0] == null) {
            return 5;
        } else if (((Double) i[0]) <= 0.0) {
            return 5;
        } else if (((Double) i[0]) > 0.0) {
            return WekaDecisionClassifier.N70ee01a15(i);
        }
        return 5;
    }

    static int N70ee01a15(Object[] i) {

        if (i[4] == null) {
            return 3;
        } else if (((Double) i[4]) <= 167.0) {
            return 3;
        } else if (((Double) i[4]) > 167.0) {
            return WekaDecisionClassifier.N7a75a1a56(i);
        }
        return 5;
    }

    static int N7a75a1a56(Object[] i) {

        if (i[3] == null) {
            return 5;
        } else if (((Double) i[3]) <= 1057.626978) {
            return 5;
        } else if (((Double) i[3]) > 1057.626978) {
            return WekaDecisionClassifier.N35fc76de7(i);
        }
        return 5;
    }

    static int N35fc76de7(Object[] i) {

        if (i[4] == null) {
            return 3;
        } else if (((Double) i[4]) <= 231.0) {
            return 3;
        } else if (((Double) i[4]) > 231.0) {
            return 5;
        }
        return 5;
    }

    static int N8432f368(Object[] i) {

        if (i[0] == null) {
            return 5;
        } else if (((Double) i[0]) <= 16.0) {
            return 5;
        } else if (((Double) i[0]) > 16.0) {
            return 4;
        }
        return 5;
    }

    static int N820d4969(Object[] i) {

        if (i[0] == null) {
            return 2;
        } else if (((Double) i[0]) <= 30.0) {
            return WekaDecisionClassifier.N5f0f24be10(i);
        } else if (((Double) i[0]) > 30.0) {
            return 1;
        }
        return 5;
    }

    static int N5f0f24be10(Object[] i) {

        if (i[4] == null) {
            return 0;
        } else if (((Double) i[4]) <= 0.0) {
            return 0;
        } else if (((Double) i[4]) > 0.0) {
            return 2;
        }
        return 5;
    }
}

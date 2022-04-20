package edu.brook.aa.weka;

// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Apr 02 20:14:10 CDT 2022

import edu.brook.aa.log.EventType;

public class WekaDecisionClassifier {

    public static EventType classify(Object[] i) {
        int result = (int) WekaDecisionClassifier.N1cbae59012(i);
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
            case 5:
                return EventType.NONE;
            default:
                return EventType.NONE;
        }
    }

    static double N1cbae59012(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 5;
        } else if (((Double) i[5]).doubleValue() <= 0.124967) {
            p = WekaDecisionClassifier.N49daba813(i);
        } else if (((Double) i[5]).doubleValue() > 0.124967) {
            p = WekaDecisionClassifier.N793a055420(i);
        }
        return p;
    }

    static double N49daba813(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 16.0) {
            p = WekaDecisionClassifier.N4a92e87d14(i);
        } else if (((Double) i[0]).doubleValue() > 16.0) {
            p = WekaDecisionClassifier.N2206a8ea15(i);
        }
        return p;
    }

    static double N4a92e87d14(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 5;
        } else if (i[1].equals("true")) {
            p = 5;
        } else if (i[1].equals("false")) {
            p = 2;
        }
        return p;
    }

    static double N2206a8ea15(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 30.0) {
            p = WekaDecisionClassifier.N69db4f5f16(i);
        } else if (((Double) i[0]).doubleValue() > 30.0) {
            p = WekaDecisionClassifier.N72792717(i);
        }
        return p;
    }

    static double N69db4f5f16(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 4;
        } else if (i[1].equals("true")) {
            p = 4;
        } else if (i[1].equals("false")) {
            p = 2;
        }
        return p;
    }

    static double N72792717(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 32.0) {
            p = WekaDecisionClassifier.N5d7eb85418(i);
        } else if (((Double) i[0]).doubleValue() > 32.0) {
            p = WekaDecisionClassifier.N4aa80ae919(i);
        }
        return p;
    }

    static double N5d7eb85418(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 31.0) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() > 31.0) {
            p = 5;
        }
        return p;
    }

    static double N4aa80ae919(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 35.0) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() > 35.0) {
            p = 1;
        }
        return p;
    }

    static double N793a055420(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 30.0) {
            p = WekaDecisionClassifier.N6a92e01521(i);
        } else if (((Double) i[0]).doubleValue() > 30.0) {
            p = WekaDecisionClassifier.N1c25867b25(i);
        }
        return p;
    }

    static double N6a92e01521(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 5;
        } else if (i[1].equals("true")) {
            p = WekaDecisionClassifier.N4286590b22(i);
        } else if (i[1].equals("false")) {
            p = 2;
        }
        return p;
    }

    static double N4286590b22(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if (((Double) i[4]).doubleValue() <= 900.0) {
            p = WekaDecisionClassifier.N48f3ecf023(i);
        } else if (((Double) i[4]).doubleValue() > 900.0) {
            p = 5;
        }
        return p;
    }

    static double N48f3ecf023(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 799.0) {
            p = WekaDecisionClassifier.N61ca401f24(i);
        } else if (((Double) i[4]).doubleValue() > 799.0) {
            p = 5;
        }
        return p;
    }

    static double N61ca401f24(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 2.0) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 2.0) {
            p = 3;
        }
        return p;
    }

    static double N1c25867b25(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 36.0) {
            p = WekaDecisionClassifier.N6c1bdaf526(i);
        } else if (((Double) i[0]).doubleValue() > 36.0) {
            p = 1;
        }
        return p;
    }

    static double N6c1bdaf526(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 5;
        } else if (i[1].equals("true")) {
            p = 5;
        } else if (i[1].equals("false")) {
            p = 2;
        }
        return p;
    }
}

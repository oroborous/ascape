package edu.brook.aa.weka;

// Generated with Weka 3.8.6
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Sat Apr 02 20:14:10 CDT 2022

import edu.brook.aa.log.EventType;

public class WekaDecisionClassifier {

    public static EventType classify(Object[] i) {
        int result = (int) WekaDecisionClassifier.N727e5e0049(i);
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

    static double N727e5e0049(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 5;
        } else if (((Double) i[5]).doubleValue() <= 0.122) {
            p = WekaDecisionClassifier.N4842d96650(i);
        } else if (((Double) i[5]).doubleValue() > 0.122) {
            p = WekaDecisionClassifier.Nc9442e274(i);
        }
        return p;
    }

    static double N4842d96650(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 16.0) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() > 16.0) {
            p = WekaDecisionClassifier.N717f6c0051(i);
        }
        return p;
    }

    static double N717f6c0051(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 30.0) {
            p = WekaDecisionClassifier.N541eebcc52(i);
        } else if (((Double) i[0]).doubleValue() > 30.0) {
            p = WekaDecisionClassifier.N33df35af65(i);
        }
        return p;
    }

    static double N541eebcc52(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.101) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 0.101) {
            p = WekaDecisionClassifier.N5490211153(i);
        }
        return p;
    }

    static double N5490211153(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.109) {
            p = WekaDecisionClassifier.Na475b0c54(i);
        } else if (((Double) i[6]).doubleValue() > 0.109) {
            p = WekaDecisionClassifier.N23b1f27556(i);
        }
        return p;
    }

    static double Na475b0c54(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 5;
        } else if (((Double) i[5]).doubleValue() <= 0.107) {
            p = WekaDecisionClassifier.Nd2392855(i);
        } else if (((Double) i[5]).doubleValue() > 0.107) {
            p = 5;
        }
        return p;
    }

    static double Nd2392855(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.105) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.105) {
            p = 4;
        }
        return p;
    }

    static double N23b1f27556(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.115) {
            p = WekaDecisionClassifier.N767cc7bb57(i);
        } else if (((Double) i[5]).doubleValue() > 0.115) {
            p = WekaDecisionClassifier.N7b57650c60(i);
        }
        return p;
    }

    static double N767cc7bb57(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 0.115) {
            p = WekaDecisionClassifier.N46536d4358(i);
        } else if (((Double) i[6]).doubleValue() > 0.115) {
            p = 4;
        }
        return p;
    }

    static double N46536d4358(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.111) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 0.111) {
            p = WekaDecisionClassifier.N57d917d859(i);
        }
        return p;
    }

    static double N57d917d859(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.113) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.113) {
            p = 4;
        }
        return p;
    }

    static double N7b57650c60(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.117) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.117) {
            p = WekaDecisionClassifier.N6b8624c261(i);
        }
        return p;
    }

    static double N6b8624c261(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 0.122) {
            p = WekaDecisionClassifier.N36e2658462(i);
        } else if (((Double) i[6]).doubleValue() > 0.122) {
            p = 4;
        }
        return p;
    }

    static double N36e2658462(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.118) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 0.118) {
            p = WekaDecisionClassifier.N291ad35763(i);
        }
        return p;
    }

    static double N291ad35763(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.119) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.119) {
            p = WekaDecisionClassifier.N6925bc7d64(i);
        }
        return p;
    }

    static double N6925bc7d64(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.12) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 0.12) {
            p = 5;
        }
        return p;
    }

    static double N33df35af65(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 32.0) {
            p = WekaDecisionClassifier.N1634a81566(i);
        } else if (((Double) i[0]).doubleValue() > 32.0) {
            p = WekaDecisionClassifier.N41cff2e72(i);
        }
        return p;
    }

    static double N1634a81566(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 31.0) {
            p = WekaDecisionClassifier.N69cb51ef67(i);
        } else if (((Double) i[0]).doubleValue() > 31.0) {
            p = 5;
        }
        return p;
    }

    static double N69cb51ef67(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() <= 0.106) {
            p = 4;
        } else if (((Double) i[5]).doubleValue() > 0.106) {
            p = WekaDecisionClassifier.N2dc9deff68(i);
        }
        return p;
    }

    static double N2dc9deff68(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.113) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.113) {
            p = WekaDecisionClassifier.N67f1e4a469(i);
        }
        return p;
    }

    static double N67f1e4a469(Object[] i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.11) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() > 0.11) {
            p = WekaDecisionClassifier.N54b69c4f70(i);
        }
        return p;
    }

    static double N54b69c4f70(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 4;
        } else if (((Double) i[6]).doubleValue() <= 0.122) {
            p = WekaDecisionClassifier.N6741fb1f71(i);
        } else if (((Double) i[6]).doubleValue() > 0.122) {
            p = 5;
        }
        return p;
    }

    static double N6741fb1f71(Object[] i) {
        double p = Double.NaN;
        if (i[6] == null) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() <= 0.116) {
            p = 5;
        } else if (((Double) i[6]).doubleValue() > 0.116) {
            p = 4;
        }
        return p;
    }

    static double N41cff2e72(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 34.0) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() > 34.0) {
            p = WekaDecisionClassifier.N6c20ffcd73(i);
        }
        return p;
    }

    static double N6c20ffcd73(Object[] i) {
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

    static double Nc9442e274(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 30.0) {
            p = WekaDecisionClassifier.N64e5c96c75(i);
        } else if (((Double) i[0]).doubleValue() > 30.0) {
            p = WekaDecisionClassifier.N427b3acd78(i);
        }
        return p;
    }

    static double N64e5c96c75(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 5;
        } else if (i[1].equals("true")) {
            p = WekaDecisionClassifier.N763af5176(i);
        } else if (i[1].equals("false")) {
            p = 2;
        }
        return p;
    }

    static double N763af5176(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 5;
        } else if (((Double) i[4]).doubleValue() <= 873.0) {
            p = WekaDecisionClassifier.N6cd6e6e777(i);
        } else if (((Double) i[4]).doubleValue() > 873.0) {
            p = 5;
        }
        return p;
    }

    static double N6cd6e6e777(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 797.0) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 797.0) {
            p = 5;
        }
        return p;
    }

    static double N427b3acd78(Object[] i) {
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
}

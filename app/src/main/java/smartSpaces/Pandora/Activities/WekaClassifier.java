package smartSpaces.Pandora.Activities;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N5c5001780(i);
        return p;
    }
    static double N5c5001780(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 3.720700741) {
            p = WekaClassifier.N43f282a41(i);
        } else if (((Double) i[1]).doubleValue() > 3.720700741) {
            p = WekaClassifier.N667fcee914(i);
        }
        return p;
    }
    static double N43f282a41(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 1.072602391) {
            p = WekaClassifier.N7fa81cc02(i);
        } else if (((Double) i[1]).doubleValue() > 1.072602391) {
            p = WekaClassifier.N2b8af0337(i);
        }
        return p;
    }
    static double N7fa81cc02(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= -1.211828947) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > -1.211828947) {
            p = WekaClassifier.N2143fe5a3(i);
        }
        return p;
    }
    static double N2143fe5a3(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 0.07813625) {
            p = WekaClassifier.N7d4460cb4(i);
        } else if (((Double) i[0]).doubleValue() > 0.07813625) {
            p = WekaClassifier.N2af42a655(i);
        }
        return p;
    }
    static double N7d4460cb4(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= -1.338071108) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > -1.338071108) {
            p = 2;
        }
        return p;
    }
    static double N2af42a655(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 8.884721756) {
            p = WekaClassifier.N15057de76(i);
        } else if (((Double) i[0]).doubleValue() > 8.884721756) {
            p = 1;
        }
        return p;
    }
    static double N15057de76(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.929336905) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.929336905) {
            p = 2;
        }
        return p;
    }
    static double N2b8af0337(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 8.445150375) {
            p = WekaClassifier.N75969fb18(i);
        } else if (((Double) i[2]).doubleValue() > 8.445150375) {
            p = WekaClassifier.N3c19ff5711(i);
        }
        return p;
    }
    static double N75969fb18(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1.8262043) {
            p = WekaClassifier.N7eba24649(i);
        } else if (((Double) i[0]).doubleValue() > 1.8262043) {
            p = 1;
        }
        return p;
    }
    static double N7eba24649(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= -2.834734917) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > -2.834734917) {
            p = WekaClassifier.N40ee841110(i);
        }
        return p;
    }
    static double N40ee841110(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 0.459127814) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 0.459127814) {
            p = 1;
        }
        return p;
    }
    static double N3c19ff5711(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 10.01586819) {
            p = WekaClassifier.N7d33847212(i);
        } else if (((Double) i[2]).doubleValue() > 10.01586819) {
            p = 1;
        }
        return p;
    }
    static double N7d33847212(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 0.896192193) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 0.896192193) {
            p = WekaClassifier.N520516b413(i);
        }
        return p;
    }
    static double N520516b413(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 5.996253014) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 5.996253014) {
            p = 1;
        }
        return p;
    }
    static double N667fcee914(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 6.134951591) {
            p = WekaClassifier.N156f663615(i);
        } else if (((Double) i[0]).doubleValue() > 6.134951591) {
            p = 1;
        }
        return p;
    }
    static double N156f663615(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 1.380624771) {
            p = WekaClassifier.N749e70fd16(i);
        } else if (((Double) i[4]).doubleValue() > 1.380624771) {
            p = WekaClassifier.N214d0e6328(i);
        }
        return p;
    }
    static double N749e70fd16(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 4.970748425) {
            p = WekaClassifier.Ned4786c17(i);
        } else if (((Double) i[2]).doubleValue() > 4.970748425) {
            p = WekaClassifier.N7bd89f4a26(i);
        }
        return p;
    }
    static double Ned4786c17(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 2.029827833) {
            p = WekaClassifier.N1644e7d918(i);
        } else if (((Double) i[5]).doubleValue() > 2.029827833) {
            p = 1;
        }
        return p;
    }
    static double N1644e7d918(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -2.287551165) {
            p = WekaClassifier.N78f46df119(i);
        } else if (((Double) i[2]).doubleValue() > -2.287551165) {
            p = WekaClassifier.N7f6af38a20(i);
        }
        return p;
    }
    static double N78f46df119(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -2.522765636) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -2.522765636) {
            p = 0;
        }
        return p;
    }
    static double N7f6af38a20(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= -1.292056084) {
            p = WekaClassifier.N5250f31321(i);
        } else if (((Double) i[5]).doubleValue() > -1.292056084) {
            p = WekaClassifier.N631bacaf22(i);
        }
        return p;
    }
    static double N5250f31321(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 9.114209175) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 9.114209175) {
            p = 0;
        }
        return p;
    }
    static double N631bacaf22(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 9.485827446) {
            p = WekaClassifier.N38dbded823(i);
        } else if (((Double) i[1]).doubleValue() > 9.485827446) {
            p = 0;
        }
        return p;
    }
    static double N38dbded823(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 2.185307026) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 2.185307026) {
            p = WekaClassifier.N10058cea24(i);
        }
        return p;
    }
    static double N10058cea24(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1.083518505) {
            p = WekaClassifier.N45182d9225(i);
        } else if (((Double) i[0]).doubleValue() > 1.083518505) {
            p = 2;
        }
        return p;
    }
    static double N45182d9225(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 9.35159874) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 9.35159874) {
            p = 3;
        }
        return p;
    }
    static double N7bd89f4a26(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 8.097088814) {
            p = WekaClassifier.N1d13a3f727(i);
        } else if (((Double) i[1]).doubleValue() > 8.097088814) {
            p = 1;
        }
        return p;
    }
    static double N1d13a3f727(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 6.77948761) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 6.77948761) {
            p = 2;
        }
        return p;
    }
    static double N214d0e6328(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 1.849439979) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > 1.849439979) {
            p = WekaClassifier.N253dd36529(i);
        }
        return p;
    }
    static double N253dd36529(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= -0.816129327) {
            p = WekaClassifier.N1aa02a4730(i);
        } else if (((Double) i[3]).doubleValue() > -0.816129327) {
            p = WekaClassifier.N1aac3eff31(i);
        }
        return p;
    }
    static double N1aa02a4730(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= 2.070080042) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() > 2.070080042) {
            p = 1;
        }
        return p;
    }
    static double N1aac3eff31(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 2.268241405) {
            p = WekaClassifier.N535b15df32(i);
        } else if (((Double) i[4]).doubleValue() > 2.268241405) {
            p = 3;
        }
        return p;
    }
    static double N535b15df32(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 8.283128738) {
            p = WekaClassifier.N18b6522a33(i);
        } else if (((Double) i[1]).doubleValue() > 8.283128738) {
            p = 3;
        }
        return p;
    }
    static double N18b6522a33(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 1.979533553) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() > 1.979533553) {
            p = 1;
        }
        return p;
    }
}

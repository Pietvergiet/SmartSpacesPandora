package smartSpaces.Pandora.Activities;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N546b564366(i);
        return p;
    }
    static double N546b564366(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 9.280380249) {
            p = WekaClassifier.N7912c25b67(i);
        } else if (((Double) i[2]).doubleValue() > 9.280380249) {
            p = WekaClassifier.N69764635105(i);
        }
        return p;
    }
    static double N7912c25b67(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 1.660676718) {
            p = WekaClassifier.N3dfdddcf68(i);
        } else if (((Double) i[1]).doubleValue() > 1.660676718) {
            p = WekaClassifier.N394431fd77(i);
        }
        return p;
    }
    static double N3dfdddcf68(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -2.278296232) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > -2.278296232) {
            p = WekaClassifier.N706bd31469(i);
        }
        return p;
    }
    static double N706bd31469(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 1.493981838) {
            p = WekaClassifier.N7e3c4a1470(i);
        } else if (((Double) i[0]).doubleValue() > 1.493981838) {
            p = 2;
        }
        return p;
    }
    static double N7e3c4a1470(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -5.406942844) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > -5.406942844) {
            p = WekaClassifier.N11d34f9271(i);
        }
        return p;
    }
    static double N11d34f9271(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= -4.613539219) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > -4.613539219) {
            p = WekaClassifier.N4213fbc272(i);
        }
        return p;
    }
    static double N4213fbc272(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 3.083590984) {
            p = WekaClassifier.N6cce63e773(i);
        } else if (((Double) i[2]).doubleValue() > 3.083590984) {
            p = WekaClassifier.N260abf8076(i);
        }
        return p;
    }
    static double N6cce63e773(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= -1.826984286) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() > -1.826984286) {
            p = WekaClassifier.N70bef88074(i);
        }
        return p;
    }
    static double N70bef88074(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= -2.049436569) {
            p = WekaClassifier.N7b9a96da75(i);
        } else if (((Double) i[1]).doubleValue() > -2.049436569) {
            p = 0;
        }
        return p;
    }
    static double N7b9a96da75(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= -1.642775774) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > -1.642775774) {
            p = 2;
        }
        return p;
    }
    static double N260abf8076(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 7.784741402) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 7.784741402) {
            p = 1;
        }
        return p;
    }
    static double N394431fd77(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 1.397309065) {
            p = WekaClassifier.N3e39a4ad78(i);
        } else if (((Double) i[4]).doubleValue() > 1.397309065) {
            p = WekaClassifier.N651930b1100(i);
        }
        return p;
    }
    static double N3e39a4ad78(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 9.709485054) {
            p = WekaClassifier.N11bad9f679(i);
        } else if (((Double) i[0]).doubleValue() > 9.709485054) {
            p = WekaClassifier.N24b78aea97(i);
        }
        return p;
    }
    static double N11bad9f679(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 0;
        } else if (((Double) i[5]).doubleValue() <= 3.876785278) {
            p = WekaClassifier.N623f92fe80(i);
        } else if (((Double) i[5]).doubleValue() > 3.876785278) {
            p = 2;
        }
        return p;
    }
    static double N623f92fe80(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 6.366593838) {
            p = WekaClassifier.N7a6c8c9d81(i);
        } else if (((Double) i[2]).doubleValue() > 6.366593838) {
            p = WekaClassifier.N3458036e95(i);
        }
        return p;
    }
    static double N7a6c8c9d81(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= -3.06485939) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() > -3.06485939) {
            p = WekaClassifier.N90343b182(i);
        }
        return p;
    }
    static double N90343b182(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= -1.027346849) {
            p = WekaClassifier.N4eb14e0e83(i);
        } else if (((Double) i[4]).doubleValue() > -1.027346849) {
            p = WekaClassifier.N5d9c271586(i);
        }
        return p;
    }
    static double N4eb14e0e83(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (((Double) i[4]).doubleValue() <= -1.123809218) {
            p = WekaClassifier.N50878d4484(i);
        } else if (((Double) i[4]).doubleValue() > -1.123809218) {
            p = 0;
        }
        return p;
    }
    static double N50878d4484(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 0.449951172) {
            p = WekaClassifier.N4e11fbad85(i);
        } else if (((Double) i[3]).doubleValue() > 0.449951172) {
            p = 2;
        }
        return p;
    }
    static double N4e11fbad85(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 4.712995529) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 4.712995529) {
            p = 2;
        }
        return p;
    }
    static double N5d9c271586(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= -3.08373189) {
            p = WekaClassifier.N1999c39687(i);
        } else if (((Double) i[2]).doubleValue() > -3.08373189) {
            p = WekaClassifier.N3495c1c189(i);
        }
        return p;
    }
    static double N1999c39687(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= -4.745307922) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > -4.745307922) {
            p = WekaClassifier.N7598804c88(i);
        }
        return p;
    }
    static double N7598804c88(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() <= 0.726776958) {
            p = 0;
        } else if (((Double) i[3]).doubleValue() > 0.726776958) {
            p = 2;
        }
        return p;
    }
    static double N3495c1c189(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 2.921569347) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 2.921569347) {
            p = WekaClassifier.N80decc990(i);
        }
        return p;
    }
    static double N80decc990(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 9.412836075) {
            p = WekaClassifier.N5f5f1b2d91(i);
        } else if (((Double) i[1]).doubleValue() > 9.412836075) {
            p = 0;
        }
        return p;
    }
    static double N5f5f1b2d91(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 2.442160368) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 2.442160368) {
            p = WekaClassifier.N6ef47a6a92(i);
        }
        return p;
    }
    static double N6ef47a6a92(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 7.270887852) {
            p = WekaClassifier.N74f535bc93(i);
        } else if (((Double) i[0]).doubleValue() > 7.270887852) {
            p = 2;
        }
        return p;
    }
    static double N74f535bc93(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 3.373705387) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 3.373705387) {
            p = WekaClassifier.N13f9f44f94(i);
        }
        return p;
    }
    static double N13f9f44f94(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 0.505814016) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 0.505814016) {
            p = 2;
        }
        return p;
    }
    static double N3458036e95(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 6.351893902) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 6.351893902) {
            p = WekaClassifier.N793208a596(i);
        }
        return p;
    }
    static double N793208a596(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= -1.333040118) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > -1.333040118) {
            p = 0;
        }
        return p;
    }
    static double N24b78aea97(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 13.76134968) {
            p = WekaClassifier.N783049bf98(i);
        } else if (((Double) i[0]).doubleValue() > 13.76134968) {
            p = 2;
        }
        return p;
    }
    static double N783049bf98(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 2.560897112) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 2.560897112) {
            p = WekaClassifier.Nc8776a499(i);
        }
        return p;
    }
    static double Nc8776a499(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 8.843519211) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 8.843519211) {
            p = 0;
        }
        return p;
    }
    static double N651930b1100(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 1.849941254) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() > 1.849941254) {
            p = WekaClassifier.N7de10622101(i);
        }
        return p;
    }
    static double N7de10622101(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 6.459381104) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 6.459381104) {
            p = WekaClassifier.N766c590102(i);
        }
        return p;
    }
    static double N766c590102(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 3;
        } else if (((Double) i[0]).doubleValue() <= 4.479588509) {
            p = WekaClassifier.N30e3b18e103(i);
        } else if (((Double) i[0]).doubleValue() > 4.479588509) {
            p = 2;
        }
        return p;
    }
    static double N30e3b18e103(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 3;
        } else if (((Double) i[4]).doubleValue() <= 2.268241405) {
            p = WekaClassifier.N1cd3939b104(i);
        } else if (((Double) i[4]).doubleValue() > 2.268241405) {
            p = 3;
        }
        return p;
    }
    static double N1cd3939b104(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 7.750941277) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 7.750941277) {
            p = 3;
        }
        return p;
    }
    static double N69764635105(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 10.0913229) {
            p = WekaClassifier.N588d54ce106(i);
        } else if (((Double) i[2]).doubleValue() > 10.0913229) {
            p = WekaClassifier.N7173ff1116(i);
        }
        return p;
    }
    static double N588d54ce106(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -0.156997681) {
            p = WekaClassifier.N75cff190107(i);
        } else if (((Double) i[1]).doubleValue() > -0.156997681) {
            p = WekaClassifier.N64e109c9112(i);
        }
        return p;
    }
    static double N75cff190107(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 9.775382042) {
            p = WekaClassifier.N7580fbf9108(i);
        } else if (((Double) i[2]).doubleValue() > 9.775382042) {
            p = WekaClassifier.N16cc67da111(i);
        }
        return p;
    }
    static double N7580fbf9108(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 9.677167892) {
            p = WekaClassifier.N2e9c5c92109(i);
        } else if (((Double) i[2]).doubleValue() > 9.677167892) {
            p = 2;
        }
        return p;
    }
    static double N2e9c5c92109(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -1.59151268) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > -1.59151268) {
            p = WekaClassifier.N1c8556ab110(i);
        }
        return p;
    }
    static double N1c8556ab110(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 3.248073101) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 3.248073101) {
            p = 2;
        }
        return p;
    }
    static double N16cc67da111(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= -0.678987145) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > -0.678987145) {
            p = 1;
        }
        return p;
    }
    static double N64e109c9112(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 0.922303021) {
            p = WekaClassifier.N2855b10b113(i);
        } else if (((Double) i[0]).doubleValue() > 0.922303021) {
            p = WekaClassifier.N6bd4cc03115(i);
        }
        return p;
    }
    static double N2855b10b113(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.984672725) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.984672725) {
            p = WekaClassifier.N1e539aa4114(i);
        }
        return p;
    }
    static double N1e539aa4114(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 2.286854267) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 2.286854267) {
            p = 2;
        }
        return p;
    }
    static double N6bd4cc03115(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 1.101245165) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 1.101245165) {
            p = 2;
        }
        return p;
    }
    static double N7173ff1116(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 2;
        } else if (((Double) i[2]).doubleValue() <= 10.50128937) {
            p = WekaClassifier.N2cf1bf1117(i);
        } else if (((Double) i[2]).doubleValue() > 10.50128937) {
            p = WekaClassifier.N73640198120(i);
        }
        return p;
    }
    static double N2cf1bf1117(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 2.602576733) {
            p = WekaClassifier.N4b37b6118(i);
        } else if (((Double) i[0]).doubleValue() > 2.602576733) {
            p = 2;
        }
        return p;
    }
    static double N4b37b6118(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 1;
        } else if (((Double) i[5]).doubleValue() <= 0.592261553) {
            p = WekaClassifier.N4db24768119(i);
        } else if (((Double) i[5]).doubleValue() > 0.592261553) {
            p = 2;
        }
        return p;
    }
    static double N4db24768119(Object []i) {
        double p = Double.NaN;
        if (i[5] == null) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() <= -0.064070083) {
            p = 2;
        } else if (((Double) i[5]).doubleValue() > -0.064070083) {
            p = 1;
        }
        return p;
    }
    static double N73640198120(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 14.64575958) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 14.64575958) {
            p = WekaClassifier.N98e9c96121(i);
        }
        return p;
    }
    static double N98e9c96121(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 11.63799763) {
            p = WekaClassifier.N50a1ec96122(i);
        } else if (((Double) i[0]).doubleValue() > 11.63799763) {
            p = 2;
        }
        return p;
    }
    static double N50a1ec96122(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 3.941412687) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() > 3.941412687) {
            p = 0;
        }
        return p;
    }
}
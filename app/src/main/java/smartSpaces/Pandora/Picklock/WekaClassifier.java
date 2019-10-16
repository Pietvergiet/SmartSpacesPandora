package smartSpaces.Pandora.Picklock;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.Nda4124a181(i);
        return p;
    }
    static double Nda4124a181(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 0.923261166) {
            p = WekaClassifier.N2b0d0d8f182(i);
        } else if (((Double) i[0]).doubleValue() > 0.923261166) {
            p = WekaClassifier.N59f26515183(i);
        }
        return p;
    }
    static double N2b0d0d8f182(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= -0.122996911) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() > -0.122996911) {
            p = 2;
        }
        return p;
    }
    static double N59f26515183(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 8.432846069) {
            p = WekaClassifier.N79488c0f184(i);
        } else if (((Double) i[0]).doubleValue() > 8.432846069) {
            p = 4;
        }
        return p;
    }
    static double N79488c0f184(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 0.913527191) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() > 0.913527191) {
            p = WekaClassifier.N5453e72e185(i);
        }
        return p;
    }
    static double N5453e72e185(Object []i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 5.472361565) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() > 5.472361565) {
            p = 3;
        }
        return p;
    }
}

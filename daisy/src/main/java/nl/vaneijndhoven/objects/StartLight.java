package nl.vaneijndhoven.objects;


public enum StartLight {
    OFF {
        @Override
        public StartLight off() {
            return OFF;
        }
    },
    LIT {
        @Override
        public StartLight off() {
            nrOfOffs++;
            if (nrOfOffs >= DIMMED_DETECTION_THRESHOLD) {
                return DIMMED;
            } else {
                return this;
            }
        }
    },
    DIMMED {
        @Override
        public StartLight on() {
            return DIMMED;
        }

        @Override
        public boolean started() {
            return true;
        }
    };

    private static final int DIMMED_DETECTION_THRESHOLD = 3;
    protected int nrOfOffs = 0;

    public static StartLight init() {
        return OFF;
    }

    public StartLight on() {
        nrOfOffs = 0;
        return LIT;
    }

    public StartLight off() {
        return DIMMED;
    }

    public boolean started() {
        return false;
    }
}
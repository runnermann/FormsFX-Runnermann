package type.celltypes;

public enum SingleCellType implements CellLayout<SingleCellType> {
    AV() {
        @Override
        public char get() {
            return 'm';
        }
    },
    CANVAS() {
        @Override
        public char get() {
            return 'c';
        }
    },
    DRAWING() {
        @Override
        public char get() {
            return 'd';
        }
    },
    LATEX() {
        @Override
        public char get() {
            return 'l';
        }
    },
    TEXT() {
        @Override
        public char get() {
            return 't';
        }
    };
}

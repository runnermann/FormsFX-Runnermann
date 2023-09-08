package type.celltypes;

public enum DoubleCellType implements CellLayout<DoubleCellType> {
    AV() {
        @Override
        public char get() {
            return 'M';
        }
    },
    CANVAS() {
        @Override
        public char get() {
            return 'C';
        }
    },
    DRAWING() {
        @Override
        public char get() {
            return 'D';
        }
    },
    LATEX() {
        @Override
        public char get() {
            return 'd';
        }
    };
}

package com.cpr3663.autocreation.util;

public class Enums {
    public enum Themes {
        LIGHT,
        DARK,
        SYSTEM
    }

    public enum Sections {
        MENU,
        EVENTS,
        FIELD,
        EDITOR
    }

    public enum DelayTypes {
        NONE(true),
        TIME(true),
        PROGRESS(false),
        DISTANCE(false);

        private final boolean worksAfterPrev;

        DelayTypes(boolean worksAfterPrev) {
            this.worksAfterPrev = worksAfterPrev;
        }

        public boolean worksAfterPrev() {
            return worksAfterPrev;
        }
    }
}

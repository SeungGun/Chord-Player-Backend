package com.windry.chordplayer.spec;

public enum SortStrategy {
    NAME,
    CHRONOLOGICAL,
    VIEW;

    public static SortStrategy findMatchedEnumFromString(String s) {
        for (SortStrategy ss : SortStrategy.values()) {
            if (ss.name().equalsIgnoreCase(s))
                return ss;
        }
        return null;
    }
}

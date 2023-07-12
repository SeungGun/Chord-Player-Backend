package com.windry.chordplayer.spec;

public enum SearchCriteria {
    TITLE,
    ARTIST;

    //    BPM
    public static SearchCriteria findMatchedEnumFromString(String s) {
        for (SearchCriteria sc : SearchCriteria.values()) {
            if (sc.name().equalsIgnoreCase(s))
                return sc;
        }
        return null;
    }
}

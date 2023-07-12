package com.windry.chordplayer.spec;

public enum Gender {
    MALE,
    FEMALE,
    MIXED;

    public static Gender findMatchedEnumFromString(String s) {
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(s))
                return g;
        }
        return null;
    }
}

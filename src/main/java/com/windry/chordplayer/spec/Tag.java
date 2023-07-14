package com.windry.chordplayer.spec;

public enum Tag {
    INTRO,
    VERSE,
    CHORUS,
    INTERLUDE,
    MODULATION,
    BRIDGE,
    OUTRO;

    public static Tag findTagByString(String str) {
        for (Tag tag : Tag.values()) {
            if (tag.name().equalsIgnoreCase(str))
                return tag;
        }
        return Tag.VERSE;
    }
}

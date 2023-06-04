package com.windry.chordplayer.domain;

public enum Tag {
    INTRO,
    INTERLUDE,
    MODULATION,
    OUTRO;

    public static Tag findTagByString(String str){
        for(Tag tag: Tag.values()){
            if(tag.name().equalsIgnoreCase(str))
                return tag;
        }
        return null;
    }
}

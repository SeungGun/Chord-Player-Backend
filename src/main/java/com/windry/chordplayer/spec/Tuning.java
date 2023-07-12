package com.windry.chordplayer.spec;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tuning {
    STANDARD("E A D G B E"),
    //    DROPPED_D("D A D G B E"),
//    OPEN_D("D A D F# A D"),
//    MODAL_D("D A D G A D"),
//    OPEN_G("D G D G B D"),
//    OPEN_G_MINOR("D G D G Bb D"),
//    OPEN_C("C G C G C E"),
//    MODAL_E("E A D E A E"),
//    BARITONE_4("B E A D F# B"),
//    BARITONE_5("A D G C E A"),
    HALF_STEP("Eb Ab Db Gb Bb Eb"),
    WHOLE_STEP("D G C F A D");

    private final String sequence;

    public static Tuning findMatchedEnumFromString(String s) {
        for (Tuning t : Tuning.values()) {
            if (t.name().equalsIgnoreCase(s))
                return t;
        }
        return null;
    }

}

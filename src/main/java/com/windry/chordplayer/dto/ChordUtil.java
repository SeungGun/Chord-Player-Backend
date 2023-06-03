package com.windry.chordplayer.dto;

import java.util.Arrays;
import java.util.List;

public class ChordUtil {

    private static final String[] n = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    public static final List<String> notes = Arrays.stream(n).toList();

}

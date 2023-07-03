package com.windry.chordplayer.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChordUtilTest {

    @Test
    @DisplayName("다양한 변형 코드에 대해 변경하고자 하는 정수만큼 변경하면 루트 코드가 그만큼 변경되어야한다.")
    void changeKey() {
        String s1 = ChordUtil.changeKey("Gbm/Bb", 3); // Gb - G - G# - A
        String s2 = ChordUtil.changeKey("F#sus4/A#", 3);
        String s3 = ChordUtil.changeKey("G/B", -2);
        String s4 = ChordUtil.changeKey("FM7", 2);
        String s5 = ChordUtil.changeKey("Bb9sus4", 3);
        String s6 = ChordUtil.changeKey("B7/A", -4);
        String s7 = ChordUtil.changeKey("AbM7", 1);
        String s8 = ChordUtil.changeKey("Fmaj7", 3);
        String s9 = ChordUtil.changeKey("C#m7", -4);

        assertEquals("Am/C#", s1);
        assertEquals("Asus4/C#", s2);
        assertEquals("F/A", s3);
        assertEquals("GM7", s4);
        assertEquals("C#9sus4", s5);
        assertEquals("G7/F", s6);
        assertEquals("AM7", s7);
        assertEquals("G#maj7", s8);
        assertEquals("Am7", s9);
    }
}
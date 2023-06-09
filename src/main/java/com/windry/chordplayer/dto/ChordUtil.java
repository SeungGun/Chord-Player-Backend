package com.windry.chordplayer.dto;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChordUtil {
    private static final Map<String, Integer> chordOrdersAtoI = init();
    private static final List<String[]> chordOrdersItoA = init2();
    private static final Pattern CHORD_PATTERN = Pattern.compile("^([A-G][#b]?).*"); // A ~ G , # 또는 b가 0 또는 1 회 등장, 그 뒤에 임의의 문자

    private static Map<String, Integer> init() {
        Map<String, Integer> map = new HashMap<>();
        map.put("C", 0);
        map.put("Db", 1);
        map.put("C#", 1);
        map.put("D", 2);
        map.put("D#", 3);
        map.put("Eb", 3);
        map.put("E", 4);
        map.put("F", 5);
        map.put("F#", 6);
        map.put("Gb", 6);
        map.put("G", 7);
        map.put("G#", 8);
        map.put("Ab", 8);
        map.put("A", 9);
        map.put("A#", 10);
        map.put("Bb", 10);
        map.put("B", 11);
        return map;
    }

    private static List<String[]> init2() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"C"});
        list.add(new String[]{"C#", "Db"});
        list.add(new String[]{"D"});
        list.add(new String[]{"D#", "Eb"});
        list.add(new String[]{"E"});
        list.add(new String[]{"F"});
        list.add(new String[]{"F#", "Gb"});
        list.add(new String[]{"G"});
        list.add(new String[]{"G#", "Ab"});
        list.add(new String[]{"A"});
        list.add(new String[]{"A#", "Bb"});
        list.add(new String[]{"B"});
        return list;
    }

    public static String changeKey(String originChord, int amount) {
        if (originChord.contains("/")) {
            String[] split = originChord.split("/");

            String baseChord = extractBaseChord(split[0]);
            String slashRoot = split[1];

            int order1 = chordOrdersAtoI.get(baseChord);
            int order2 = chordOrdersAtoI.get(slashRoot);

            String s = chordOrdersItoA.get((order1 + amount) % 12)[0];
            String s1 = chordOrdersItoA.get((order2 + amount) % 12)[0];
            return split[0].replace(baseChord, s) + "/" + s1;
        }

        String base = extractBaseChord(originChord);
        int order = chordOrdersAtoI.get(base);

        return originChord.replace(base, chordOrdersItoA.get((order + amount) % 12)[0]);
    }

    public static String extractBaseChord(String chord) {
        Matcher matcher = CHORD_PATTERN.matcher(chord);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

}

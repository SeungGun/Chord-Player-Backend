package com.windry.chordplayer.dto;

import com.windry.chordplayer.exception.NotFoundChordException;

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

    /**
     * 주어진 변형 코드와 변경할 값만큼 키를 변경해주는 기능
     *
     * @param originChord 주어진 변형 코드
     * @param amount      변경하고싶은 키 값 (음수, 양수 모두 가능)
     * @return 최종 키가 변경된 코드 문자열
     */
    public static String changeKey(String originChord, int amount) {
        if (amount == 0)
            return originChord;

        if (originChord == null)
            throw new NotFoundChordException();

        // 분수 코드 존재할 경우
        if (originChord.contains("/")) {
            String[] split = originChord.split("/");

            String baseChord = extractBaseChord(split[0]);
            String slashRoot = split[1];

            int baseOrder = chordOrdersAtoI.get(baseChord);
            int slashOrder = chordOrdersAtoI.get(slashRoot);

            String changedBase = chordOrdersItoA.get(getCircularOrder(baseOrder, amount))[0];
            String changedSlash = chordOrdersItoA.get(getCircularOrder(slashOrder, amount))[0];
            return split[0].replace(baseChord, changedBase) + "/" + changedSlash;
        }

        String base = extractBaseChord(originChord);
        int order = chordOrdersAtoI.get(base);

        return originChord.replace(base, chordOrdersItoA.get(getCircularOrder(order, amount))[0]);
    }

    /**
     * 주어진 코드를 정규표현식에 따라 매칭하고 원본 코드를 추출
     *
     * @param chord 주어진 변형 코드
     * @return 변형 코드에서 추출한 원본 코드
     * @throws NotFoundChordException: 주어진 코드에서 원본 코드를 추출할 수 없을 때 발생
     */
    public static String extractBaseChord(String chord) {
        Matcher matcher = CHORD_PATTERN.matcher(chord);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new NotFoundChordException();
    }

    /**
     * 주어진 음계 시퀀스의 순서를 키 변경에 따라 순환에 의한 순서를 반환해주는 기능
     *
     * @param order  주어진 순서
     * @param amount 바꾸고자 하는 키 값
     * @return 순환에 따라 변경된 순서 값 반환
     */
    private static int getCircularOrder(int order, int amount) {
        if (order + amount < 0) {
            return chordOrdersItoA.size() + (order + amount);
        } else {
            return (order + amount) % chordOrdersItoA.size();
        }
    }
}

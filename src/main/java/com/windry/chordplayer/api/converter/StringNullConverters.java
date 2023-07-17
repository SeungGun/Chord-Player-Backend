package com.windry.chordplayer.api.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

public class StringNullConverters {

    @Component
    public static class LongAsNullConverter implements Converter<String, Long> {

        @Override
        public Long convert(String source) {
            if ("null".equals(source)) {
                return null;
            }
            return Long.parseLong(source);
        }
    }

    @Component
    public static class BooleanAsNullConverter implements Converter<String, Boolean> {

        @Override
        public Boolean convert(String source) {
            if ("null".equals(source)) {
                return null;
            }
            return Boolean.parseBoolean(source);
        }
    }

    @Component
    public static class IntegerAsNullConverter implements Converter<String, Integer> {

        @Override
        public Integer convert(String source) {
            if ("null".equals(source)) {
                return null;
            }
            return Integer.parseInt(source);
        }
    }

    @Component
    public static class StringAsNullConverter implements Converter<String, String> {

        @Override
        public String convert(String source) {
            if ("null".equals(source)) {
                return null;
            }
            return source;
        }
    }
}

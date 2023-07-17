package com.windry.chordplayer.config;

import com.windry.chordplayer.api.converter.StringNullConverters;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 커스텀 컨버터 등록
        registry.addConverter(new StringNullConverters.BooleanAsNullConverter());
        registry.addConverter(new StringNullConverters.IntegerAsNullConverter());
        registry.addConverter(new StringNullConverters.LongAsNullConverter());
        registry.addConverter(new StringNullConverters.StringAsNullConverter());
    }
}

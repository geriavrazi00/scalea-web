package com.scalea.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Messages {

    @Autowired
    private MessageSource messageSource;

    public String get(String code, Object... params) {
    	return this.messageSource.getMessage(code, params, LocaleContextHolder.getLocale());
    }
}
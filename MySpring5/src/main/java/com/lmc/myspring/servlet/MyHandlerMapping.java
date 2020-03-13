package com.lmc.myspring.servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Author Li Meichao
 * @Date 2020/3/12 0012
 * @Description
 */
@Data
public class MyHandlerMapping {
    private Method method;
    private Object instance;
    private Pattern pattern;

    public MyHandlerMapping(Pattern pattern, Object instance, Method method) {
        this.pattern = pattern;
        this.instance = instance;
        this.method = method;
    }
}

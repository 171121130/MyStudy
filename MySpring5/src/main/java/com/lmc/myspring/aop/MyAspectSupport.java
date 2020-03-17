package com.lmc.myspring.aop;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author Li Meichao
 * @Date 2020/3/17
 * @Description
 */
@Data
public class MyAspectSupport {
    private final Pattern pointCutClassPattern;
    private final String aspecName;
    private final Map<String, Method> aspectMethods;
    private final Pattern pointcutPattern;

    public MyAspectSupport(Pattern pointCutClassPattern, Map<String, Method> aspectMethods, String aspectName, Pattern pointcutPattern) {
        this.pointCutClassPattern = pointCutClassPattern;
        this.aspectMethods = aspectMethods;
        this.aspecName = aspectName;
        this.pointcutPattern = pointcutPattern;
    }
}

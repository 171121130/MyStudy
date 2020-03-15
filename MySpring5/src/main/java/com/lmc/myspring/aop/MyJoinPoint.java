package com.lmc.myspring.aop;

import java.lang.reflect.Method;

/**
 * @Author Li Meichao
 * @Date 2020/3/15 0015
 * @Description
 */
public class MyJoinPoint {
    public Object[] getArgs() {
        return new Object[0];
    }


    public Method getSignature() {
        return null;
    }
}

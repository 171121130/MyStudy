package com.lmc.myspring.annotation;


import java.lang.annotation.*;

/**
 * @Author Li Meichao
 * @Date 2020/3/15 0015
 * @Description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyBefore {
    String value();
}

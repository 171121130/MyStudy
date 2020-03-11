package com.lmc.myspring.annotation;

import java.lang.annotation.*;

/**
 * @Author Li Meichao
 * @Date 2020/3/2 0002
 * @Description
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}

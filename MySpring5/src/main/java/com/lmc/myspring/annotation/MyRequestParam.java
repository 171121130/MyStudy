package com.lmc.myspring.annotation;

import java.lang.annotation.*;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}

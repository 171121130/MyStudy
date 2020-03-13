package com.lmc.myspring.beans;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyBeanPostProcessor {
    public void postProcessBeforeInitialization(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessBeforeInitialization");
    }

    public void postProcessAfterInitialization(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessAfterInitialization");
    }
}

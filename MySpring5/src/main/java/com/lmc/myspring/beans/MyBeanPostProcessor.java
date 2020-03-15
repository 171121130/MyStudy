package com.lmc.myspring.beans;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public interface MyBeanPostProcessor {

    public Object postProcessBeforeInstantiation(Object instance, String beanName);

    public Object postProcessBeforeInitialization(Object instance, String beanName);

    public Object postProcessAfterInitialization(Object instance, String beanName);
}

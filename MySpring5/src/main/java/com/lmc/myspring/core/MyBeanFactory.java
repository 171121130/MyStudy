package com.lmc.myspring.core;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public interface MyBeanFactory {
    Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}

package com.lmc.myspring.ioc;

import com.lmc.myspring.beans.MyBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();
}

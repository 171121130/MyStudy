package com.lmc.myspring.beans;

import com.lmc.myspring.context.MyAbstractApplicationContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {

    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();

    public String[] getBeanNames() {
        String[] beanNames = new String[beanDefinitionMap.size()];
        Collection<MyBeanDefinition> beanDefinitions = beanDefinitionMap.values();
        Iterator<MyBeanDefinition> iterator = beanDefinitions.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            beanNames[i] = iterator.next().getBeanClassName();
            i++;
        }
        return beanNames;
    }


}

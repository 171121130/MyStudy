package com.lmc.myspring.beans;

import lombok.Data;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
@Data
public class MyBeanDefinition {
    //全类名
    private String beanClassName;
    private boolean lazyInit;
    //key:类名/接口名
    private String factoryBeanName;
}

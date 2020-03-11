package com.lmc.myspring.beans;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public MyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }

}

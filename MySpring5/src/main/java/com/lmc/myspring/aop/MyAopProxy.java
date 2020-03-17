package com.lmc.myspring.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Author Li Meichao
 * @Date 2020/3/17
 * @Description
 */
public class MyAopProxy implements InvocationHandler {
    private final MyAspectSupport aspect;
    private final Object targetInstance;
    private final String targetName;

    public MyAopProxy(Object targetInstance, MyAspectSupport aspect, String targetBeanName) {
        this.aspect = aspect;
        this.targetInstance = targetInstance;
        this.targetName = targetBeanName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //TODO 调用advices链，保证顺序！

        System.out.println("AOP, 调用成功！！！");
        Object result = method.invoke(targetInstance, args);
        return result;
    }

    public Object getProxy() {
        Class<?>[] interfaces = null;
        try {
            interfaces = targetInstance.getClass().getInterfaces();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Proxy.newProxyInstance(targetInstance.getClass().getClassLoader(), interfaces, this);
    }
}

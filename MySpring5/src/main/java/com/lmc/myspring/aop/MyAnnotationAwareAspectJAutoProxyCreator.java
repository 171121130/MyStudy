package com.lmc.myspring.aop;

import com.lmc.myspring.annotation.MyAspect;
import com.lmc.myspring.annotation.MyPointcut;
import com.lmc.myspring.beans.MyBeanPostProcessor;
import com.lmc.myspring.beans.MyDefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @Author Li Meichao
 * @Date 2020/3/15 0015
 * @Description
 */
public class MyAnnotationAwareAspectJAutoProxyCreator implements MyBeanPostProcessor {
    private List<String> aspectBeanNames = new LinkedList<String>();
    private final MyDefaultListableBeanFactory beanFactory;
    private Map<String, List<MyAdvisor>> advisorsCache;
    Pattern pointCutClassPattern;
    Map<Method, List<Object>> methodAdivcesCacheMap;
    Map<String, Method> aspectMethods;
    private List<MyAspectSupport> allAspectList = new LinkedList<MyAspectSupport>();

    public MyAnnotationAwareAspectJAutoProxyCreator(MyDefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Object instance, String beanNameToProcess) {
        System.out.println("MyBeanPostProcessor.postProcessBeforeInstantiation");
        //解析切面类，生成拦截器链
        List<MyAdvisor> advisors = new LinkedList();
        String[] beanNames = this.beanFactory.getBeanNames();

        //找到切面类，
        if (this.aspectBeanNames.isEmpty()) {
            synchronized(this) {
                for (String beanName : beanNames) {
                    try {
                        Class<?> clazz = Class.forName(beanName);
                        if (clazz.isAnnotationPresent(MyAspect.class)) {
                            this.aspectBeanNames.add(beanName);
                            parse(beanName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        //处理当前bean的所有方法是否需要进行切面，若需要，保存到method->所有advices的map
        try {
            processMatchTargetClass(beanNameToProcess);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void parse(String aspectName) throws Exception {

        Class<?> aspectClazz = Class.forName(aspectName);
        aspectMethods = new HashMap<String, Method>();
        Pattern pointcutPattern = null;
        methodAdivcesCacheMap = new HashMap<Method, List<Object>>();

        for (Method method : aspectClazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MyPointcut.class)) {
                String pointcut = method.getAnnotation(MyPointcut.class).value();
                pointcutPattern = compile(pointcut);
                String pointCutForClass = pointcut.substring(pointcut.lastIndexOf(" ") + 1, pointcut.lastIndexOf("(") - 2);
                pointCutClassPattern = compile(pointCutForClass);
            }
            //存储切面方法map
            aspectMethods.put(method.getDeclaredAnnotations()[0].annotationType().getName(), method);
        }

        this.allAspectList.add(new MyAspectSupport(pointCutClassPattern, aspectMethods, aspectName, pointcutPattern));
    }


    private void processMatchTargetClass(String beanName) throws Exception {

        Class<?> targetClazz = Class.forName(beanName);

        for (Method targetMethod : targetClazz.getDeclaredMethods()) {
            String methodString = targetMethod.toString();
            if (methodString.contains("throws")) {
                methodString = methodString.substring(0, methodString.indexOf("throws")).trim();
            }

            for (MyAspectSupport myAspectSupport : this.allAspectList) {

                Class<?> aspectClazz = Class.forName(myAspectSupport.getAspecName());
                Matcher matcher = myAspectSupport.getPointcutPattern().matcher(methodString);
                //目前遍历到的方法需要切面处理
                List<Object> advices = new LinkedList<Object>();
                if (matcher.matches()) {
                    advices.add(new MyMethodBeforeAdvice(aspectMethods.get("com.lmc.myspring.annotation.MyBefore"), aspectClazz));
                    advices.add(new MyMethodBeforeAdvice(aspectMethods.get("com.lmc.myspring.annotation.MyAfter"), aspectClazz));
                    if (methodAdivcesCacheMap.containsKey(targetMethod)) {
                        methodAdivcesCacheMap.get(targetMethod).add(advices);
                    } else {
                        methodAdivcesCacheMap.put(targetMethod, advices);
                    }
                }
            }


        }


    }


    @Override
    public Object postProcessBeforeInitialization(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessBeforeInitialization");
        return null;
    }


    @Override
    public Object postProcessAfterInitialization(Object targetInstance, String targetBeanName) {
        System.out.println("MyBeanPostProcessor.postProcessAfterInitialization");

        for (MyAspectSupport aspect : this.allAspectList) {
            if (aspect.getPointCutClassPattern().matcher(targetBeanName + ".").matches()) {
                targetInstance = new MyAopProxy(targetInstance, aspect, targetBeanName).getProxy();
            }
        }

        return targetInstance;
    }

    private Object wrapIfNecessary(Object instance, String beanName) {
        return null;
    }

    private boolean shouldSkip(Object instance, String beanName) {
        return true;
    }
    }

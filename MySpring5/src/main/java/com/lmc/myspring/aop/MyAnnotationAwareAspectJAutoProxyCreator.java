package com.lmc.myspring.aop;

import com.lmc.myspring.annotation.MyAspect;
import com.lmc.myspring.annotation.MyBefore;
import com.lmc.myspring.beans.MyBeanPostProcessor;
import com.lmc.myspring.beans.MyDefaultListableBeanFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author Li Meichao
 * @Date 2020/3/15 0015
 * @Description
 */
public class MyAnnotationAwareAspectJAutoProxyCreator implements MyBeanPostProcessor {
    private List<String> aspectBeanNames = null;
    private final MyDefaultListableBeanFactory beanFactory;
    private Map<String, List<MyAdvisor>> advisorsCache;

    public MyAnnotationAwareAspectJAutoProxyCreator(MyDefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInstantiation(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessBeforeInstantiation");
        //处理切面类！
        if (this.shouldSkip(instance, beanName)) {
            return null;
        }

        return null;
    }



    @Override
    public Object postProcessBeforeInitialization(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessBeforeInitialization");
        return null;
    }
    @Override
    public Object postProcessAfterInitialization(Object instance, String beanName) {
        System.out.println("MyBeanPostProcessor.postProcessAfterInitialization");
        //处理被代理类
        return wrapIfNecessary(instance, beanName);
    }

    private Object wrapIfNecessary(Object instance, String beanName) {
/*        if (beanName != null && this.targetSourcedBeans.contains(beanName)) {
            return bean;
        } else if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
            return bean;
        } else if (!this.isInfrastructureClass(bean.getClass()) && !this.shouldSkip(bean.getClass(), beanName)) {
            Object[] specificInterceptors = this.getAdvicesAndAdvisorsForBean();
            if (specificInterceptors != DO_NOT_PROXY) {
                this.advisedBeans.put(cacheKey, Boolean.TRUE);
                Object proxy = this.createProxy(bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
                this.proxyTypes.put(cacheKey, proxy.getClass());
                return proxy;
            } else {
                this.advisedBeans.put(cacheKey, Boolean.FALSE);
                return bean;
            }
        } else {
            this.advisedBeans.put(cacheKey, Boolean.FALSE);
            return bean;
        }*/
        return null;
    }


    private boolean shouldSkip(Object instance, String beanName) {
        List<MyAdvisor> candidateAdvisors = this.findAdvisorBeans();
        Iterator var4 = candidateAdvisors.iterator();

        MyAdvisor advisor;
        do {
            if (!var4.hasNext()) {
                return false;
            }
            advisor = (MyAdvisor)var4.next();
        } while(!((MyAbstractAspectJAdvice)advisor.getAdvice()).getAspectName().equals(beanName));

        return true;
    }

    //拿到所有切面的增强器
    private Object[] getAdvicesAndAdvisorsForBean() {
        return buildAspectJAdvisors().toArray();
    }

    //wrapIfNecessary()和shouldSkip（）、getAdvicesAndAdvisorsForBean（）都调用了它
    private List<MyAdvisor> findAdvisorBeans() {
        return buildAspectJAdvisors();
    }

    //参考BeanFactoryAspectJAdvisorsBuilder
    private List<MyAdvisor> buildAspectJAdvisors() {
         //this.aspectBeanNames第一次为null，找到aspectBeanNames和advisors
        List<String> aspectNames = this.aspectBeanNames;
        if (aspectNames == null) {
            synchronized(this) {
                aspectNames = this.aspectBeanNames;
                if (aspectNames == null) {
                    List<MyAdvisor> advisors = new LinkedList();
                    aspectNames = new LinkedList();
                    String[] beanNames = this.beanFactory.getBeanNames();

                    for (String beanName : beanNames) {
                        try {
                            Class<?> clazz = Class.forName(beanName);
                            if (clazz.isAnnotationPresent(MyAspect.class)) {
                                aspectNames.add(beanName);

                                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(factory);

                                this.advisorsCache.put(beanName, classAdvisors);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    this.aspectBeanNames = aspectNames;
                    return advisors;
                }
            }
        }

        //this.aspectBeanNames不为null，cache找到advisors
        if (aspectNames.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<MyAdvisor> advisors = new LinkedList();
            Iterator var3 = aspectNames.iterator();

            while(var3.hasNext()) {
                String aspectName = (String)var3.next();
                List<MyAdvisor> cachedAdvisors = this.advisorsCache.get(aspectName);
                if (cachedAdvisors != null) {
                    advisors.addAll(cachedAdvisors);
                } else {
                    List<MyAdvice> advices = new ArrayList();
                    try {
                        Class<?> clazz = Class.forName(aspectName);
                        Method[] methods = clazz.getMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(MyBefore.class)) {
                                advices.add(new MyBeforeAdvice(method, clazz.newInstance()));
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    advisors.put(mObject, advices);
                }
            }

            return advisors;
        }
        return null;
    }
}

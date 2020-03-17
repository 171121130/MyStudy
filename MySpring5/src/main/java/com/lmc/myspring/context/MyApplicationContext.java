package com.lmc.myspring.context;

import com.lmc.myspring.annotation.MyAutowired;
import com.lmc.myspring.annotation.MyController;
import com.lmc.myspring.annotation.MyService;
import com.lmc.myspring.aop.MyAnnotationAwareAspectJAutoProxyCreator;
import com.lmc.myspring.beans.*;
import com.lmc.myspring.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {
    private final String[] configLocations;
    private BeanDefinitionReader reader;
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, MyBeanWrapper>();
    //TODO 什么情况下使用线程安全map？？？
    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();
    private final List<MyBeanPostProcessor> beanPostProcessors = new ArrayList();

    public MyApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            register();
            refresh();
            System.out.println("====启动成功====");
            System.out.println("测试地址：http://localhost:8080/test/query?username=lmc");
            System.out.println("测试地址：http://localhost:8080/test/listClassName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void register() throws Exception {
        //定位配置文件
        reader = new BeanDefinitionReader(this.configLocations);
        //扫描加载Bean定义
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册Bean定义到容器
        doRegisterBeanDefinition(beanDefinitions);
    }

    @Override
    protected void refresh() throws Exception {
        System.out.println("MyApplicationContext.refresh");

        //先初始化BeanPostProcessor,加到BeanFactory的List
        registerBeanPostProcessors();

        //把不延迟加载的类提前初始化
        finishBeanFactoryInitialization();
    }

    private void registerBeanPostProcessors() {
        this.beanPostProcessors.add(new MyAnnotationAwareAspectJAutoProxyCreator(this));
    }

    private void finishBeanFactoryInitialization() {
        System.out.println("MyApplicationContext.doAutoWired");
        for (Map.Entry<String, MyBeanDefinition> stringMyBeanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            if (!stringMyBeanDefinitionEntry.getValue().isLazyInit()) {
                String beanName = stringMyBeanDefinitionEntry.getKey();
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        System.out.println("MyApplicationContext.doRegisterBeanDefinition");
        System.out.println("beanDefinitions = " + beanDefinitions);
        for (MyBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception(beanDefinition.getFactoryBeanName() + " is exist");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {

        System.out.println("beanName = " + beanName);

        Object instance = null;
        //拿到后置处理器实例
        MyBeanPostProcessor beanPostProcessor = this.beanPostProcessors.get(0);
        //拿到bean定义
        MyBeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

        //AOP, proxy尝试返回，
        beanPostProcessor.postProcessBeforeInstantiation(instance, beanDefinition.getBeanClassName());
//        shouldSkip ->findCandidateAdvisors() -> buildAspectJAdvisors() ->
//        Object proxy = this.createProxy(beanClass, beanName, specificInterceptors, targetSource);
//
        //实例化bean
        instance = instantiateBean(beanDefinition);

        //包装实例
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
        //存储cache
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        //初始化注入属性
        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
        populateBean(beanName, instance);
        Object proxy = beanPostProcessor.postProcessAfterInitialization(instance, beanDefinition.getBeanClassName());

        if (proxy != null) {
            instance = proxy;
            //包装实例
            beanWrapper = new MyBeanWrapper(instance);
            //存储cache
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
        }

        //返回cache包装实例
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, Object instance) {
        System.out.println("MyApplicationContext.populateBean");
        System.out.println("beanName = " + beanName + ", instance = " + instance);
        //拿到实例类的所有fields，检查注解和value，有则从容器取注入到该实例的field
        Class<?> clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(MyService.class) && !clazz.isAnnotationPresent(MyController.class)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(MyAutowired.class)) {
                continue;
            }
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);
            String autowiredBeanNameValue = annotation.value().trim();
            if ("".equals(autowiredBeanNameValue)) {
                autowiredBeanNameValue = field.getType().getName();
            }

            if (!factoryBeanInstanceCache.containsKey(autowiredBeanNameValue)) {
                try {
                    this.getBean(autowiredBeanNameValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Object beanInstance = factoryBeanInstanceCache.get(autowiredBeanNameValue).getWrappedInstance();

            field.setAccessible(true);
            try {
                field.set(instance, beanInstance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Object instantiateBean(MyBeanDefinition beanDefinition) {
        System.out.println("MyApplicationContext.instantiateBean");
        System.out.println("beanDefinition = " + beanDefinition);

        Object instance = null;
        String beanClassName = beanDefinition.getBeanClassName();
        try {
            if (this.factoryBeanObjectCache.containsKey(beanClassName)) {
                instance = this.factoryBeanObjectCache.get(beanClassName);
            } else {
                Class<?> clazz = Class.forName(beanClassName);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(beanClassName, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return this.getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        return super.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

}

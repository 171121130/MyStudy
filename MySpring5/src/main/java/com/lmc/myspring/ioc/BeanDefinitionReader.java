package com.lmc.myspring.ioc;

import com.lmc.myspring.beans.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();
    //全类名
    private List<String> registryBeanClasses = new ArrayList<String>();

    public BeanDefinitionReader(String[] configLocations) {
        System.out.println("BeanDefinitionReader.BeanDefinitionReader");
        System.out.println("configLocations = " + Arrays.deepToString(configLocations));
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:", ""));
        try {
            config.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        doScan(config.getProperty("scan-package"));
    }

    private void doScan(String scanPackage) {
        System.out.println("BeanDefinitionReader.doScan");
        System.out.println("scanPackage = " + scanPackage);
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File filePath = new File(url.getFile());
        for (File file : filePath.listFiles()) {
            if (file.isDirectory()) {
                doScan(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                registryBeanClasses.add(className);
            }
        }

    }

    //根据全类名，拿到类信息
    public List<MyBeanDefinition> loadBeanDefinitions() {
        System.out.println("BeanDefinitionReader.loadBeanDefinitions");
        List<MyBeanDefinition> result = new ArrayList<MyBeanDefinition>();
        for (String className : this.registryBeanClasses) {
            try {
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()) {
                    continue;
                }
                //类小写名-bean名
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                for (Class<?> anInterface : beanClass.getInterfaces()) {
                    //接口全名-bean名
                    result.add(doCreateBeanDefinition(anInterface.getName(), beanClass.getName()));
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String name) {
        System.out.println("BeanDefinitionReader.doCreateBeanDefinition");
        System.out.println("factoryBeanName = " + factoryBeanName + ", name = " + name);
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(name);
        return beanDefinition;
    }

    private String toLowerFirstCase(String name) {
        char[] chars = name.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}

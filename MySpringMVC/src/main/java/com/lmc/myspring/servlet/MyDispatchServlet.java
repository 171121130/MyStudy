package com.lmc.myspring.servlet;

import com.lmc.myspring.annotation.MyAutowired;
import com.lmc.myspring.annotation.MyController;
import com.lmc.myspring.annotation.MyRequestMapping;
import com.lmc.myspring.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @Author Li Meichao
 * @Date 2020/3/2 0002
 * @Description
 */
public class MyDispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();
    private List<String> classNameList = new ArrayList<String>();
    private HashMap<String, Object> iocMap = new HashMap<String, Object>();
    private HashMap<String, Method> handlerMapping = new HashMap<String, Method>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException {
        String contextPath = req.getContextPath();
        String requestURL = req.getRequestURI();
        String url = requestURL.replaceAll(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            try {
                resp.getWriter().write("404!!!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Method method = handlerMapping.get(url);
        String beanName = this.toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        Object object = iocMap.get(beanName);
        method.invoke(object, req, resp);
        System.out.println("success!" + iocMap.get(beanName) + "and" + method.getName());

    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

        //1、加载配置文件
        doLoadConfig(servletConfig.getInitParameter("contextConfigLocation"));

        //2、扫描配置类
        doScanner(contextConfig.getProperty("scan-package"));

        //3、初始化IOC容器，保存实例到容器中
        doInstance();

        //4、依赖注入
        doAutowired();

        //5、初始化HandlerMapping
        initHandlerMapping();

        System.out.println("MySpring Framework is initialized");

        doTestPrintData();

    }

    /**
     * 6、打印数据
     */
    private void doTestPrintData() {

        System.out.println("[INFO-6]----data------------------------");

        System.out.println("contextConfig.propertyNames()-->" + contextConfig.propertyNames());

        System.out.println("[classNameList]-->");
        for (String str : classNameList) {
            System.out.println(str);
        }

        System.out.println("[iocMap]-->");
        for (Map.Entry<String, Object> entry : iocMap.entrySet()) {
            System.out.println(entry);
        }

        System.out.println("[handlerMapping]-->");
        for (Map.Entry<String, Method> entry : handlerMapping.entrySet()) {
            System.out.println(entry);
        }

        System.out.println("[INFO-6]----done-----------------------");

        System.out.println("====启动成功====");
        System.out.println("测试地址：http://localhost:8080/test/query?username=lmc");
        System.out.println("测试地址：http://localhost:8080/test/listClassName");
    }

    private void initHandlerMapping() {
        if (iocMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> iocEntry : iocMap.entrySet()) {
            Class<?> clazz = iocEntry.getValue().getClass();
            if (!clazz.isAnnotationPresent(MyController.class)) {
                continue;
            }

            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                baseUrl = clazz.getAnnotation(MyRequestMapping.class).value();
            }

            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                String metholdUrl = method.getAnnotation(MyRequestMapping.class).value();
                String url = ("/" + baseUrl + "/" + metholdUrl).replaceAll("/+", "/");
                handlerMapping.put(url, method);
            }

        }
    }


    private void doAutowired() {

        for (Map.Entry<String, Object> entry : iocMap.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }

                MyAutowired fieldAnnotation = field.getAnnotation(MyAutowired.class);
                String beanName = fieldAnnotation.value().trim();
                if ("".equals(beanName)) {
                    beanName = this.toLowerFirstCase(field.getType().getSimpleName());
                }

                field.setAccessible(true);

                try {
                    field.set(entry.getValue(), iocMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void doInstance() {
        if (classNameList.isEmpty()) {
            return;
        }

        try {
            for (String className : classNameList) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    String beanName = this.toLowerFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    iocMap.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(MyService.class)) {
                    String beanName = this.toLowerFirstCase(clazz.getSimpleName());
                    MyService myService = clazz.getAnnotation(MyService.class);
                    if (!"".equals(myService.value())) {
                        beanName = myService.value();
                    }

                    Object instance = clazz.newInstance();
                    iocMap.put(beanName, instance);

                    //关联service的所有接口
                    for (Class<?> serviceInterface : clazz.getInterfaces()) {
                        if (iocMap.containsKey(this.toLowerFirstCase(serviceInterface.getSimpleName()))) {
                            throw new Exception("Bean已存在！");
                        }
                        iocMap.put(this.toLowerFirstCase(serviceInterface.getSimpleName()), instance);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doScanner(String scanPackage) {

        URL resource = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        if (resource == null) {
            return;
        }

        File files = new File(resource.getFile());

        for (File file : files.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }

                String classname = (scanPackage + "." + file.getName().replace(".class", ""));
                classNameList.add(classname);
            }
        }
    }

    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

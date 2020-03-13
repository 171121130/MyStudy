package com.lmc.myspring.servlet;

import com.lmc.myspring.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Li Meichao
 * @Date 2020/3/12 0012
 * @Description
 */
public class HandlerAdapter {

    public MyModelView handle(HttpServletRequest req, HttpServletResponse resp, MyHandlerMapping handler) throws InvocationTargetException, IllegalAccessException {

        //注解 加入参数列表
        Annotation[][] parameterAnnotations = handler.getMethod().getParameterAnnotations();
        HashMap<String, Integer> paramMapping = new HashMap<String, Integer>();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof MyRequestParam) {
                    if ("".equals(((MyRequestParam) annotation).value().trim())) {
                        continue;
                    }
                    paramMapping.put(((MyRequestParam) annotation).value().trim(), i);
                }
            }
        }

        //Servlet原生无注解类型加入参数列表
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == HttpServletRequest.class || parameterTypes[i] == HttpServletResponse.class) {
                paramMapping.put(parameterTypes[i].getName(), i);
            }
        }

        //遍历request域中入参给params赋值
        Object[] paramsValue = new Object[parameterTypes.length];
        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            if (paramMapping.containsKey(entry.getKey())) {
                Integer index = paramMapping.get(entry.getKey());
                //替换[]和空格
                String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
                paramsValue[index] = caseStringValue(value, parameterTypes[index]);
            }
        }
        //需要注入httpRequest入参
        if (paramMapping.containsKey(HttpServletRequest.class.getName())) {
            Integer index = paramMapping.get(HttpServletRequest.class.getName());
            paramsValue[index] = req;
        }
        if (paramMapping.containsKey(HttpServletResponse.class.getName())) {
            Integer index = paramMapping.get(HttpServletResponse.class.getName());
            paramsValue[index] = resp;
        }

        Object result = handler.getMethod().invoke(handler.getInstance(), paramsValue);

        if (result == null) {
            return null;
        }

        if (handler.getMethod().getReturnType() == MyModelView.class) {
            return (MyModelView) result;
        }

        return null;

    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (parameterType == String.class) {
            return value;
        } else if (parameterType == Integer.class) {
            return Integer.valueOf(value);
        } else if (parameterType == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }

    public boolean supports(MyHandlerMapping handler) {
        return handler instanceof MyHandlerMapping;
    }
}

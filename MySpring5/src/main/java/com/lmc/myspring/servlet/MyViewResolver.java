package com.lmc.myspring.servlet;

import java.io.File;

/**
 * @Author Li Meichao
 * @Date 2020/3/13 0013
 * @Description
 */
public class MyViewResolver {

    private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private String templateFilePath;
    private String viewName;

    public MyViewResolver(String filePath) {
        this.templateFilePath = filePath;
    }

    public MyView resolveViewName(String viewName, Object ob) {
        this.viewName = viewName;
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)? viewName:(viewName + DEFAULT_TEMPLATE_SUFFIX);
        File file = new File((templateFilePath + "/" + viewName).replaceAll("/+", "/"));
        return new MyView(file);
    }
}

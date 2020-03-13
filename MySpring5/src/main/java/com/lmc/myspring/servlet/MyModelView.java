package com.lmc.myspring.servlet;

import lombok.Data;

import java.util.Map;

/**
 * @Author Li Meichao
 * @Date 2020/3/13 0013
 * @Description
 */
@Data
public class MyModelView {

    private String viewName;
    private Map<String, ?> model;

    public MyModelView(String viewName) {
        this(viewName, null);
    }

    public MyModelView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}

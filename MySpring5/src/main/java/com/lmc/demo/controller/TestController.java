package com.lmc.demo.controller;

import com.lmc.demo.service.TestService;
import com.lmc.myspring.annotation.MyAutowired;
import com.lmc.myspring.annotation.MyController;
import com.lmc.myspring.annotation.MyRequestMapping;
import com.lmc.myspring.annotation.MyRequestParam;
import com.lmc.myspring.servlet.MyModelView;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Author Li Meichao
 * @Date 2020/3/3 0003
 * @Description
 */
@MyController
@MyRequestMapping("/test")
@Slf4j
public class TestController {

    @MyAutowired
    TestService testService;

    @MyRequestMapping("/query")
    public MyModelView query(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("username") String name) {

        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("data", "\\\\test+test+{}*^.&");

        return new MyModelView("first", model);
    }

    @MyRequestMapping("/listClassName")
    public void listClassName(HttpServletRequest request, HttpServletResponse response) {
        String listClassName = testService.listClassName();
        System.out.println(listClassName);
        try {
            response.getWriter().write(listClassName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

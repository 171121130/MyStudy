package com.lmc.demo.controller;

import com.lmc.demo.service.TestService;
import com.lmc.myspring.annotation.MyAutowired;
import com.lmc.myspring.annotation.MyController;
import com.lmc.myspring.annotation.MyRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Li Meichao
 * @Date 2020/3/3 0003
 * @Description
 */
@MyController
@MyRequestMapping("/test")
public class TestController {

    @MyAutowired
    TestService testService;

    @MyRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp) {

        if (req.getParameter("username") == null) {
            try {
                resp.getWriter().write("param username is null");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            String paramName = req.getParameter("username");
            try {
                resp.getWriter().write("param username is " + paramName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[INFO-req] New request param username-->" + paramName);
        }

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

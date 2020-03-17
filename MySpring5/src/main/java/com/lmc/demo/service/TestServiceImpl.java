package com.lmc.demo.service;

import com.lmc.myspring.annotation.MyService;

/**
 * @Author Li Meichao
 * @Date 2020/3/3 0003
 * @Description
 */
@MyService
public class TestServiceImpl implements TestService {

    @Override
    public String listClassName() {
        System.out.println("service to aop");
        return "lmc-test";
    }
}



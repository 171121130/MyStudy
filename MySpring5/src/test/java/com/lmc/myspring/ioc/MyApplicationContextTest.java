package com.lmc.myspring.ioc;


import org.junit.Test;

/**
 * @Author Li Meichao
 * @Date 2020/3/11
 * @Description
 */
public class MyApplicationContextTest {

    @Test
    public void refresh() throws Exception {
        MyApplicationContext myApplicationContext = new MyApplicationContext("classpath:application.properties");
    }

}

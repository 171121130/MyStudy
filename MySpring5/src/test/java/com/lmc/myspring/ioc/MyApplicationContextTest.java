package com.lmc.myspring.ioc;


import com.lmc.myspring.context.MyApplicationContext;
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

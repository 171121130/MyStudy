package com.atguigu.service;

import com.atguigu.bean.Teacher;
import com.atguigu.dao.TeacherDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Li Meichao
 * @Date 2020/2/29 0029
 * @Description
 */
@Service
public class TeacherService {

    @Autowired
    TeacherDao teacherDao;

    public Teacher hello(Integer id) {
        return teacherDao.getById(id);
    }

}

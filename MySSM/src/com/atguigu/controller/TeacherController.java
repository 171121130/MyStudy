package com.atguigu.controller;

/**
 * @Author Li Meichao
 * @Date 2020/2/29 0029
 * @Description
 */

import com.atguigu.bean.Teacher;
import com.atguigu.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TeacherController {

    @Autowired
    TeacherService teacherService;

    @RequestMapping("/getTea")
    public String getTeacher(@RequestParam(value = "id")Integer id, Model model) {
        Teacher t = teacherService.hello(id);
        model.addAttribute("teacher", t);
        return "success";
    }

}

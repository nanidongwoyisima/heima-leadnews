package com.heima.freemarker.controller;

import com.heima.freemarker.pojos.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/10/14:20
 * @Description:am
 */
@Controller
public class HelloController {


    @GetMapping("/basic")
    public String freeMarker(Model model){
        model.addAttribute("name","freeMarker");
        Student student = new Student();
        student.setName("嗯呐");
        student.setAge(20);
        model.addAttribute("stu",student);

        return "01-basic";
    }

}

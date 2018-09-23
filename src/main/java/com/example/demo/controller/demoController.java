package com.example.demo.controller;

import com.example.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DATE 2018/9/23.
 * @author xupeng.
 */
@RestController
public class demoController {

    @Autowired
    DemoService demoService;

    @GetMapping("/hello")
    public String run(){
        return demoService.run();
    }
}

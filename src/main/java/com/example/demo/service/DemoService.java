package com.example.demo.service;

import com.example.mark.annotation.Mark;
import org.springframework.stereotype.Service;

/**
 * DATE 2018/9/23.
 * @author xupeng.
 */
@Service
public class DemoService {

    @Mark(message = "bitcoin is the future")
    public String run(){
        return "this is a demo";
    }
}

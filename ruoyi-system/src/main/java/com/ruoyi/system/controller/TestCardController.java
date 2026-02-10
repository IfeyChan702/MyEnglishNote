package com.ruoyi.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author boyo
 */
@RestController
@RequestMapping("/test")
public class TestCardController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello World";
    }
}

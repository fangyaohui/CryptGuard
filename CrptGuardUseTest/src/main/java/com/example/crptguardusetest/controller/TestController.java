package com.example.crptguardusetest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @FileName TestController
 * @Description
 * @Author yaoHui
 * @date 2024-12-09
 **/
@Slf4j
@RestController
public class TestController {


    @GetMapping("/getTest")
    public String getTest(){
        return "getTest";
    }


}

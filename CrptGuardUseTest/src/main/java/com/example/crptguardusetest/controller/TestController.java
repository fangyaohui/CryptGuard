package com.example.crptguardusetest.controller;

import com.crypt.cryptguard.annotation.DecryptRequest;
import com.example.crptguardusetest.Entity.UserInfoPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /***
     * @Author yaoHui
     * @Date 2024/12/14
     * @Description URL&参数需要解密Post请求
     */
    @PostMapping("/decrypt/postRequest")
    public UserInfoPO decryptPostRequest(@RequestBody UserInfoPO userInfoPO){
        log.info("decryptPostRequest params is {}",userInfoPO.toString());
        return userInfoPO;
    }

    @PostMapping("/decrypt/annotation/postRequest")
    @DecryptRequest()
    public UserInfoPO decryptAnnotationPostRequest(@RequestBody UserInfoPO userInfoPO){
        log.info("decryptAnnotationPostRequest params is {}",userInfoPO.toString());
        return userInfoPO;
    }

    @PostMapping("/decrypt/annotation/decryptValuesOnlyRequest")
    @DecryptRequest(decryptValuesOnly = true)
    public UserInfoPO decryptValuesOnlyRequest(@RequestBody UserInfoPO userInfoPO){
        log.info("decryptValuesOnlyRequest params is {}",userInfoPO.toString());
        return userInfoPO;
    }

    /***
     * @Author yaoHui
     * @Date 2024/12/14
     * @Description URL&参数不需要解密Post请求
     */
    @PostMapping("/noDecrypt/postRequest")
    public String noDecryptPostRequest(){
        return "noDecryptPostRequest";
    }


}

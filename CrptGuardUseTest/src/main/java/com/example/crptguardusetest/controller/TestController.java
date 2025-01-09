package com.example.crptguardusetest.controller;

import com.crypt.cryptguard.annotation.*;
import com.example.crptguardusetest.Entity.ComplexUser;
import com.example.crptguardusetest.Entity.R;
import com.example.crptguardusetest.Entity.UserInfoPO;
import com.example.crptguardusetest.service.impl.UserInfoServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @FileName TestController
 * @Description
 * @Author yaoHui
 * @date 2024-12-09
 **/
@Slf4j
@RestController
@AllArgsConstructor
@CryptController(allParamsDecrypt = false)
//@DecryptController(allParamsDecrypt = false)
//@EncryptController
public class TestController {

    private UserInfoServiceImpl userInfoService;


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
    @DecryptRequest(allParamsDecrypt = false)
    @EncryptResponse()
    public R<ComplexUser> decryptValuesOnlyRequest(@RequestBody ComplexUser complexUser){
        log.info("decryptValuesOnlyRequest params is {}",complexUser.toString());
        return R.success(complexUser);
    }

    @PostMapping("/decrypt/annotation/cryptMethodRequest")
    @CryptMethod(allParamsDecrypt = false)
    public R<ComplexUser> cryptMethodRequest(@RequestBody ComplexUser complexUser){
        log.info("cryptMethodRequest params is {}",complexUser.toString());
        return R.success(complexUser);
    }

    @PostMapping("/decrypt/annotation/decryptControllerMethodRequest")
    public R<ComplexUser> decryptControllerMethodRequest(@RequestBody ComplexUser complexUser){
        log.info("decryptControllerMethodRequest params is {}",complexUser.toString());
        UserInfoPO userInfoPO = userInfoService.getById(1);
        log.info("find user_info is : "+userInfoPO.toString());
        UserInfoPO tempUserInfo = new UserInfoPO();
        tempUserInfo.setUsername("fang"+ UUID.randomUUID());
        tempUserInfo.setPassword("fang" + UUID.randomUUID());
        tempUserInfo.setAge("124313");
        userInfoService.save(tempUserInfo);
        return R.success(complexUser);
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

package com.example.crptguardusetest.Entity;

import lombok.Data;

import java.util.List;

/**
 * @FileName UserInfoPO
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Data
public class UserInfoPO {

    private String username;

    private String password;

    private List<String> stringList;

}

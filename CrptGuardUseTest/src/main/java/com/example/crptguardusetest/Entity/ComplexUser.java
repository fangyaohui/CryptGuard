package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @FileName ComplexUser
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Data
public class ComplexUser {

//    private String name;
//    @DecryptTransient
//    private String email;
//    private Address address;
//    @DecryptTransient
//    private List<Account> accounts;
    @DecryptTransient
    private Map<String, Object> metadata;
}

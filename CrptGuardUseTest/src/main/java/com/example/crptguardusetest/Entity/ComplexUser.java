package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.CryptTransient;
import com.crypt.cryptguard.annotation.DecryptTransient;
import com.crypt.cryptguard.annotation.EncryptTransient;
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
//@EncryptTransient
//@DecryptTransient
@CryptTransient
public class ComplexUser {

    private String name;

    private String email;

    private Address address;

    private List<Account> accounts;

    private Map<String, Object> metadata;
}

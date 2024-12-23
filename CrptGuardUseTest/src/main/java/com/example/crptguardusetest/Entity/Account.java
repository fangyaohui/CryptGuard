package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;

/**
 * @FileName Account
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
public class Account {
    private String type;
    private String balance;
    @DecryptTransient
    private String limit;
    private Credentials credentials;
}

package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;
import com.crypt.cryptguard.annotation.EncryptTransient;
import lombok.Data;

/**
 * @FileName Account
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Data
public class Account {
    private String type;
    private String balance;
    @DecryptTransient
    @EncryptTransient
    private String limit;
    private Credentials credentials;
}

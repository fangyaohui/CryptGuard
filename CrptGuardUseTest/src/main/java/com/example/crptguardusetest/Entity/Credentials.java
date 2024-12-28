package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;
import lombok.Data;

/**
 * @FileName Credentials
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Data
public class Credentials {
    @DecryptTransient
    private String password;
    private String pin;
}

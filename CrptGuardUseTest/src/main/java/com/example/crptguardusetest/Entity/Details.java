package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;

/**
 * @FileName Details
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
public class Details {

    private String buildingCode;

    @DecryptTransient
    private String securityCode;
}

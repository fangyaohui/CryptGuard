package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;
import com.crypt.cryptguard.annotation.EncryptTransient;
import lombok.Data;

/**
 * @FileName Details
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Data
public class Details {

    private String buildingCode;

    @DecryptTransient
    @EncryptTransient
    private String securityCode;
}

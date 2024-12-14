package com.example.cryptGuard;

import com.example.cryptguard.utils.AESUtils;
import org.junit.jupiter.api.Test;

/**
 * @FileName CryptGuardTest
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
public class CryptGuardTest {

    @Test
    void AESUtilsTest(){
        /*
         * /api/crypt/decrypt/postRequest : 8FDE0E2D7BB7B3EB4B402962F0DAF67E611E4ACB06ADC0E3C7E610F5F01FC268
         * fangyaohui : 53BF507896DE85521F302A572BDEA342
         * 123456 : AD40FA5492E44B19277FF3370A65CB49
         */
        String originalText = "123456";
        String password = "fang";

        // 加密
        String encryptedText = AESUtils.encode(originalText, password);
        System.out.println("Encrypted: " + encryptedText);

        // 解密
        String decryptedText = AESUtils.decode(encryptedText, password);
        System.out.println("Decrypted: " + decryptedText);
    }

    @Test
    void requestTest() {

    }
}

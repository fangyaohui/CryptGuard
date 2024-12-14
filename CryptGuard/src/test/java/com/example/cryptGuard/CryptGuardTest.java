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
         * 92032247FBEB6DC8FB9695EEEA0B1C4CF2CEBBC21F64CBA999553710F7FD955EF8C085640C1BBF71BF25DA45F695C73FAAB18F0871B052A4F2D513416300DFC28CC2F815F4C403EDB97AA70223F625A60721C569F7F000D0C82D099188EF21E466723E6D59DEB2594614E032D68CAAF9D543366A6103A7F2B1150B5078F39217
         */
        String originalText = "{\n" +
                "    \"username\" : \"fangyaohui\",\n" +
                "    \"password\" : \"123456\",\n" +
                "    \"stringList\": [\n" +
                "    \"item1\",\n" +
                "    \"item2\",\n" +
                "    \"item3\"\n" +
                "  ]\n" +
                "}";
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

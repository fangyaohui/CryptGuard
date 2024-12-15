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

    public String password = "fang";

    @Test
    void AESUtilsDecryptTest(){
        String encryptedText = "5959C40ACE09C70BD8507DF08ED8A2A55BD3A1420527F46C28183DFBD151742B0CC3272787B25A3F9907B0A5C38AAAAF969F0372520BDD952B3D7C8C845659A2B0688B5C875488195A8898D29E4BE7CA53E1FB985032BA1D1953CD56FC7C80FF";

        // 解密
        String decryptedText = AESUtils.decode(encryptedText, password);
        System.out.println("Decrypted: " + decryptedText);
    }

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

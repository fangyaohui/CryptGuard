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
         * 80924550A2E689E80DBE94E202292878B183A051D4E59A9D73CE8C23E68032C7D59045DAFA25F0B5D285941EA5B76CA16308EDF9E4D8D37E211D030F2D8D2EAD5A8E9CD35B7B9A0DE4E0FE0C56BAF6DC3B55D7AB67892B104A71A30145DEF3A1802E48FFF8083B6CFA1EF2FED0C2E45B1B5827D85406021BB3C45A60EA2875B2B4BF68C22F1F3C93010EBE3E8580C73A0FC89CEF61109C190E6FAE2C1ADD9C9EC7B500E3216A484B8B24AD6A8EF31C4A7533BE212B70AA7B8871A8C6E6C408C66D27548797B5EC588DB8DB9649DF1A77DD2550B3ABB6882C10CE80FD11E7CC89BD75C314F52B8BC223A0C60F7AE8831AD11C8CE801D44233E66C20753B86841244A4996463EBC7D93E88E83A0FB5A5F3E42E17D2BD934B7B8089FD9B3919CE95CD29AC9D299426639A42CCC3860529B47E6ECD6CF8B078C5CE79FA1FB203E561C15A2FCC45954001E184C76A71DB1E94D18A6AB42287FC694F7CC9888DC77560DD2B057C265B1663D4F3180B5A2F3C6B53CBDBBCD9572870857B2E3DF06F9D09E5D199E189F39FB3A835D28A475B45C2
         */

        String originalText = "{\n" +
                "  \"username\": \"exampleUsername\",\n" +
                "  \"password\": \"examplePassword\",\n" +
                "  \"stringList\": [\"item1\", \"item2\", \"item3\"],\n" +
                "  \"paramLongObject\": 1234567890123,\n" +
                "  \"paramIntegerObject\": 123,\n" +
                "  \"paramLong\": 1234567890,\n" +
                "  \"paramInt\": 456,\n" +
                "  \"paramDoubleObject\": 12345.67,\n" +
                "  \"paramDouble\": 12345.67,\n" +
                "  \"paramFloat\": 123.45,\n" +
                "  \"paramFloatObject\": 123.45,\n" +
                "  \"rolePO\": {\n" +
                "    \"roleName\": \"Admin\",\n" +
                "    \"roleId\": 1\n" +
                "  }\n" +
                "}\n";


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

package com.crypt.cryptguard.strategy;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @FileName DefaultCryptionStrategy
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
public class DefaultCryptStrategy implements CryptStrategy{

    // AES加密算法模式和填充方式
    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_MODE = "AES/ECB/PKCS5Padding";
    private static final String privateKey = "fang";

    @Override
    public String encrypt(String plainText) {
        try {
            // 获取AES密钥
            SecretKey key = generateSecretKey();

            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // 执行加密
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            // 返回加密结果（转换为十六进制字符串）
            return bytesToHex(encryptedBytes);

        } catch (Exception e) {
            // 记录异常信息，并返回null
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decrypt(String encryptedText) {
        try {
            // 判断传入的字符串是否是有效的十六进制
            if (!isHex(encryptedText)) {
                // 如果不是有效的十六进制字符串，直接返回原始字符串（认为是明文）
                return encryptedText;
            }

            // 将十六进制密文转换为字节数组
            byte[] encryptedBytes = hexToBytes(encryptedText);

            // 获取AES密钥
            SecretKey key = generateSecretKey();

            // 创建密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE, key);

            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // 返回解密结果（转换为字符串）
            return new String(decryptedBytes);

        } catch (Exception e) {
            // 记录异常信息，可能是由于解密失败引起的
//            e.printStackTrace();
            // 返回原始字符串
            return encryptedText;
        }
    }

    /**
     * 根据密码生成AES密钥
     * @return SecretKey
     */
    private static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(DefaultCryptStrategy.privateKey.getBytes());
        keyGenerator.init(128, secureRandom);
        return new SecretKeySpec(keyGenerator.generateKey().getEncoded(), ALGORITHM);
    }

    /**
     * 将字节数组转换为十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // 将每个字节转换为两位十六进制数
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 判断字符串是否是有效的十六进制字符串
     * @param str 字符串
     * @return 如果是有效的十六进制字符串返回true，否则返回false
     */
    private static boolean isHex(String str) {
        if (str == null || str.length() % 2 != 0) {
            return false;
        }

        // 判断字符串是否只包含0-9和A-F字符
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c) && (c < 'A' || c > 'F')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将十六进制字符串转换为字节数组
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    private static byte[] hexToBytes(String hexString) {
        int length = hexString.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length.");
        }

        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return result;
    }
}

package com.crypt.cryptguard.strategy;

/**
 * @FileName CryptStrategy
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
public interface CryptStrategy {

    /**
     * 加密方法
     * @param plainText 明文
     * @return 密文
     */
    String encrypt(String plainText) throws Exception;

    /**
     * 解密方法
     * @param encryptedText 密文
     * @return 明文
     */
    String decrypt(String encryptedText) throws Exception;

}

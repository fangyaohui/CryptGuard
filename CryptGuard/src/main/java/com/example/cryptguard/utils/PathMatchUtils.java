package com.example.cryptguard.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @FileName PathMatchUtils
 * @Description 该URL是否是用户配置的需要进行加密或者解密
 * @Author yaoHui
 * @date 2024-12-14
 **/
public class PathMatchUtils {

    // 将用户配置的URL模式转换为正则表达式
    private static String convertToRegexPattern(String urlPattern) {
        // 转换 /api/* 为 /api/.* 以适应正则表达式
        return urlPattern.replace("/*", "/.*");
    }

    // 判断解密后的URL是否符合用户配置的URL列表
    public static boolean isPathMatching(String path, List<String> userConfiguredURLs) {
        for (String pattern : userConfiguredURLs) {
            // 转换用户配置的URL模式（如 /api/*）为正则表达式
            String regexPattern = convertToRegexPattern(pattern);
            Pattern compiledPattern = Pattern.compile(regexPattern);

            // 如果解密后的URL匹配其中一个配置
            if (compiledPattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }
}

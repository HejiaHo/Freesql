package com.freesql.util;

/**
 * 字符串工具类
 * User: jse7en
 * Date: 14-3-19
 * Time: 上午1:21
 * Version: 1.0
 */
public class StringUtil {
    /**
     * 对字符串进行压缩，删除所有空格、换行符、制表符
     * @param s 要压缩的字符串
     * @return 压缩完成后的字符串
     */
    public static String compress(String s) {
        return s.replaceAll("\\s", "");
    }
}

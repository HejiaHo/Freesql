package com.freesql.function;

import java.util.List;
import java.util.Map;

/**
 * Freesql函数接口
 * User: jse7en
 * Date: 14-3-19
 * Time: 上午12:45
 * Version: 1.0
 */
public interface FreesqlFunction {
    /**
     * 基于函数参数，及Map对象，返回sql参数列表，
     * 该方法返回的sql参数列表应该与sqlReplace的参数相对应
     * @param funcParas 函数参数字符串
     * @param requestParams 参数值
     * @return sql参数列表
     * @throws FunctionParamsException
     */
    List<String> setSqlParas(String funcParas, Map<String, Object> requestParams)
            throws FunctionParamsException;

    /**
     * 对sql的函数片段进行替换
     * @param funcParas 函数参数字符串
     * @return sql函数替换结果
     */
    String sqlReplace(String funcParas);
}

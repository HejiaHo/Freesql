package com.freesql.function;


import com.freesql.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基本函数的实现
 * User: jse7en
 * Date: 14-3-19
 * Time: 上午1:10
 * Version: 1.0
 */
public abstract class CommonFunction implements FreesqlFunction {
    @Override
    public List<String> setSqlParas(String funcParas, Map<String, Object> requestParams) throws FunctionParamsException {
        funcParas = StringUtil.compress(funcParas);
        String[] paras = funcParas.split(",");

        List<Object> funcParaValues = new ArrayList<Object>();
        for (String para : paras) {
            funcParaValues.add(requestParams.get(para));
        }

        List<String> list = new ArrayList<String>();
        list.add(processParas(funcParaValues));
        return list;
    }

    /**
     * 对函数的参数值列表进行处理，并返回处理结果
     * @param paraValues 参数值列表
     * @return 处理结果
     */
    protected abstract String processParas(List<Object> paraValues);

    @Override
    public String sqlReplace(String funcParas) {
        return "?";
    }
}

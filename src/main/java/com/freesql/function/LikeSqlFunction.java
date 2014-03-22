package com.freesql.function;

import java.util.List;

/**
 * 对Like查询的处理函数
 * User: jse7en
 * Date: 14-3-19
 * Time: 上午12:54
 * Version: 1.0
 */
public class LikeSqlFunction extends CommonFunction {

    @Override
    protected String processParas(List<Object> paraValues) {
        return "%" + paraValues.get(0) + "%";
    }

    @Override
    public String sqlReplace(String funcParas) {
        return "LIKE ?";
    }
}

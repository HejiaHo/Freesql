package com.freesql.util;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Freemarker工具类
 * User: jse7en
 * Date: 14-3-17
 * Time: 下午4:19
 * Version: 1.0
 */
public class FreemarkerUtil {
    /**
     * 对freemarker模板进行解析，返回解析结果
     * @param template Freemarker模板对象
     * @param model 参数
     * @return 解析完成后的字符串
     * @throws java.io.IOException
     * @throws freemarker.template.TemplateException
     */
    public static String processTempIntoString(Template template, Object model) throws IOException, TemplateException {
        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }
}

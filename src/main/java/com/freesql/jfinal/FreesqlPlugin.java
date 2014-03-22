package com.freesql.jfinal;

import com.freesql.function.FreesqlFunction;
import com.freesql.function.FunctionParamsException;
import com.freesql.function.LikeSqlFunction;
import com.freesql.util.FreemarkerUtil;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基于jfinal的Freesql插件
 * User: jse7en
 * Date: 14-3-19
 * Time: 下午4:52
 * Version: 1.0
 */
public class FreesqlPlugin implements IPlugin {
    public static final String DEFAULT_FREESQL_PATH = "freesql";

    private static Configuration config;
    private static Map<String, FreesqlFunction> funcMap;
    private static Pattern pattern;

    /**
     *
     * @param dir freesql模板基于web根目录的存放位置
     */
    public FreesqlPlugin(String dir) {
        config = new Configuration();

        funcMap = new HashMap<String, FreesqlFunction>();
        pattern = Pattern.compile("\\{(\\w+)(\\(((\\w+ *,? *)+)\\))?\\}");

        try {
            config.setDirectoryForTemplateLoading(new File(PathKit.getWebRootPath(), dir));
            funcMap.put("like", new LikeSqlFunction());
        } catch (IOException e) {
            throw new RuntimeException("文件路径未找到", e);
        }

    }

    public FreesqlPlugin() {
        this(DEFAULT_FREESQL_PATH);
    }

    /**
     * 设置freemarker参数
     * @param key 参数名
     * @param value 参数值
     */
    public void setProperty(String key, String value) {
        try {
            config.setSetting(key, value);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置freemarker参数
     * @param properties 参数
     */
    public static void setProperties(Properties properties) {
        try {
            config.setSettings(properties);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加freesql函数，在模板文件中检测到函数时，会对函数名进行匹配，调用对应注册的freesql函数进行解析
     * @param funcName freesql函数名
     * @param function freesql函数
     */
    public void addFunction(String funcName, FreesqlFunction function) {
        funcMap.put(funcName, function);
    }

    @Override
    public boolean start() {

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    /**
     * 对模板进行处理
     * @param tmpName 基于模板目录的模板名
     * @param objs 用于存放参数的列表
     * @param root 参数映射
     * @return 解析完成后的sql
     */
    public static String processSql(String tmpName, List<Object> objs, Map<String, String[]> root) {
        try {
            Template template = config.getTemplate(tmpName);

            //处理请求参数，如果值的大小为1， 则直接设置为第一个值
            Map<String, Object> paras = new HashMap<String, Object>();
            for (String key : root.keySet()) {
                if (root.get(key).length == 1) {
                    paras.put(key, root.get(key)[0]);
                } else {
                    paras.put(key, root.get(key));
                }
            }

            String result = FreemarkerUtil.processTempIntoString(template, paras);

            StringBuffer sb = new StringBuffer();
            Matcher matcher = pattern.matcher(result);

            while(matcher.find()) {
                //匹配function中的值
                if (matcher.group(3) != null) {
                    String func = matcher.group(1);
                    String key = matcher.group(3);

                    FreesqlFunction function = funcMap.get(func);
                    if (function == null)
                        throw new RuntimeException("未找到 函数" + func + " 的定义");


                    List<String> funcParasList = function.setSqlParas(key, paras);
                    if (funcParasList != null) {
                        objs.addAll(funcParasList);
                    }

                    matcher.appendReplacement(sb, function.sqlReplace(key));
                } else {
                    String key = matcher.group(1);

                    if (paras.containsKey(key)) {

                        objs.add(paras.get(key));
                    }
                    matcher.appendReplacement(sb, "?");
                }

            }

            matcher.appendTail(sb);

            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("文件未找到", e);
        } catch (TemplateException e) {
            throw new RuntimeException("Freemarker文件错误", e);
        } catch (FunctionParamsException e) {
            throw new RuntimeException("freesql功能参数有误", e);
        }

    }
}

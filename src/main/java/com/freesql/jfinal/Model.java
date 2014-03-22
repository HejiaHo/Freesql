package com.freesql.jfinal;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于原功能，增加使用Freesql模板的方法
 * User: jse7en
 * Date: 14-3-15
 * Time: 下午7:40
 * Version: 1.0
 */
public abstract class Model<M extends Model> extends com.jfinal.plugin.activerecord.Model<M> {

    /**
     * 根据模板进行分页
     * @param pageNumber 当前页数
     * @param pageSize 每页显示大小
     * @param tempName 基于模板目录的模板名
     * @param root 参数映射
     * @return 分页结果
     */
    public Page<M> paginateByTemp(int pageNumber, int pageSize, String tempName, Map<String, String[]> root) {
        List<Object> objects = new ArrayList<Object>();
        String result = FreesqlPlugin.processSql(tempName, objects, root);
        String countSql = "SELECT count(*) FROM (" + result + ") _t_";

        long totalRow = Db.queryLong(countSql, objects.toArray());
        int totalPage = 0;


        totalPage = (int) (totalRow / pageSize);
        if (totalRow % pageSize != 0) {
            totalPage++;
        }


        String fSql = "";
        if (DbKit.getDialect() instanceof OracleDialect) {
            int start = (pageNumber - 1) * pageSize + 1;
            int end = pageNumber * pageSize;

            fSql = "SELECT * FROM ( SELECT _t1_.*, rownum AS _r_ FROM ( " + result
                    + " ) AS _t1_ WHERE rownum <= " + end + " ) _t2_ WHERE t2._r_ >= " + start;
        } else if (DbKit.getDialect() instanceof PostgreSqlDialect) {

            int offset = pageSize * (pageNumber - 1);
            fSql = result + " LIMIT " + pageSize + " OFFSET " + offset;
        } else {
            //mysql、sqlite
            int offset = pageSize * (pageNumber - 1);
            fSql = result + " LIMIT " + offset + ", " + pageSize;
        }

        System.out.printf("执行查询:\n%s\n参数值: %s", fSql, objects);
        List<M> list = this.find(fSql, objects.toArray());

        return new Page<M>(list, pageNumber, pageSize, totalPage, (int)totalRow);
    }

    /**
     * 根据模板执行sql进行查询
     * @param tempName 基于模板目录的模板名
     * @param root 参数映射
     * @return 结果列表
     */
    public List<M> findByTemp(String tempName, Map<String, String[]> root) {
        List<Object> objects = new ArrayList<Object>();
        String result = FreesqlPlugin.processSql(tempName, objects, root);

        System.out.printf("执行查询:\n%s\n参数值: %s", result, objects);
        return this.find(result, objects.toArray());
    }

    /**
     * 根据模板执行sql，获取第一条结果
     * @param tempName 基于模板目录的模板名
     * @param root 参数映射
     * @return 第一条结果
     */
    public M findFirstByTemp(String tempName, Map<String, String[]> root) {
        List<M> result = findByTemp(tempName, root);
        return result.size() > 0 ? result.get(0) : null;
    }

}

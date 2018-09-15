package cn.lonsun.site.template.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.lsrobot.vo.RobotPageVO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.supervise.vo.SupervisePageVO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;

/**
 * @author gu.fei
 * @version 2015-9-2 11:23
 */
public class SqlHelper {

    /*
    * keys : keyValue       模糊查询
    * sortField : sortOrder 排序字段
    * */
    public static String getSearchAndOrderSql(String sql,ParamDto paramDto) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(paramDto.getKeyValue()) && !AppUtil.isEmpty(paramDto.getKeys())) {
            sb.append(" and (");
            String[] str = paramDto.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(paramDto.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(paramDto.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortField()) ? "createDate" : paramDto.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortOrder()) ? "desc" : paramDto.getSortOrder());

        return sb.toString();
    }

    /*
    * keys : keyValue       模糊查询
    * sortField : sortOrder 排序字段
    * */
    public static String getSearchAndOrderSql(String sql,WordsPageVO vo) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(vo.getKeyValue()) && !AppUtil.isEmpty(vo.getKeys())) {
            sb.append(" and (");
            String[] str = vo.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortField()) ? "createDate" : vo.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortOrder()) ? "desc" : vo.getSortOrder());

        return sb.toString();
    }

    public static String getSearchAndOrderSql(String sql,CollectPageVO vo) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(vo.getKeyValue()) && !AppUtil.isEmpty(vo.getKeys())) {
            sb.append(" and (");
            String[] str = vo.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortField()) ? "createDate" : vo.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortOrder()) ? "desc" : vo.getSortOrder());

        return sb.toString();
    }

    public static String getSearchAndOrderSql(String sql,RobotPageVO vo) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(vo.getKeyValue()) && !AppUtil.isEmpty(vo.getKeys())) {
            sb.append(" and (");
            String[] str = vo.getKeys().split(",");
            if(vo.getIfExact()) {
                for(int i = 0 ; i < str.length ; i++) {
                    if(i == 0)
                        sb.append(" " + str[i] + "='" + vo.getKeyValue() + "'");
                    else {
                        sb.append(" or ");
                        sb.append(" " + str[i] + "='" + vo.getKeyValue() + "'");
                    }
                }
            } else {
                for(int i = 0 ; i < str.length ; i++) {
                    if(i == 0)
                        sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                    else {
                        sb.append(" or ");
                        sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                    }
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortField()) ? "createDate" : vo.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortOrder()) ? "desc" : vo.getSortOrder());

        return sb.toString();
    }

    /*
    * keys : keyValue       模糊查询
    * sortField : sortOrder 排序字段
    * */
    public static String getSearchAndOrderSql(String sql,WeiboPageVO vo) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(vo.getKeyValue()) && !AppUtil.isEmpty(vo.getKeys())) {
            sb.append(" and (");
            String[] str = vo.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortField()) ? "createDate" : vo.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortOrder()) ? "desc" : vo.getSortOrder());

        return sb.toString();
    }

    /*
    * keys : keyValue       模糊查询
    * sortField : sortOrder 排序字段
    * */
    public static String getSearchAndOrderSqlByAS(String sql,ParamDto paramDto) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(paramDto.getKeyValue()) && !AppUtil.isEmpty(paramDto.getKeys())) {
            sb.append(" and (");
            String[] str = paramDto.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" t." + str[i] + " like '%" + SqlUtil.prepareParam4Query(paramDto.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" t." + str[i] + " like '%" + SqlUtil.prepareParam4Query(paramDto.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortField()) ? "t.createDate" : paramDto.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(paramDto.getSortOrder()) ? "desc" : paramDto.getSortOrder());

        return sb.toString();
    }

    /*
   * keys : keyValue       模糊查询
   * sortField : sortOrder 排序字段
   * */
    public static String getSearchAndOrderSql(String sql,SupervisePageVO vo) {

        StringBuffer sb = new StringBuffer(sql);

        if(!AppUtil.isEmpty(vo.getKeyValue()) && !AppUtil.isEmpty(vo.getKeys())) {
            sb.append(" and (");
            String[] str = vo.getKeys().split(",");
            for(int i = 0 ; i < str.length ; i++) {
                if(i == 0)
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                else {
                    sb.append(" or ");
                    sb.append(" " + str[i] + " like '%" + SqlUtil.prepareParam4Query(vo.getKeyValue()) + "%'");
                }
            }

            sb.append(" ) ");
        }

        sb.append(" order by");
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortField()) ? "createDate" : vo.getSortField());
        sb.append(" ");
        sb.append(AppUtil.isEmpty(vo.getSortOrder()) ? "desc" : vo.getSortOrder());

        return sb.toString();
    }
}

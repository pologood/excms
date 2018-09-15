package cn.lonsun.nlp.utils;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: liuk
 * @version: v1.0
 * @date:2018/6/29 15:30
 */
public class ContentUtil {

    // 文件分隔符
    public static final String SEPARATOR = "/";
    // 默认首页名称
    private static final String DEFAULT_INDEX_NAME = "index";
    // 默认文件类型
    private static final String DEFAULT_FILE_SUFFIX = ".html";


    /**
     * 获取文章页访问路径
     * @param columnId
     * @param contentId
     * @param siteId
     * @return
     */
    public static String getLinkPath(Long columnId,Long contentId,Long siteId) {

        String uri = "";
        SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
        if(siteMgrEO==null){
            return uri;
        }
        uri = siteMgrEO.getUri();
        StringBuffer lp = new StringBuffer();
        lp.append(processUri(uri));// 加上域名
        if (null != columnId) {// 拼接栏目路径
            ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
            if(column == null){ //如果栏目不存在，则查询单位
                OrganEO eo = CacheHandler.getEntity(OrganEO.class, columnId);
                if(eo != null){
                    lp.append(SEPARATOR).append("public").append(SEPARATOR).append(columnId);
                }
            }else{
                lp.append(SEPARATOR).append(column.getUrlPath());
            }
        }
        if (null != contentId) {// 文章页
            lp.append(SEPARATOR).append(contentId);
        }else{ //不是文章就是栏目页
            lp.append(SEPARATOR).append(DEFAULT_INDEX_NAME);
        }
        lp.append(DEFAULT_FILE_SUFFIX);

        return lp.toString();
    }


    /**
     * 处理uri，删除后面的斜杠
     *
     * @param uri
     * @return
     * @author fangtinghua
     */
    private static String processUri(String uri) {
        uri = StringUtils.defaultString(uri);
        if (StringUtils.isEmpty(uri)) {
            return StringUtils.EMPTY;
        }
        int lastLen = uri.lastIndexOf(SEPARATOR);
        return lastLen == uri.length() - 1 ? uri.substring(0, lastLen) : uri;
    }
}

package cn.lonsun.solr;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.XSSFilterUtil;
import cn.lonsun.solr.vo.SolrPageQueryVO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.parser.QueryParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2017-04-21 10:31
 */
public class SolrUtil {

    //所有字段数组
    public static final String[] ALL_FIELDS_SOLR = new String[]{"title", "remark", "content", "setAccord", "applyCondition", "handleData", "handleProcess"};
    //默认类型
    private static final String[] DEFAULT_TYPE_CODES = new String[]{BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(), BaseContentEO.TypeCode.videoNews.toString()};

    private static final String SITE_ID = "siteId";
    private static final String COLUMN_ID = "columnId";
    private static final String TYPE_CODE = "typeCode";
    private static final String TYPE = "type";
    private static final String INDEX_NUM = "indexNum";

    private static boolean fuzzySearch = false;//设置是否模糊匹配

    /**
     * 根据条件获取solrQuery
     *
     * @param vo
     * @return
     * @throws SolrServerException
     */
    public static SolrQuery getSolrQuery(SolrPageQueryVO vo) throws SolrServerException {
        String keywords = XSSFilterUtil.stripXSSSolr(vo.getKeywords());
        Long siteId = vo.getSiteId();
        Long columnId = vo.getColumnId();
        String columnIds = vo.getColumnIds();
        String excColumns = vo.getExcColumns();
        String typeCode = vo.getTypeCode();
        SolrQuery query = new SolrQuery();

        fuzzySearch = vo.isFuzzySearch();//设置是否模糊匹配

        //关键字条件，用逗号隔开条件在这里是 “并” 条件
        if (!AppUtil.isEmpty(keywords)) {
            keywords = StringUtils.rplcBlank(keywords, ",");
            //对keywords做处理,查询之前对字符串做转义
            keywords = dencodeHTML(keywords);
            keywords = QueryParser.escape(keywords);
            List<String> kwarr = StringUtils.getListWithString(keywords, ",");
            String fromcodes = vo.getFromCode();
            int count = 0;
            /*或关系*/
            StringBuilder bq = new StringBuilder();
            //设置过滤
            StringBuilder dismaxvalue = new StringBuilder();
            if (AppUtil.isEmpty(fromcodes)) {
                for (String str : kwarr) {
                    if (count++ == 0) {
                        bq.append(getSolrParmsString(str, ALL_FIELDS_SOLR));
                    } else {
                        bq.append(getSolrParmsString(str, ALL_FIELDS_SOLR));
                    }
                }
            } else {
                List<String> list = StringUtils.getListWithString(fromcodes, ",");
                for (String str : kwarr) {
                    if (count++ == 0) {
                        bq.append(getSolrParmsString(str, list));
                    } else {
                        bq.append(getSolrParmsString(str, list));
                    }
                }
            }
            query.set(DisMaxParams.QF,dismaxvalue.toString().trim());
            query.setQuery(bq.toString());//查询之前对字符串做转义
            /*且关系*/
//            if(AppUtil.isEmpty(fromcodes)) {
//                for(String str : kwarr) {
//                    if(count++ == 0) {
//                        query.setQuery(getSolrParmsString(str,ALL_FIELDS_SOLR));
//                    }
//                    else {
//                        query.addFilterQuery(getSolrParmsString(str,ALL_FIELDS_SOLR));
//                    }
//                }
//            }
//            else {
//                List<String> list = StringUtils.getListWithString(fromcodes,",");
//                for(String str : kwarr) {
//                    if(count++ == 0) {
//                        query.setQuery(getSolrParmsString(str,list));
//                    }
//                    else {
//                        query.addFilterQuery(getSolrParmsString(str,list));
//                    }
//                }
//            }
        }
        //过滤不允许搜索的关键字
        if (vo.getFilterKeyWords() != null && vo.getFilterKeyWords().length > 0) {
            query.addFilterQuery(getSolrValuesStringNotIn(ALL_FIELDS_SOLR, vo.getFilterKeyWords()));
        }

        //过滤站点
//        if (null != siteId) {
//            query.addFilterQuery(getSolrValuesString(SITE_ID, String.valueOf(siteId)));
//        }

        //过滤栏目
        if (null != columnId) {
            query.addFilterQuery(getSolrValuesString(COLUMN_ID, String.valueOf(columnId)));
        } else if (null != columnIds) {
            List<String> list = StringUtils.getListWithString(columnIds, ",");
//            for(String column : list) {
            query.addFilterQuery(getSolrValuesString(COLUMN_ID, list).trim());
//            }
        }

        if (null != excColumns) {
            List<String> list = StringUtils.getListWithString(excColumns, ",");
            for (String column : list) {
                query.addFilterQuery("!" + getSolrValuesString(COLUMN_ID, column).trim());
            }
        }
        //栏目类型
        if (!AppUtil.isEmpty(typeCode)) {
            //点击全部时查询所有
            if("all".equals(typeCode)){
                typeCode = "workGuide,public_content,guestBook,articleNews,pictureNews,videoNews";
            }
            List<String> list = StringUtils.getListWithString(typeCode, ",");
            query.addFilterQuery(getSolrValuesString(TYPE_CODE, list));
        } else {
            query.addFilterQuery(getSolrValuesString(TYPE_CODE, DEFAULT_TYPE_CODES));
        }

        //政民互动类型
        if (!AppUtil.isEmpty(vo.getType())) {
            query.addFilterQuery(getSolrValuesString(TYPE, vo.getType()));
        }

        //信息公开索引
        if (!AppUtil.isEmpty(vo.getIndexNum())) {
            query.addFilterQuery(getSolrValuesString(INDEX_NUM, vo.getIndexNum()));
        }

        //根据时间段检索
        if (null != vo.getBeginDate() && null != vo.getEndDate()) {
            query.addFilterQuery("createDate:[" + getSolrDate(vo.getBeginDate()) + " TO " + getSolrDate(vo.getEndDate()) + "]");
        } else if (null != vo.getBeginDate() && null == vo.getEndDate()) {
            query.addFilterQuery("createDate:[" + getSolrDate(vo.getBeginDate()) + " TO * ]");
        } else if (null == vo.getBeginDate() && null != vo.getEndDate()) {
            query.addFilterQuery("createDate:[ * TO " + getSolrDate(vo.getEndDate()) + "]");
        }

        query.setStart(0);
        query.setRows(10);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>" + query.toString());
        return query;
    }


    /**
     * 筛选字段中不允许搜索的内容关键字
     *
     * @param field
     * @param values
     * @return
     */
    public static String getSolrValuesStringNotIn(String[] field, String... values) {
        StringBuffer sb = new StringBuffer();
        if (values != null && values.length >= 1) {
            for (int i = 0; i < field.length; i++) {
                for (String value : values) {
                    sb.append(" ").append(field[i]);
                    sb.append(":(- \"");
                    sb.append(value).append("\")");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 组合solr 或 查询条件 ||
     *
     * @param field
     * @param values
     * @return
     */
    public static String getSolrValuesString(String field, String... values) {
        StringBuffer sb = new StringBuffer();
        if (values != null && values.length >= 1) {
            for (String value : values) {
                sb.append(" ").append(field);
                sb.append(":");
                if (fuzzySearch) {//如果模糊匹配，则不需要加引号
                    sb.append(value);
                }else {
                    sb.append("\"").append(value).append("\"");
                }
                sb = setTitlePinyinQuery(sb,field,value);
            }
        }
        return sb.toString();
    }

    /**
     * 组合solr 或 查询条件 ||
     *
     * @param field
     * @param values
     * @return
     */
    public static String getSolrValuesString(String field, List<String> values) {
        StringBuffer sb = new StringBuffer();
        if (values != null && values.size() >= 1) {
            for (String value : values) {
                sb.append(" ").append(field);
                sb.append(":");
                if (fuzzySearch) {//如果模糊匹配，则不需要加引号
                    sb.append(value);
                }else {
                    sb.append("\"").append(value).append("\"");
                }
                sb = setTitlePinyinQuery(sb,field,value);
            }
        }
        return sb.toString();
    }

    /**
     * 组合solr 或 查询条件 ||
     *
     * @param value
     * @param fields
     * @return
     */
    public static String getSolrParmsString(String value, String... fields) {
        StringBuffer sb = new StringBuffer();
        if (fields != null && fields.length >= 1) {
            for (String field : fields) {
                sb.append(" ").append(field);
                sb.append(":");
                if (fuzzySearch) {//如果模糊匹配，则不需要加引号
                    sb.append(value);
                }else {
                    sb.append("\"").append(value).append("\"");
                }
                sb = setTitlePinyinQuery(sb,field,value);
            }
        }
        return sb.toString();
    }

    /**
     * 组合solr 或 查询条件 ||
     *
     * @param value
     * @param fields
     * @return
     */
    public static String getSolrParmsString(String value, List<String> fields) {
        StringBuffer sb = new StringBuffer();
        if (fields != null && fields.size() >= 1) {
            for (String field : fields) {
                sb.append(" ").append(field);
                sb.append(":");
                if (fuzzySearch) {//如果模糊匹配，则不需要加引号
                    sb.append(value);
                }else {
                    sb.append("\"").append(value).append("\"");
                }
                sb = setTitlePinyinQuery(sb,field,value);
            }
        }
        return sb.toString();
    }

    /**
     * 获取拼音检索
     * @param sb
     * @param field
     * @param value
     * @return
     */
    private static StringBuffer setTitlePinyinQuery(StringBuffer sb,String field,String value) {
        if(null != field && null != value &&
                field.equals("title") && !isContainChinese(value)) {
            if(isPingyin(value)) {
                sb.append(" ").append("titlePinyin");
                sb.append(":").append("\"");
                sb.append(value).append("\"");
            }
        }
        return sb;
    }

    /**
     * 判断是否包含中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isPingyin(String str) {
        Pattern p = Pattern.compile("^[A-Za-z]+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 格式化时间，匹配solr 时间格式，解决时区差异问题
     *
     * @param date
     * @return
     */
    private static String getSolrDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    /**
     * 去除前台传入的字符串中的特殊字符
     * @param t
     * @return
     */
    public static String dencodeHTML(String t) {
        if (!AppUtil.isEmpty(t)) {
            t = t.replaceAll("@amp;", "&");
            t = t.replaceAll("&quot;", "\"");
            t = t.replaceAll("@lt;", "<");
            t = t.replaceAll("@gt;", ">");
            t = t.replaceAll("@#146;", "'");
            t = t.replaceAll("@nbsp;", " ");
            t = t.replaceAll("%23", "#");

            t = t.replaceAll("\n\r", "\n");
            t = t.replaceAll("\r\n", "\n");
        }
        return t;
    }

    public static void main(String[] args) {
    }
}

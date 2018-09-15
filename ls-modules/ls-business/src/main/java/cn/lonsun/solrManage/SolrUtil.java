package cn.lonsun.solrManage;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import cn.lonsun.util.FileUtil;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.PropertiesHelper;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2017-08-25 10:08
 */
public class SolrUtil {

    private static final Logger logger = LoggerFactory.getLogger(SolrUtil.class);

    private static HttpSolrServer server;

    private static PropertiesHelper properties = SpringContextHolder.getBean("propertiesHelper");

    private static TaskExecutor taskExecutor = SpringContextHolder.getBean("taskExecutor");

    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder.getBean("mongoDbFileServer");

    private static IDataDictItemService dataDictItemService = SpringContextHolder.getBean("dataDictItemService");

    private static String HREF_REGEX = "<a\\s+[^<>]*\\s+href=\"/download/([^<>\"]*)\"[^<>]*>";

    //类型排序信息
    private static Map<String,Integer> typeSortMap = null;

    public static final Integer default_sort_num = 10000;

    static {
        init();
    }

    /**
     * 重新设置typeSortMap
     */
    public static void resetTypeSort() {
        typeSortMap = new HashMap<String, Integer>();
        Pagination dictPage = dataDictItemService.getPageByDictId(0L, 100, 6384876L, null);
        List<DataDictItemEO> list = (List<DataDictItemEO>)dictPage.getData();

        for (DataDictItemEO eo:list) {
            String code = eo.getCode();
            String descrip = eo.getDescription();
            JSONObject obj = JSONObject.parseObject(descrip);
            boolean isShow = obj.getBooleanValue("isShow");
            //如果前台未设置显示，则排序值设为默认值
            Integer sortNum = isShow? Integer.parseInt(eo.getValue()) : default_sort_num;

            String[] codes = code.split(",");
            for (String key : codes) {
                typeSortMap.put(key,sortNum);
            }
        }
    }

    /**
     * 获取typeSortMap
     */
    public static Map<String,Integer> getTypeSortMap() {
        if (typeSortMap == null) {
            resetTypeSort();
        }
        return typeSortMap;
    }

    /**
     * 设置类型排序
     * @param vo
     */
    public static void setTypeSortNum(SolrIndexVO vo) {
        Map<String,Integer> tempMap = getTypeSortMap();
        vo.setTypeSortNum(tempMap.get(vo.getTypeCode()) == null ? default_sort_num : tempMap.get(vo.getTypeCode()));
    }

    /**
     * 创建单个索引
     * @param vo
     */
    public static boolean createIndex(final SolrIndexVO vo) {
        setTypeSortNum(vo);//设置类型排序
        setFileIndex(vo);
        if(null != vo.getContent()) {
            vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
        }
        UpdateResponse response = null;
        try {
            server.addBean(vo);
            response = server.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 批量创建索引
     * @param vos
     */
    public static boolean createIndex(final Set<SolrIndexVO> vos) {
        final Set<SolrIndexVO> voset = new HashSet<SolrIndexVO>();
        for(SolrIndexVO vo : vos) {
            setTypeSortNum(vo);//设置类型排序
            setFileIndex(vo);
            if(null != vo.getContent()) {
                vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
            }

            if(null != vo.getColumnId()) {
                voset.add(vo);
            }
        }
        UpdateResponse response = null;
        try {
            server.addBeans(voset.iterator());
            response = server.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 异步批量创建索引
     * @param vos
     */
    public static void asynCreateIndex(final Set<SolrIndexVO> vos) {
        final Set<SolrIndexVO> voset = new HashSet<SolrIndexVO>();
        for(SolrIndexVO vo : vos) {
            setTypeSortNum(vo);//设置类型排序
            setFileIndex(vo);
            if(null != vo.getContent()) {
                vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
            }
            if(null != vo.getColumnId()) {
                voset.add(vo);
            }
        }
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("========启动异步线程创建索引========");
                server.setRequestWriter(new BinaryRequestWriter());
                try {
                    server.addBeans(voset.iterator());
                    server.commit();
                    logger.info("========索引创建完成========");
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 批量更新索引
     * @param vos
     */
    public static boolean updateIndex(final Set<SolrIndexVO> vos) {
        List<String> ids = new ArrayList<String>();
        for (SolrIndexVO vo : vos) {
            ids.add(vo.getId());
        }
        if(deleteIndex(ids)) {
            return createIndex(vos);
        }
        return false;
    }

    /**
     * 更新索引
     * @param vo
     */
    public static boolean updateIndex(final SolrIndexVO vo) {
        if (null != vo && vo.getId() != null) {
            if(deleteIndex(vo.getId())) {
                return createIndex(vo);
            }
        }
        return false;
    }

    /**
     * 根据ID单个删除索引
     * @param id
     */
    public static boolean deleteIndex(final String id) {
        UpdateResponse response = null;
        try {
            server.deleteByQuery("id:\"" + id + "\"");
            response = server.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 删除索引
     * @param typeCode
     */
    public static boolean deleteIndexByTypeCode(String typeCode,Long siteId) {
        UpdateResponse response = null;
        try {
            StringBuilder s = new StringBuilder("typeCode:\"" + typeCode + "\"");
            if(null != siteId) {
                s.append(" AND siteId:\"" + siteId + "\"");
            }
            server.deleteByQuery(s.toString());
            response = server.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 批量删除索引
     * @param columnId
     */
    public static boolean deleteIndex(final Long columnId) {
        UpdateResponse response = null;
        try {
            server.deleteByQuery("columnId:\"" + columnId + "\"");
            response = server.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 批量删除索引
     * @param ids
     */
    public static boolean deleteIndex(final List<String> ids) {
        StringBuilder sb = new StringBuilder();
        if (null != ids) {
            boolean flag = true;
            for (String id : ids) {
                if (flag) {
                    sb.append("id:\"" + id + "\"");
                    flag = false;
                } else {
                    sb.append("OR id:\"" + id + "\"");
                }
            }
        }
        UpdateResponse response = null;
        try {
            server.deleteByQuery(sb.toString());
            response = server.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 清空所有索引
     */
    public static boolean clearIndex() {
        return clearIndex(null);
    }

    /**
     * 清空指定站点下索引
     * @param siteId
     * @return
     */
    public static boolean clearIndex(Long siteId) {
        UpdateResponse response = null;
        try {
            String q = "*:*";
            if(null != siteId) {
                q = "siteId:\" + siteId + \"";
            }
            server.deleteByQuery(q);
            response = server.commit();
        } catch (SolrServerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if(null != response && response.getStatus() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 设置
     * @param vo
     */
    public static SolrIndexVO setFileIndex(SolrIndexVO vo) {
        if(null != vo.getContent()) {
            List<String> mongIds = getRegexList(vo.getContent(),HREF_REGEX);
            if(null != mongIds && !mongIds.isEmpty()) {
                for(String mongId : mongIds) {
                    GridFSDBFile grid = mongoDbFileServer.getGridFSDBFile(mongId,null);
                    if(null != grid) {
                        String content = FileUtil.getFileString(grid);
                        if(null != content) {
                            vo.setContent(vo.getContent() + content);
                        }
                    }
                }
            }
        }
        return vo;
    }

    /**
     * 正则表达式匹配
     * @param content
     * @param regex
     * @return
     */
    private static List<String> getRegexList(String content,String regex){
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);// 匹配的模式
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i));
            i++;
        }
        return list;
    }

    public static void init() {
        server = new HttpSolrServer(properties.getSorlUrl());
        server.setConnectionTimeout(5000);
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxRetries(1);
        server.setMaxTotalConnections(100);
        //通过二种方式增加docs,其中server.add(docs.iterator())效率最高
        server.setRequestWriter(new BinaryRequestWriter());
    }
}

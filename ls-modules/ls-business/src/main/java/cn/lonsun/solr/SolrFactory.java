package cn.lonsun.solr;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.HtmlUtil;
import cn.lonsun.util.PropertiesHelper;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author gu.fei
 * @version 2016-1-13 9:44
 */
public class SolrFactory {

    private static final Logger logger = LoggerFactory.getLogger(SolrFactory.class);

    private static HttpSolrServer server;

    private static PropertiesHelper properties = SpringContextHolder.getBean("propertiesHelper");

    private static String HREF_REGEX = "<a\\s+[^<>]*\\s+href=\"/download/([^<>\"]*)\"[^<>]*>";

    @Autowired
    private static TaskExecutor taskExecutor;

    static {
        server = new HttpSolrServer(properties.getSorlUrl());
        server.setConnectionTimeout(5000);
        server.setDefaultMaxConnectionsPerHost(100);
        server.setMaxRetries(1);
        server.setMaxTotalConnections(100);
        taskExecutor = SpringContextHolder.getBean("taskExecutor");
    }

    /**
     * 重启时同步索引数据
     */
    public static void syncIndexFromDb() throws
            SolrServerException, IOException {
        //清空索引
        clearIndex();

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                try {
                    SolrBaseIndexUtil.createPictureNewsIndex(); //图片新闻
                    SolrBaseIndexUtil.createVideoNewsIndex(); //视频新闻
                    SolrBaseIndexUtil.createWorkGuidesIndex(); //网上办事
                    SolrBaseIndexUtil.createGuestsIndex(); //留言
                    SolrBaseIndexUtil.createSurveysIndex(); //调查管理
                    SolrBaseIndexUtil.createReviewsIndex(); //网上调查
                    SolrBaseIndexUtil.createInterviewsIndex(); //在线访谈
                    SolrBaseIndexUtil.createCollectInfosIndex(); //民意征集
                    SolrBaseIndexUtil.createLeaderInfosIndex(); //领导之窗
                    SolrBaseIndexUtil.createArticleNewsIndex(); //文字新闻
                    SolrBaseIndexUtil.createPublicInfoIndex(); //信息公开
                    SolrBaseIndexUtil.createKnowledgeBaseIndex();//问答知识库
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }
        });
    }

    /**
     * 创建单个索引
     * @param vo
     */
    public static void createIndex(final SolrIndexVO vo) throws
            SolrServerException, IOException {
        cn.lonsun.solrManage.SolrUtil.setTypeSortNum(vo);//设置类型排序
        cn.lonsun.solrManage.SolrUtil.setFileIndex(vo);
        if(null != vo.getContent()) {
            vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
        }
        if(null != vo.getColumnId()) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.setRequestWriter(new BinaryRequestWriter());
                        server.addBean(vo);
                        server.optimize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 创建单个索引
     * @param vo
     */
    public static void createIndexSyn(final SolrIndexVO vo) throws
            SolrServerException, IOException {
        cn.lonsun.solrManage.SolrUtil.setTypeSortNum(vo);//设置类型排序
        cn.lonsun.solrManage.SolrUtil.setFileIndex(vo);
        if(null != vo.getContent()) {
            vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
        }
        if(null != vo.getColumnId()) {
            try {
                server.setRequestWriter(new BinaryRequestWriter());
                server.addBean(vo);
                server.optimize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 批量创建索引
     * @param vos
     */
    public static void createIndex(final Set<SolrIndexVO> vos) throws
            SolrServerException, IOException {
        final Set<SolrIndexVO> voset = new HashSet<SolrIndexVO>();
        for(SolrIndexVO vo : vos) {
            cn.lonsun.solrManage.SolrUtil.setTypeSortNum(vo);//设置类型排序
            cn.lonsun.solrManage.SolrUtil.setFileIndex(vo);
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
                try {
                    server.setRequestWriter(new BinaryRequestWriter());
                    server.addBeans(voset.iterator());
                    server.optimize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 批量创建索引
     * @param vos
     */
    public static void createIndexSyn(final Set<SolrIndexVO> vos) throws
            SolrServerException, IOException {
        final Set<SolrIndexVO> voset = new HashSet<SolrIndexVO>();
        for(SolrIndexVO vo : vos) {
            cn.lonsun.solrManage.SolrUtil.setTypeSortNum(vo);//设置类型排序
            cn.lonsun.solrManage.SolrUtil.setFileIndex(vo);
            if(null != vo.getContent()) {
                vo.setContent(HtmlUtil.getTextFromTHML(vo.getContent()));
            }
            if(null != vo.getColumnId()) {
                voset.add(vo);
            }
        }
        try {
            server.setRequestWriter(new BinaryRequestWriter());
            server.addBeans(voset.iterator());
            server.optimize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量更新索引
     * @param vos
     */
    public static void updateIndex(final Set<SolrIndexVO> vos) throws
            SolrServerException, IOException {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> ids = new ArrayList<String>();
                    for (SolrIndexVO vo : vos) {
                        ids.add(vo.getId());
                    }
                    deleteIndexSyn(ids);
                    createIndexSyn(vos);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新索引
     * @param vo
     */
    public static void updateIndex(final SolrIndexVO vo) throws
            SolrServerException, IOException {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != vo && vo.getId() != null) {
                        deleteIndexSyn(vo.getId());
                        createIndexSyn(vo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据ID单个删除索引
     * @param id
     */
    public static void deleteIndex(final String id) throws
            SolrServerException, IOException {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    server.deleteByQuery("id:\"" + id + "\"");
                    server.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据ID单个删除索引
     * @param id
     */
    public static void deleteIndexSyn(final String id) throws
            SolrServerException, IOException {
        try {
            server.deleteByQuery("id:\"" + id + "\"");
            server.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引
     * @param typeCode
     */
    public static void deleteIndexByTypeCodeSyn(final String typeCode) throws
            SolrServerException, IOException {
        try {
            server.deleteByQuery("typeCode:\"" + typeCode + "\"");
            server.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除索引
     *
     * @param columnId
     */
    public static void deleteIndex(final Long columnId) throws
            SolrServerException, IOException {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    server.deleteByQuery("columnId:\"" + columnId + "\"");
                    server.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 批量删除索引
     * @param ids
     */
    public static void deleteIndexSyn(final List<String> ids) throws
            SolrServerException, IOException {
        try {
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
            server.deleteByQuery(sb.toString());
            server.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除索引
     * @param ids
     */
    public static void deleteIndex(final List<String> ids) throws
            SolrServerException, IOException {
        try {
            server.deleteById(ids);
            server.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空索引
     */
    public static void clearIndex() throws
            SolrServerException, IOException {
        server.deleteByQuery("*:*");
        server.commit();
    }
}

package cn.lonsun.activemq;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.impl.GuestBookServiceImpl;
import cn.lonsun.content.internal.service.impl.KnowledgeBaseServiceImpl;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.Impl.MessageBoardServiceImpl;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.impl.WorkGuideService;
import cn.lonsun.publicInfo.internal.service.impl.PublicContentServiceImpl;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.io.IOException;
import java.util.*;

/**
 * @author Hewbing
 * @ClassName: MessageSenderUtil
 * @Description: 发布取消发布并创建或删除索引
 * @date 2016年1月15日 上午11:30:22
 */
public class MessageSenderUtil {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderUtil.class);
    private static TaskExecutor taskExecutor = SpringContextHolder.getBean(TaskExecutor.class);
    private static ContentMongoServiceImpl fileServer = SpringContextHolder.getBean("contentMongoServiceImpl");
    private static GuestBookServiceImpl guestServer = SpringContextHolder.getBean("guestBookService");
    private static MessageBoardServiceImpl messageBoardService = SpringContextHolder.getBean("messageBoardService");
    private static PublicContentServiceImpl publicContentService = SpringContextHolder.getBean("publicContentServiceImpl");
    private static KnowledgeBaseServiceImpl knowledgeBaseService = SpringContextHolder.getBean("knowledgeBaseServiceImpl");
    private static WorkGuideService workGuideService = SpringContextHolder.getBean("workGuideService");
    // 这些文章类型需要建立索引
    private static List<String> typeList = Arrays.asList(BaseContentEO.TypeCode.articleNews.toString(), BaseContentEO.TypeCode.pictureNews.toString(),BaseContentEO.TypeCode.knowledgeBase.toString(),
            BaseContentEO.TypeCode.videoNews.toString(), BaseContentEO.TypeCode.guestBook.toString(),BaseContentEO.TypeCode.messageBoard.toString(), BaseContentEO.TypeCode.public_content.toString(),BaseContentEO.TypeCode.journal.toString(),BaseContentEO.TypeCode.workGuide.toString());

    /**
     * 发布或取消发布文章新闻，用来在文章已经发布后进行的操作
     * 
     * @author fangtinghua
     * @param articleNewsList
     * @param type
     * @return
     */
    public static boolean publishArticleNews(List<BaseContentEO> articleNewsList, Long type) {
        if (null == articleNewsList || articleNewsList.isEmpty()) {
            return false;
        }
        Long columnId = null;
        List<Long> publishIds = new ArrayList<Long>();
        for (BaseContentEO eo : articleNewsList) {
            if (eo.getIsPublish() == 1) {// 只有发布的文章才操作
                publishIds.add(eo.getId());
                columnId = eo.getColumnId();
            }
        }
        if (null == columnId) {// 当没有文章时
            return false;
        }
        MessageStaticEO message = new MessageStaticEO();
        message.setSiteId(LoginPersonUtil.getSiteId());
        message.setColumnId(columnId);
        message.setContentIds(publishIds.toArray(new Long[] {}));
        message.setSource(MessageEnum.CONTENTINFO.value());
        message.setType(type);
        return publishContent(message, MessageEnum.PUBLISH.value().equals(type) ? 1 : 2);
    }

    /**
     * 发送拷贝新闻的消息
     *
     * @param messageInfo 消息
     * @param type        发布或未发布
     * @param source      内容管理or信息公开
     * @return
     */
    private static void sendCopyNewsMessages(String messageInfo, Long type, Long source) {
        if (StringUtils.isNotEmpty(messageInfo)) {
            String[] newsMsg = messageInfo.split(",");
            Map<String, List<Long>> siteColumnMap = new HashMap<String, List<Long>>();// 按照站点栏目分组
            for (int i = 0; i < newsMsg.length; i++) { // 分组的目的是为了避免在相同时间操作同一个栏目页
                String msg = newsMsg[i];
                int j = msg.lastIndexOf("_");
                String siteColumn = msg.substring(0, j);
                if (!siteColumnMap.containsKey(siteColumn)) {
                    List<Long> longList = new ArrayList<Long>();
                    siteColumnMap.put(siteColumn, longList);
                }
                siteColumnMap.get(siteColumn).add(Long.parseLong(msg.substring(j + 1)));
            }
            for (Map.Entry<String, List<Long>> entry : siteColumnMap.entrySet()) {// 发送消息
                String[] keyArr = entry.getKey().split("_");
                Long siteId = Long.parseLong(keyArr[0]); // 站点
                Long columnId = Long.parseLong(keyArr[1]); // 栏目
                Long[] contentIds = entry.getValue().toArray(new Long[]{}); // 文章

                publishContent(new MessageStaticEO(siteId, columnId, contentIds).setSource(source).setType(type), 1);
            }
        }
    }

    /**
     * 发送拷贝新闻的消息
     *
     * @param messges 消息
     * @param type    发布或未发布
     * @return
     */
    private static boolean sendCopyNewsMessages(String messges, Long type) {
        if (StringUtils.isNotEmpty(messges)) {//生成静态
            String newsInfo = "";
            String publicInfo = "";
            if (messges.contains("&")) {//有信息公开静态页需要生成
                // "&"符号前面的是要生成的新闻信息，后面的是信息公开信息
                newsInfo = messges.substring(0, messges.indexOf("&"));
                publicInfo = messges.substring(messges.indexOf("&") + 1);
            } else {// 只有新闻静态页需要生成
                newsInfo = messges;
            }
            sendCopyNewsMessages(newsInfo, type, MessageEnum.CONTENTINFO.value());
            sendCopyNewsMessages(publicInfo, type, MessageEnum.PUBLICINFO.value());
        }
        return true;
    }

    /**
     * 复制的新闻或信息公开生成静态
     *
     * @param messges
     * @return
     * @author liuk
     */
    public static boolean publishCopyNews(String messges) {
        return sendCopyNewsMessages(messges, MessageEnum.PUBLISH.value());
    }

    /**
     * 复制的新闻或信息公开生成静态
     * 取消发布
     *
     * @param messges
     * @return
     * @author liuk
     */
    public static boolean unPublishCopyNews(String messges) {
        return sendCopyNewsMessages(messges, MessageEnum.UNPUBLISH.value());
    }

    /**
     * 发布并建立索引
     * 
     * @author fangtinghua
     * @param eo
     * @param status
     *            1:发布时 2:取消发布时
     * @return
     */
    public static boolean publishContent(MessageStaticEO eo, Integer status) {
        if(AppUtil.isEmpty(eo.getUserId())){
            eo.setUserId(LoginPersonUtil.getUserId());// 设置用户id
        }
        taskExecutor.execute(new SenderRunnable(eo, status));// 异步执行
        return true;
    }

    /**
     * 线程 ADD REASON. <br/>
     * 
     * @date: 2016年9月1日 下午3:44:38 <br/>
     * @author fangtinghua
     */
    private static class SenderRunnable implements Runnable {

        private MessageStaticEO eo;
        private Integer status;

        public SenderRunnable(MessageStaticEO eo, Integer status) {
            super();
            this.eo = eo;
            this.status = status;
        }

        @Override
        public void run() {
            // 绑定session至当前线程中
            SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
            boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
            try {
                Long[] contentIds = eo.getContentIds();
                if (status == 1) {
                    createOrUpdateIndex(contentIds, eo.getColumnId());
                } else if (status == 2) {// 删除索引
                    List<String> ids = new ArrayList<String>();
                    for (Long contentId : contentIds) {
                        ids.add(String.valueOf(contentId));
                    }
                    SolrFactory.deleteIndex(ids);
                }
                MessageSender.sendMessage(eo);// 发送消息
            } catch (Throwable e) {
                logger.error("生成静态消息发送错误或索引建立错误！", e);
            }finally {
                // 关闭session
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }

        }
    }

    /**
     * 创建更新索引
     * 
     * @author fangtinghua
     * @param contentIds
     * @param columnId
     * @throws SolrServerException
     * @throws IOException
     */
    public static void createOrUpdateIndex(Long[] contentIds, Long columnId) throws SolrServerException, IOException {
        Set<SolrIndexVO> set = new HashSet<SolrIndexVO>();
        for (Long contentId : contentIds) {
            SolrIndexVO vo = new SolrIndexVO();
            vo.setId(String.valueOf(contentId));
            BaseContentEO baseEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
            if (null == baseEO || !typeList.contains(baseEO.getTypeCode())) {
                continue;
            }
            String typeCode = baseEO.getTypeCode();
            vo.setColumnId(columnId);
            vo.setSiteId(baseEO.getSiteId());
            vo.setTitle(baseEO.getTitle());
            vo.setCreateDate(baseEO.getPublishDate());
            vo.setAuthor(baseEO.getAuthor());
            if(null != baseEO.getRedirectLink()) {
                vo.setUrl(baseEO.getRedirectLink());
            }
            vo.setTypeCode(typeCode);
            // 留言特殊处理
            if (BaseContentEO.TypeCode.guestBook.toString().equals(typeCode)) {
                GuestBookEditVO editVO = guestServer.getVO(contentId);
                vo.setType(editVO.getClassCode());
                vo.setContent(editVO.getGuestBookContent());
                vo.setCreateDate(editVO.getPublishDate() == null ? new Date() : editVO.getPublishDate());
            } else if (BaseContentEO.TypeCode.messageBoard.toString().equals(typeCode)) {
                MessageBoardEditVO editVO = messageBoardService.getVO(contentId);
                vo.setTypeCode(BaseContentEO.TypeCode.guestBook.toString());
                vo.setType(editVO.getClassCode());
                vo.setContent(editVO.getMessageBoardContent());
                vo.setCreateDate(editVO.getPublishDate() == null ? (editVO.getAddDate() == null ? new Date():editVO.getAddDate()) : editVO.getPublishDate());
            } else if (BaseContentEO.TypeCode.workGuide.toString().equals(typeCode)) {
                //问答知识库内容提取
                CmsWorkGuideEO workGuideEO = workGuideService.getByContentId(contentId);
                if (workGuideEO != null) {
                    vo.setContent(workGuideEO.getContent());
                }
            } else if (BaseContentEO.TypeCode.knowledgeBase.toString().equals(typeCode)) {
                //问答知识库内容提取
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("contentId",contentId);
                KnowledgeBaseEO knowledgeBaseEO = knowledgeBaseService.getEntity(KnowledgeBaseEO.class,map);
                if (knowledgeBaseEO != null) {
                    vo.setContent(knowledgeBaseEO.getContent());
                }
            } else {
                ContentMongoEO contentMongoEO = fileServer.queryById(contentId);
                if (null == contentMongoEO) {
                    vo.setContent(baseEO.getRemarks());
                } else {
                    vo.setContent(contentMongoEO.getContent());
                }
                if (BaseContentEO.TypeCode.public_content.toString().equals(typeCode)) {// 更新文号和索引号索引
                    PublicContentVO publicContentVO = publicContentService.getPublicContentByBaseContentId(contentId);
                    if (publicContentVO != null) {
                        if (!AppUtil.isEmpty(publicContentVO.getFileNum())) {
                            vo.setFileNum(publicContentVO.getFileNum());
                        }
                        if (!AppUtil.isEmpty(publicContentVO.getIndexNum())) {
                            vo.setIndexNum(publicContentVO.getIndexNum());
                        }
                    }
                }
            }
            set.add(vo);
        }
        SolrFactory.updateIndex(set);
    }
}
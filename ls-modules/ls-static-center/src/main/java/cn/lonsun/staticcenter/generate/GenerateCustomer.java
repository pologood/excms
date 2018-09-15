/*
 * GenerateCustomer.java         2015年8月19日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.staticcenter.generate;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.AbstractCustomer;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.message.internal.entity.Message;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.nlp.utils.MemberLabelUtil;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.GenerateRunnable;
import cn.lonsun.staticcenter.generate.thread.ThreadCount;
import cn.lonsun.staticcenter.generate.thread.ThreadHolder;
import cn.lonsun.staticcenter.generate.util.ContextUtil;
import cn.lonsun.staticcenter.generate.util.TplUtil;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import cn.lonsun.util.HibernateSessionUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 生成静态页消费者 <br/>
 * 优化生成静态逻辑，在生成成功后修改文章的状态为发布状态
 * 文章页生成成功后再生成栏目页，防止文章页生成失败，栏目页点击出现404问题
 * 修改时间 2017年5月3日
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年8月19日 <br/>
 */
@Component
public class GenerateCustomer extends AbstractCustomer<StaticTaskEO> {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger("staticGen");
    @Value("${html.pool.size}")
    private int poolSize;// 线程池大小
    @Value("${html.customer.num}")
    private int customerNum;// 消费者个数
    @Resource
    private IStaticTaskService staticTaskService;
    @Resource
    private IBaseContentService baseContentService;
    @Autowired
    private IColumnConfigService configService;
    @Autowired
    private IIndicatorService indicatorService;

    /**
     * 页面静态化执行方法
     * 当全站生成时，需要根据栏目id查询出所有子栏目，生成栏目列表的栏目页和文章页
     * 当文章发布时，需要计算出需要生成的文章页和栏目页，需文章生成成功后再生成栏目页
     * 判断站点信息，当站点信息不存在时，不做任何操作。
     * 计算栏目页和文章页时，不存在的栏目直接过滤掉，无需提示。
     * <p>
     * 修改时间 2017/8/4
     * 发布文章时需要先把所有文章的状态改为已发布，一系列动作完成之后成功则不操作，失败则回滚
     * 取消发布文章时，因所有标签查询的都是已发布的文章，因此中间状态没影响。
     * 一系列动作完成之后成功则修改文章为未发布状态，失败则修改为已发布状态。
     *
     * @param staticTaskEO 生成静态对象
     */
    @Override
    public void execute(StaticTaskEO staticTaskEO) {
        logger.info("开始生成.");
        long time = System.currentTimeMillis();// 计算耗时
        ThreadCount.add();// 线程数加1
        Long taskId = staticTaskEO.getId();// 任务id
        MessageStaticEO messageEO = null;// 消息体
        ExecutorService threadPool = null;// 线程池
        GenerateRecord generateRecord = new GenerateRecord();// 创建生成全局变量
        try {
            messageEO = JSON.parseObject(staticTaskEO.getJson(), MessageStaticEO.class);// 消息体
            if (null == messageEO || null == messageEO.getSiteId() || null == messageEO.getType()) {
                return;// 判断消息体不能为空、站点id不能为空、操作类型不能为空
            }
            Long siteId = messageEO.getSiteId();// 站点id
            messageEO.setTaskId(staticTaskEO.getId());//任务id
            IndicatorEO siteInfo = CacheHandler.getEntity(IndicatorEO.class, siteId);
            SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
            if (null == siteInfo || null == siteConfigEO) {
                logger.error(String.format("站点[id:s%]不存在.", siteId));
                return;
            }
            generateRecord.setTodb(messageEO.isTodb()); // 消息是否入库
            generateRecord.setName(staticTaskEO.getTitle());// 设置标题
            threadPool = Executors.newFixedThreadPool(poolSize);// 创建固定大小线程池
            // 保存站点和站点配置信息
            ThreadHolder.setContext(ThreadHolder.LocalParamsKey.site.toString(), siteInfo);
            ThreadHolder.setContext(ThreadHolder.LocalParamsKey.siteConfig.toString(), siteConfigEO);
            // 计算生成静态上下文列表
            if (messageEO.isTodb()) {// 全站生成
                Set<Context> contextList = new HashSet<Context>();
                Long scope = messageEO.getScope();// 1.首页 2.栏目页 3.文章页
                if (MessageEnum.INDEX.value().equals(scope)) {// 生成首页
                    this.buildIndexList(messageEO.getSource(), messageEO, contextList);
                } else {// 生成栏目页和文章页
                    this.buildColumnArticleList(messageEO, contextList);
                }
                if (!contextList.isEmpty()) {
                    generateRecord.setTotal(((long) contextList.size()));// 设置全局变量
                    this.executeGenerateList(contextList, threadPool, messageEO, generateRecord, siteInfo);// 开始生成
                    ContextUtil.finishTask(taskId);// 清空缓存
                }
            } else {// 单个文章发布时，需要先生成文章页，才继续生成栏目页和首页
                Set<Context> articleList = new HashSet<Context>();// 文章页
                Set<Context> noArticleList = new HashSet<Context>();// 非文章页
                //如果是取消发布栏目页
                if(MessageEnum.UNPUBLISH.value().equals(messageEO.getType()) && messageEO.getColumnId() != null &&
                        (messageEO.getContentIds() == null || messageEO.getContentIds().length <= 0)){
                    this.generateRemovedColumnList(messageEO, noArticleList);
                }else if (this.isPublicCatalog(messageEO)) {// 生成信息公开目录
                    noArticleList.add(this.buildContext(messageEO).setFileType(Context.FileType.JS.toString()));
                } else {// 获取文章页和非文章页
                    this.buildNoArticleList(messageEO, noArticleList);
                    this.buildArticleList(messageEO.getContentIds(), messageEO, articleList);
                }
                int articleSize = articleList.size();
                int noArticleSize = noArticleList.size();
                int total = articleSize + noArticleSize;
                // 设置全局变量
                generateRecord.setTotal(((long) total));
                // 这里区分两种情况.
                // 当发布文章时，需要先生成文章页，只有文章页生成成功之后才生成首页和栏目页
                // 当取消发布文章时，需要先生成首页和栏目页，只有首页和栏目生成成功才生成文章页
                if (MessageEnum.PUBLISH.value().equals(messageEO.getType())) {// 发布生成文章页
                    // 这里状态修改放在外面防止外链新闻不需要生成文章页
                    Long[] contentIds = messageEO.getContentIds();
                    if (null != contentIds && contentIds.length > 0) {// 当存在文章页时才修改状态
                        Long status = this.getStatusByException(false, messageEO.getType());// 改为发布状态
                        baseContentService.changePublish(new ContentPageVO(null, null, status.intValue(), messageEO.getContentIds(), null));
                    }
                }
                if (!noArticleList.isEmpty()) {// 开始生成
                    this.executeGenerateList(noArticleList, threadPool, messageEO, generateRecord, siteInfo);
                }
                if (!articleList.isEmpty() && !generateRecord.isException()) {// 开始生成
                    this.executeGenerateList(articleList, threadPool, messageEO, generateRecord, siteInfo);
                }
            }
        } catch (Throwable e) {// 记录主线程异常信息
            String tips = "生成静态服务异常！";
            logger.error("生成静态失败，" + tips, e);
            generateRecord.exception(new GenerateException(tips));
        } finally {// 关闭线程池，发送消息
            logger.info("生成静态任务耗时：{}", System.currentTimeMillis() - time);
            try {
                if (null != threadPool) {
                    threadPool.shutdownNow();// 立刻结束所有线程
                    while (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) ;// 等待所有任务完成
                    logger.info("线程池关闭成功.");
                }
                logger.info("开始发送消息==============================");
                // 当非全站生成时，或者不是异常结束时，这里的异常结束是任务终止导致的
                if (!messageEO.isTodb() || !StaticTaskEO.EXCEPTION.equals(ContextUtil.getTypeByTaskId(taskId))) {
                    logger.info("进入发送消息方法==============================");
                    this.sendMessage(staticTaskEO, messageEO, generateRecord, time);// 发送消息
                }
            } catch (Throwable e) {
                logger.error("activemq连接失败，请检查其是否启动！", e);
            } finally {
                ThreadCount.minus();// 线程数减一
                ThreadHolder.clearContext();// 清除
            }
        }
    }

    /**
     * 执行生成静态列表,阻塞生成
     *
     * @param contextList    上下文列表
     * @param threadPool     线程池
     * @param messageEO      消息内容
     * @param generateRecord 全局生成记录
     * @param indicatorEO    站点信息
     * @throws InterruptedException
     */
    private void executeGenerateList(Set<Context> contextList, ExecutorService threadPool, MessageStaticEO messageEO,
                                     GenerateRecord generateRecord, IndicatorEO indicatorEO) throws InterruptedException {
        if (null == contextList || contextList.isEmpty()) {
            return;
        }
        Long taskId = messageEO.getTaskId();
        boolean startTask = false;
        int size = contextList.size();
        CountDownLatch countDownLatch = new CountDownLatch(size);
        logger.info("待生成数据 [{}] 条", contextList.size());
        for (Context context : contextList) {
            if (messageEO.isTodb()) {// 全站生成
                context.setTaskId(taskId);
                if (!startTask) {// 开始任务
                    startTask = true;
                    ContextUtil.startTask(taskId, messageEO.getType(), countDownLatch, context);
                }
            }
            context.setUserId(messageEO.getUserId());
            context.setSiteType(indicatorEO.getType());
            context.setGenerateRecord(generateRecord);
            context.setUri(indicatorEO.getUri());
            if (context.getColumnId() != null && StringUtils.isEmpty(context.getUrlPath())) {
                ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, context.getColumnId());
                if (column != null) {
                    context.setUrlPath(column.getUrlPath());
                }
            }
            threadPool.execute(new GenerateRunnable(context, messageEO.isTodb(), countDownLatch));// 线程池生成
        }
        countDownLatch.await();// 阻塞当前线程
    }

    /**
     * 生成首页，根据来源判断生成站点首页还是信息公开首页
     * 首页不管是发布还是取消发布，统一为发布状态
     *
     * @param source      内容协同or信息公开
     * @param messageEO   消息体
     * @param contextList 上下文列表
     */
    private void buildIndexList(Long source, MessageStaticEO messageEO, Set<Context> contextList) {
        Long siteId = messageEO.getSiteId();
        Map<String, TemplateConfEO> templateMap = TplUtil.getIndexTemplate(source);
        IndicatorEO indicatorEO = ThreadHolder.getContext(IndicatorEO.class, ThreadHolder.LocalParamsKey.site.toString());
        Context context = this.buildContext(messageEO).setTitle(indicatorEO.getName());
        for (Map.Entry<String, TemplateConfEO> entry : templateMap.entrySet()) {
            if (null == entry.getValue()) {
                String tips = String.format("站点[id:%s]%s首页模板没有配置.", siteId, entry.getKey());
                logger.error(tips);
                contextList.add(context.setErrorMsg(tips));
            } else {
                logger.info(String.format("首页模板[id:%s][name:%s]", entry.getValue().getId(), entry.getValue().getName()));
                context.setTemplateConfEO(entry.getValue()).setSource(source).setScope(MessageEnum.INDEX.value());
                contextList.add(context.setFrom(entry.getKey()).setType(MessageEnum.PUBLISH.value()));
            }
        }
    }

    /**
     * 全站生成，构建包括文章页和栏目页列表
     * 信息公开无栏目页
     * 这里不包括父栏目、关联栏目页的生成
     *
     * @param messageEO 消息体
     * @author fangtinghua
     */
    private void buildColumnArticleList(MessageStaticEO messageEO, Set<Context> contextList) {
        Long siteId = messageEO.getSiteId();// 站点id
        Long columnId = messageEO.getColumnId();// 栏目或部门id
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        Long scope = messageEO.getScope();// 1.首页 2.栏目页 3.文章页 针对全站生成情况
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            Map<Long, IndicatorEO> columnMap = new HashMap<Long, IndicatorEO>();// 生成的栏目列表
            if (null == columnId) {// 栏目id为空
                this.buildColumnList(siteId, scope, false, null, columnMap);
            } else {
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
                this.buildColumnList(siteId, scope, true, indicatorEO, columnMap);
            }
            if (!columnMap.isEmpty()) {// 生成
                for (Map.Entry<Long, IndicatorEO> entry : columnMap.entrySet()) {
                    if (MessageEnum.COLUMN.value().equals(scope)) {
                        this.generateColumnList(entry.getValue(), messageEO, contextList);// 生成本栏目栏目页
                    } else if (MessageEnum.CONTENT.value().equals(scope)) {// 是子列表才生成文章页
                        this.buildArticleList(entry.getKey(), messageEO, contextList);// 生成本栏目文章页
                    }
                }
            }
        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            Map<Long, OrganEO> organMap = new HashMap<Long, OrganEO>();// 生成的单位列表
            if (null == columnId) {// 单位id为空
                this.buildOrganList(false, null, organMap);
            } else {
                OrganEO eo = CacheHandler.getEntity(OrganEO.class, columnId);
                this.buildOrganList(true, eo, organMap);
            }
            if (!organMap.isEmpty()) {// 生成
                for (Map.Entry<Long, OrganEO> entry : organMap.entrySet()) {
                    if (MessageEnum.CONTENT.value().equals(scope)) {
                        this.buildArticleList(entry.getKey(), messageEO, contextList);// 生成本栏目文章页
                    }
                }
            }
        }
    }

    /**
     * 判断是否生成信息公开目录
     *
     * @param messageEO 消息体
     * @return
     */
    private boolean isPublicCatalog(MessageStaticEO messageEO) {
        Long columnId = messageEO.getColumnId();// 获取栏目id
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        Long[] contentIds = messageEO.getContentIds();//信息公开特殊判断，生成目录js
        return MessageEnum.PUBLICINFO.value().equals(source) && null == columnId && (null == contentIds || contentIds.length == 0);
    }

    /**
     * 生成指定栏目id下的非文章页，包含首页、栏目页等
     * 需要生成栏目的所有父栏目页以及关联栏目页
     * 特殊情况，信息公开生成的是信息公开目录js文件，此时无栏目id
     *
     * @param messageEO 消息体
     * @author fangtinghua
     */
    private void buildNoArticleList(MessageStaticEO messageEO, Set<Context> contextList) {
        Long columnId = messageEO.getColumnId();// 获取栏目id
        Long[] contentIds = messageEO.getContentIds();// 获取栏目id
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        if (null == columnId) {// 栏目id不能为空
            return;
        }
        String synColumnIds = "";//同步生成的栏目id
        this.buildIndexList(MessageEnum.CONTENTINFO.value(), messageEO, contextList); // 生成站点首页
        Map<Long, IndicatorEO> columnMap = new HashMap<Long, IndicatorEO>();
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            ColumnConfigEO columnConfigEO = null;
            IndicatorEO indicatorEO = null;
            //如果是取消发布栏目
            if(messageEO.getType().equals(MessageEnum.UNPUBLISH.value())
                    && (contentIds == null || contentIds.length == 0)){
                logger.info("取消发布栏目:{}", columnId);
                columnConfigEO = configService.getEntity(ColumnMgrEO.class, columnId);
                indicatorService.getEntity(IndicatorEO.class, columnId);
            }else{
                indicatorEO = CacheHandler.getEntity(IndicatorEO.class, columnId);
                columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
            }
            if (this.buildColumnList(indicatorEO, columnConfigEO, columnMap)) {
                columnMap.put(columnId, indicatorEO);// 加上本栏目
                this.buildColumnList(indicatorEO, columnMap);// 生成父栏目
                synColumnIds = columnConfigEO.getGenePageIds();//获取同步需要生成的栏目
            }
        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            this.buildIndexList(MessageEnum.PUBLICINFO.value(), messageEO, contextList);// 生成信息公开首页
            OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, columnId);
            if (null != organConfigEO) {
                synColumnIds = organConfigEO.getLinkPageIds();//获取同步需要生成的栏目
            }
        }
        this.buildColumnList(synColumnIds, columnMap);// 生成关联栏目
        if (!columnMap.isEmpty()) {// 生成
            for (Map.Entry<Long, IndicatorEO> entry : columnMap.entrySet()) {
                this.generateColumnList(entry.getValue(), messageEO, contextList);// 生成本栏目栏目页
            }
        }
    }

    /**
     * 生成指定栏目id下的非文章页，包含首页、栏目页等
     * 需要生成栏目的所有父栏目页以及关联栏目页
     * 特殊情况，信息公开生成的是信息公开目录js文件，此时无栏目id
     *
     * @param messageEO 消息体
     * @author fangtinghua
     */
    private void generateRemovedColumnList(MessageStaticEO messageEO, Set<Context> contextList) {
        Long columnId = messageEO.getColumnId();// 获取栏目id
        Long[] contentIds = messageEO.getContentIds();// 获取栏目id
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        if (null == columnId) {// 栏目id不能为空
            return;
        }
        if (!MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            return;
        }
        //如果是取消发布栏目
        if(!messageEO.getType().equals(MessageEnum.UNPUBLISH.value())
                || !(contentIds == null || contentIds.length == 0)){
            return;
        }
        logger.info("取消发布栏目:{}", columnId);

        this.buildIndexList(MessageEnum.CONTENTINFO.value(), messageEO, contextList); // 生成站点首页
        Map<Long, IndicatorEO> columnMap = new HashMap<Long, IndicatorEO>();

        IndicatorEO indicatorEO = indicatorService.getPhysicalById(columnId);

        // 构建上下文,当前栏目取消发布
        Context context = this.buildContext(messageEO).setColumnId(columnId).setTitle(indicatorEO.getName());
        context.setUrlPath(indicatorEO.getUrlPath());
        contextList.add(context.setType(MessageEnum.UNPUBLISH.value()));

        //父栏目和其他兄弟栏目重新发布
        this.buildColumnList(columnMap, indicatorEO.getParentId());// 生成父栏目
        //生成兄弟栏目页
        Long parentId = indicatorEO.getParentId();
        if (null != parentId && parentId > 0L) {
            try {
                List<IndicatorEO> brothers = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, parentId);
                if(brothers != null && brothers.size() > 0){ //如果有兄弟节点则重新生成兄弟节点的栏目页
                    for(IndicatorEO brother : brothers){
                        ColumnConfigEO pc = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, brother.getIndicatorId());
                        if (this.buildColumnList(brother, pc, columnMap)
                                //不是当前节点
                                && !brother.getIndicatorId().equals(indicatorEO.getIndicatorId())) {
                            columnMap.put(brother.getIndicatorId(), brother);
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("重新生成兄弟栏目失败：{}({})", indicatorEO.getName(), indicatorEO.getIndicatorId());
            }
        }
//        this.buildColumnList(indicatorEO.getGenePageIds(), columnMap);// 生成关联栏目
        if (!columnMap.isEmpty()) {// 生成父栏目和兄弟栏目
            for (Map.Entry<Long, IndicatorEO> entry : columnMap.entrySet()) {
                this.generateColumnList(entry.getValue(), messageEO, contextList);// 生成本栏目栏目页
            }
        }
    }

    /**
     * 生成栏目所有父栏目静态文件，以及兄弟栏目静态文件
     *
     * @param indicatorEO 栏目
     * @param columnMap   栏目列表
     * @author fangtinghua
     */
    private void buildColumnList(IndicatorEO indicatorEO, Map<Long, IndicatorEO> columnMap) {
        Long parentId = indicatorEO.getParentId();
        buildColumnList(columnMap, parentId);
    }

    private void buildColumnList(Map<Long, IndicatorEO> columnMap, Long parentId) {
        if (null != parentId && parentId > 0L) {
            IndicatorEO p = CacheHandler.getEntity(IndicatorEO.class, parentId);
            ColumnConfigEO pc = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, parentId);
            if (this.buildColumnList(p, pc, columnMap)) {
                columnMap.put(parentId, p);
                this.buildColumnList(p, columnMap);
            }
        }
    }

    /**
     * 根据栏目id数组获取关联栏目生成列表
     *
     * @param columnIds 关联栏目id
     * @param columnMap 栏目列表
     * @author fangtinghua
     */
    private void buildColumnList(String columnIds, Map<Long, IndicatorEO> columnMap) {
        if (StringUtils.isEmpty(columnIds)) {
            return;
        }
        String[] idArr = columnIds.split(",");
        for (String columnId : idArr) {
            Long columnIdLong = NumberUtils.toLong(columnId);
            if (columnIdLong <= 0L) {
                continue;
            }
            IndicatorEO p = CacheHandler.getEntity(IndicatorEO.class, columnIdLong);
            ColumnConfigEO pc = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnIdLong);
            if (this.buildColumnList(p, pc, columnMap)) {
                columnMap.put(columnIdLong, p);
            }
        }
    }

    /**
     * 过滤栏目列表。
     * 当columnConfigEO为空时，判断indicatorEO这个对象是否为栏目
     * 当columnConfigEO不为空时，还需要判断是否跳转链接
     * 当栏目不在columnMap列表中时添加
     *
     * @param indicatorEO    栏目信息
     * @param columnConfigEO 栏目配置信息
     * @param columnMap      栏目列表
     */
    private boolean buildColumnList(IndicatorEO indicatorEO, ColumnConfigEO columnConfigEO, Map<Long, IndicatorEO> columnMap) {
        if (null == indicatorEO
                || !(IndicatorEO.Type.CMS_Section.toString().equals(indicatorEO.getType()) || IndicatorEO.Type.COM_Section.toString()
                .equals(indicatorEO.getType()))) {// 非栏目类型
            return false;
        }
        if (null == columnConfigEO || columnConfigEO.getIsStartUrl().equals(1)) {//跳转链接
            return false;
        }
        return !columnMap.containsKey(indicatorEO.getIndicatorId());
    }

    /**
     * 构建栏目列表，全站递归生成
     *
     * @param siteId      站点id
     * @param scope       栏目页或文章页
     * @param current     是否当前栏目
     * @param indicatorEO 栏目对象
     * @param columnMap   生成的栏目列表
     * @author fangtinghua
     */
    private void buildColumnList(Long siteId, Long scope, boolean current, IndicatorEO indicatorEO, Map<Long, IndicatorEO> columnMap) {
        if (current) {// 这时要生成本栏目，这是说明
            if (null != indicatorEO) {// indicatorEO不能为空
                Long indicatorId = indicatorEO.getIndicatorId();
                ColumnConfigEO configEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, indicatorId);
                if (this.buildColumnList(indicatorEO, configEO, columnMap)) {
                    if (!MessageEnum.CONTENT.value().equals(scope) || indicatorEO.getIsParent().equals(0)) {
                        columnMap.put(indicatorId, indicatorEO);// 当为文章页时，必须是叶子节点才生成
                    }
                }
                if (!indicatorEO.getIsParent().equals(0)) {// 生成子栏目
                    this.buildColumnList(siteId, scope, false, indicatorEO, columnMap);
                }
            }
        } else if (null == indicatorEO) {// 站点下所有栏目
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, siteId);
            if (null != list && !list.isEmpty()) {
                for (IndicatorEO eo : list) {// 虚拟子站，暂时不做处理，通过切换站点的方式生成
                    this.buildColumnList(siteId, scope, true, eo, columnMap);
                }
            }
        } else {// 生成所有子栏目，这里的栏目肯定不为空
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_PARENTID, indicatorEO.getIndicatorId());
            if (null != list && !list.isEmpty()) {
                for (IndicatorEO eo : list) {
                    this.buildColumnList(siteId, scope, true, eo, columnMap);
                }
            }
        }
    }

    /**
     * 构建单位列表，全站递归生成
     *
     * @param current
     * @param organEO
     * @param organMap
     * @author fangtinghua
     */
    public void buildOrganList(boolean current, OrganEO organEO, Map<Long, OrganEO> organMap) {
        if (current) {// 本单位
            if (null != organEO) { // 为信息公开单位并且单位配置过信息公开目录和配置过内容模型才生成
                if (OrganEO.Type.Organ.toString().equals(organEO.getType()) && Integer.valueOf(1).equals(organEO.getIsPublic())) {
                    OrganConfigEO configEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organEO.getOrganId());
                    if (null != configEO && null != configEO.getCatId() &&
                            StringUtils.isNotEmpty(configEO.getContentModelCode()) && !organMap.containsKey(organEO.getOrganId())) {
                        organMap.put(organEO.getOrganId(), organEO);
                    }
                }
                if (organEO.getHasOrgans().equals(1)) {// 有子单位
                    this.buildOrganList(false, organEO, organMap);// 生成子单位
                }
            }
        } else if (null == organEO) {// 站点绑定单位
            SiteConfigEO siteConfigEO = ThreadHolder.getContext(SiteConfigEO.class, ThreadHolder.LocalParamsKey.siteConfig.toString());
            if (null != siteConfigEO && StringUtils.isNotEmpty(siteConfigEO.getUnitIds())) {
                organEO = CacheHandler.getEntity(OrganEO.class, Long.valueOf(siteConfigEO.getUnitIds()));
                this.buildOrganList(true, organEO, organMap);
            }
        } else {// 所有子单位
            List<OrganEO> list = CacheHandler.getList(OrganEO.class, CacheGroup.CMS_PARENTID, organEO.getOrganId());
            if (null != list && !list.isEmpty()) {
                for (OrganEO eo : list) {
                    this.buildOrganList(true, eo, organMap);
                }
            }
        }
    }

    /**
     * 生成栏目页，连接管理生成js文件
     * 只有内容协同有栏目页
     *
     * @param indicatorEO 栏目
     * @param messageEO   消息体
     * @param contextList 上下文列表
     */
    private void generateColumnList(IndicatorEO indicatorEO, MessageStaticEO messageEO, Set<Context> contextList) {
        // 生成栏目页时，source统一为内容协同
        Long source = MessageEnum.CONTENTINFO.value();
        Long columnId = indicatorEO.getIndicatorId();
        // 构建上下文
        Context context = this.buildContext(messageEO).setColumnId(columnId).setTitle(indicatorEO.getName());
        if (StringUtils.isEmpty(indicatorEO.getParentNamesPinyin())) {
            context.setUrlPath(indicatorEO.getNamePinyin());
        } else {
            context.setUrlPath(indicatorEO.getParentNamesPinyin() + "/" + indicatorEO.getNamePinyin());
        }
        // 获取模板信息
        Map<String, TemplateConfEO> templateMap = TplUtil.getColumnTemplate(columnId, source);
        // 获取栏目类型
        String columnType = ThreadHolder.getContext(String.class, ThreadHolder.LocalParamsKey.modelTypeCode.toString());
        // 链接管理特殊处理 生成js
        if (BaseContentEO.TypeCode.linksMgr.toString().equals(columnType)) {
            if (indicatorEO.getIsParent().equals(0)) {// 叶子节点
                context.setFileType(Context.FileType.JS.toString());
                contextList.add(context.setType(MessageEnum.PUBLISH.value()));// 为发布
            }
            ThreadHolder.setContext(ThreadHolder.LocalParamsKey.modelTypeCode.toString(), "");
        } else {
            for (Map.Entry<String, TemplateConfEO> template : templateMap.entrySet()) {
                if (null == template.getValue()) {
                    String tips = String.format("%s[id:%s]%s文章页模板没有配置.", this.getSourceName(source), columnId, template.getKey());
                    logger.error(tips);
                    contextList.add(context.setErrorMsg(tips));
                } else {
                    context.setTemplateConfEO(template.getValue()).setScope(MessageEnum.COLUMN.value());
                    contextList.add(context.setFrom(template.getKey()).setType(MessageEnum.PUBLISH.value()));
                }
            }
        }
    }

    /**
     * 获取文章页列表，文章ids优先级最高
     *
     * @param siteId     站点id
     * @param columnId   栏目id
     * @param contentIds 文章ids
     * @return
     */
    private List<BaseContentEO> getArticleList(Long siteId, Long columnId, Long[] contentIds) {
        // 当文章被删除时，查询为空，所以这里需调用此方法
        if (null != contentIds && contentIds.length > 0) {
            List<BaseContentEO> articleList = new ArrayList<BaseContentEO>();
            for (Long contentId : contentIds) {
                articleList.add(baseContentService.getRemoved(contentId));
            }
            return articleList;
        }
        if (null != columnId) { // 所有的文章页都需要重新生成
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("siteId", siteId);// 站点
            paramMap.put("columnId", columnId);// 栏目
            paramMap.put("isPublish", MessageEnum.PUBLISH.value().intValue());// 发布
            paramMap.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());// 状态
            return baseContentService.getEntities(BaseContentEO.class, paramMap);
        }
        return Collections.emptyList();
    }

    /**
     * 根据栏目id生成文章页
     *
     * @param columnId    栏目id
     * @param messageEO   消息体
     * @param contextList 上下文列表
     * @author fangtinghua
     */
    private void buildArticleList(Long columnId, MessageStaticEO messageEO, Set<Context> contextList) {
        if (null == columnId) {// 排除
            return;
        }
        Long siteId = messageEO.getSiteId();
        List<BaseContentEO> articleList = this.getArticleList(siteId, columnId, null);
        this.generateContentList(articleList, messageEO, contextList);
    }

    /**
     * 根据文章ids生成文章页
     *
     * @param contentIds  文章id数组
     * @param messageEO   消息体
     * @param contextList 上下文列表
     * @author fangtinghua
     */
    private void buildArticleList(Long[] contentIds, MessageStaticEO messageEO, Set<Context> contextList) {
        if (null == contentIds || contentIds.length == 0) {//排除
            return;
        }
        Long siteId = messageEO.getSiteId();
        List<BaseContentEO> articleList = this.getArticleList(siteId, null, contentIds);
        this.generateContentList(articleList, messageEO, contextList);
    }

    /**
     * 生成文章页
     *
     * @param articleList
     * @param messageEO
     * @param contextList
     * @author fangtinghua
     */
    private void generateContentList(List<BaseContentEO> articleList, MessageStaticEO messageEO, Set<Context> contextList) {
        if (null == articleList || articleList.isEmpty()) {
            return;
        }
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        for (BaseContentEO content : articleList) {
            // 内容为空、文章类型为链接管理、文章页是跳转连接
            if (null == content || BaseContentEO.TypeCode.linksMgr.toString().equals(content.getTypeCode())
                    || !StringUtils.isEmpty(content.getRedirectLink())) {
                continue;
            }
            Long columnId = content.getColumnId();// 获取栏目id
            if (MessageEnum.PUBLISH.value().equals(messageEO.getType())) {// 发布生成文章页

                //发布文章时提取关键词信息
//                try{
//                    MemberLabelUtil.analyseKeyWords(content.getId(),content.getSiteId());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

                String typeCode = content.getTypeCode();
                Map<String, TemplateConfEO> templateMap = TplUtil.getContentTemplate(columnId, source, typeCode);
                for (Map.Entry<String, TemplateConfEO> entry : templateMap.entrySet()) {
                    Context context = this.buildContext(messageEO).setColumnId(columnId).setContentId(content.getId());
                    context.setTypeCode(typeCode).setTitle(content.getTitle()).setScope(MessageEnum.CONTENT.value());
                    if (null == entry.getValue()) {
                        String tips = String.format("%s[id:%s]%s文章页模板没有配置.", this.getSourceName(source), columnId, entry.getKey());
                        logger.error(tips);
                        contextList.add(context.setErrorMsg(tips));
                    } else {
                        context.setTemplateConfEO(entry.getValue()).setScope(MessageEnum.CONTENT.value());
                        contextList.add(context.setFrom(entry.getKey()));
                    }
                }
            } else {// 创建上下文
                String typeCode = content.getTypeCode();
                Context context = this.buildContext(messageEO).setColumnId(columnId).setContentId(content.getId());
                contextList.add(context.setTitle(content.getTitle()).setTypeCode(typeCode).setScope(MessageEnum.CONTENT.value()));// 生成
            }
        }
    }

    /**
     * 发送消息
     *
     * @param staticTaskEO
     * @param messageEO
     * @param generateRecord
     * @param time
     * @author fangtinghua
     */
    private void sendMessage(StaticTaskEO staticTaskEO, MessageStaticEO messageEO, GenerateRecord generateRecord, long time) {
        // 异常
        boolean isException = generateRecord.isException();
        MessageSystemEO eo = new MessageSystemEO();
        eo.setSiteId(messageEO.getSiteId());
        eo.setTitle("生成静态");
        eo.setRecUserIds(String.valueOf(messageEO.getUserId()));
        eo.setMessageType(MessageSystemEO.TIP);
        if (isException) {
            eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
        } else {
            eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        }
        // 构建消息
        Message message = new Message();
        message.setTotal(generateRecord.getTotal());
        message.setComplete(generateRecord.getComplete());
        message.setError(generateRecord.getError());
        // 入库
        if (messageEO.isTodb()) {
            Long taskId = staticTaskEO.getId();// 任务id
            message.setTaskId(taskId);
            if (MessageEnum.OVER.value().equals(ContextUtil.getTypeByTaskId(taskId))) {// 取消任务
                staticTaskService.delete(StaticTaskEO.class, taskId);// 直接删除
            } else {
                message.setTime(System.currentTimeMillis() - time);// 返回耗时
                message.setStatus(isException ? StaticTaskEO.EXCEPTION : StaticTaskEO.COMPLETE);
                staticTaskEO.setStatus(isException ? StaticTaskEO.EXCEPTION : StaticTaskEO.COMPLETE);
                staticTaskEO.setTime(message.getTime());
                staticTaskEO.setCount(generateRecord.getTotal());
                staticTaskEO.setDoneCount(generateRecord.getComplete());
                staticTaskEO.setFailCount(generateRecord.getError());
                staticTaskService.updateEntity(staticTaskEO);
            }
        } else { // 文章要重建索引和更改状态
            try {
                Long status = this.getStatusByException(isException, messageEO.getType());
                eo.setIsPublish(status.intValue());// 返回真实的发布状态
                eo.setContentIds(messageEO.getContentIds());// 设置文章数组
                this.changeStatusAndUpdateIndex(messageEO, isException);
            } catch (Throwable e) {
                String tips = "数据库或solr服务异常！";
                logger.error("生成静态失败，" + tips, e);
                generateRecord.exception(new GenerateException(tips));
            }
        }
        eo.setData(message); // 设置消息体
        eo.setContent(this.getMessageContent(staticTaskEO, messageEO, generateRecord));
        logger.info("发送消息:" + JSON.toJSONString(eo));
        MessageSender.sendMessage(eo);// 发送消息
    }

    /**
     * 文章要重建索引和更改状态
     *
     * @param messageEO
     * @param isException
     * @throws Throwable
     */
    private void changeStatusAndUpdateIndex(MessageStaticEO messageEO, boolean isException) throws Throwable {
        Long[] contentIds = messageEO.getContentIds();
        if (null == contentIds || contentIds.length <= 0) {
            return;
        }
        List<String> ids = new ArrayList<String>();
        for (Long contentId : contentIds) {
            ids.add(String.valueOf(contentId));
        }
        Long status = this.getStatusByException(isException, messageEO.getType());
        String columnType = ThreadHolder.getContext(String.class, ThreadHolder.LocalParamsKey.modelTypeCode.toString());
        if (BaseContentEO.TypeCode.linksMgr.toString().equals(columnType)) {// 链接管理特殊处理 生成js
            baseContentService.changePublish(new ContentPageVO(null, null, status.intValue(), contentIds, null));// 不需要区分异常，直接改状态
        } else { // 这里先改状态，后更新索引，防止solr异常导致状态改不回来
            if (MessageEnum.PUBLISH.value().equals(messageEO.getType())) {// 发布生成文章页
                if (isException) {// 发布文章页失败时回滚
                    baseContentService.changePublish(new ContentPageVO(null, null, status.intValue(), contentIds, null));
                    SolrFactory.deleteIndex(ids);
                } else {
                    MessageSenderUtil.createOrUpdateIndex(contentIds, messageEO.getColumnId());
                }
            } else {
                baseContentService.changePublish(new ContentPageVO(null, null, status.intValue(), contentIds, null));
                if (isException) {// 取消发布文章页失败时回滚
                    MessageSenderUtil.createOrUpdateIndex(contentIds, messageEO.getColumnId());
                } else {
                    SolrFactory.deleteIndex(ids);
                }
            }
        }
        // 更新缓存
        CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentService.getEntities(BaseContentEO.class, contentIds));
    }

    /**
     * 获取消息内容
     *
     * @return
     * @author fangtinghua
     */
    public String getMessageContent(StaticTaskEO staticTaskEO, MessageStaticEO messageEO, GenerateRecord generateRecord) {
        Long source = messageEO.getSource();// 来源 1.内容协同 2.信息公开
        Long columnId = messageEO.getColumnId();// 获取栏目id
        Long scope = messageEO.getScope();// 1.首页 2.栏目页 3.文章页
        // 消息内容
        StringBuffer sb = new StringBuffer();
        if (messageEO.isTodb()) {// 全站生成
            Long taskId = staticTaskEO.getId();// 任务id
            if (MessageEnum.OVER.value().equals(ContextUtil.getTypeByTaskId(taskId))) {
                sb.append("任务取消");// 终止任务
            } else if (MessageEnum.INDEX.value().equals(scope)) {
                sb.append(String.format("生成站点[%s]", staticTaskEO.getTitle()));
                sb.append(this.getScopeName(scope));// 加上范围
            } else {// 栏目页或者文章页
                sb.append(String.format("生成%s[%s]", this.getSourceName(source), staticTaskEO.getTitle()));
                sb.append(this.getScopeName(scope));// 加上范围
            }
        } else {
            if (null != columnId) {// 栏目不为空
                Long[] contentIds = messageEO.getContentIds();
                String tips = String.format("生成%s[%s]", this.getSourceName(source), staticTaskEO.getTitle());
                if (null != contentIds && contentIds.length > 0) {
                    if (contentIds.length > 1) {// 多条
                        tips = "批量" + tips;
                    } else {
                        BaseContentEO contentEO = baseContentService.getRemoved(contentIds[0]);
                        tips += null != contentEO ? String.format("-[%s]", contentEO.getTitle()) : "";
                    }
                }
                sb.append(tips).append(this.getScopeName(scope));// 加上范围
            } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开生成单位目录
                sb.append("生成单位信息公开目录");
            }
        }
        return generateRecord.isException() ? sb.append("失败,").append(HibernateSessionUtil.getStackTrace(generateRecord.getException(),
                GenerateException.class)).toString() : sb.append("成功.").toString();
    }

    /**
     * 根据异常判断发布状态
     *
     * @param isException 是否失败
     * @param type        发布或取消发布
     * @return
     */
    private Long getStatusByException(boolean isException, Long type) {
        if (isException) {// 成功
            return MessageEnum.PUBLISH.value().equals(type) ? 0L : 1L;
        }
        return MessageEnum.PUBLISH.value().equals(type) ? 1L : 0L;
    }

    /**
     * 获取提示信息名称
     *
     * @param source
     * @return
     * @author fangtinghua
     */

    private String getSourceName(Long source) {
        if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同
            return MessageEnum.CONTENTINFO.getName();
        } else if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开
            return MessageEnum.PUBLICINFO.getName();
        }
        return "";
    }

    /**
     * 获取提示信息名称
     *
     * @param scope
     * @return
     * @author fangtinghua
     */
    private String getScopeName(Long scope) {
        if (MessageEnum.INDEX.value().equals(scope)) {
            return MessageEnum.INDEX.getName();
        } else if (MessageEnum.COLUMN.value().equals(scope)) {
            return MessageEnum.COLUMN.getName();
        } else if (MessageEnum.CONTENT.value().equals(scope) || null == scope) {
            return MessageEnum.CONTENT.getName();
        }
        return "";
    }

    /**
     * 构造上下文
     *
     * @param messageEO
     * @return
     * @author fangtinghua
     */
    public Context buildContext(MessageStaticEO messageEO) {
        Context context = new Context();
        context.setSiteId(messageEO.getSiteId()).setType(messageEO.getType());
        return context.setSource(messageEO.getSource()).setUserId(messageEO.getUserId());
    }

    /**
     * 重写线程的线程个数
     * 生成静态的消费者的个数
     *
     * @return
     */
    @Override
    public int getCustomerNum() {
        return customerNum;
    }
}
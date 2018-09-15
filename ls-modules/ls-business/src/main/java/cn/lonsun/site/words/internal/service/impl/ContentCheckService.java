package cn.lonsun.site.words.internal.service.impl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IBaseContentDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.util.Type;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import cn.lonsun.site.words.internal.dao.IContentCheckDao;
import cn.lonsun.site.words.internal.entity.ContentCheckEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.site.words.internal.service.IContentCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-31 13:46
 */
@Repository
public class ContentCheckService extends MockService<ContentCheckEO> implements IContentCheckService {

    @Autowired
    private IContentCheckDao contentCheckDao;

    @Resource
    private IBaseContentDao baseContentDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Resource
    private ContentMongoServiceImpl mongoService;

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardReplyService messageBoardReplyService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Pagination getPageEO(WordsPageVO vo) {
        return contentCheckDao.getPageEO(vo);
    }

    @Override
    public void deleteByCheckType(String checkType,Long siteId) {
        contentCheckDao.deleteByCheckType(checkType, siteId);
    }

    @Override
    public void deleteByCheckType(Long contentId, String checkType) {
        contentCheckDao.deleteByCheckType(contentId, checkType);
    }

    @Override
    public void checkContent(String checkType,Long siteId) {
        this.deleteByCheckType(checkType, siteId);
        //检测文章新闻
        checkArticle(checkType, siteId);
        //检测留言
//        checkGuest(checkType, siteId);
        //检测多回复留言
        checkMessageboard(checkType, siteId);
    }

    @Override
    public void replace(List<ContentCheckEO> eos) {
        for(ContentCheckEO ceo : eos) {
            ContentCheckEO eo = getEntity(ContentCheckEO.class, ceo.getId());
            if(eo.getType().equals(ContentCheckEO.Type.content.toString())) {
                //内容
                ContentMongoEO contentMongoEO = mongoService.queryById(eo.getContentId());
                if(null != contentMongoEO) {
                    String content = contentMongoEO.getContent().replaceAll(eo.getWords(), ceo.getReplaceWords());
                    contentMongoEO.setContent(content);
                    mongoService.save(contentMongoEO);
                }
                BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
                publishChange(contentEO);
            } else if(eo.getType().equals(ContentCheckEO.Type.title.toString())) {
                //标题
                BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
                if(null != contentEO) {
                    contentEO.setTitle(eo.getTitle().replaceAll(eo.getWords(), ceo.getReplaceWords()));
                    baseContentService.updateEntity(contentEO);
                }
                publishChange(contentEO);
            } else if(eo.getType().equals(ContentCheckEO.Type.subTitle.toString())) {
                //副标题
                BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
                if(null != contentEO) {
                    contentEO.setSubTitle(eo.getSubTitle().replaceAll(eo.getWords(), ceo.getReplaceWords()));
                    baseContentService.updateEntity(contentEO);
                }
                publishChange(contentEO);
            } else if(eo.getType().equals(ContentCheckEO.Type.remarks.toString())) {
                //摘要
                BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
                publishChange(contentEO);
            } else if(eo.getType().equals(ContentCheckEO.Type.guest.toString())) {
                //留言
                MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class,eo.getContentId());
                if(null != messageBoardEO) {
                    messageBoardEO.setMessageBoardContent(eo.getGuest().replaceAll(eo.getWords(), ceo.getReplaceWords()));
                    messageBoardService.updateEntity(messageBoardEO);
                    if(null != messageBoardEO.getBaseContentId()) {
                        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,messageBoardEO.getBaseContentId());
                        publishChange(contentEO);
                    }
                }
//                GuestBookEO guestBookEO = guestBookService.getEntity(GuestBookEO.class,eo.getContentId());
//                if(null != guestBookEO) {
//                    guestBookEO.setGuestBookContent(eo.getGuest().replaceAll(eo.getWords(), ceo.getReplaceWords()));
//                    guestBookService.updateEntity(guestBookEO);
//                }
            } else if(eo.getType().equals(ContentCheckEO.Type.repGuest.toString())) {
                //留言回复
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("messageBoardId",eo.getContentId());
                List<MessageBoardReplyEO> replyEOs = messageBoardReplyService.getEntities(MessageBoardReplyEO.class,map);
                if(replyEOs!=null&&replyEOs.size()>0){
                    for(MessageBoardReplyEO replyEO:replyEOs){
                        replyEO.setReplyContent(replyEO.getReplyContent().replaceAll(eo.getWords(), ceo.getReplaceWords()));
                        messageBoardReplyService.updateEntity(replyEO);
                    }
                }
//                GuestBookEO guestBookEO = guestBookService.getEntity(GuestBookEO.class,eo.getContentId());
//                if(null != guestBookEO) {
//                    guestBookEO.setResponseContent(eo.getRepGuest().replaceAll(eo.getWords(), ceo.getReplaceWords()));
//                    guestBookService.updateEntity(guestBookEO);
//                }
                MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class,eo.getContentId());
                BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,messageBoardEO.getBaseContentId());
                publishChange(contentEO);
            }
            contentCheckDao.deleteById(eo.getId());
        }
    }

    /**
     * 发布更改
     * @param contentEO
     */
    private void publishChange(BaseContentEO contentEO) {
        if(null != contentEO) {
            if(contentEO.getIsPublish() == 1) {
                //先删除
                MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.UNPUBLISH.value()),2);
                //再创建
                MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{contentEO.getId()}).setType(MessageEnum.PUBLISH.value()),0);
            }
        }
    }

    /**
     * 检测文章新闻
     * @param checkType
     * @param siteId
     */
    private void checkArticle(String checkType,Long siteId) {
        List<BaseContentEO> eos = getArticleNews(BaseContentEO.TypeCode.articleNews.toString(), siteId);
        //文章新闻
        for(BaseContentEO eo : eos) {
            if(checkType.equals(Type.SENSITIVE.toString())) {

                //标题检测
                if(!AppUtil.isEmpty(eo.getTitle())) {
                    List<WordsSensitiveEO> title = WordsSplitHolder.wordsCheck(eo.getTitle(), checkType, siteId);
                    if (null != title && !title.isEmpty()) {
                        saveSensitive(eo, title, ContentCheckEO.Type.title.toString(), siteId);
                    }
                }

                //副标题检测
                if(!AppUtil.isEmpty(eo.getSubTitle())) {
                    List<WordsSensitiveEO> subTitle = WordsSplitHolder.wordsCheck(eo.getSubTitle(), checkType, siteId);
                    if (null != subTitle && !subTitle.isEmpty()) {
                        saveSensitive(eo, subTitle, ContentCheckEO.Type.subTitle.toString(), siteId);
                    }
                }

                //摘要
                if(!AppUtil.isEmpty(eo.getRemarks())) {
                    List<WordsSensitiveEO> remarks = WordsSplitHolder.wordsCheck(eo.getRemarks(), checkType, siteId);
                    if (null != remarks && !remarks.isEmpty()) {
                        saveSensitive(eo, remarks, ContentCheckEO.Type.remarks.toString(), siteId);
                    }
                }

                //内容检测
                if(!AppUtil.isEmpty(eo.getArticle())) {
                    List<WordsSensitiveEO> content = WordsSplitHolder.wordsCheck(eo.getArticle(), checkType, siteId);
                    if (null != content && !content.isEmpty()) {
                        saveSensitive(eo, content, ContentCheckEO.Type.content.toString(), siteId);
                    }
                }
            } else if(checkType.equals(Type.EASYERR.toString())) {
                //标题检测
                if(!AppUtil.isEmpty(eo.getTitle())) {
                    List<WordsEasyerrEO> title = WordsSplitHolder.wordsCheck(eo.getTitle(), checkType,siteId);
                    if (null != title && !title.isEmpty()) {
                        saveEasyerr(eo, title, ContentCheckEO.Type.title.toString(), siteId);
                    }
                }

                //副标题检测
                if(!AppUtil.isEmpty(eo.getSubTitle())) {
                    List<WordsEasyerrEO> subTitle = WordsSplitHolder.wordsCheck(eo.getSubTitle(), checkType,siteId);
                    if(null != subTitle && !subTitle.isEmpty()) {
                        saveEasyerr(eo, subTitle, ContentCheckEO.Type.subTitle.toString(), siteId);
                    }
                }

                //摘要
                if(!AppUtil.isEmpty(eo.getRemarks())) {
                    List<WordsEasyerrEO> remarks = WordsSplitHolder.wordsCheck(eo.getRemarks(), checkType, siteId);
                    if (null != remarks && !remarks.isEmpty()) {
                        saveEasyerr(eo, remarks, ContentCheckEO.Type.remarks.toString(), siteId);
                    }
                }

                //内容检测
                if(!AppUtil.isEmpty(eo.getArticle())) {
                    List<WordsEasyerrEO> content = WordsSplitHolder.wordsCheck(eo.getArticle(), checkType, siteId);
                    if (null != content && !content.isEmpty()) {
                        saveEasyerr(eo, content, ContentCheckEO.Type.content.toString(), siteId);
                    }
                }
            }
        }
    }

    /**
     * 获取新闻
     * @param siteId
     * @return
     */
    private List<BaseContentEO> getArticleNews(String typeCode, Long siteId) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder("select id as id,");
        sb.append(" title as title,");
        sb.append(" columnId as columnId,");
        sb.append(" siteId as siteId,");
        sb.append(" author as author,");
        sb.append(" typeCode as typeCode,");
        sb.append(" remarks as remarks,");
        sb.append(" createDate as createDate");
        sb.append(" from BaseContentEO where recordStatus='Normal' and typeCode=? and isPublish = 1");
        values.add(typeCode);
        if(null != siteId) {
            sb.append(" and siteId = ?");
            values.add(siteId);
        }
        List<BaseContentEO> list = (List<BaseContentEO>)baseContentDao.getBeansByHql(sb.toString(),values.toArray(),BaseContentEO.class);
        if (null != list && list.size() > 0) {// 设置内容字段
            logger.info("获取文字新闻，总共数量:" + list.size());
            setMongData(list);
        }
        return list;
    }

    private void setMongData(List<BaseContentEO> data) {
        ContentMongoServiceImpl mongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);
        if (null != data && !data.isEmpty()) {
            List<Long> ids = new ArrayList<Long>();
            for (BaseContentEO li : data) {
                if (null != li && null != li.getId()) {
                    ids.add(li.getId());
                }
            }

            Long[] idsarr = ids.toArray(new Long[ids.size()]);
            List<ContentMongoEO> mongoEOs = null;
            try {
                mongoEOs = mongoService.queryListByIds(idsarr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<Long, String> map = null;
            if (null != mongoEOs) {
                map = new HashMap<Long, String>();
                for (ContentMongoEO mongoEO : mongoEOs) {
                    if (null != mongoEO.getContent()) {
                        map.put(mongoEO.getId(), mongoEO.getContent());
                    }
                }
            }

            if (null != map) {
                for (BaseContentEO li : data) {
                    String content = map.get(li.getId());
                    if (null != content) {
                        li.setArticle(content);
                    }
                }
            }
        }
    }

    /**
     * 检测留言
     * @param checkType
     * @param siteId
     */
    public void checkGuest(String checkType,Long siteId) {
        List<GuestBookEditVO> vos = guestBookService.getGuestBookBySiteId(siteId);
        if(!AppUtil.isEmpty(vos)) {
            for(GuestBookEditVO vo : vos) {
                if(checkType.equals(Type.SENSITIVE.toString())) {
                    //留言
                    if(!AppUtil.isEmpty(vo.getGuestBookContent())) {
                        List<WordsSensitiveEO> guest = WordsSplitHolder.wordsCheck(vo.getGuestBookContent(), checkType, siteId);
                        if (null != guest && !guest.isEmpty()) {
                            saveSensitive(vo,guest,ContentCheckEO.Type.guest.toString(),siteId);
                        }
                    }

                    //留言回复
                    if(!AppUtil.isEmpty(vo.getResponseContent())) {
                        List<WordsSensitiveEO> repGuest = WordsSplitHolder.wordsCheck(vo.getResponseContent(), checkType, siteId);
                        if (null != repGuest && !repGuest.isEmpty()) {
                            saveSensitive(vo,repGuest,ContentCheckEO.Type.repGuest.toString(),siteId);
                        }
                    }
                } else if(checkType.equals(Type.EASYERR.toString())) {
                    //留言
                    if(!AppUtil.isEmpty(vo.getGuestBookContent())) {
                        List<WordsEasyerrEO> guest = WordsSplitHolder.wordsCheck(vo.getGuestBookContent(), checkType, siteId);
                        if (null != guest && !guest.isEmpty()) {
                            saveEasyerr(vo, guest, ContentCheckEO.Type.guest.toString(), siteId);
                        }
                    }

                    //留言回复
                    if(!AppUtil.isEmpty(vo.getResponseContent())) {
                        List<WordsEasyerrEO> repGuest = WordsSplitHolder.wordsCheck(vo.getResponseContent(), checkType, siteId);
                        if (null != repGuest && !repGuest.isEmpty()) {
                            saveEasyerr(vo, repGuest, ContentCheckEO.Type.repGuest.toString(), siteId);
                        }
                    }
                }
            }
        }
    }


    /**
     * 检测多回复留言
     * @param checkType
     * @param siteId
     */
    public void checkMessageboard(String checkType,Long siteId) {
        List<MessageBoardEditVO> vos = messageBoardService.getMessageBoardBySiteId(siteId);
        List<MessageBoardReplyEO> reply = messageBoardReplyService.getAll();
        HashMap<Long,List<MessageBoardReplyEO>> replyMap = groupReply(reply);

        if(!AppUtil.isEmpty(vos)) {
            for(MessageBoardEditVO vo : vos) {
                if(checkType.equals(Type.SENSITIVE.toString())) {
                    //留言
                    if(!AppUtil.isEmpty(vo.getMessageBoardContent())) {
                        List<WordsSensitiveEO> guest = WordsSplitHolder.wordsCheck(vo.getMessageBoardContent(), checkType, siteId);
                        if (null != guest && !guest.isEmpty()) {
                            saveSensitive(vo,guest,ContentCheckEO.Type.guest.toString(),siteId);
                        }
                    }

                    //留言回复
                    if(replyMap!=null&&replyMap.get(vo.getId())!=null) {
                        List<WordsSensitiveEO> repGuest = new ArrayList<WordsSensitiveEO>();
                        for(MessageBoardReplyEO eo:replyMap.get(vo.getId())){
                            List<WordsSensitiveEO> rep = WordsSplitHolder.wordsCheck(eo.getReplyContent(), checkType, siteId);
                            if(rep!=null&&rep.size()>0){
                                repGuest.addAll(rep);
                            }
                        }
                        if (null != repGuest && !repGuest.isEmpty()) {
                            saveSensitive(vo,repGuest,ContentCheckEO.Type.repGuest.toString(),siteId);
                        }
                    }
                } else if(checkType.equals(Type.EASYERR.toString())) {
                    //留言
                    if(!AppUtil.isEmpty(vo.getMessageBoardContent())) {
                        List<WordsEasyerrEO> guest = WordsSplitHolder.wordsCheck(vo.getMessageBoardContent(), checkType, siteId);
                        if (null != guest && !guest.isEmpty()) {
                            saveEasyerr(vo, guest, ContentCheckEO.Type.guest.toString(), siteId);
                        }
                    }

                    //留言回复
                    if(replyMap!=null&&replyMap.get(vo.getId())!=null) {
                        List<WordsEasyerrEO> repGuest = new ArrayList<WordsEasyerrEO>();
                        for(MessageBoardReplyEO eo:replyMap.get(vo.getId())){
                            List<WordsEasyerrEO> rep = WordsSplitHolder.wordsCheck(eo.getReplyContent(), checkType, siteId);
                            if(rep!=null&&rep.size()>0){
                                repGuest.addAll(rep);
                            }
                        }
                        if (null != repGuest && !repGuest.isEmpty()) {
                            saveEasyerr(vo, repGuest, ContentCheckEO.Type.repGuest.toString(), siteId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存敏感词检测结果
     * @param contentEO
     * @param eos
     * @param type
     */
    private void saveSensitive(BaseContentEO contentEO,List<WordsSensitiveEO> eos,String type,Long siteId) {
        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsSensitiveEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(contentEO.getId());
            eo.setTitle(contentEO.getTitle());
            eo.setSubTitle(contentEO.getSubTitle());
            eo.setRemarks(contentEO.getRemarks());
            eo.setContentType(contentEO.getTypeCode());
            eo.setCheckType(ContentCheckEO.checkType.SENSITIVE.toString());
            eo.setType(type);
            eo.setContent(contentEO.getArticle());
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }

    /**
     * 保存易错词检测结果
     * @param contentEO
     * @param eos
     * @param type
     */
    private void saveEasyerr(BaseContentEO contentEO,List<WordsEasyerrEO> eos,String type,Long siteId) {
        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsEasyerrEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(contentEO.getId());
            eo.setTitle(contentEO.getTitle());
            eo.setSubTitle(contentEO.getSubTitle());
            eo.setRemarks(contentEO.getRemarks());
            eo.setContentType(contentEO.getTypeCode());
            eo.setCheckType(ContentCheckEO.checkType.EASYERR.toString());
            eo.setType(type);
            eo.setContent(contentEO.getArticle());
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }

    /**
     * 保存敏感词检测结果
     * @param vo
     * @param eos
     * @param type
     */
    private void saveSensitive(GuestBookEditVO vo,List<WordsSensitiveEO> eos,String type,Long siteId) {
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,vo.getBaseContentId());
        String title = null;
        if(!AppUtil.isEmpty(baseContentEO)) {
            title = baseContentEO.getTitle();
        }
        if(AppUtil.isEmpty(title)) {
            if(type.equals(ContentCheckEO.Type.guest.toString())) {
                title = vo.getGuestBookContent();
            } else if(type.equals(ContentCheckEO.Type.repGuest.toString())) {
                title = vo.getResponseContent();
            }
        }
        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsSensitiveEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(vo.getId());
            eo.setTitle(title);
            eo.setGuest(vo.getGuestBookContent());
            eo.setRepGuest(vo.getResponseContent());
            eo.setCheckType(ContentCheckEO.checkType.SENSITIVE.toString());
            eo.setType(type);
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }


    /**
     * 保存敏感词检测结果
     * @param vo
     * @param eos
     * @param type
     */
    private void saveSensitive(MessageBoardEditVO vo,List<WordsSensitiveEO> eos,String type,Long siteId) {
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,vo.getBaseContentId());
        String title = null;
        if(!AppUtil.isEmpty(baseContentEO)) {
            title = baseContentEO.getTitle();
        }
        if(AppUtil.isEmpty(title)) {
            if(type.equals(ContentCheckEO.Type.guest.toString())) {
                title = vo.getMessageBoardContent();
            } else if(type.equals(ContentCheckEO.Type.repGuest.toString())) {
                title = vo.getResponseContent();
            }
        }
        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsSensitiveEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(vo.getId());
            eo.setTitle(title);
            eo.setGuest(vo.getMessageBoardContent());
            eo.setRepGuest(vo.getResponseContent());
            eo.setCheckType(ContentCheckEO.checkType.SENSITIVE.toString());
            eo.setType(type);
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }
    /**
     * 保存易错词检测结果
     * @param vo
     * @param eos
     * @param type
     */
    private void saveEasyerr(GuestBookEditVO vo,List<WordsEasyerrEO> eos,String type,Long siteId) {
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,vo.getBaseContentId());
        String title = null;
        if(!AppUtil.isEmpty(baseContentEO)) {
            title = baseContentEO.getTitle();
        }
        if(AppUtil.isEmpty(title)) {
            if(type.equals(ContentCheckEO.Type.guest.toString())) {
                title = vo.getGuestBookContent();
            } else if(type.equals(ContentCheckEO.Type.repGuest.toString())) {
                title = vo.getResponseContent();
            }
        }

        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsEasyerrEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(vo.getId());
            eo.setTitle(title);
            eo.setGuest(vo.getGuestBookContent());
            eo.setRepGuest(vo.getResponseContent());
            eo.setCheckType(ContentCheckEO.checkType.EASYERR.toString());
            eo.setType(type);
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }

    /**
     * 保存易错词检测结果
     * @param vo
     * @param eos
     * @param type
     */
    private void saveEasyerr(MessageBoardEditVO vo,List<WordsEasyerrEO> eos,String type,Long siteId) {
        BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class,vo.getBaseContentId());
        String title = null;
        if(!AppUtil.isEmpty(baseContentEO)) {
            title = baseContentEO.getTitle();
        }
        if(AppUtil.isEmpty(title)) {
            if(type.equals(ContentCheckEO.Type.guest.toString())) {
                title = vo.getMessageBoardContent();
            } else if(type.equals(ContentCheckEO.Type.repGuest.toString())) {
                title = vo.getResponseContent();
            }
        }

        List<ContentCheckEO> checkEOs = new ArrayList<ContentCheckEO>();
        for(WordsEasyerrEO seo : eos) {
            ContentCheckEO eo = new ContentCheckEO();
            eo.setSiteId(siteId);
            eo.setContentId(vo.getId());
            eo.setTitle(title);
            eo.setGuest(vo.getMessageBoardContent());
            eo.setRepGuest(vo.getResponseContent());
            eo.setCheckType(ContentCheckEO.checkType.EASYERR.toString());
            eo.setType(type);
            eo.setWords(seo.getWords());
            eo.setReplaceWords(seo.getReplaceWords());
            checkEOs.add(eo);
        }
        this.saveEntities(checkEOs);
    }

    private HashMap<Long,List<MessageBoardReplyEO>> groupReply(List<MessageBoardReplyEO> replys){
        HashMap<Long,List<MessageBoardReplyEO>> map = new HashMap<Long,List<MessageBoardReplyEO>>();
        for(MessageBoardReplyEO eo:replys){
            List<MessageBoardReplyEO> list = map.get(eo.getMessageBoardId());
            if(list!=null&&list.size()>0){
                list.add(eo);
                map.put(eo.getMessageBoardId(),list);
            }else{
                list = new ArrayList<MessageBoardReplyEO>();
                list.add(eo);
                map.put(eo.getMessageBoardId(),list);
            }
        }
        return map;
    }
}

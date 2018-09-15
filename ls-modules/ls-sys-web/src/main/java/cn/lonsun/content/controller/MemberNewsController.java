package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.CopyReferVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.util.ColumnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author Hewbing
 * @ClassName: ArticleNewsController
 * @Description: article news
 * @date 2015年11月4日 上午11:06:50
 */
@Controller
@RequestMapping(value = "memberNews")
public class MemberNewsController extends BaseController {

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @RequestMapping("index")
    public String contentList(Long pageIndex, ModelMap map) {
        if (pageIndex == null)
            pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        return "/content/membernews/member_news_list";
    }


    /**
     * @param pageVO
     * @return
     * @Description get news page by pageVO
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:11
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {
        try {
            String str = new String(pageVO.getTitle().getBytes("ISO-8859-1"), "utf-8");
            pageVO.setTitle(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return getObject(baseContentService.getPage(pageVO));
    }

    @RequestMapping("detail")
    public ModelAndView detail(Long id) {
        ModelAndView model = new ModelAndView("/content/membernews/member_news_detail");
        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        ContentMongoEO _eo = contentMongoService.queryOne(query);
        String content = "";
        if (!AppUtil.isEmpty(_eo)) {
            content = _eo.getContent();
        }
        eo.setArticle(content);
        model.addObject("eo", eo);
        return model;
    }

    @RequestMapping("back")
    public String back(Model model, Long id) {
        model.addAttribute("id", id);
        return "/content/membernews/member_news_back";
    }

    @ResponseBody
    @RequestMapping("saveBack")
    public Object saveBack(Long id, String backReason) {
        BaseContentEO eo = baseContentService.getEntity(BaseContentEO.class, id);
        eo.setBackReason(backReason);
        eo.setMemberConStu(BaseContentEO.MemConStu.isBack.getMemConStu());
        baseContentService.updateEntity(eo);
        return getObject();
    }

    /**
     * 新闻采用
     *
     * @param copyReferVO
     * @return
     * @author xuzijing
     */
    @ResponseBody
    @RequestMapping("use")
    public Object use(CopyReferVO copyReferVO) {
        String returnStr = "";
        String synColumnIds = copyReferVO.getSynColumnIds();
        String isPublish = copyReferVO.getSynColumnIsPublishs();
        String[] contentIds = copyReferVO.getContentId().replace("[", "").replace("]", "").split(",");// 新闻id
        if (!AppUtil.isEmpty(synColumnIds)) {
            String[] columnIdSiteId = synColumnIds.split("_");
            Long columnId = Long.valueOf(columnIdSiteId[0]);
            Long siteId = Long.valueOf(columnIdSiteId[1]);
            for (String id : contentIds) {
                Long newColumnId = 0L;
                Long newSiteId = 0L;
                Long contentId = Long.parseLong(id);
                BaseContentEO baseContentEO = baseContentService.getEntity(BaseContentEO.class, contentId);
                if (baseContentEO.getMemberConStu() != 1)
                    continue;
                ColumnConfigEO configEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
                if (configEO.getColumnTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())) {
                    baseContentService.getImageLinkFromContent(baseContentEO);//缩略图为空时取内容里第一张图片作为缩略图
                    if (AppUtil.isEmpty(baseContentEO.getImageLink())) {//缩略图为空
                        String msg = "文章中没有任何图片，不能采用到" + ColumnUtil.getColumnName(newColumnId, newSiteId) + "栏目.";
                        throw new BaseRunTimeException(TipsMode.Message.toString(), msg);
                    }
                }

                baseContentEO.setMemberConStu(BaseContentEO.MemConStu.isUse.getMemConStu());
                baseContentEO.setIsPublish(Integer.valueOf(isPublish));
                baseContentEO.setColumnId(columnId);
                baseContentEO.setSiteId(siteId);
                baseContentEO.setTypeCode(configEO.getColumnTypeCode());
                if (Integer.valueOf(isPublish) == 1)
                    baseContentEO.setPublishDate(new Date());
                baseContentService.updateEntity(baseContentEO);
                // 更新缓存
                CacheHandler.saveOrUpdate(BaseContentEO.class, baseContentEO);

                // 生成静态
                if (Integer.valueOf(isPublish) == 1) {// 发布状态
                    returnStr += newSiteId + "_" + newColumnId + "_" + baseContentEO.getId() + ",";
                }
            }
        }
        if (!AppUtil.isEmpty(returnStr)) {//去除最后的逗号
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        MessageSenderUtil.publishCopyNews(returnStr);
        return getObject();
    }

}

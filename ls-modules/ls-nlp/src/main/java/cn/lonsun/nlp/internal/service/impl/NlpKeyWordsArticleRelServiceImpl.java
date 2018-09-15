package cn.lonsun.nlp.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsArticleRelDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsArticleRelEO;
import cn.lonsun.nlp.internal.service.INlpKeyWordsArticleRelService;
import cn.lonsun.nlp.internal.service.INlpKeyWordsService;
import cn.lonsun.nlp.internal.vo.ContentVO;
import cn.lonsun.nlp.utils.ContentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 关键词与文章对应关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Service
public class NlpKeyWordsArticleRelServiceImpl extends BaseService<NlpKeyWordsArticleRelEO> implements INlpKeyWordsArticleRelService {

    @Autowired
    private INlpKeyWordsArticleRelDao nlpKeyWordsArticleRelDao;

    @Autowired
    private INlpKeyWordsService nlpKeyWordsService;


    @Override
    public List<Long> analyseKeyWords(Long contentId,Long siteId, String content) {
        List<Long> keywordIds = new ArrayList<Long>();
        if(contentId!=null&&!AppUtil.isEmpty(content)){
            keywordIds = nlpKeyWordsService.analyseAndSaveWord(content);
            if(keywordIds!=null&&keywordIds.size()>0){
                nlpKeyWordsArticleRelDao.delByContentId(contentId);//删除原有的关键词对应关系
                for(Long keywordId:keywordIds){
                    NlpKeyWordsArticleRelEO nlpKeyWordsArticleRelEO = new NlpKeyWordsArticleRelEO();
                    nlpKeyWordsArticleRelEO.setContentId(contentId);
                    nlpKeyWordsArticleRelEO.setKeyWordId(keywordId);
                    nlpKeyWordsArticleRelEO.setSiteId(siteId);
                    this.saveEntity(nlpKeyWordsArticleRelEO);
                }
            }
        }
        return keywordIds;
    }

    @Override
    public void delByContentId(Long contentId) {
        nlpKeyWordsArticleRelDao.delByContentId(contentId);
    }

    @Override
    public List<NlpKeyWordsArticleRelEO> getByContentId(Long contentId) {
        return nlpKeyWordsArticleRelDao.getByContentId(contentId);
    }

    @Override
    public List<ContentVO> queryContentsByKeyWordId(Long[] keyWordId,Long siteId, Date st, Date ed, Integer num) {
        List<ContentVO> contentVOS = nlpKeyWordsArticleRelDao.queryContentsByKeyWordId(keyWordId,siteId,st,ed,num);
        if(contentVOS!=null&&contentVOS.size()>0){
            for(ContentVO vo:contentVOS){
                //处理文章页访问路径
                if(!AppUtil.isEmpty(vo.getRedirectLink())){
                    vo.setUrl(vo.getRedirectLink());
                }else{
                    vo.setUrl(ContentUtil.getLinkPath(vo.getColumnId(),vo.getContentId(),vo.getSiteId()));
                }
                vo.setTypeCodeName(getTypeCodeName(vo.getTypeCode()));
            }
        }
        return contentVOS;
    }

    @Override
    public Pagination queryContentPage(Long[] keyWordId, Long siteId, Date st, Date ed, Long pageIndex, Integer pageSize) {
        if(pageIndex==null){
            pageIndex = 0l;
        }
        if(pageSize==null){
            pageSize = 15;
        }
        Pagination page = nlpKeyWordsArticleRelDao.queryContentPage(keyWordId, siteId, st, ed, pageIndex, pageSize);
        List<?> dataList = page.getData();
        if(dataList!=null&&dataList.size()>0){
            for(Object object:dataList){
                ContentVO vo = (ContentVO)object;
                //处理文章页访问路径
                if(!AppUtil.isEmpty(vo.getRedirectLink())){
                    vo.setUrl(vo.getRedirectLink());
                }else{
                    vo.setUrl(ContentUtil.getLinkPath(vo.getColumnId(),vo.getContentId(),vo.getSiteId()));
                }
                vo.setTypeCodeName(getTypeCodeName(vo.getTypeCode()));
            }
        }
        return page;
    }


    private String getTypeCodeName(String typeCode){
        String typeCodeName = "相关资讯";
        if(BaseContentEO.TypeCode.articleNews.toString().equals(typeCode)){
            typeCodeName = "文章新闻";
        }else if(BaseContentEO.TypeCode.pictureNews.toString().equals(typeCode)){
            typeCodeName = "图片新闻";
        }else if(BaseContentEO.TypeCode.videoNews.toString().equals(typeCode)){
            typeCodeName = "视频新闻";
        }else if(BaseContentEO.TypeCode.workGuide.toString().equals(typeCode)){
            typeCodeName = "网上办事";
        }else if(BaseContentEO.TypeCode.messageBoard.toString().equals(typeCode)){
            typeCodeName = "政民互动";
        }else if(BaseContentEO.TypeCode.public_content.toString().equals(typeCode)){
            typeCodeName = "信息公开";
        }
        return typeCodeName;
    }
}

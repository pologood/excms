package cn.lonsun.nlp.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.nlp.internal.dao.INlpKeyWordsMemberRelDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsArticleRelEO;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsMemberRelEO;
import cn.lonsun.nlp.internal.service.INlpKeyWordsArticleRelService;
import cn.lonsun.nlp.internal.service.INlpKeyWordsMemberRelService;
import cn.lonsun.nlp.internal.service.INlpMemberLabelRelService;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员浏览关键词相关文章记录
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Service
public class NlpKeyWordsMemberRelServiceImpl extends BaseService<NlpKeyWordsMemberRelEO> implements INlpKeyWordsMemberRelService {

    @Autowired
    private INlpKeyWordsMemberRelDao nlpKeyWordsMemberRelDao;
    @Autowired
    private INlpKeyWordsArticleRelService nlpKeyWordsArticleRelService;
    @Autowired
    private INlpMemberLabelRelService nlpMemberLabelRelService;

    @Override
    public void saveMemberRel(Long memberId, String ip, Long contentId,Long siteId) {
        if(AppUtil.isEmpty(memberId)&&AppUtil.isEmpty(ip)){
            return;
        }
        if(AppUtil.isEmpty(contentId)){
            return;
        }
        //获取文章所对应的关键词
        List<NlpKeyWordsArticleRelEO> wordsArticleRelEOS = nlpKeyWordsArticleRelService.getByContentId(contentId);
        if(wordsArticleRelEOS!=null&&wordsArticleRelEOS.size()>0){
            for(NlpKeyWordsArticleRelEO wordsArticleRelEO:wordsArticleRelEOS){
                NlpKeyWordsMemberRelEO wordsMemberRelEO = new NlpKeyWordsMemberRelEO();
                wordsMemberRelEO.setContentId(contentId);
                wordsMemberRelEO.setIp(ip);
                wordsMemberRelEO.setMemberId(memberId);
                wordsMemberRelEO.setKeyWordId(wordsArticleRelEO.getKeyWordId());
                wordsMemberRelEO.setSiteId(siteId);
                this.saveEntity(wordsMemberRelEO);
            }
        }

    }

    @Override
    public List<NlpKeyWordsVO> getMemberKeywords(Long memberId,Long siteId,Integer num) {
        List<NlpKeyWordsVO> memberKeywords = nlpKeyWordsMemberRelDao.getMemberKeywords(memberId,siteId,num);
        if(memberKeywords!=null){
            List<Long> labelIds = new ArrayList<Long>();
            if(memberId!=null){
                List<NlpKeyWordsVO> memberLabels = nlpMemberLabelRelService.getLabelByMemberId(memberId);
                if(memberLabels!=null&&memberLabels.size()>0){
                    for(NlpKeyWordsVO label:memberLabels){
                        labelIds.add(label.getKeyWordId());
                    }
                }
            }
            for(NlpKeyWordsVO vo:memberKeywords){
                if(labelIds.contains(vo.getKeyWordId())){
                    vo.setIsLabel(1);//已关注
                }
            }
        }
        return memberKeywords;
    }
}

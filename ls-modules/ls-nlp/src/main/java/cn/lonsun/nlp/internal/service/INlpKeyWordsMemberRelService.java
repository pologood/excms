package cn.lonsun.nlp.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsMemberRelEO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;

import java.util.List;

/**
 * 会员浏览关键词相关文章记录
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpKeyWordsMemberRelService extends IBaseService<NlpKeyWordsMemberRelEO> {
    /**
     * 保存会员或者游客访问记录
     * @param memberId
     * @param contentId
     */
    void saveMemberRel(Long memberId,String ip,Long contentId,Long siteId);

    /**
     * 根据会员id获取会员可能关注关键词
     * @param memberId
     * @return
     */
    List<NlpKeyWordsVO> getMemberKeywords(Long memberId,Long siteId,Integer num);
}

package cn.lonsun.nlp.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.nlp.internal.entity.NlpKeyWordsMemberRelEO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;

import java.util.List;

/**
 * 会员浏览关键词相关文章记录
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpKeyWordsMemberRelDao extends IBaseDao<NlpKeyWordsMemberRelEO>{
    /**
     * 根据会员id获取会员可能关注关键词
     * @param memberId
     * @return
     */
    List<NlpKeyWordsVO> getMemberKeywords(Long memberId,Long siteId,Integer num);
}

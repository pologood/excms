package cn.lonsun.nlp.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.nlp.internal.entity.NlpMemberLabelRelEO;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;

import java.util.List;

/**
 * 会员关注会员标签关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
public interface INlpMemberLabelRelDao extends IMockDao<NlpMemberLabelRelEO> {
    /**
     * 删除会员标签
     * @param memberId
     * @param keyWordId
     */
    void delMemberLabel(Long memberId,Long keyWordId);

    /**
     * 根据会员id获取关注的标签
     * @param memberId
     * @return
     */
    List<NlpKeyWordsVO> getLabelByMemberId(Long memberId);
}

package cn.lonsun.nlp.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.nlp.internal.dao.INlpMemberLabelRelDao;
import cn.lonsun.nlp.internal.entity.NlpMemberLabelRelEO;
import cn.lonsun.nlp.internal.service.INlpMemberLabelRelService;
import cn.lonsun.nlp.internal.vo.NlpKeyWordsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员关注会员标签关系
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 10:21
 */
@Service
public class NlpMemberLabelRelServiceImpl extends MockService<NlpMemberLabelRelEO> implements INlpMemberLabelRelService {

    @Autowired
    private INlpMemberLabelRelDao nlpMemberLabelRelDao;

    @Override
    public void saveMemberLabel(Long memberId, Long keyWordId,Long siteId) {
        if(AppUtil.isEmpty(memberId)||AppUtil.isEmpty(keyWordId)){
            return;
        }
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("memberId",memberId);
        param.put("labelId",keyWordId);
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        if(siteId!=null){
            param.put("siteId",siteId);
        }
        NlpMemberLabelRelEO memberLabelRelEO = this.getEntity(NlpMemberLabelRelEO.class,param);
        if(memberLabelRelEO!=null){//标签已关注，不能重复关注
            return;
        }
        memberLabelRelEO = new NlpMemberLabelRelEO();
        memberLabelRelEO.setLabelId(keyWordId);
        memberLabelRelEO.setMemberId(memberId);
        memberLabelRelEO.setSiteId(siteId);
        this.saveEntity(memberLabelRelEO);
    }

    @Override
    public void delMemberLabel(Long memberId, Long keyWordId) {
        nlpMemberLabelRelDao.delMemberLabel(memberId,keyWordId);
    }

    @Override
    public List<NlpKeyWordsVO> getLabelByMemberId(Long memberId) {
        return nlpMemberLabelRelDao.getLabelByMemberId(memberId);
    }
}

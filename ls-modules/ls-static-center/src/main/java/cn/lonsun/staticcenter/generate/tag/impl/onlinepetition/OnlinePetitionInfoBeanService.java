package cn.lonsun.staticcenter.generate.tag.impl.onlinepetition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.lonsun.content.onlinePetition.internal.dao.IOnlinePetitionDao;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.site.site.internal.cache.DictItemCache;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.datadictionary.vo.DataDictVO;

import com.alibaba.fastjson.JSONObject;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-17<br/>
 */
@Component
public class OnlinePetitionInfoBeanService extends AbstractBeanService {
    @Resource
    private IOnlinePetitionDao petitionDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();// 获得文章页id，即主表的id
        StringBuffer sql =
                new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId ")
                        .append(" ,c.publish_date as publishDate,c.is_publish as isPublish,c.author as author,o.is_public as isPublic,o.occupation as occupation,o.phone_num as phoneNum,o.address as address")
                        .append(" ,o.purpose_code as purposeCode,o.category_code as categoryCode")
                        .append(" ,o.create_date as createDate,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                        .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,o.rec_unit_name as recUnitName")
                        .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                        .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and o.record_status='"
                                + AMockEntity.RecordStatus.Normal.toString() + "'")
                        // .append(" and r.record_status='" +
                        // AMockEntity.RecordStatus.Normal.toString() + "'");
                        .append(" and c.is_publish=1 and c.id=" + contentId);

        sql.append(" order by  c.create_date desc,c.id desc");
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("columnId");
        fields.add("siteId");
        fields.add("publishDate");
        fields.add("isPublish");
        fields.add("author");
        fields.add("isPublic");

        fields.add("occupation");
        fields.add("phoneNum");
        fields.add("address");
        fields.add("purposeCode");
        fields.add("categoryCode");

        fields.add("createDate");
        fields.add("petitionId");
        fields.add("dealStatus");
        fields.add("content");
        fields.add("ip");
        fields.add("attachId");
        fields.add("attachName");
        fields.add("replyDate");
        fields.add("replyId");
        fields.add("replyContent");
        fields.add("replyIp");
        fields.add("recUnitName");
        String[] str = new String[fields.size()];
        PetitionPageVO vo = (PetitionPageVO) petitionDao.getBeanBySql(sql.toString(), new Object[] {}, PetitionPageVO.class, fields.toArray(str));
        return vo;
    }

    /**
     *
     * 预处理数据
     * 
     * @throws GenerateException
     *
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        List<DataDictVO> plist = DictItemCache.get("petition_purpose");
        List<DataDictVO> clist = DictItemCache.get("petition_category");
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("plist", plist);
        map.put("clist", clist);
        return map;
    }
}

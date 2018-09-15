package cn.lonsun.staticcenter.generate.tag.impl.onlinepetition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.lonsun.content.onlinePetition.internal.dao.IOnlinePetitionDao;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;

import com.alibaba.fastjson.JSONObject;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-17<br/>
 */
@Component
public class OnlinePetitionInfoListBeanService extends AbstractBeanService {
    @Resource
    private IOnlinePetitionDao petitionDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        // 此写法是为了使得在主页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id没有在标签页面默认传入的
            columnId = context.getColumnId();
        }

        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        StringBuffer sql =
            new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId ")
                .append(" ,c.publish_date as publishDate,c.is_publish as isPublish,c.author as author,o.is_public as isPublic")
                .append(" ,o.create_date as createDate,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,r.reply_user_name as replyUserName")
                .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and o.record_status='"
                    + AMockEntity.RecordStatus.Normal.toString() + "'")
                // .append(" and r.record_status='" +
                // AMockEntity.RecordStatus.Normal.toString() + "'");
                .append(" and c.column_id=" + columnId + " and c.site_id="+context.getSiteId()+" and c.is_publish=1 ");
        sql.append(" and rownum <= ").append(num);
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
        fields.add("replyUserName");
        String[] str = new String[fields.size()];
        return petitionDao.getBeansBySql(sql.toString(), new Object[]{}, PetitionPageVO.class, fields.toArray(str));
    }

    /**
     * 预处理数据
     *
     * @throws GenerateException
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<PetitionPageVO> list = (List<PetitionPageVO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (PetitionPageVO vo : list) {
                String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getId());
                vo.setLink(path);
            }
        }
        return super.doProcess(resultObj, paramObj);
    }

}

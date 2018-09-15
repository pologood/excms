package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
* @ClassName: GuestBookArticleBeanService
* @Description: TODO
* @author hujun
* @date 2015年12月2日 下午5:46:38
*
 */

@Component
public class GuestBookArticleBeanService extends AbstractBeanService{

	@DbInject("guestBook")
	private IGuestBookDao gusetBookDao;

	@Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
		String id = context.getParamMap().get("id");
		Long contentId = null;
		if (StringUtils.isEmpty(id)) {
			contentId = context.getContentId();
		} else {
			contentId = Long.parseLong(id);
		}
        
        StringBuffer hql = new StringBuffer("select c.title as title,c.columnId as columnId,c.publishDate as publishDate,c.createDate as createDate," )
				.append("c.isPublish as isPublish,g.id as id,g.responseContent as responseContent,g.guestbookContent as guestbookContent,g.personIp as personIp,")
				.append( "g.personName as personName,g.replyDate as replyDate,g.baseContentId as baseContentId,g.receiveId as receiveId,g.replyUserId as replyUserId,")
				.append( "g.isResponse as isResponse,g.dealStatus as dealStatus,g.classCode as classCode,g.personPhone as personPhone,g.addDate as addDate,")
				.append(" g.commentCode as commentCode,g.recType as recType,g.userName as userName,g.replyUserId as replyUserId,g.replyUserName as replyUserName")
				.append("  from BaseContentEO c,GuestBookEO g where g.baseContentId=c.id and c.recordStatus=? and g.recordStatus=? ");
		hql.append(" and c.id = ? ");
		hql.append(" and c.isPublish=1 ");
		hql.append(" order by g.addDate desc ");
        
        List<Object> values = new ArrayList<Object>();
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(AMockEntity.RecordStatus.Normal.toString());
		values.add(contentId);

		List<GuestBookEditVO> list=(List<GuestBookEditVO>) gusetBookDao.getBeansByHql(hql.toString(), values.toArray(), GuestBookEditVO.class);
		GuestBookEditVO vo=null;
		if(list!=null&&list.size()>0){
			vo=list.get(0);
			if (vo.getRecType() != null&&vo.getRecType()!=2) {
				if (vo.getRecType().equals(0)) {
					if (vo.getReceiveId() != null) {
						OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getReceiveId());
						if (organEO != null) {
							vo.setReceiveName(organEO.getName());
						}
					}
				} else {
					if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
						DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
						if (dictVO != null) {
							vo.setReceiveUserName(dictVO.getKey());
						}
					}
				}
			}
			if (!StringUtils.isEmpty(vo.getCommentCode())) {
				DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
				if (dictVO != null) {
					vo.setCommentName(dictVO.getKey());
				}
			} else {
				DataDictVO dictVO = DataDictionaryUtil.getDefuatItem("guest_comment", vo.getSiteId());
				if (dictVO != null) {
					vo.setCommentCode(dictVO.getCode());
				}
			}

		}else{
			return "1";
		}
		return  vo;
		
	}
	public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
		Map<String, Object> map = super.doProcess(resultObj, paramObj);
		if ("1".equals(resultObj)) {
			map.put("message", "<div class=\"guestbook-msg-info\">您查询的信息不存在，请核准后再试！</div>");
		}else{
			GuestBookEditVO editVO = (GuestBookEditVO) resultObj;
			if(editVO!=null){
				ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(editVO.getColumnId(), editVO.getSiteId());
				if (configVO != null) {
					map.put("isAssess", configVO.getIsAssess());
				}
			}
			List<DataDictVO> commentList =DataDictionaryUtil.getItemList("guest_comment", editVO.getSiteId());
			map.put("commentList", commentList);
		}
		return map;
	}

}

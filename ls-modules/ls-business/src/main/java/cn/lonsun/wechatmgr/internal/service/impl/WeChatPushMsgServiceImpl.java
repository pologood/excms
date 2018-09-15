package cn.lonsun.wechatmgr.internal.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.dao.IUserGroupDao;
import cn.lonsun.wechatmgr.internal.dao.IWeChatArticleDao;
import cn.lonsun.wechatmgr.internal.dao.IWeChatPushMsgDao;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatPushMsgService;
import cn.lonsun.wechatmgr.vo.PushArticleVO;
import cn.lonsun.wechatmgr.vo.PushMsg;
import cn.lonsun.wechatmgr.vo.PushMsgVO;

@Service("weChatPushMsgService")
public class WeChatPushMsgServiceImpl extends MockService<WeChatPushMsgEO>
		implements IWeChatPushMsgService {
	
	@Autowired
	private IWeChatPushMsgDao weChatPushMsgDao;
	
	@Autowired
	private IUserGroupDao userGroupDao;
	
	@Autowired
	private IWeChatArticleDao weChatArticleDao;

	@Override
	public Pagination getPage(PushMsgVO msgVO) {
		Long siteId=LoginPersonUtil.getSiteId();
		if(siteId==null){
			return new Pagination();
		}else{
			msgVO.setSiteId(siteId);
			Pagination page = weChatPushMsgDao.getPage(msgVO);
			List<PushMsg> list=new ArrayList<PushMsg>();
			List<WeChatPushMsgEO> data = (List<WeChatPushMsgEO>)page.getData();
			for(WeChatPushMsgEO d:data){
			try {
				PushMsg mg=new PushMsg();
				BeanUtils.copyProperties(mg,d);
				list.add(mg);
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
			page.setData(list);
			List<UserGroupEO> gps = userGroupDao.getListBySite(siteId);
			Map<Long,String> map=new HashMap<Long,String>();
			for(UserGroupEO gp:gps){
				map.put(gp.getGroupid(), gp.getName());
			}
			for(PushMsg li:list){
				if(li.getPushGroup()<0L){
					li.setGroupName("全部");
				}else{
					li.setGroupName(map.get(li.getPushGroup())==null?"全部":map.get(li.getPushGroup()));
				}
			}
			page.setData(list);
			return page;
		}
	}

	@Override
	public void updatePublish(Long id,Long msgId,  Integer status) {
		weChatPushMsgDao.updatePublish(id,msgId, status);
	}

	@Override
	public PushArticleVO getPushMsg(Long id) {
		WeChatPushMsgEO eo = getEntity(WeChatPushMsgEO.class, id);
		PushArticleVO vo=new PushArticleVO();
		try {
			BeanUtils.copyProperties(vo,eo);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if(eo.getType()==2){
			if(!AppUtil.isEmpty(eo.getArticles())){
				List<WeChatArticleEO> arts=new ArrayList<WeChatArticleEO>();
				String[] str=eo.getArticles().split(",");
				for(int i=0;i<str.length;i++){
					WeChatArticleEO _eo = weChatArticleDao.getEntity(WeChatArticleEO.class, Long.parseLong(str[i]));
					arts.add(_eo);
				}
				vo.setNewsList(arts);
			}
		}
		return vo;
	}
	
	
}

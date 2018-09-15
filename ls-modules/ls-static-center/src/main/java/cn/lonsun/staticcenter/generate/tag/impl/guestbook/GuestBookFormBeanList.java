package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.util.LoginPersonUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class GuestBookFormBeanList extends AbstractBeanService{
	@Autowired
    private IContentModelService contModelService;
	
	@Override
	public Object getObject(JSONObject paramObj) {
		 Context context = ContextHolder.getContext();
         Long columnId = context.getColumnId();
	     // 此写法是为了使得在主页面这样调用也能解析
	     if (null == columnId) {// 如果栏目id为空说明，栏目id是在标签页面默认传入的
	        columnId = paramObj.getLong(GenerateConstant.ID);
	     }
		 List<ContentModelParaVO> list = contModelService.getParam(columnId, LoginPersonUtil.getSiteId(),null);
		 return list;
	}
	
}

package cn.lonsun.staticcenter.generate.tag.impl.interview;

import org.springframework.stereotype.Component;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.interview.vo.InterviewUrlVO;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;

import com.alibaba.fastjson.JSONObject;

@Component
public class InterviewInfoColumnBeanService extends AbstractBeanService {

	@Override
	public Object getObject(JSONObject paramObj) throws GenerateException {
		Context context = ContextHolder.getContext();
		Long columnId = paramObj.getLong(GenerateConstant.ID);
		// 此写法是为了使得在页面这样调用也能解析
		if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
			columnId = context.getColumnId();
		}
		Integer itype= paramObj.getInteger(GenerateConstant.ITYPE);
		String itypeParams = context.getParamMap().get(GenerateConstant.ITYPE);
		if(!StringUtils.isEmpty(itypeParams)){
			try{
				itype = Integer.parseInt(itypeParams);
			}catch(NumberFormatException e){}
		}
		InterviewUrlVO vo = new InterviewUrlVO();
		vo.setItype(itype);
		vo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), columnId,null));
		return vo;
	}

}

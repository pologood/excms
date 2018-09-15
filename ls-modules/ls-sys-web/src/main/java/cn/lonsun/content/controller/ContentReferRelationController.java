package cn.lonsun.content.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.core.base.controller.BaseController;

@Controller
@RequestMapping(value = "referRelation")
public class ContentReferRelationController extends BaseController {

	@Autowired
	private IContentReferRelationService contentReferRelationService;
	
	@RequestMapping("getRelation")
	@ResponseBody
	public Object getRelation(Long causeId,String modelCode,String type){
		List<ContentReferRelationEO> relation=new ArrayList<ContentReferRelationEO>();
		if(causeId!=null){
			relation = contentReferRelationService.getByCauseId(causeId, modelCode,type);
		}
		return getObject(relation);
	}
}

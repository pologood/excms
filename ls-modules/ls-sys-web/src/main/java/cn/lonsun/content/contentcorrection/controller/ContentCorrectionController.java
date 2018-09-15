package cn.lonsun.content.contentcorrection.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.internal.service.IContentCorrectionService;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;

@Controller
@RequestMapping(value="correction")
public class ContentCorrectionController extends BaseController {

	@Autowired 
	private IContentCorrectionService contentCorrectionService;
	
	@RequestMapping("index")
	public String index(ModelMap map){
		map.put("type", DataDictionaryUtil.getDDList("error_correction"));
		return "/content/correction/index";
	}
	
	@RequestMapping("formPage")
	public String formPage(){
		return "/content/correction/form_page";
	}
	
	@RequestMapping("saveForm")
	@ResponseBody
	public Object saveForm(ContentCorrectionEO contentCorrectionEo,HttpServletRequest request){
		contentCorrectionEo.setIp(IpUtil.getIpAddr(request));
		contentCorrectionService.saveEntity(contentCorrectionEo);
		return ajaxOk("提交成功");
	}
	
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(CorrectionPageVO pageVO){
		Long siteId=LoginPersonUtil.getSiteId();
		if(AppUtil.isEmpty(siteId)){
			return ajaxErr("站点信息有误");
		}else{
			pageVO.setSiteId(siteId);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(new Date());
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		Pagination page = contentCorrectionService.getPage(pageVO);
		@SuppressWarnings("unchecked")
		List<ContentCorrectionEO> list = (List<ContentCorrectionEO>) page.getData();
		for(ContentCorrectionEO l:list){
			if(l.getCreateDate().getTime()<=date.getTime()){
				l.setDateType(1);
			}else{
				l.setDateType(0);
			}
		}
		page.setData(list);
		return getObject(page);
	}
	
	@RequestMapping("changPubish")
	@ResponseBody
	public Object changPubish(Long id){
		contentCorrectionService.changePublish(id);
		return getObject();
	}
	
	@RequestMapping("batchChangePublish")
	@ResponseBody
	public Object batchChangePublish(@RequestParam(value="ids[]",required=false)Long[] ids,Integer status){
		contentCorrectionService.updatePublish(ids, status);
		return getObject();
	}
	
	@RequestMapping("delete")
	@ResponseBody
	public Object delete(Long id){
		contentCorrectionService.delete(ContentCorrectionEO.class, id);
		return getObject();
	}
	
	@RequestMapping("deletes")
	@ResponseBody
	public Object deletes(@RequestParam(value="ids[]",required=false)Long[] ids){
		contentCorrectionService.delete(ContentCorrectionEO.class, ids);
		return getObject();
	}
	
	@RequestMapping("edit")
	public String edit(ModelMap map,Long id){
	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
	    String str1 = sdf1.format(new Date());  
	    map.put("nowDate", str1);
		map.put("ID", id);
		map.put("type", DataDictionaryUtil.getDDList("error_correction"));
		return "/content/correction/edit";
	}
	
	@RequestMapping("getCorrection")
	@ResponseBody
	public Object getCorrection(Long id){
		return getObject(contentCorrectionService.getEntity(ContentCorrectionEO.class, id));
	}
	
	@RequestMapping("saveEdit")
	@ResponseBody
	public Object saveEdit(ContentCorrectionEO cmm){
		if(AppUtil.isEmpty(cmm.getId())){
			return ajaxErr("传参错误(ID:Error)");
		}
		contentCorrectionService.updateEntity(cmm);
		return getObject();
		
	}
}

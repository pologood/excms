package cn.lonsun.content.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.IpUtil;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 
* @ClassName: GuestBookFrontController
* @Description: TODO
* @author hujun
* @date 2015年12月10日 上午10:50:36
*
 */
@Controller
@RequestMapping(value = "guestBookFront")
public class GuestBookFrontController extends BaseController{
	@Autowired
	private IGuestBookService guestBookService;
	
	@Autowired
	private IBaseContentService baseContentService;
	
	@Autowired
	private IContentModelService contentModelService;
	
	@Autowired
    private IContentModelService contModelService;
	
	//保存用户留言
	@RequestMapping(value = "frontSave")
	@ResponseBody
	public Object frontSave(GuestBookEO eo1,BaseContentEO eo2){
		Boolean b = contentModelService.IsLoginComment(eo2.getColumnId());
		if(b == true){
			Long id=baseContentService.saveEntity(eo2);
			CacheHandler.saveOrUpdate(BaseContentEO.class,eo2);
			eo1.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
			eo1.setBaseContentId(id);
			guestBookService.saveGuestBook(eo1,eo2.getSiteId(),eo2.getColumnId());
			return getObject(1);
		}else{
			return getObject(0);
		}
	}
	
	
	@RequestMapping(value = "guestBookList")
	public ModelAndView guestBooKList(Long columnId,Long siteId){
		List<ContentModelParaVO> list = contModelService.getParam(columnId,siteId,null);
		ModelAndView mv = new ModelAndView("/content/fontGuestBook"); 
		mv.addObject("list",list);
		return mv;
	}

	/**
	 * 前台新增留言
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "saveGuestBook")
	@ResponseBody
	public Object saveGuestBook(GuestBookEditVO vo){
		Boolean b = contentModelService.IsLoginComment(vo.getColumnId());
		if(b == true){
			BaseContentEO contentEO=new BaseContentEO();
			AppUtil.copyProperties(contentEO,vo);
			GuestBookEO guestBookEO=new GuestBookEO();
			AppUtil.copyProperties(guestBookEO,vo);
			Long id=baseContentService.saveEntity(contentEO);
			CacheHandler.saveOrUpdate(BaseContentEO.class,contentEO);
			guestBookEO.setPersonIp(IpUtil.getIpAddr(LoginPersonUtil.getRequest()));
			guestBookEO.setBaseContentId(id);
			guestBookService.saveGuestBook(guestBookEO,vo.getSiteId(),vo.getColumnId());
			return getObject(1);
		}else{
			return getObject(0);
		}
	}

}

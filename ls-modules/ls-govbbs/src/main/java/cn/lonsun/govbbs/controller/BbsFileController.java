package cn.lonsun.govbbs.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "bbsFile")
public class BbsFileController extends BaseController {

	@Autowired
	private IBbsFileService bbsFileService;


	@Autowired
	private IMongoDbFileServer mongoDbFileServer;


	@RequestMapping("index")
	public String list(Long pageIndex,Model model) {
		if (pageIndex == null) pageIndex = 0L;
		model.addAttribute("pageIndex", pageIndex);
		return "/bbs/bbSFile";
	}


	/**
	 * 获取视频新闻分页
	 *
	 * @return
	 */
	@RequestMapping("getPage")
	@ResponseBody
	public Object getPage(FileCenterVO fileVO) {

		Pagination page = bbsFileService.getPage(fileVO);
		return getObject(page);
	}


	/**
	 * @param ids
	 * @throws isDel 0 逻辑删除 1 物理删除
	 */
	@RequestMapping("deleteFiles")
	@ResponseBody
	public Object deleteFiles(Long[] ids,Integer isDel) {
		//判断是否为超级管理员或root
		if (ids.length <= 0) {
			return ajaxErr("参数不能为空");
		}
		bbsFileService.deleteFiles(ids,isDel);
		return getObject();
	}


	@RequestMapping("fileUpload")
	@ResponseBody
	public Object fileUpload(MultipartFile Filedata, HttpServletRequest request, BbsFileEO file, String sessionId) {
		if (Filedata.isEmpty()) {
			return ajaxErr("文件上传失败(File upload failed)");
		}
		try{
			bbsFileService.fileUpload(Filedata,request,file);
			file.setStatus(1);
		}catch (Exception e){
			return ajaxErr("文件上传失败(File upload failed)");
		}
		return file;
	}
	/**
	 * @param mongoId
	 * @param request
	 * @param response
	 * @return void
	 * @throws
	 * @Title: download
	 * @Description: file download public interface
	 */
	@RequestMapping("download/{mongoId}")
	public void download(@PathVariable String mongoId, HttpServletRequest request, HttpServletResponse response) {
		mongoDbFileServer.downloadFile(response, mongoId, null);
	}
}

package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.util.EditorUploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * @author liuk
 * @ClassName: NewsPushController
 * @Description: article news
 * @date 2017年2月23日17:22:08
 */
@Controller
@RequestMapping(value = "newsPush")
public class NewsPushController extends BaseController {

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IContentPicService contentPicService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;



    /**
     * 保存新闻
     * @param baseContentEO
     * @param content
     * @return
     */
    @RequestMapping("savePost")
    @ResponseBody
    public Object savePost(HttpServletRequest request,BaseContentEO baseContentEO,String content,String checkCode){
        if(StringUtils.isEmpty(checkCode)){
            return ajaxErr("验证码不能为空！");
        }
        Object obj = request.getSession().getAttribute("webCode");
        if(obj == null){
            return ajaxErr("验证码已经失效！");
        }
        String webCode = (String)obj;
        if(!checkCode.trim().toLowerCase().equals(webCode.toLowerCase())){
            return ajaxErr("验证码不正确，请重新输入");
        }

        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, baseContentEO.getColumnId());
        baseContentEO.setTypeCode(columnMgrEO.getColumnTypeCode());
        if(baseContentEO.getTypeCode().equals(BaseContentEO.TypeCode.pictureNews.toString())){
            contentPicService.savePicNews(baseContentEO,content,null,null);
        }else if(baseContentEO.getTypeCode().equals(BaseContentEO.TypeCode.articleNews.toString())){
            baseContentService.saveArticleNews(baseContentEO, content, null, null, null, null);
        }

        return getObject("报送成功！");
    }


    /**
     * @param request
     * @param response
     * @param
     * @return
     * @throws UnsupportedEncodingException
     * @Description 秀秀上传图片
     * @author Hewbing
     * @date 2015年9月14日 下午2:49:32
     */
    @RequestMapping("uploadAttachment")
    @ResponseBody
    public Object uploadAttachment(HttpServletRequest request, HttpServletResponse response, MultipartFile Filedata) throws UnsupportedEncodingException {
        /* 还需旧图片垃圾处理 */
        MongoFileVO mongvo = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        System.out.println(mongvo.getMongoId());
        return mongvo.getMongoId();
    }

    /**
     * @param request
     * @param response
     * @Description 编辑器上传文件
     * @author Hewbing
     * @date 2015年9月15日 下午3:52:08
     */
    @RequestMapping("upload")
    public void uploadFiles(HttpServletRequest request, HttpServletResponse response) {
        EditorUploadUtil.uploadByKindEditor(SystemCodes.contentMgr, request, response);
    }

    @RequestMapping("uploadNew")
    public void uploadNew(HttpServletRequest request, HttpServletResponse response) {
        EditorUploadUtil.uploadByKindEditorNew(SystemCodes.contentMgr, request, response);
    }





}

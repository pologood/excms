package cn.lonsun.journals;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.journals.service.IJournalsService;
import cn.lonsun.journals.vo.JournalsQueryVO;
import cn.lonsun.journals.vo.JournalsSearchVO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by lonsun on 2017-1-3.
 */

@Controller
@RequestMapping(value = "journal")
public class JournalsController extends BaseController {
    @Autowired
    private IBaseContentService baseContentService;
    @Value("${html.create.path}")
    private String pdfPath;
    @Autowired
    private IJournalsService journalsService;

    /**
     * 去往列表页面
     * @param model
     * @return
     */
    @RequestMapping("index")
    public String getList( Model model) {

        return "/journals/index";
    }

    @RequestMapping("getTestList")
    @ResponseBody
    public Object getTestList( ) {
        List<JournalsSearchVO> list = journalsService.getAllSearchVO();
        return getObject(list);
    }





    @RequestMapping("getJournalsList")
    @ResponseBody
    public Object getJournalsList(JournalsQueryVO journalsQueryVO) {
        journalsQueryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = journalsService.getJournalsList(journalsQueryVO);
        return getObject(pagination);
    }

    /**
     * 新增编辑素材
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("editPage")
    public String editPage( Model model,Long id,Long columnId) {
        model.addAttribute("id",id);
        model.addAttribute("columnId",columnId);
        return "/journals/editPage";
    }


    @RequestMapping("uploadImge")
    @ResponseBody
    public Object uploadImge(MultipartFile Filedata,Long siteId) {
        siteId = LoginPersonUtil.getSiteId();
        MongoFileVO mongoFileVO = FileUploadUtil.uploadUtil(Filedata, FileCenterEO.Type.NotDefined.toString(), FileCenterEO.Code.Default.toString(), siteId, null, null, null, "");
        return getObject(mongoFileVO);

    }
    @RequestMapping("uploadFile")
    @ResponseBody
    public Object uploadFile(MultipartFile Filedata,Long siteId){
        String fileName = Filedata.getOriginalFilename();
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        File file = null;
        MongoFileVO mongoVO =new MongoFileVO();
        try {
//            if(".mp4".equals(suffix)||".flv".equals(suffix)){//flv格式直接上传到mongoDB上
//                mongoVO = FileUploadUtil.uploadUtil(Filedata, FileCenterEO.Type.Video.toString(), FileCenterEO.Code.VideoNewsUpload.toString(),
//                        siteId, columnId, contentId, "视频新闻", LoginPersonUtil.getRequest());
//            }else{//非flv格式先拷到服务器本地
            if (StringUtils.isEmpty(prefix) || prefix.length() < 3) {
                prefix = prefix + "_tmp";
            }
            file = File.createTempFile(prefix, suffix);
            FileUtils.copyInputStreamToFile(Filedata.getInputStream(), file);

            mongoVO.setMongoId(file.getPath());
            mongoVO.setFileName(fileName);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return   getObject(mongoVO);
    }


    @RequestMapping("getJournals")
    @ResponseBody
    public  Object getJournals(Long id){
        JournalsEO journalsEO =new JournalsEO();
        if(!AppUtil.isEmpty(id)){
            journalsEO =  journalsService.getEntity(JournalsEO.class,id);
            BaseContentEO baseContentEO =  baseContentService.getEntity(BaseContentEO.class, journalsEO.getBaseContentId());
            journalsEO.setIsPublish(baseContentEO.getIsPublish());
            journalsEO.setImageLink(baseContentEO.getImageLink());
        }
        return getObject(journalsEO);
    }


    @RequestMapping("saveJournals")
    @ResponseBody
    public Object saveJournals(JournalsEO journalsEO,HttpServletRequest request,Long columnId){
        String context =request.getRealPath("/");
        journalsEO.setSiteId(LoginPersonUtil.getSiteId());
        if(journalsEO.getIsPublish()==1&&AppUtil.isEmpty(journalsEO.getFilePath())){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"发布信息需上传素材!");
        }



        String filePath =  journalsEO.getFilePath();
        if(!AppUtil.isEmpty(filePath)&&filePath.contains("temp")){
            try {
                File file = new File(filePath);
                Calendar calendar =Calendar.getInstance();
                String flvPrefix =pdfPath+"/"+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/";
                File tempFile = new File(flvPrefix);
                if(!tempFile.exists()){
                    tempFile.mkdirs();
                }
                Integer byteread=0;
                String uuid=AppUtil.getUuid();
                tempFile=new File(flvPrefix+uuid+".pdf");
                tempFile.createNewFile();
//                tempFile=File.createTempFile(uuid,".pdf",tempFile);
                if (file.exists()) {                  //文件存在时
                    InputStream inStream = new FileInputStream(filePath);      //读入原文件
                    FileOutputStream fs = new FileOutputStream(tempFile.getPath());
                    byte[] buffer = new byte[1024];
                    int length;
                    while ( (byteread = inStream.read(buffer)) != -1) {
                        fs.write(buffer, 0, byteread);
                    }
                    inStream.close();
                    fs.flush();
                    fs.close();
                }
                file.delete();
                journalsEO.setFilePath(flvPrefix+uuid+".pdf");
            } catch (IOException e) {
                throw  new  BaseRunTimeException(TipsMode.Message.toString(),"保存PDF文件失败");
            }

        }


        journalsEO =  journalsService.saveJournals(journalsEO,columnId);


        return getObject();
    }


    @RequestMapping("batchPublish")
    @ResponseBody
    public Object batchPublish(Long[] ids,Long[] contentIds){
        if(null==ids&&ids.length<1){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待发布项!");
        }
        List<JournalsEO> journalsEOs = journalsService.getEntities(JournalsEO.class,ids);
        for(JournalsEO journalsEO: journalsEOs){
            if(AppUtil.isEmpty(journalsEO.getFilePath())){
                throw new BaseRunTimeException(TipsMode.Message.toString(),journalsEO.getMateriaName()+"需上传素材才能发布!");

            }

        }


        List<BaseContentEO> baseContentEOs = baseContentService.getEntities(BaseContentEO.class, contentIds);
        for(BaseContentEO baseContentEO: baseContentEOs){
            baseContentEO.setIsPublish(1);
            baseContentEO.setPublishDate(new Date());
        }

        baseContentService.updateEntities(baseContentEOs);
        CacheHandler.saveOrUpdate(BaseContentEO.class,baseContentEOs);

                MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), baseContentEOs.get(0).getColumnId(), contentIds).setType(MessageEnum.PUBLISH.value()), 1);

        return getObject();

    }


    @RequestMapping("batchCancelPublish")
    @ResponseBody
    public Object batchCancelPublish(Long[] ids){
        if(null==ids&&ids.length<1){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待取消发布项!");
        }


        List<BaseContentEO> baseContentEOs = baseContentService.getEntities(BaseContentEO.class, ids);
        for(BaseContentEO baseContentEO: baseContentEOs){
            baseContentEO.setIsPublish(0);
        }

        baseContentService.updateEntities(baseContentEOs);
        CacheHandler.saveOrUpdate(BaseContentEO.class,baseContentEOs);
        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), baseContentEOs.get(0).getColumnId()
                , ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        return getObject();

    }


    @RequestMapping("batchDel")
    @ResponseBody
    public Object batchDel(Long[] ids,Long[] contentIds,HttpServletRequest request){
        if(null==ids&&ids.length<1){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待删除项!");
        }
        List<JournalsEO> journalsEOs =  journalsService.getEntities(JournalsEO.class, ids);
        String[] mongosId=new String[journalsEOs.size()];
        for(int i=0;i<journalsEOs.size();i++){
            if(!AppUtil.isEmpty(journalsEOs.get(i).getFilePath())){
                mongosId[i]=journalsEOs.get(i).getFilePath();
                File file=new File(request.getRealPath("/")+mongosId[i]);
                if(file.exists()){
                    file.delete();
                }


            }

        }
//        FileUploadUtil.deleteFileCenterEO(mongosId);
        journalsService.delete(JournalsEO.class,ids);
       List<BaseContentEO> baseContentEOs =  baseContentService.getEntities(BaseContentEO.class, contentIds);
        baseContentService.delete(baseContentEOs);
        CacheHandler.delete(BaseContentEO.class,baseContentEOs);
        return getObject();
    }



}

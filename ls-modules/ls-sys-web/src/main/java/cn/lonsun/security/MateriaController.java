package cn.lonsun.security;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.security.internal.entity.SecurityMateria;
import cn.lonsun.security.internal.service.IMateriaService;
import cn.lonsun.security.internal.vo.MateriaQueryVO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
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

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * Created by lonsun on 2016-12-9.
 */
@Controller
@RequestMapping(value = "materia")
public class MateriaController extends BaseController {
    @Autowired
    private IBaseContentService baseContentService;
    @Autowired
    private IMateriaService materiaService;

    @Value("${pdfPath}")
    private String pdfPath;
    @Value("${html.create.path}")
    private String html;
    @Value("${cotextUrl}")
    private  String cotextUrl;



    /**
     * 去往列表页面
     * @param model
     * @return
     */
    @RequestMapping("list")
    public String getList( Model model) {

        return "/security/list";
    }







    @RequestMapping("getmMateriaList")
    @ResponseBody
    public Object getmMateriaList(MateriaQueryVO materiaQueryVO) {
        materiaQueryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = materiaService.getmMateriaList(materiaQueryVO);
        return getObject(pagination);
    }

    /**
     * 新增编辑素材
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("editPage")
    public String editPage( Model model,Long id) {
        model.addAttribute("id",id);
        return "/security/editPage";
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


    @RequestMapping("getMateria")
    @ResponseBody
    public  Object getMateria(Long id){
        SecurityMateria securityMateria =new SecurityMateria();
        if(!AppUtil.isEmpty(id)){
            securityMateria =  materiaService.getEntity(SecurityMateria.class,id);
            BaseContentEO  baseContentEO =  baseContentService.getEntity(BaseContentEO.class, securityMateria.getBaseContentId());
            securityMateria.setIsPublish(baseContentEO.getIsPublish());
            securityMateria.setImageLink(baseContentEO.getImageLink());
       }
        return getObject(securityMateria);
    }


    @RequestMapping("saveMateria")
    @ResponseBody
    public Object saveMateria(SecurityMateria securityMateria,HttpServletRequest request){
        String context =html+"/"+cotextUrl.replace("http://","");
        securityMateria.setSiteId(LoginPersonUtil.getSiteId());
        if(securityMateria.getIsPublish()==1&&AppUtil.isEmpty(securityMateria.getFilePath())){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"发布信息需上传素材!");
        }



        String filePath =  securityMateria.getFilePath();
        if(!AppUtil.isEmpty(filePath)&&filePath.contains("temp")){
            try {
                File file = new File(filePath);
                Calendar calendar =Calendar.getInstance();
                String flvPrefix =pdfPath+"/"+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/";
                File tempFile = new File(context+flvPrefix);
                if(!tempFile.exists()){
                    tempFile.mkdirs();
                }
                Integer byteread=0;
                String uuid=AppUtil.getUuid();
                tempFile=new File(context+flvPrefix+uuid+".pdf");
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
                securityMateria.setFilePath(flvPrefix+uuid+".pdf");
            } catch (IOException e) {
                throw  new  BaseRunTimeException(TipsMode.Message.toString(),"保存PDF文件失败");
            }

        }


        securityMateria =  materiaService.saveMateria(securityMateria);
        return getObject();
    }


    @RequestMapping("batchPublish")
    @ResponseBody
    public Object batchPublish(Long[] ids,Long[] contentIds){
        if(null==ids&&ids.length<1){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待发布项!");
        }
            List<SecurityMateria> securityMaterias = materiaService.getEntities(SecurityMateria.class,ids);
            for(SecurityMateria securityMateria: securityMaterias){
                if(AppUtil.isEmpty(securityMateria.getFilePath())){
                    throw new BaseRunTimeException(TipsMode.Message.toString(),securityMateria.getMateriaName()+"需上传素材才能发布!");

                }

            }


            List<BaseContentEO> baseContentEOs = baseContentService.getEntities(BaseContentEO.class, contentIds);
            for(BaseContentEO baseContentEO: baseContentEOs){
                baseContentEO.setIsPublish(1);
                baseContentEO.setPublishDate(new Date());
            }

        baseContentService.updateEntities(baseContentEOs);


        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), null, ids).setType(MessageEnum.PUBLISH.value()), 1);

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
        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), null
                , ids).setType(MessageEnum.UNPUBLISH.value()), 2);
        return getObject();

    }


    @RequestMapping("batchDel")
    @ResponseBody
    public Object batchDel(Long[] ids,Long[] contentIds,HttpServletRequest request){
        if(null==ids&&ids.length<1){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择待删除项!");
        }
        List<SecurityMateria> securityMaterias =  materiaService.getEntities(SecurityMateria.class, ids);
        String[] mongosId=new String[securityMaterias.size()];
        for(int i=0;i<securityMaterias.size();i++){
            if(!AppUtil.isEmpty(securityMaterias.get(i).getFilePath())){
                mongosId[i]=securityMaterias.get(i).getFilePath();
                File file=new File(request.getRealPath("/")+mongosId[i]);
                if(file.exists()){
                    file.delete();
                }


            }

        }
//        FileUploadUtil.deleteFileCenterEO(mongosId);
        materiaService.delete(SecurityMateria.class,ids);
        baseContentService.delete(BaseContentEO.class,contentIds);
        return getObject();
    }

}

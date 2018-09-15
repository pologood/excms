/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.desktop.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.common.util.ThumbUtil;
import cn.lonsun.common.util.TokenUtil;
import cn.lonsun.common.util.UploadState;
import cn.lonsun.core.util.AjaxObj;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.desktop.internal.entity.SystemBackgroundEO;
import cn.lonsun.desktop.internal.entity.SystemInfoWinEO;
import cn.lonsun.desktop.internal.service.ISystemBackgroundService;
import cn.lonsun.desktop.internal.service.ISystemInfoWinService;
import cn.lonsun.desktop.vo.SystemBackgroundQueryVO;
import cn.lonsun.desktop.vo.SystemInfoWinQueryVO;

/**
 * 桌面应用控制器
 *
 * @author Dzl
 */
@Controller
@RequestMapping("systemMgr/desktopMgr")
public class DesktopMgrController extends cn.lonsun.core.base.controller.BaseController {

    @Autowired
    private ISystemBackgroundService systemBackgroundService;
    @Autowired
    private ISystemInfoWinService systemInfoWinService;
    private Logger logger = LoggerFactory.getLogger(getClass());


    @RequestMapping("bgMgr")
    public ModelAndView bgMgrPage() {
        return new ModelAndView("app/desktop/set_sysbg_mgr");
    }

    @RequestMapping("bgMgrEdit")
    public ModelAndView bgMgrEditPage() {
        return new ModelAndView("app/desktop/set_sysbg_edit");
    }

    /**
     * 上传背景
     *
     * @param model
     * @return
     */
    @RequestMapping("setBgUpload")
    public ModelAndView setBgUploadPage(Model model) {
        return new ModelAndView("app/desktop/set_bg_upload");
    }

    // <editor-fold defaultstate="collapsed" desc="桌面背景操作">

    /**
     * 获取桌面背景列表
     *
     * @param queryVo isSysBg=true 返回系统背景 isSysBg=false 返回用户背景
     * @return
     */
    @RequestMapping("getDesktopBgList")
    @ResponseBody
    public Object getDesktopBgList(SystemBackgroundQueryVO queryVo) {
        queryVo.setIsSysBg(true);
        queryVo.setSortOrder("asc");
        queryVo.setSortField("id");
        List<SystemBackgroundEO> sysBgList = systemBackgroundService.getList(queryVo);
        return ajaxOk(sysBgList);
    }

    /**
     * 获取桌面背景EO
     *
     * @param id
     * @return
     */
    @RequestMapping("getDesktopBgEO")
    @ResponseBody
    public Object getDesktopBgEO(
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {

        SystemBackgroundEO eo;
        if (id > 0) {
            eo = systemBackgroundService.getEntity(SystemBackgroundEO.class, id);
            if (eo == null) {
                return ajaxErr("记录不存在");
            }
        } else {
            eo = new SystemBackgroundEO();
        }
        return ajaxOk(eo);
    }

    /**
     * 保存桌面背景
     *
     * @param eo
     * @return
     */
    @RequestMapping("saveDesktopBgEO")
    @ResponseBody
    public Object saveDesktopBgEO(SystemBackgroundEO eo) {
        if (!AppUtil.isEmpty(eo.getId()) && eo.getId() > 0) {
            systemBackgroundService.updateEO(eo);
        } else {
            systemBackgroundService.saveEO(eo);
        }
        return ajaxOk(eo);
    }

    @RequestMapping("saveDesktopBgEO4Json")
    @ResponseBody
    public Object saveDesktopBgEOByRequestBody(@RequestBody SystemBackgroundEO eo) {
        if (!AppUtil.isEmpty(eo.getId()) && eo.getId() > 0) {
            systemBackgroundService.updateEO(eo);
        } else {
            systemBackgroundService.saveEO(eo);
        }
        return ajaxOk(eo);
    }

    /**
     * 删除桌面背景(仅允许用户删除自己新建的背景)
     *
     * @param req
     * @param id
     * @return
     */
    @RequestMapping("delDesktopBgEO")
    @ResponseBody
    public Object delDesktopBgEO(
            HttpServletRequest req,
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        if (0 == id) {
            return ajaxErr("参数错误");
        }
        SystemBackgroundEO eo = systemBackgroundService.getEntity(SystemBackgroundEO.class, id);
        systemBackgroundService.delPyEO(eo);
        // 删除成功后,尝试删除物理图片文件
        FileUtil.delFileByAbsPath(req, eo.getImgUrl());
        FileUtil.delFileByAbsPath(req, eo.getThumb());
        return ajaxOk();
    }

    @RequestMapping("delDesktopBgEOByUser")
    @ResponseBody
    public Object delDesktopBgEOByUser(
            HttpServletRequest req,
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        if (0 == id) {
            return ajaxErr("参数错误");
        }
        // 获取当前用户ID
        Long userId = SessionUtil.getLongProperty(req.getSession(), "userId");
        SystemBackgroundEO eo = systemBackgroundService.getEntity(SystemBackgroundEO.class, id);
        if(userId.equals(eo.getCreateUserId()) && !eo.getIsSysBg()){
            systemBackgroundService.delPyEO(eo);
            // 删除成功后,尝试删除物理图片文件
            FileUtil.delFileByAbsPath(req, eo.getImgUrl());
            FileUtil.delFileByAbsPath(req, eo.getThumb());
            return ajaxOk();
        }else{
            return ajaxErr("非法的删除请求");
        }
    }

    

    /**
     * 桌面信息窗口管理页面
     *
     * @param model
     * @return
     */
    @RequestMapping("infoWinMgr")
    public ModelAndView infoWinMgrPage(Model model) {
        return new ModelAndView("app/desktop/infowin_mgr");
    }

    /**
     * 桌面信息窗口编辑页面
     *
     * @param model
     * @return
     */
    @RequestMapping("infoWinMgrEdit")
    public ModelAndView infoWinMgrEditPage(Model model) {
        return new ModelAndView("app/desktop/infowin_mgr_edit");
    }

    @RequestMapping("getInfoWinList")
    @ResponseBody
    public Object getInfoWinList(SystemInfoWinQueryVO queryVo) {
        List<SystemInfoWinEO> list = systemInfoWinService.getList(queryVo);
        return ajaxOk(list);
    }

    @RequestMapping("getInfoWinEO")
    @ResponseBody
    public Object getInfoWinEO(
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {

        SystemInfoWinEO eo;
        if (id > 0) {
            eo = systemInfoWinService.getEntity(SystemInfoWinEO.class, id);
            if (eo == null) {
                return ajaxErr("记录不存在");
            }
        } else {
            eo = new SystemInfoWinEO();
        }
        return ajaxOk(eo);
    }
    
    @RequestMapping("saveInfoWin4Json")
    @ResponseBody
    public Object saveInfoWin(@RequestBody SystemInfoWinEO eo) {
        if (!AppUtil.isEmpty(eo.getId()) && eo.getId() > 0) {
            systemInfoWinService.updateEO(eo);
        } else {
            systemInfoWinService.saveEO(eo);
        }
        return ajaxOk(eo);
    }

    @RequestMapping("delInfoWinEO")
    @ResponseBody
    public Object delInfoWinEO(
            HttpServletRequest req,
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id) {
        if (0 == id) {
            return ajaxErr("参数错误");
        }
        SystemInfoWinEO eo = systemInfoWinService.getEntity(SystemInfoWinEO.class, id);
        if (null == eo) {
            return ajaxErr("记录不存在");
        }
        systemInfoWinService.delPyEO(eo);
        return ajaxOk();
    }

    @RequestMapping("batchDelInfoWinEO")
    @ResponseBody
    public Object batchDelInfoWinEO(
            HttpServletRequest req,
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids) {
        if (AppUtil.isEmpty(ids)) {
            return ajaxErr("参数错误");
        }
        systemInfoWinService.batchDelPyEO(ids);
        return ajaxOk();
    }



    @RequestMapping("delBgImg")
    @ResponseBody
    public Object delBgImg(
            HttpServletRequest req,
            @RequestParam(value = "id", required = false, defaultValue = "0") Long id,
            @RequestParam(value = "fileName", required = false, defaultValue = "") String fileName,
            @RequestParam(value = "thumb", required = false, defaultValue = "") String thumb
    ) {

        if (AppUtil.isEmpty(fileName) || AppUtil.isEmpty(thumb) ) {
            return ajaxErr("参数错误");
        }
        // 删除文件
        FileUtil.delFileByAbsPath(req, fileName);
        FileUtil.delFileByAbsPath(req, thumb);
        return ajaxOk();
    }


    private final String rootSavePath = "/upload/";
    public final String[] typeImages = new String[]{"gif", "jpeg", "png", "jpg", "tif", "bmp"};


    /**
     * 上传桌面背景
     *
     * @param req
     * @param file
     * @return
     * @throws java.io.IOException
     * @throws Exception
     */
    @RequestMapping("uploadImg")
    @ResponseBody
    public Object uploadImg(HttpServletRequest req, @RequestParam("Filedata") MultipartFile file)
            throws IOException, Exception {
        if (file.isEmpty()) return AjaxObj.Err("上传文件不能为空");
        // 获取当前用户ID
        Long userId = SessionUtil.getLongProperty(req.getSession(), "userId");
        //文件保存目录路径
        String savePath = rootSavePath + "systemData/desktopBackground/";
        // 判断路径是否以"/"结尾
        if (!StringUtils.endsWith(savePath, "/")) {
            savePath += "/";
        }

        String uploadDir = req.getSession().getServletContext().getRealPath(savePath);
        // 判断目录是否存在
        File dirPath = new File(uploadDir);
        if (!dirPath.exists()) {
            dirPath.mkdirs();  // 创建文件夹
        }
        //以当前精确到毫秒的日期为上传的文件的文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddkkmmssSSS");
        // 原文件名称
        String oldFileName = file.getOriginalFilename();
        // 取得不带.的扩展名
        String fileExt = FileUtil.getType(oldFileName);
        // 保存文件的新的名称(仅文件名部分,不含扩展名)
        String newFileNamePrev = sdf.format(new Date());

        // 保存文件的新的名称
        String newFileName = newFileNamePrev + "." + fileExt;
        // 包含路径信息的全路径文件地址
        String newFullFileName = savePath + newFileName;
        // 保存文件的物理全路径
        // 使用FileUtil.getDoPath来统一路径,以适用window与linux平台
        String newSaveFullFileName = FileUtil.getDoPath(uploadDir + "\\" + newFileName);
        File savedFile = new File(uploadDir, newFileName);
        Object ret = AjaxObj.Err("上传失败");

        /*保存文件*/
        UploadState flag = FileUtil.upload4Stream(newFileName, uploadDir, file.getInputStream(), typeImages);
        logger.info("flag.getFlag() = " + flag.getFlag());
        logger.info("flag.getState() = " + flag.getState());
        if (flag.getFlag() == 0) {
            // 根据文件名和大小生成token,并返回到前端
            // 前端删除文件时,必须要发送token,服务端根据接收到的token来删除文件
            // 以实现安全的删除文件
            // 创建文件的token
            String fileToken = TokenUtil.generateToken(newSaveFullFileName, String.valueOf(FileUtil.getFileSize(newSaveFullFileName)));
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("oldFileName", oldFileName);
            map.put("fileName", newFullFileName);
            map.put("fileNameToken", fileToken);
            // 生成缩略图
            int defWidth = 122;
            int defHeight = 77;
            // 缩略图文件名
            String thumbFileName = newFileNamePrev + "_s." + fileExt;
            String thumbNewFullFileName = savePath + thumbFileName;
            // 使用FileUtil.getDoPath来统一路径,以适用window与linux平台
            String thumbSaveFullFileName = FileUtil.getDoPath(uploadDir + "\\" + thumbFileName);
            ThumbUtil thumbUtil = new ThumbUtil();
            // 强制限制生成缩略图大小
            thumbUtil.saveImageAsJpg(newSaveFullFileName, thumbSaveFullFileName, defWidth, defHeight, 1);
            // 创建缩略图的token
            String thumbToken = TokenUtil.generateToken(thumbSaveFullFileName, String.valueOf(FileUtil.getFileSize(thumbSaveFullFileName)));
            map.put("thumb", thumbNewFullFileName);
            map.put("thumbToken", thumbToken);
            ret = AjaxObj.Ok(map);
        } else {
            ret = AjaxObj.Err(flag.getState());
        }

        return ret;

    }

  

    
}

package cn.lonsun.site.site;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-3<br/>
 */
@Controller
@RequestMapping("waterMark")
public class WaterMarkController extends BaseController {
    @Autowired
    private IWaterMarkConfigService configService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @RequestMapping("index")
    public String getInfo(Long siteId, Model model, HttpSession session) {
        if (siteId == null) {
            siteId = (Long) session.getAttribute("siteId");
        }
        model.addAttribute("siteId", siteId);
        return "site/site/waterMark_info";
    }

    /**
     * 获取水印配置
     *
     * @param siteId
     * @return
     */
    @RequestMapping("getConfigEO")
    @ResponseBody
    public Object getConfigEO(Long siteId) {
        WaterMarkConfigEO eo = configService.getConfigBySiteId(siteId);
        if (eo == null) {
            eo = new WaterMarkConfigEO();
            eo.setSiteId(siteId);
            eo.setEnableStatus(0);
            eo.setType(0);
            eo.setIsBold(0);
        }
        return getObject(eo);
    }

    /**
     * 保存修改水印配置
     *
     * @param eo
     * @return
     */
    @RequestMapping("saveConfigEO")
    @ResponseBody
    public Object saveConfigEO(WaterMarkConfigEO eo) {
        if (eo.getTransparency() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "透明度不能为空");

        }
        if (eo.getTransparency() <= 0 || eo.getTransparency() > 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "透明度应大于0.0，不大于1.0");
        }
        configService.saveConfigEO(eo);
        return getObject(0);
    }

    @RequestMapping("toUploadPic")
    public String toUploadPic() {
        return "site/site/img_upload";
    }

    /**
     * 缩略图上传
     *
     * @param request
     * @param response
     * @param Filedata
     * @param siteId
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("uploadPic")
    @ResponseBody
    public Object uploadPic(HttpServletRequest request, HttpServletResponse response,
                            MultipartFile Filedata, Long siteId, String picPath)
        throws UnsupportedEncodingException {
        if (siteId == null) {
            siteId = LoginPersonUtil.getSiteId();
        }
        MongoFileVO mongvo = FileUploadUtil.uploadUtil(Filedata, FileCenterEO.Type.Image.toString(), FileCenterEO.Code.ThumbUpload.toString(), siteId, null, null, "水印图片", request);
        FileUploadUtil.deleteFileCenterEO(picPath);
        //MongoFileVO mongvo = mongoDbFileServer.uploadMultipartFile(Filedata, null);
        return mongvo;
    }

    /**
     * 去往预览页面
     *
     * @return
     */
    @RequestMapping("toPreview")
    public String toPreview() {
        return "site/site/waterMark_preview";
    }

    /**
     * 水印预览方法
     *
     * @param eo
     * @param response
     * @return
     */
    @RequestMapping("preview")
    @ResponseBody
    public Object preview(WaterMarkConfigEO eo, HttpServletResponse response) {
        if (eo.getTransparency() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "透明度不能为空");
        }
        if (eo.getTransparency() <= 0 || eo.getTransparency() > 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "透明度应大于0.0，不大于1.0");
        }
        InputStream in = null;
        Resource res = new ClassPathResource("/bg/bg.png");
        try {
            in = res.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] out = null;
        if (eo.getType() == 1) {
            try {
                GridFSDBFile file = mongoDbFileServer.getGridFSDBFile(eo.getPicPath(), null);
                InputStream inStream = file.getInputStream();
                ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int rc = 0;
                while ((rc = inStream.read(buff, 0, 100)) > 0) {
                    swapStream.write(buff, 0, rc);
                }
                byte[] out1 = swapStream.toByteArray();
                out = ImgHander.waterMarkByPic(in, out1, eo.getWidth(), eo.getHeight(), eo.getTransparency(), eo.getRotate(), eo.getPosition(), "jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            String color16 = "000000";
                /* 16进制颜色与Color类转换 */
            if (!AppUtil.isEmpty(eo.getFontColor()))
                color16 = eo.getFontColor().substring(1, 7);
            Color color = new Color(Integer.parseInt(color16, 16));
            out = ImgHander.waterMarkByWord(in, color, eo.getFontFamily(), eo.getFontSize(), eo.getWordContent(), eo.getTransparency(),
                eo.getIsBold(), eo.getRotate(), eo.getPosition(), "jpg");
        }
        MongoFileVO vo = mongoDbFileServer.uploadByteFile(out, RandomDigitUtil.getRandomDigit(), null, null);
        return getObject(vo);
    }

    /**
     * 获取字体
     *
     * @return
     */
    @RequestMapping("getFontFamily")
    @ResponseBody
    public Object getFontFamily() {
        return DataDictionaryUtil.getItemList("water_mark_font_family", LoginPersonUtil.getSiteId());
    }
}

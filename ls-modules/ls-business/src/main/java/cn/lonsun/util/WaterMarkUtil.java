package cn.lonsun.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.site.site.internal.entity.WaterMarkConfigEO;
import cn.lonsun.site.site.internal.service.IWaterMarkConfigService;
import cn.lonsun.site.site.vo.WaterMarkPosition;

import com.mongodb.gridfs.GridFSDBFile;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * @author Hewbing
 * @ClassName: WaterMarkUtil
 * @Description: 水印生成工具
 * @date 2015年10月14日 下午2:24:21
 */
public class WaterMarkUtil {
    private static IWaterMarkConfigService waterMarkConfigService = SpringContextHolder
        .getBean("waterMarkConfigService");
    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder
        .getBean("mongoDbFileServer");

    /**
     * @param in
     * @param siteId
     * @Description 生成水印
     * @author Hewbing
     * @date 2015年10月14日 下午2:50:31
     */
    public static byte[] createWaterMark(InputStream in, Long siteId, String suffix) {
        WaterMarkConfigEO wmc = waterMarkConfigService.getConfigBySiteId(siteId);
        byte[] bt = null;
        //IFileServer fileServer = FileManager.getFileServer();
        byte[] b = null;
        if (!AppUtil.isEmpty(wmc) && wmc.getEnableStatus() == 1) {
            if (wmc.getType() == 0) {
                String color16 = "000000";
                /* 16进制颜色与Color类转换 */
                if (!AppUtil.isEmpty(wmc.getFontColor())){
                    if(wmc.getFontColor().length() >=7){
                        color16 = wmc.getFontColor().substring(1, 7);
                    }
                }
                Color color = new Color(Integer.parseInt(color16, 16));
                /* 调用文字水印生成方法 */
                bt = ImgHander.waterMarkByWord(in, color, wmc.getFontFamily(),
                    wmc.getFontSize(), wmc.getWordContent(),
                    wmc.getTransparency(), wmc.getIsBold(),
                    wmc.getRotate(), wmc.getPosition(), suffix);
            } else if (wmc.getType() == 1) {
                if (!AppUtil.isEmpty(wmc.getPicPath())) {
                    GridFSDBFile iconPic = mongoDbFileServer.getGridFSDBFile(wmc.getPicPath(), null);
                    b = inputStreamToByte(iconPic.getInputStream());
                }
                if (!AppUtil.isEmpty(b)) {
					/* 调用图片水印生成方法 */
                    bt = ImgHander.waterMarkByPic(in, b, wmc.getWidth(),
                        wmc.getHeight(), wmc.getTransparency(),
                        wmc.getRotate(), wmc.getPosition(), suffix);
                } else {
                    bt = inputStreamToByte(in);
                }
            } else {
                bt = inputStreamToByte(in);
            }
        } else {
            bt = inputStreamToByte(in);
        }
        return bt;
    }

    /**
     * @param in
     * @return
     * @Description InputStream转换为byte[]
     * @author Hewbing
     * @date 2015年10月15日 上午10:29:30
     */
    public static byte[] inputStreamToByte(InputStream in) {
        byte[] bt = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[2 << 12];
        int n = 0;
        try {
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            bt = out.toByteArray();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bt;
    }


    /**
     * @param posType
     * @param imgW
     * @param imgH
     * @param iconW
     * @param iconH
     * @return
     * @Description 水印位置
     * @author Hewbing
     * @date 2015年10月14日 上午11:01:21
     */
    public static WaterMarkPosition getPositon(int posType, int imgW, int imgH,
                                               int iconW, int iconH) {
        WaterMarkPosition wmp = new WaterMarkPosition();
        switch (posType) {
            case 1: //左上
                wmp.setWidth(5);
                wmp.setHeight(5);
                break;
            case 2: //上
                wmp.setWidth((imgW - iconW) / 2);
                wmp.setHeight(5);
                break;
            case 3: //右上
                wmp.setWidth(imgW - iconW -5);
                wmp.setHeight(5);
                break;
            case 4: //左
                wmp.setWidth(5);
                wmp.setHeight((imgH - iconH) / 2);
                break;
            case 6: //右
                wmp.setWidth(imgW - iconW -5);
                wmp.setHeight((imgH - iconH) / 2);
                break;
            case 7: //左下
                wmp.setWidth(5);
                wmp.setHeight(imgH - iconH - 5);
                break;
            case 8: //下
                wmp.setWidth((imgW - iconW) / 2);
                wmp.setHeight(imgH - iconH - 5);
                break;
            case 9: //右下
                wmp.setWidth(imgW - iconW - 5);
                wmp.setHeight(imgH - iconH - 5);
                break;
            default: // 中
                wmp.setWidth((imgW - iconW) / 2);
                wmp.setHeight((imgH - iconH) / 2);
                break;
        }
        return wmp;
    }

}

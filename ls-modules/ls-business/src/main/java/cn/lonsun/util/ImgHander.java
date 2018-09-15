package cn.lonsun.util;

import cn.lonsun.site.site.vo.WaterMarkPosition;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.web.multipart.MultipartFile;
import sun.awt.image.GifImageDecoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;

/**
 * @author Hewbing
 * @ClassName: ImgHander
 * @Description: Image processing tools
 * @date 2015年10月10日 下午3:30:03
 */
public class ImgHander {

    private static String PNG = "png";
    private static String JPG = "jpg";
    private static String SWF = "swf";

    /**
     * @param in 输入流
     * @param nw 生成宽度
     * @Description Compression ratio
     * @author Hewbing
     * @date 2015年10月10日 下午3:30:26
     */
    public static void ImgTrans(InputStream in, int nw, int nh, ByteArrayOutputStream out) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > -1 ) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            InputStream stream1 = new ByteArrayInputStream(baos.toByteArray());
            InputStream stream2 = new ByteArrayInputStream(baos.toByteArray());
            BufferedImage bis = ImageIO.read(stream1); // 读取图片
            int w = bis.getWidth();
            int h = bis.getHeight();
            if ((w <= nw || h <= nh) || (nw == 0 && nh == 0)) {
                nw = w;
                nh = h;
            } else if (nw != 0 && nh == 0) {
                nh = (nw * h) / w;
            } else if (nw == 0 && nh != 0) {
                nw = (nh * w) / h;
            }
            Thumbnails.of(stream2).size(nw, nh).keepAspectRatio(false).toOutputStream(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        try {
//            /*
//             * AffineTransform 类表示 2D 仿射变换，它执行从 2D 坐标到其他 2D
//			 * 坐标的线性映射，保留了线的“直线性”和“平行性”。可以使用一系 列平移、缩放、翻转、旋转和剪切来构造仿射变换。
//			 */
//            AffineTransform transform = new AffineTransform();
//            BufferedImage bis = ImageIO.read(in); // 读取图片
//            int w = bis.getWidth();
//            int h = bis.getHeight();
//            double sx = (double) nw / w;
//            double sy = (double) nh / h;
//            if ((w <= nw || h <= nh) || (nw == 0 && nh == 0)) {
//                sx = sy = 1.0D;
//                nw = w;
//                nh = h;
//            } else if (nw != 0 && nh == 0) {
//                nh = (nw * h) / w;
//                sx = (double) nw / w;
//                sy = (double) nh / h;
//            } else if (nw == 0 && nh != 0) {
//                nw = (nh * w) / h;
//                sx = (double) nw / w;
//                sy = (double) nh / h;
//            }
//
//            transform.setToScale(sx, sy);
//            // 将此变换设置为缩放变换。
//            /*
//             * AffineTransformOp类使用仿射转换来执行从源图像或 Raster 中 2D 坐标到目标图像或 Raster 中 2D
//			 * 坐标的线性映射。所使用的插值类型由构造方法通过 一个 RenderingHints 对象或通过此类中定义的整数插值类型之一来指定。
//			 * 如果在构造方法中指定了 RenderingHints 对象，则使用插值提示和呈现
//			 * 的质量提示为此操作设置插值类型。要求进行颜色转换时，可以使用颜色 呈现提示和抖动提示。 注意，务必要满足以下约束：源图像与目标图像
//			 * 必须不同。 对于 Raster 对象，源图像中的 band 数必须等于目标图像中 的 band 数。
//			 */
//            AffineTransformOp ato = new AffineTransformOp(transform, null);
//            BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
//            /*
//             * TYPE_3BYTE_BGR 表示一个具有 8 位 RGB 颜色分量的图像， 对应于 Windows 风格的 BGR
//			 * 颜色模型，具有用 3 字节存 储的 Blue、Green 和 Red 三种颜色。
//			 */
//            ato.filter(bis, bid);
//            ImageIO.write(bid, "png", out);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * @param in 输入流
     * @param nw 生成宽度
     * @Description 此类依然存在问题，对PNG的图像处理好会变的比原图更大。
     * @author doocal
     * @date 2015年10月10日 下午3:30:26
     */
    public static void ImgTrans(InputStream in, int nw, int nh, ByteArrayOutputStream out, String suffix) {
        ImgTrans(in, nw, nh, out);
//        try {
//            /*
//             * AffineTransform 类表示 2D 仿射变换，它执行从 2D 坐标到其他 2D
//			 * 坐标的线性映射，保留了线的“直线性”和“平行性”。可以使用一系 列平移、缩放、翻转、旋转和剪切来构造仿射变换。
//			 */
//            Image bis = ImageIO.read(in); // 读取图片
//            int w = bis.getWidth(null);
//            int h = bis.getHeight(null);
//            double sx = (double) nw / w;
//            double sy = (double) nh / h;
//            if ((w <= nw || h <= nh) || (nw == 0 && nh == 0)) {
//                sx = sy = 1.0D;
//                nw = w*2;
//                nh = h*2;
//            } else if (nw != 0 && nh == 0) {
//                nh = (nw * h) / w;
//                sx = (double) nw / w;
//                sy = (double) nh / h;
//            } else if (nw == 0 && nh != 0) {
//                nw = (nh * w) / h;
//                sx = (double) nw / w;
//                sy = (double) nh / h;
//            }
//
//            //
//            BufferedImage bid = new BufferedImage(nw, nh, BufferedImage.TYPE_3BYTE_BGR);
//            Graphics2D g2d = bid.createGraphics();
//            if ("png".equals(suffix)) {
//                bid = g2d.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
//            }
//            g2d = bid.createGraphics();
//            g2d.drawImage(bis, 0, 0, nw, nh, null);
//            if ("jpg".equals(suffix)) {
//                //一定程序解决失真的问题
//                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bid);
//                jep.setQuality(1f, true);
//                encoder.encode(bid, jep);
//            }
//            ImageIO.write(bid, suffix, out);
//
//            g2d.dispose();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
    /**
     * @param in 输入流
     * @param nw 生成宽度
     * @Description 此类依然存在问题，对PNG的图像处理好会变的比原图更大。
     * @author doocal
     * @date 2015年10月10日 下午3:30:26
     */
    public static void ImgTransWithOutWater(InputStream in, int nw, int nh, ByteArrayOutputStream out, String suffix) {

        try {
            if("gif".equals(suffix)) {//gif单独处理，不能转换为BufferedImage，
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > -1 ) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            }else{
                BufferedImage bid = ImageIO.read(in); // 读取图片
                int w = bid.getWidth();
                int h = bid.getHeight();
                if ((w <= nw || h <= nh) || (nw == 0 && nh == 0)) {
                    nw = w;
                    nh = h;
                } else if (nw != 0 && nh == 0) {
                    nh = (nw * h) / w;
                } else if (nw == 0 && nh != 0) {
                    nw = (nh * w) / h;
                }

                if ("jpg".equals(suffix)) {
                    //一定程序解决失真的问题
                    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                    JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bid);
                    jep.setQuality(1f, true);
                    encoder.encode(bid, jep);
                }
                ImageIO.write(bid, suffix, out);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param in
     * @param wordColor
     * @param fontFamily
     * @param fontSize
     * @param markWord
     * @param alpha
     * @param isBold
     * @param rotate
     * @param pos
     * @return
     * @Description Generate text watermarking
     * @author Hewbing
     * @date 2015年10月15日 上午9:07:24
     */
    public static byte[] waterMarkByWord(InputStream in, Color wordColor,
                                         String fontFamily, Integer fontSize, String markWord, Float alpha,
                                         Integer isBold, Integer rotate, Integer pos, String suffix) {
        byte[] bt = null;
        try {
            // 读取原图片信息
            Image srcImg = ImageIO.read(in);
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);

            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = bufImg.createGraphics();
            if ("png".equals(suffix)) {
                bufImg = g.getDeviceConfiguration().createCompatibleImage(srcImgWidth, srcImgHeight, Transparency.TRANSLUCENT);
            }
            g = bufImg.createGraphics();

            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            // 设置水印旋转角度
            if (rotate == null) {
                rotate = 0;
            }
            g.rotate(Math.toRadians(rotate), (double) bufImg.getWidth() / 2, (double) bufImg.getHeight() / 2);
            // 透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // Font font = new Font("Courier New", Font.PLAIN, 12);
            Font font;
            if (fontSize == null || fontSize == 0) {
                fontSize = 14;
            }
            if (isBold == null || isBold == 0) {
                //font = new Font("宋体", Font.BOLD, fontSize);
                font = FontUtil.buildFont(fontFamily, Font.PLAIN, fontSize);
            } else {
                //font = new Font(fontFamily, Font.PLAIN, fontSize);
                font = FontUtil.buildFont(fontFamily, Font.BOLD, fontSize);
            }
            g.setColor(wordColor); // 根据图片的背景设置水印颜色
            g.setFont(font);
            Double textLen = getTextWidth(g, markWord);
            Double textHeight = getTextHeight(g);
            WaterMarkPosition wmp = WaterMarkUtil.getPositon(pos, srcImgWidth, srcImgHeight, textLen.intValue(), textHeight.intValue());
            if (textLen < (srcImgWidth * 0.6)) {
                g.drawString(markWord, wmp.getWidth(), wmp.getHeight());
            }
            g.dispose();
            // 输出图片
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            if ("jpg".equals(suffix)) {
                //一定程序解决失真的问题
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bufImg);
                jep.setQuality((float) 1.0, true);
                encoder.encode(bufImg, jep);
            }

            ImageIO.write(bufImg, suffix, out);
            bt = out.toByteArray();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bt;
    }

    /**
     * @param posType
     * @return
     * @Description 水印位置
     * @author Hewbing
     * @date 2015年10月14日 上午11:01:21
     */
    public static Positions getPosition(int posType) {
        switch (posType) {
            case 1: //左上
                return Positions.TOP_LEFT;
            case 2: //上
                return Positions.TOP_CENTER;
            case 3: //右上
                return Positions.TOP_RIGHT;
            case 4: //左
                return Positions.CENTER_LEFT;
            case 6: //右
                return Positions.CENTER_RIGHT;
            case 7: //左下
                return Positions.BOTTOM_LEFT;
            case 8: //下
                return Positions.BOTTOM_CENTER;
            case 9: //右下
                return Positions.BOTTOM_RIGHT;
            default: // 中
                return Positions.CENTER;
        }
    }

    /**
     * 获取水印文字总长度
     *
     * @param waterMarkContent 水印的文字
     * @param g
     * @return 水印文字总长度
     */
    @Deprecated
    private static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).stringWidth(waterMarkContent);
    }

    /**
     * 获取对应的文字所占有的长度
     * @param g2d
     * @return
     * @parm
     * @exception
     */
    public static double getTextWidth(Graphics2D g2d, String text) {
        // 设置大字体
        FontRenderContext context = g2d.getFontRenderContext();
        // 获取字体的像素范围对象
        Rectangle2D stringBounds = g2d.getFont().getStringBounds(text, context);
        double fontWidth = stringBounds.getWidth();
        return fontWidth;
    }

    /**
     * 获取对应字体的文字的高度
     * @param g2d
     * @return
     * @parm
     * @exception
     */
    public static double getTextHeight(Graphics2D g2d) {
        // 设置大字体
        FontRenderContext context = g2d.getFontRenderContext();
        // 获取字体的像素范围对象
        Rectangle2D stringBounds = g2d.getFont().getStringBounds("w", context);
        double fontWidth = stringBounds.getWidth();
        return fontWidth;
    }

    /**
     * @param in
     * @param iconByte
     * @param wm_width
     * @param wm_height
     * @param alpha
     * @param rotate
     * @param pos
     * @return
     * @Description Generate image watermarking
     * @author Hewbing
     * @date 2015年10月15日 上午9:07:49
     */
    public static byte[] waterMarkByPic(InputStream in, byte[] iconByte,
                                        Integer wm_width, Integer wm_height, Float alpha, Integer rotate,
                                        Integer pos, String suffix) {
        byte[] btArr = null;
        try {
            Image srcImg = ImageIO.read(in);
            int srcImgWidth = srcImg.getWidth(null);
            int srcImgHeight = srcImg.getHeight(null);

            // 得到Image对象。
            ByteArrayInputStream is = new ByteArrayInputStream(iconByte);
            Image img = ImageIO.read(is);
            if (wm_width == null || wm_width == 0)
                wm_width = img.getWidth(null);
            if (wm_height == null || wm_height == 0)
                wm_height = img.getHeight(null);

            //compare size
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight,
                BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = bufImg.createGraphics();

            if ("png".equals(suffix)) {
                bufImg = g.getDeviceConfiguration().createCompatibleImage(srcImgWidth, srcImgHeight, Transparency.TRANSLUCENT);
            }
            g = bufImg.createGraphics();

            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            // 旋转角度
            if (rotate == null)
                rotate = 0;
            g.rotate(Math.toRadians(rotate), (double) bufImg.getWidth() / 2, (double) bufImg.getHeight() / 2);
            // 透明度
            if (alpha == null)
                alpha = 1F;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            // 获取水印图片
            // 将小图片绘到大图片上。
            // g.drawImage(img,x,y,width,height,null);
            // x,y水印的位置；width,height水印的大小。
            WaterMarkPosition wmp = WaterMarkUtil.getPositon(pos, srcImgWidth, srcImgHeight, wm_width, wm_height);
//			g.drawImage(img, wmp.getWidth(), wmp.getHeight(), wm_width,
//					wm_height, null);
            if (wm_width <= srcImgWidth * 0.8 || wm_height <= srcImgHeight * 0.8) {
                g.drawImage(img, wmp.getWidth(), wmp.getHeight(), wm_width, wm_height, null);
            }
            g.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            if ("jpg".equals(suffix)) {
                //一定程序解决失真的问题
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bufImg);
                jep.setQuality((float) 1.0, true);
                encoder.encode(bufImg, jep);
            }

            ImageIO.write(bufImg, suffix, out);
            btArr = out.toByteArray();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return btArr;
    }

}
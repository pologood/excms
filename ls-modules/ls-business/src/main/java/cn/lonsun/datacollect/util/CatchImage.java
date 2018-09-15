package cn.lonsun.datacollect.util;

import cn.lonsun.common.util.AppUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gu.fei
 * @version 2016-1-29 13:52
 */
public class CatchImage {

    // 编码
    private static final String ECODING = "UTF-8";
    // 获取img标签正则  
//    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>/ig";

    public static final Pattern pattern = Pattern.compile("<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final int connet_timeout = 500000;

    private static final int read_timeout = 500000;

    public static void main(String[] args) throws Exception {
        String str = "<img style=\"border-left-width: 0px; border-right-width: 0px; border-bottom-width: 0px; border-top-width: 0px\" alt=\"\" oldsrc=\"W020160322307201077737.jpg\" complete=\"complete\" src=\"./W020160322307201077737.jpg\" />";
        List<String> list = CatchImage.getImageSrc(str);
        for (String url : list) {
            System.out.println("url:" + url);
        }
    }

    /**
     * 获取HTML内容
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String getHTML(String url) throws Exception {
        URL uri = new URL(url);
        URLConnection connection = uri.openConnection();
        InputStream in = connection.getInputStream();
        byte[] buf = new byte[1024];
        int length = 0;
        StringBuffer sb = new StringBuffer();
        while ((length = in.read(buf, 0, buf.length)) > 0) {
            sb.append(new String(buf, ECODING));
        }
        in.close();
        return sb.toString();
    }

    /**
     * 获取ImageUrl地址
     * @param html
     * @return
     */
    public static List<String> getImageUrl(String html) {
        Matcher matcher = pattern.matcher(html);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            if (group.startsWith("'")) {
                list.add(group.substring(1, group.indexOf("'", 1)));
            } else if (group.startsWith("\"")) {
                list.add(group.substring(1, group.indexOf("\"", 1)));
            } else {
                list.add(group.split("\\s")[0]);
            }
        }
        return list;
    }

    /**
     * 获取ImageSrc地址
     *
     * @param html
     * @return
     */
    public static List<String> getImageSrc(String html) {
        List<String> listImg = getImageUrl(html);
        return listImg;
    }

    /**
     * 获取ImageSrc地址
     *
     * @param listImageUrl
     * @return
     */
    public static List<String> getImageSrc(List<String> listImageUrl) {
        List<String> listImgSrc = new ArrayList<String>();
        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>").matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group(1));
            }
        }
        return listImgSrc;
    }

    /**
     * 下载图片
     *
     * @param listImgSrc
     */
    public static void Download(List<String> listImgSrc) {
        try {
            for (String url : listImgSrc) {
                String imageName = url.substring(url.lastIndexOf("/") + 1, url.length());
                URL uri = new URL(url);
                InputStream in = uri.openStream();
                FileOutputStream fo = new FileOutputStream(new File(imageName));
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                in.close();
                fo.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片
     *
     * @param url
     */
    public static InputStream getImageStream(String url) {
        try {
            URL uri = new URL(url);
            URLConnection connection = uri.openConnection();
            connection.setConnectTimeout(connet_timeout);
            connection.setReadTimeout(read_timeout);
            InputStream in = connection.getInputStream();
            return in;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取图片
     *
     * @param url
     */
    public static byte[] getImageBytes(String url) {
        if(AppUtil.isEmpty(url)) {
            return null;
        }
        try {
            URL uri = new URL(url);
            URLConnection connection = uri.openConnection();
            connection.setConnectTimeout(connet_timeout);
            connection.setReadTimeout(read_timeout);
            InputStream in = connection.getInputStream();
            byte[] bytes = inputStream2Byte(in);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param in
     * @return
     * @Description InputStream转换为byte[]
     * @author Hewbing
     * @date 2015年10月15日 上午10:29:30
     */
    public static byte[] inputStream2Byte(InputStream in) {
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

}
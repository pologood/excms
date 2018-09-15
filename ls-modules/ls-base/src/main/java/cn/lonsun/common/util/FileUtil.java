/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.lonsun.common.util;

import cn.lonsun.core.util.ContextHolderUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件上传工具类
 *
 * @author Dzl
 */
public class FileUtil {

    private final static Logger log = LoggerFactory.getLogger(FileUtil.class);
    private static String message;
    //上传文件保存路径
    public static String path = "/upload/";
    //定义可以上传文件的后缀数组,默认"*"，代表所有
    public static String[] filePostfixs = {"*"};
    public static String[] typeImages = {"gif", "jpeg", "png", "jpg", "tif", "bmp"};
    public static String[] typeDocs = {"doc", "docx"};
    public static String[] typeOthers = {"doc", "docx", "wps", "xls", "xlsx", "zip", "rar", "txt", "pdf"};

    //上传文件的最大长度
    public static long maxFileSize = 1024 * 1024 * 200L; //200M
    //一次读取多少字节
    public static int bufferSize = 1024 * 8;

    private final static void init() {
        if (bufferSize > Integer.MAX_VALUE) {
            bufferSize = 1024 * 8;
        } else if (bufferSize < 8) {
            bufferSize = 8;
        }
        if (maxFileSize < 1) {
            maxFileSize = 1024 * 1024 * 1024 * 2L;
        } else if (maxFileSize > Long.MAX_VALUE) {
            maxFileSize = 1024 * 1024 * 1024 * 2L;
        }
    }


    /**
     * 文件下载
     *
     * @param path 文件路径
     * @throws Exception
     */
    public static void downLoadFile(String path, String name, HttpServletResponse response) throws Exception {
        HttpServletRequest req = ContextHolderUtils.getRequest();
        /*String filepath = req.getRequestURI();
        int index = filepath.indexOf(path);
        if(index > 0) {
            filepath = filepath.substring(index + path.length());
        }
        try {
            filepath = UriUtils.decode(filepath, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            log.error(String.format("解释文件路径失败，URL地址为%s", filepath), e1);
        }*/
        String rootPath = req.getSession().getServletContext().getRealPath("/");
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        File file = new File(rootPath + path);
        Exception exception = null;
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        try {
            //FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());
            //response.setHeader("Content-Type", "application/octet-stream");
            long fileLength = file.length();
            response.setContentType("application/x-msdownload;");
            //response.setHeader("Content-disposition", "attachment; filename="+new String(name.getBytes(),"iso-8859-1"));
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
            //System.out.println("================="+name);
            response.setHeader("Content-Length", String.valueOf(fileLength));

            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            exception = new FileNotFoundException("请求的文件不存在");
            if (log.isWarnEnabled()) {
                log.warn(String.format("请求的文件%s不存在", file), e);
            }
            throw exception;
        } catch (IOException e) {
            exception = new IOException("输出文件出错，请联系管理员", e);
            if (log.isErrorEnabled()) {
                log.error(String.format("输出文件%s出错", file), e);
            }
            throw exception;
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        return;
    }

    /**
     * <b>function:</b>通过输入流参数上传文件
     *
     * @param fileName 文件名称
     * @param path     保存路径
     * @param is       上传的文件的输入流
     * @return 是否上传成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 9, 2010 11:22:47 PM
     */
    public static UploadState upload4Stream(String fileName, String path, InputStream is) throws Exception {
        init();
        UploadState state = UploadState.UPLOAD_FAILURE;
        FileOutputStream fos = null;

        try {
            path = getDoPath(path);
            mkDir(path);
            fos = new FileOutputStream(path + fileName);

            byte[] buffer = new byte[bufferSize];
            int len = 0;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            state = UploadState.UPLOAD_SUCCSSS;
        } catch (FileNotFoundException e) {
            state = UploadState.UPLOAD_NOTFOUND;
            throw e;
        } catch (IOException e) {
            state = UploadState.UPLOAD_FAILURE;
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
        return state;
    }

    /**
     * <b>function:</b>上传文件
     *
     * @param fileName 文件名称
     * @param path     保存路径
     * @param file     上传的文件
     * @return 是否上传成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 9, 2010 11:33:27 PM
     */
    public static UploadState upload4Stream(String fileName, String path, File file) throws Exception {
        init();
        UploadState state = UploadState.UPLOAD_FAILURE;
        FileInputStream fis = null;
        try {
            long size = file.length();
            if (size <= 0) {
                state = UploadState.UPLOAD_ZEROSIZE;
            } else {
                if (size <= maxFileSize) {
                    fis = new FileInputStream(file);
                    state = upload4Stream(fileName, path, fis);
                } else {
                    state = UploadState.UPLOAD_OVERSIZE;
                }
            }
        } catch (FileNotFoundException e) {
            state = UploadState.UPLOAD_NOTFOUND;
            throw e;
        } catch (IOException e) {
            state = UploadState.UPLOAD_FAILURE;
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return state;
    }

    /**
     * <b>function:</b>通过数组进行验证文件类型上传
     *
     * @param fileName   文件名称
     * @param path       文件路径
     * @param file       文件
     * @param allowTypes 文件后缀、类型数组
     * @return 返回是否上传成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 10, 2010 3:39:34 PM
     */
    public static UploadState upload4Stream(String fileName, String path, File file, String[] allowTypes) throws Exception {
        UploadState state = UploadState.UPLOAD_FAILURE;
        if (validTypeByName(fileName, allowTypes)) {
            state = upload4Stream(fileName, path, file);
        } else {
            state = UploadState.UPLOAD_TYPE_ERROR;
        }
        return state;
    }

    /**
     * <b>function:</b>通过数组进行验证文件类型上传
     *
     * @param fileName   文件名称
     * @param path       文件路径
     * @param fs         文件输入流
     * @param allowTypes 文件后缀、类型数组
     * @return 返回是否上传成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 10, 2010 3:43:30 PM
     */
    public static UploadState upload4Stream(String fileName, String path, InputStream fs, String[] allowTypes) throws Exception {
        UploadState state = UploadState.UPLOAD_FAILURE;
        if (validTypeByName(fileName, allowTypes)) {
            state = upload4Stream(fileName, path, fs);
        } else {
            state = UploadState.UPLOAD_TYPE_ERROR;
        }
        return state;
    }

    /**
     * <b>function:</b> 利用FileUtils上传文件；其中maxFileSize是限制上传文件的大小
     *
     * @param fileName 文件名称
     * @param path     保存路径
     * @param file     文件
     * @return 是否上传成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 9, 2010 11:49:15 PM
     */
    public static UploadState upload4CopyFile(String fileName, String path, File file) throws Exception {
        init();
        UploadState state = UploadState.UPLOAD_FAILURE;
        if (file.length() <= maxFileSize) {
            //path = getDoPath(path);
            log.info("文件的上传路径是:" + path);
            log.info("上传的文件是:" + fileName);
            mkDir(path);
            File savedFile = new File(path, fileName);
            FileUtils.copyFile(file, savedFile);

            log.info("上传成功");
            state = UploadState.UPLOAD_SUCCSSS;
        } else {
            log.error("文件超出大小");
            state = UploadState.UPLOAD_OVERSIZE;
        }
        return state;
    }

    /**
     * <b>function:</b>上传指定文件类型的文件
     *
     * @param fileName   文件名
     * @param path       路径
     * @param file       文件
     * @param allowTypes 类型、后缀数组
     * @return 成功上传的文件名
     * @throws Exception
     * @author hoojo
     * @createDate Oct 10, 2010 12:30:09 PM
     */
    public static UploadState upload4CopyFile(String fileName, String path, File file, String[] allowTypes) throws Exception {
        UploadState state = UploadState.UPLOAD_FAILURE;
        if (validTypeByName(fileName, allowTypes)) {
            log.info("上传文件验证通过,fileName=" + fileName);
            state = upload4CopyFile(fileName, path, file);
        } else {
            log.error("不允许上传的文件类型");
            state = UploadState.UPLOAD_TYPE_ERROR;
        }
        return state;
    }

    /**
     * <b>function:</b> 根据文件名和类型数组验证文件类型是否合法，flag是否忽略大小写
     *
     * @param fileName   文件名
     * @param allowTypes 类型数组
     * @param flag       是否获得大小写
     * @return 是否验证通过
     * @author hoojo
     * @createDate Oct 10, 2010 11:54:54 AM
     */
    public static boolean validTypeByName(String fileName, String[] allowTypes, boolean flag) {
        String suffix = getType(fileName);
        boolean valid = false;
        if (allowTypes.length > 0 && "*".equals(allowTypes[0])) {
            valid = true;
        } else {
            for (String type : allowTypes) {
                if (flag) {//不区分大小写后缀
                    if (suffix != null && suffix.equalsIgnoreCase(type)) {
                        valid = true;
                        break;
                    }
                } else {//严格区分大小写
                    if (suffix != null && suffix.equals(type)) {
                        valid = true;
                        break;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * <b>function:</b>根据文件名称和类型数组验证文件类型是否合法
     *
     * @param fileName   文件名
     * @param allowTypes 文件类型数组
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 10:27:17 AM
     */
    public static boolean validTypeByName(String fileName, String[] allowTypes) {
        return validTypeByName(fileName, allowTypes, true);
    }

    /**
     * <b>function:</b> 根据后缀和类型数组验证文件类型是否合法，flag是否区分后缀大小写，true严格大小写
     *
     * @param suffix     后缀名
     * @param allowTypes 文件类型数组
     * @param flag       是否区分大小写
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 12:00:10 PM
     */
    public static boolean validTypeByPostfix(String suffix, String[] allowTypes, boolean flag) {
        boolean valid = false;
        if (allowTypes.length > 0 && "*".equals(allowTypes[0])) {
            valid = true;
        } else {
            for (String type : allowTypes) {
                if (flag) {//不区分大小写后缀
                    if (suffix != null && suffix.equalsIgnoreCase(type)) {
                        valid = true;
                        break;
                    }
                } else {//严格区分大小写
                    if (suffix != null && suffix.equals(type)) {
                        valid = true;
                        break;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * <b>function:</b>根据文件后缀名和类型数组，验证文件类型是否合法
     *
     * @param suffix     后缀名
     * @param allowTypes 类型数组
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 10:25:32 AM
     */
    public static boolean validTypeByPostfix(String suffix, String[] allowTypes) {
        return validTypeByPostfix(suffix, allowTypes, true);
    }

    /**
     * <b>function:</b>验证当前后缀、文件类型是否是图片类型
     * typeImages 可以设置图片类型
     *
     * @param suffix 验证文件的后缀
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 12:17:18 PM
     */
    public static boolean validTypeByPostfix4Images(String suffix) {
        return validTypeByPostfix(suffix, typeImages);
    }

    /**
     * <b>function:</b>验证当前后缀、文件类型是否是非图片类型（常用办公文件类型）
     * typeOthers 可以设置文件类型
     *
     * @param suffix 验证文件的后缀
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 12:18:18 PM
     */
    public static boolean validTypeByPostfix4Others(String suffix) {
        return validTypeByPostfix(suffix, typeOthers);
    }

    /**
     * <b>function:</b>验证当前文件名、文件类型是否是图片类型
     * typeImages 可以设置图片类型
     *
     * @param fileName 验证文件的名称
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 12:19:18 PM
     */
    public static boolean validTypeByName4Images(String fileName) {
        return validTypeByName(fileName, typeImages);
    }

    /**
     * <b>function:</b>验证当前文件名称、文件类型是否是非图片类型（常用办公文件类型）
     * typeOthers 可以设置文件类型
     *
     * @param fileName 验证文件的名称
     * @return 是否合法
     * @author hoojo
     * @createDate Oct 10, 2010 12:21:22 PM
     */
    public static boolean validTypeByName4Others(String fileName) {
        return validTypeByName(fileName, typeOthers);
    }

    /**
     * <b>function:</b>传递一个路径和文件名称，删除该文件
     *
     * @param fileName 文件名称
     * @param path     路径
     * @return 是否删除成功
     * @author hoojo
     * @createDate Oct 10, 2010 10:47:57 AM
     */
    public static boolean removeFile(String fileName, String path) {
        boolean flag = false;
        if (isFileExist(fileName, path)) {
            File file = new File(getDoPath(path) + fileName);
            flag = file.delete();
        }
        return flag;
    }

    /**
     * <b>function:</b>删除当前文件
     *
     * @param file 要删除的文件
     * @return 是否删除成功
     * @author hoojo
     * @createDate Oct 10, 2010 10:49:54 AM
     */
    public static boolean removeFile(File file) {
        boolean flag = false;
        if (file != null && file.exists()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * <b>function:</b>删除某个文件
     *
     * @param path 传递该文件路径
     * @return 删除是否成功
     * @author hoojo
     * @createDate Oct 12, 2010 9:33:06 PM
     */
    public static boolean removeFile(String path) {
        return removeFile(new File(path));
    }

    /**
     * <b>function:</b>删除当前文件下面所有文件
     *
     * @param file File 要删除的文件夹下面文件的文件对象
     * @return 是否删除成功，如果有一个文件删除失败，将返回false
     * @author hoojo
     * @createDate Oct 12, 2010 9:27:33 PM
     */
    public static boolean removeFile4Dir(File file) {
        boolean flag = false;
        if (file != null && file.exists() && file.isDirectory()) {
            File[] allFile = file.listFiles();
            for (File f : allFile) {
                flag = f.delete();
                if (!flag) {
                    System.err.println("删除文件" + f.getAbsolutePath() + "出错了！");
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * <b>function:</b>删除当前目录下所有文件
     *
     * @param path 目录、路径
     * @return 是否成功
     * @author hoojo
     * @createDate Oct 12, 2010 9:34:41 PM
     */
    public static boolean removeFile4Dir(String path) {
        return removeFile4Dir(new File(path));
    }

    /**
     * <b>function:</b>删除某个文件夹下的所有文件（除目录），包含子文件夹的文件
     *
     * @param file 即将删除文件夹对象
     * @return 是否删除成功
     * @author hoojo
     * @createDate Oct 12, 2010 9:30:01 PM
     */
    public static boolean removeAllFile4Dir(File file) {
        boolean flag = false;
        if (file != null && file.exists() && file.isDirectory()) {
            File[] allFile = file.listFiles();
            for (File f : allFile) {
                if (!f.isDirectory()) {
                    flag = f.delete();
                } else {
                    flag = removeAllFile4Dir(f);
                }
                if (!flag) {
                    System.err.println("删除文件" + f.getAbsolutePath() + "出错了！");
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * <b>function:</b>删除某个目录下所有文件（不包含文件夹，包含文件夹下的文件）
     *
     * @param path
     * @return
     * @author hoojo
     * @createDate Oct 12, 2010 9:36:17 PM
     */
    public static boolean removeAllFile4Dir(String path) {
        return removeAllFile4Dir(new File(path));
    }

    /**
     * <b>function:</b> 传入一个文件名，得到这个文件名称的后缀
     *
     * @param fileName 文件名
     * @return 后缀名
     * @author hoojo
     * @createDate Oct 9, 2010 11:30:46 PM
     */
    public static String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index);//后缀
            return suffix;
        } else {
            return null;
        }
    }

    /**
     * <b>function:</b>和文件后缀一样，不同的是没有“.”
     *
     * @param fileName 文件名称
     * @return
     * @author hoojo
     * @createDate Oct 10, 2010 2:42:43 PM
     */
    public static String getType(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index + 1).toLowerCase();//后缀
            return suffix;
        } else {
            return null;
        }
    }

    /**
     * <b>function:</b> 传递一个文件名称和一个新名称，组合成一个新的带后缀文件名
     * 当传递的文件名没有后缀，会添加默认的后缀
     *
     * @param fileName   文件名称
     * @param newName    新文件名称
     * @param nullSuffix 为没有后缀的文件所添加的后缀;eg:txt
     * @return String 文件名称
     * @author hoojo
     * @createDate Oct 9, 2010 10:53:06 PM
     */
    public static String getNewFileName(String fileName, String newName, String nullSuffix) {
        String suffix = getSuffix(fileName);
        if (suffix != null) {
            newName += suffix;
        } else {
            newName = newName.concat(".").concat(nullSuffix);
        }
        return newName;
    }

    /**
     * <b>function:</b> 利用uuid产生一个随机的name
     *
     * @param fileName 带后缀的文件名称
     * @return String 随机生成的name
     * @author hoojo
     * @createDate Oct 9, 2010 10:45:27 PM
     */
    public static String getRandomName(String fileName) {
        String randomName = UUID.randomUUID().toString();
        return getNewFileName(fileName, randomName, "txt");
    }

    /**
     * <b>function:</b> 用当前日期、时间和1000以内的随机数组合成的文件名称
     *
     * @param fileName 文件名称
     * @return 新文件名称
     * @author hoojo
     * @createDate Oct 9, 2010 11:01:47 PM
     */
    public static String getNumberName(String fileName) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMddhhmmss");
        int rand = new Random().nextInt(1000);
        String numberName = format.format(new Date()) + rand;
        return getNewFileName(fileName, numberName, "txt");
    }

    /**
     * <b>function:</b>判断该文件是否存在
     *
     * @param fileName 文件名称
     * @param path     目录
     * @return 是否存在
     * @author hoojo
     * @createDate Oct 10, 2010 12:00:44 AM
     */
    public static boolean isFileExist(String fileName, String path) {
        File file = new File(getDoPath(path) + fileName);
        return file.exists();
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * <b>function:</b>返回可用的文件名
     *
     * @param fileName 文件名
     * @param path     路径
     * @return 可用文件名
     * @author hoojo
     * @createDate Oct 10, 2010 1:02:45 AM
     */
    public static String getBracketFileName(String fileName, String path) {
        return getBracketFileName(fileName, fileName, path, 1);
    }


    /**
     * <b>function:</b>递归处理文件名称，直到名称不重复（对文件名、目录文件夹都可用）
     * eg: a.txt --> a(1).txt
     * 文件夹upload--> 文件夹upload(1)
     *
     * @param fileName 文件名称
     * @param path     文件路径
     * @param num      累加数字，种子
     * @return 返回没有重复的名称
     * @author hoojo
     * @createDate Oct 10, 2010 12:56:27 AM
     */
    public static String getBracketFileName(String fileName, String bracketName, String path, int num) {
        boolean exist = isFileExist(bracketName, path);
        if (exist) {
            int index = fileName.lastIndexOf(".");
            String suffix = "";
            bracketName = fileName;
            if (index != -1) {
                suffix = fileName.substring(index);
                bracketName = fileName.substring(0, index);
            }
            bracketName += "(" + num + ")" + suffix;
            num++;
            bracketName = getBracketFileName(fileName, bracketName, path, num);
        }
        return bracketName;
    }

    /**
     * <b>function:</b>处理后的系统文件路径
     *
     * @param path 文件路径
     * @return 返回处理后的路径
     * @author hoojo
     * @createDate Oct 10, 2010 12:49:31 AM
     */
    public static String getDoPath(String path) {
        path = path.replace("\\", "/");
        String lastChar = path.substring(path.length() - 1);
        if (!"/".equals(lastChar)) {
            path += "/";
        }
        return path;
    }

    /**
     * <b>function:</b> 创建指定的path路径目录
     *
     * @param path 目录、路径
     * @return 是否创建成功
     * @throws Exception
     * @author hoojo
     * @createDate Oct 9, 2010 11:03:49 PM
     */
    public static boolean mkDir(String path) throws Exception {
        File file = null;
        try {
            file = new File(path);
            if (!file.exists()) {
                return file.mkdirs();
            }
        } catch (RuntimeException e) {
            throw e;
        } finally {
            file = null;
        }
        return false;
    }

    /**
     * 格式化文件大小
     * TODO
     *
     * @param fileSize(单位byte)
     * @return
     * @author zhusy
     */
    public static String formatSize(long fileSize) {

        long result = fileSize / 1024;
        if (0 == result) {
            return fileSize + "B";
        }
        if (result > 1000) {
            //以M为单位显示,并保留两位有效数字
            return String.valueOf(new BigDecimal(((double) fileSize) / (1024 * 1024)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) + "M";
        } else {
            return result + "K";//以K为单位显示
        }
    }

    /**
     * <b>function:</b> 获取文件大小
     *
     * @param filename 完整路径+文件 (物理路径)
     * @return 返回文件大小 ,返回0为异常情况
     */
    public static Long getFileSize(String filename) {
        return getFileSize(filename, null);
    }

    /**
     * <b>function:</b> 获取文件大小
     *
     * @param filename 文件
     * @param filepath 目录、路径 (物理路径)
     * @return 返回文件大小 ,返回0为异常情况
     */
    public static Long getFileSize(String filename, String filepath) {
        FileChannel fc = null;
        Long filesize = 0L;
        File f;
        try {
            if (filepath == null) {
                f = new File(filepath, filename);
            } else {
                f = new File(filename);
            }
            if (f.exists() && f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                fc = fis.getChannel();
                filesize = fc.size();
            } else {
                log.info("file doesn't exist or is not a file");
            }
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (null != fc) {
                try {
                    fc.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return filesize;

    }


    /**
     * 创建缩略图
     * @param
     * @param
     * @throws IOException
     */
    /*public static void writeBrevityPic(File file, float width, float height) throws IOException {
        Image src = javax.imageio.ImageIO.read(file); // 构造Image对象
        int old_w = src.getWidth(null); // 得到源图宽
        int old_h = src.getHeight(null);
        int new_w = 0;
        int new_h = 0; // 得到源图长
        float tempdouble;
        if (old_w >= old_h) {
            tempdouble = old_w / width;
        } else {
            tempdouble = old_h / height;
        }

        if (old_w >= width || old_h >= height) { // 如果文件小于锁略图的尺寸则复制即可
            new_w = Math.round(old_w / tempdouble);
            new_h = Math.round(old_h / tempdouble);// 计算新图长宽
            while (new_w > width && new_h > height) {
                if (new_w > width) {
                    tempdouble = new_w / width;
                    new_w = Math.round(new_w / tempdouble);
                    new_h = Math.round(new_h / tempdouble);
                }
                if (new_h > height) {
                    tempdouble = new_h / height;
                    new_w = Math.round(new_w / tempdouble);
                    new_h = Math.round(new_h / tempdouble);
                }
            }
            BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(src, 0, 0, new_w, new_h, null); // 绘制缩小后的图
            FileOutputStream newimage = new FileOutputStream(file); // 输出到文件流
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(tag);
            param.setQuality((float) (100 / 100.0), true);// 设置图片质量,100最大,默认70
            encoder.encode(tag, param);
            encoder.encode(tag); // 将JPEG编码
            newimage.close();
        }
    }*/

    /**
     * 新建目录
     *
     * @param folderPath 目录
     * @return 返回目录创建后的路径
     */
    public static String createFolder(String folderPath) {
        String txt = folderPath;
        try {
            java.io.File myFilePath = new java.io.File(txt);
            txt = folderPath;
            if (!myFilePath.exists()) {
                myFilePath.mkdir();
            }
        } catch (Exception e) {
            message = "创建目录操作出错";
        }
        return txt;
    }

    /**
     * 删除文件
     *
     * @param filePathAndName 文本文件完整绝对路径(绝对根路径)及文件名
     * @return Boolean 成功删除返回true 遭遇异常返回false
     */
    public static boolean delFileByAbsPath(HttpServletRequest request, String filePathAndName) {
        // 获取文件真实的物理路径
        String pFileName = request.getSession().getServletContext().getRealPath(filePathAndName);
        return delFile(pFileName);
    }

    /**
     * 删除文件
     *
     * @param filePathAndName 文本文件完整绝对路径(物理路径)及文件名
     * @return Boolean 成功删除返回true 遭遇异常返回false
     */
    public static boolean delFile(String filePathAndName) {
        boolean bea = false;
        try {
            String filePath = filePathAndName;
            File myDelFile = new File(filePath);
            if (myDelFile.exists()) {
                myDelFile.delete();
                bea = true;
            } else {
                bea = false;
                message = (filePathAndName + "删除文件操作出错");
            }
        } catch (Exception e) {
            message = e.toString();
        }
        return bea;
    }

    public static String filterFileName(String filename) {
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',/?~@#%……&*+|‘；’。]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(filename);
        return m.replaceAll("").trim();

    }

    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String readFile(File file) {

        StringBuilder result = new StringBuilder();
        try {
            String encoding = "UTF-8";
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String s = null;
                while ((s = bufferedReader.readLine()) != null) {
                    result.append(s + "\r\n");
                }
                read.close();
            } else {
                log.error("找不到指定的文件");
            }
        } catch (Exception e) {
            log.error("读取文件内容出错");
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void main(String[] args) throws Exception {
        String path = "F:/Example Exercise/ExtJS/MultiUpload/WebRoot/upload";
        //System.out.println(mkDir(path));
        System.out.println(getDoPath(path));
        System.out.println(getBracketFileName("a.txt", getDoPath(path)));
        System.out.println(getNumberName("a.jpg"));
        System.out.println(getNumberName("a.jpg"));
        System.out.println(getNewFileName("a", "bbb", "txt"));
        System.out.println(getRandomName("a.htm"));
        System.out.println(getSuffix("a.jpg"));
        System.out.println(getType("a.jpg"));
        //List<File> list = getFiles(path);
        //List<File> list = getFiles(path, "xml");
        //List<File> list = getFiles(path, typeImages);
        //List<File> list = getFiles(path, typeOthers);
        //List<File> list = getFiles(path, typeImages, false);
        /*List<File> list = getFiles(path, "GIF", true);
        for (File f : list) {
            System.out.println("Name:" + f.getName());
            System.out.println(f.getAbsolutePath() + "#" + f.getPath());
        }*/
        System.out.println(removeFile("a.txt", path));
        System.out.println("#############################################");
        System.out.println("###" + validTypeByName("a", new String[]{"*"}));
        System.out.println("###" + validTypeByName("a.JPG", typeImages));
        System.out.println("###" + validTypeByName("a.JPG", typeImages, false));
        System.out.println(validTypeByPostfix("cals", new String[]{"*", "b"}));
        System.out.println(validTypeByPostfix("b", new String[]{"cal", "B"}, false));
    }
}

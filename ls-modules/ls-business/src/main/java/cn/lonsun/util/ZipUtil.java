package cn.lonsun.util;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * @author Hewbing
 * @ClassName: ZipUtil
 * @Description: File compression and decompression tool 文件压缩解压工具
 * @date 2015年11月23日 下午6:33:17
 */

public class ZipUtil {

    private static IMongoDbFileServer mongoDbFileServer = SpringContextHolder.getBean("mongoDbFileServer");

    public static OutputStream zip(String[] mongoIds, HttpServletResponse response) {
        OutputStream os = null;
        try {
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(RandomDigitUtil.getRandomDigit().toString() + ".zip", "UTF-8"));
            os = response.getOutputStream();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ZipOutputStream zos = new ZipOutputStream(os);
        try {
            byte[] buf = new byte[2 << 12];
            int len;
            for (int i = 0; i < mongoIds.length; i++) {
                GridFSDBFile file = mongoDbFileServer.getGridFSDBFile(mongoIds[i], null);
                file.getFilename();

                if (file.getInputStream() == null) {
                    continue;
                }
                ZipEntry ze = new ZipEntry(String.valueOf(i) + "_" + file.getFilename());
                zos.putNextEntry(ze);
                BufferedInputStream bis = new BufferedInputStream(file.getInputStream());
                while ((len = bis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zos.setEncoding("GBK");
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return os;

    }

    /**
     * @param zipFileName
     * @param relativePath Relative path "/" root path
     * @param directory
     * @return void
     * @throws FileNotFoundException
     * @throws IOException
     * @throws
     * @Title: zip
     * @Description: Single file compression
     */
    public static void zip(String zipFileName, String relativePath, String directory) throws FileNotFoundException, IOException {
        String fileName = zipFileName;
        if (fileName == null || fileName.trim().equals("")) {
            File temp = new File(directory);
            if (temp.isDirectory()) {
                fileName = directory + ".zip";
            } else {
                if (directory.indexOf(".") > 0) {
                    fileName = directory.substring(0, directory.lastIndexOf(".")) + "zip";
                } else {
                    fileName = directory + ".zip";
                }
            }
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));
        try {
            zip(zos, relativePath, directory);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (null != zos) {
                zos.close();
            }
        }
    }

    /**
     * @param strs
     * @param zipname
     * @return void return type
     * @throws IOException Parameter
     * @throws
     * @Title: writeZip
     * @Description: Multi file compression
     */
    private static void zip(String[] strs, String zipname) throws IOException {
        String[] files = strs;
        OutputStream os = new BufferedOutputStream(new FileOutputStream("D:/" + zipname + ".zip"));
        ZipOutputStream zos = new ZipOutputStream(os);
        byte[] buf = new byte[2 << 12];
        int len;
        for (int i = 0; i < files.length; i++) {
            File file = new File(files[i]);
            if (!file.isFile()) {
                continue;
            }
            ZipEntry ze = new ZipEntry(file.getName());
            zos.putNextEntry(ze);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            while ((len = bis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            zos.closeEntry();
        }
        zos.setEncoding("GBK");
        zos.close();

        // for(int i=0;i<files.length;i++){
        // System.out.println("------------"+files );
        // File file= new File(files[i]);
        // file.delete();
        // }
    }

    /**
     * @param zos
     * @param relativePath Relative path "/" root path
     * @param absolutPath
     * @return void return type
     * @throws IOException Parameter
     * @throws
     * @Title: zip
     * @Description: TODO
     */
    private static void zip(ZipOutputStream zos, String relativePath, String absolutPath) throws IOException {
        File file = new File(absolutPath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File tempFile = files[i];
                if (tempFile.isDirectory()) {
                    String newRelativePath = relativePath + tempFile.getName() + File.separator;
                    createZipNode(zos, newRelativePath);
                    zip(zos, newRelativePath, tempFile.getPath());
                } else {
                    zipFile(zos, tempFile, relativePath);
                }
            }
        } else {
            zipFile(zos, file, relativePath);
        }
    }

    /**
     * @param zos
     * @param file
     * @param relativePath
     * @return void return type
     * @throws IOException Parameter
     * @throws
     * @Title: zipFile
     * @Description: TODO
     */
    private static void zipFile(ZipOutputStream zos, File file, String relativePath) throws IOException {
        ZipEntry entry = new ZipEntry(relativePath + file.getName());
        zos.putNextEntry(entry);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            int BUFFERSIZE = 2 << 10;
            int length = 0;
            byte[] buffer = new byte[BUFFERSIZE];
            while ((length = is.read(buffer, 0, BUFFERSIZE)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.flush();
            zos.closeEntry();
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }

    /** */
    /**
     * 创建目录
     *
     * @param zos          zip输出流
     * @param relativePath 相对路径
     * @throws IOException
     * @author yayagepei
     * @date 2008-8-26
     */
    private static void createZipNode(ZipOutputStream zos, String relativePath) throws IOException {
        ZipEntry zipEntry = new ZipEntry(relativePath);
        zos.putNextEntry(zipEntry);
        zos.closeEntry();
    }

    /** */
    /**
     * 解压缩zip包
     *
     * @param zipFilePath zip文件路径
     * @param targetPath  解压缩到的位置，如果为null或空字符串则默认解压缩到跟zip包同目录跟zip包同名的文件夹下
     * @throws IOException
     * @author yayagepei
     * @date 2008-9-28
     */
    public static void unzip(String zipFilePath, String targetPath) throws IOException {
        OutputStream os = null;
        InputStream is = null;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath);
            String directoryPath = "";
            if (null == targetPath || "".equals(targetPath)) {
                directoryPath = zipFilePath.substring(0, zipFilePath.lastIndexOf("."));
            } else {
                directoryPath = targetPath;
            }
            Enumeration entryEnum = zipFile.getEntries();
            if (null != entryEnum) {
                ZipEntry zipEntry = null;
                while (entryEnum.hasMoreElements()) {
                    zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (zipEntry.isDirectory()) {
                        directoryPath = directoryPath + File.separator + zipEntry.getName();
                        System.out.println(directoryPath);
                        continue;
                    }
                    if (zipEntry.getSize() > 0) {
                        // 文件
                        File targetFile = buildFile(directoryPath + File.separator + zipEntry.getName(), false);
                        os = new BufferedOutputStream(new FileOutputStream(targetFile));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[2 << 12];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 2 << 12)) >= 0) {
                            os.write(buffer, 0, readLen);
                        }

                        os.flush();
                        os.close();
                    } else {
                        // 空目录
                        buildFile(directoryPath + File.separator + zipEntry.getName(), true);
                    }
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (null != zipFile) {
                zipFile = null;
            }
            if (null != is) {
                is.close();
            }
            if (null != os) {
                os.close();
            }
        }
    }

    public static File buildFile(String fileName, boolean isDirectory) {
        File target = new File(fileName);
        if (isDirectory) {
            target.mkdirs();
        } else {
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
                target = new File(target.getAbsolutePath());
            }
        }
        return target;
    }

    public static void main(String[] args) {
        // try {
        // zip("", "D:/压缩", "");
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // try {
        // unzip("D:/压缩.zip","D:/YS");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        try {
            zip(new String[]{"D:/压缩/11.jpg", "D:/压缩/bg中文.jpg"}, "压缩");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

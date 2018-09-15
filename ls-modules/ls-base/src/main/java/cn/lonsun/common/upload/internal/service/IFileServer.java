package cn.lonsun.common.upload.internal.service;

import cn.lonsun.common.upload.csource.fastdfs.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件文件上传
 *
 * @author
 *
 */
public interface IFileServer {

    /**
     * 上传文件
     * @param file 文件
     *    文件扩展名通过file.getName()获得
     * @return 文件存储路径
     * @throws java.io.IOException
     * @throws Exception
     */
    public String uploadMultipartFile(MultipartFile file) throws IOException,
            Exception;
    /**
     * 上传文件
     * @param file 文件
     *    文件扩展名通过file.getName()获得
     * @return 文件存储路径
     * @throws java.io.IOException
     * @throws Exception
     */
    public String uploadMultipartFile(MultipartFile file, String suffix) throws IOException,
            Exception;
    /**
     * 上传文件
     * @param file 文件
     *    文件扩展名通过file.getName()获得
     * @return 文件存储路径
     * @throws java.io.IOException
     * @throws Exception
     */
    public String uploadFile(File file) throws IOException, Exception ;
    /**
     * 上传文件
     * @param file  文件
     * @param suffix  文件扩展名，如 (jpg,txt)
     * @return  文件存储路径
     * @throws java.io.IOException
     * @throws Exception
     */
    public String uploadFile(File file, String suffix) throws IOException, Exception ;

    /**
     * 上传文件
     * @param fileBuff 二进制数组
     * @param suffix  文件扩展名 ，如(jpg,txt)
     * @return
     */
    public String uploadFile(byte[] fileBuff, String suffix) throws IOException, Exception;

    /**
     * 上传文件
     * @param in 二进制数组
     * @param suffix  文件扩展名 ，如(jpg,txt)
     * @return
     */
    public String uploadFile(InputStream in, String suffix) throws IOException, Exception;

    /**
     *删除文件
     * @param fileId   包含group及文件目录和名称信息
     * @return true 删除成功，false删除失败
     * @throws java.io.IOException
     * @throws Exception
     */
    public boolean deleteFile(String fileId) throws IOException,Exception;
    /**
     * 查找文件内容
     * @param fileId
     * @return  文件内容的二进制流
     * @throws java.io.IOException
     * @throws Exception
     */
    public byte[] getFileByID(String fileId)throws IOException,Exception;
    /**
     * 查找文件内容
     * @param fileId
     * @return  文件大小
     * @throws java.io.IOException
     * @throws Exception
     */
    public int downloadByID(String fileId, BufferedOutputStream out)throws IOException,Exception;
    /**
     * 查找文件
     * @param fileId
     * @return  文件内容的二进制流
     * @throws java.io.IOException
     * @throws Exception
     */
    public FileInfo getFileInfoByID(String fileId)throws IOException,Exception;

    /**
     *
     * @param path
     * @return
     * @throws Exception
     */
    public byte[] getFileByHttp(String path) throws Exception;
}

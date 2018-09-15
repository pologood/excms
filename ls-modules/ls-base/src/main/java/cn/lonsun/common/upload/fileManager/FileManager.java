package cn.lonsun.common.upload.fileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.common.upload.csource.common.MyException;
import cn.lonsun.common.upload.csource.fastdfs.ClientGlobal;
import cn.lonsun.common.upload.csource.fastdfs.StorageClient1;
import cn.lonsun.common.upload.csource.fastdfs.StorageServer;
import cn.lonsun.common.upload.csource.fastdfs.TrackerClient;
import cn.lonsun.common.upload.csource.fastdfs.TrackerServer;
import cn.lonsun.common.upload.internal.service.IFileServer;
import cn.lonsun.common.upload.internal.service.impl.FileServerImpl;

/**
 * Created by yjjun_pc on 2015-2-28.
 */
public class FileManager implements Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger logger = LoggerFactory.getLogger(FileManager.class);
    private static final String CLIENT_CONFIG_FILE = "fdfs_client.conf";

    private static String[] tgStr = null;
    private static int connect_timeout = 30000;
    private static int network_timeout = 60000;
    private static String charset = "utf-8";

    private static IFileServer fileServer;

    private static String downloadIP = "http://192.168.1.206/mongo/";

    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageServer storageServer;
    private static StorageClient1 storageClient;

    /*
     * static { // Initialize Fast DFS Client configurations
     * 
     * }
     */

    /**
     * 初始化文件服务fastdfs客户端
     *
     * @return fileServer
     */
    public static IFileServer getFileServer() {
        try {
            if (null == trackerClient) {
                String classPath = new File(FileManager.class.getResource("/").getFile()).getCanonicalPath();
                String fdfsClientConfigFilePath = classPath + File.separator + "fdfs_client.conf";
                ClientGlobal.init(fdfsClientConfigFilePath);

                String realPath = FileManager.class.getResource("/").getPath() + CLIENT_CONFIG_FILE;
                ClientGlobal.init(realPath);
            }
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageClient = new StorageClient1(trackerServer, storageServer);
            fileServer = new FileServerImpl(storageClient);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return fileServer;
    }

    public static String getDownloadIP() {
//        if (null == downloadIP || "".equals(downloadIP)) {
//            IFastDFSClientService fastdfsClientService = SpringContextHolder.getBean("fastdfsClientService");
//            FastDFSVO vo = null;
//            List<FastDFSVO> list = fastdfsClientService.getFastDFSList();
//            if (null != list && list.size() > 0) {
//                vo = list.get(0);
//                downloadIP = vo.getDownLoadIp();
//                if (null == downloadIP || "".equals(downloadIP)) {
//                    throw new BaseRunTimeException(TipsMode.Message.toString(), "无法获取FastDFS服务下载地址");
//                }
//            }
//        }
        return downloadIP;
    }

    public static void setDownloadIP(String downloadIP) {
        FileManager.downloadIP = downloadIP;
    }
}

package cn.lonsun.common.upload.pool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * FastDFS文件上传下载接口
 *
 * @author
 * */
public class FdfsUtil {

    private static final Logger logger = LoggerFactory.getLogger(FdfsUtil.class);

    private static String configName = "fdfs_client.conf";

    private static FileServer fileServer = null;

    /**
     * 读取fastdfs客户端配置文件
     */
    static {
        try {
            Resource fileRource = new ClassPathResource(configName);
            InputStream is = fileRource.getInputStream();
            Properties p = new Properties();
            p.load(is);
            String servers = p.getProperty("tracker_server");
            String poolSize = p.getProperty("pool_size");
            String[] server_arr = servers.split(",");
            String connect_timeout = p.getProperty("connect_timeout");
            String network_timeout = p.getProperty("network_timeout");
            String charset = p.getProperty("charset");
            fileServer =
                    new FileServerImpl(server_arr, Integer.parseInt(poolSize), Integer.parseInt(connect_timeout), Integer.parseInt(network_timeout), charset);
            logger.info("初始化文件服务fastdfs客户端");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化文件服务fastdfs客户端
     *
     * @return fileServer
     */
    public static FileServer getFileServer() {
        return fileServer;
    }
}
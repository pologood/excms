package cn.lonsun.common.upload.servlet;

import java.net.InetSocketAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import cn.lonsun.common.upload.csource.fastdfs.ClientGlobal;
import cn.lonsun.common.upload.csource.fastdfs.TrackerGroup;
import cn.lonsun.common.upload.fileManager.FileManager;
import cn.lonsun.common.vo.FastDFSVO;

/**
 * Created by yjjun_pc on 2015-3-23.
 */
public class FastdfsServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1237851506671541450L;

    private  String[] tgStr = null;
    private  int connect_timeout = 30000;
    private  int network_timeout=60000;
    private  String charset = "utf-8";

    private  String downLoadIp = "";

    @Override
    public void init(ServletConfig config) throws ServletException {
//        IFastDFSClientService fastdfsClientService = SpringContextHolder.getBean("fastdfsClientService");
        FastDFSVO vo = null;
//
//        List<FastDFSVO> list = fastdfsClientService.getFastDFSList();
//        if(null!=list && list.size()>0) {
//            vo = list.get(0);
//        }

        if(null!=vo) {
            String[] server_arr = vo.getTrackerServer().split(",");
            this.tgStr = server_arr;
            this.connect_timeout = Integer.parseInt(vo.getConnectTimeout())*1000;
            this.network_timeout = Integer.parseInt(vo.getNetworkTimeout())*1000;
        }

        if(null!=tgStr) {
            int num = tgStr.length;
            InetSocketAddress[] trackerServers = new InetSocketAddress[num];
            for(int i=0;i<num;i++){
                String[] ip_port = tgStr[i].split(":");
                trackerServers[i] = new InetSocketAddress(ip_port[0], Integer.parseInt(ip_port[1]));
            }

            ClientGlobal.setG_tracker_group(new TrackerGroup(trackerServers));
            ClientGlobal.setG_connect_timeout(connect_timeout);
            ClientGlobal.setG_network_timeout(network_timeout);
            ClientGlobal.setG_anti_steal_token(false);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_secret_key(null);

            config.getServletContext().setAttribute("downLoadIp", vo.getDownLoadIp());
            FileManager.setDownloadIP(vo.getDownLoadIp());
        }
    }
}

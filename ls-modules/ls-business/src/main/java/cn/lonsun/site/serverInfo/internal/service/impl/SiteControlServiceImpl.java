package cn.lonsun.site.serverInfo.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SshUtil;
import cn.lonsun.site.serverInfo.internal.entity.ServerInfoEO;
import cn.lonsun.site.serverInfo.internal.entity.SiteControlEO;
import cn.lonsun.site.serverInfo.internal.service.IServerInfoService;
import cn.lonsun.site.serverInfo.internal.service.ISiteControlService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import org.apache.tools.ant.filters.StringInputStream;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2017-08-01 9:15
 */
@Service
public class SiteControlServiceImpl extends MockService<SiteControlEO> implements ISiteControlService {

    @Resource
    private IServerInfoService serverInfoService;

    @Override
    public void updateSiteControl(Long[] siteIds, Integer status) {
        ServerInfoEO info = serverInfoService.getEntity(ServerInfoEO.class,new HashMap<String, Object>());
        if(null == info) {
            throw new BaseRunTimeException("服务器配置信息为空");
        }
        //初始化ssh连接
        SshUtil ssh = new SshUtil(info.getIp(),info.getPort(),info.getUsername(),info.getPassword());
        if(null == ssh) {
            throw new BaseRunTimeException("获取ssh链接失败");
        }

        if(null != siteIds && siteIds.length > 0) {
            for(Long siteId : siteIds) {
                if(!ssh.isConnected()) {
                    ssh.close();
                    ssh = new SshUtil(info.getIp(),info.getPort(),info.getUsername(),info.getPassword());
                }
                updateSiteControl(info,ssh,siteId,status);
            }
        }
    }

    /**
     * 启停站点
     * @param info
     * @param ssh
     * @param siteId
     * @param status
     */
    private void updateSiteControl(ServerInfoEO info,SshUtil ssh,Long siteId, Integer status) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteId",siteId);
        SiteControlEO eo = this.getEntity(SiteControlEO.class,map);
        if(null == eo) {
            eo = new SiteControlEO();
            eo.setSiteId(siteId);
        }
        SiteMgrEO siteVO = CacheHandler.getEntity(SiteMgrEO.class, eo.getSiteId());
        String domain = siteVO.getUri();
        if(null == domain) {
            throw new BaseRunTimeException("站点未绑定域名");
        }
        String start = "http://";
        if(domain.startsWith(start)) {
            domain = domain.substring(start.length(),domain.length());
        }

        if(domain.endsWith("/")) {
            domain = domain.substring(0,domain.length() - 1);
        }

        //创建文件名
        if(null == eo.getFileName()) {
            eo.setFileName("domain-".concat(domain).concat(".conf"));
        } else {
            /*域名改变则删除老的配置*/
            if(!eo.getFileName().contains(domain)) {
                if(ssh.detectedFileExist(info.getNginxFilePath(),eo.getFileName())) {
                    ssh.deleteRemoteFileOrDir(info.getNginxFilePath(),eo.getFileName());
                    eo.setFileName("domain-".concat(domain).concat(".conf"));
                }
            }
        }

        if(status.intValue() == 1) {
            //启动
            String template = info.getTemplate();
            if(null == template) {
                throw new BaseRunTimeException("模板文件未配置");
            }
            template = template.replaceAll("\\{:domain\\}",domain);
            InputStream is = new StringInputStream(template);
            ssh.fileUpload(is,info.getNginxFilePath(),eo.getFileName());
        } else if(status.intValue() == 2) {
            //停止
            InputStream is = new StringInputStream("");
            ssh.fileUpload(is,info.getNginxFilePath(),eo.getFileName());
        }

        StringBuffer cmd = new StringBuffer("cd ");
        cmd.append(info.getNginxCmdPath());
        cmd.append("; ");
        cmd.append(info.getNginxCmd());
        try {
            //执行重启nginx命令
            ssh.sendCmd(cmd.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException("启动Nginx服务异常");
        } finally {
            if(null != ssh) {
                ssh.close();
            }
        }

        eo.setStatus(status);
        if(null == eo.getId()) {
            this.saveEntity(eo);
        } else {
            this.updateEntity(eo);
        }
    }
}

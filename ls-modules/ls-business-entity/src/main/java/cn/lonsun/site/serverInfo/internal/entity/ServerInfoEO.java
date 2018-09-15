package cn.lonsun.site.serverInfo.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author gu.fei
 * @version 2017-08-01 9:06
 * 服务器信息
 */
@Entity
@Table(name = "CMS_SERVER_INFO")
public class ServerInfoEO extends AMockEntity implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    //名称
    @Column(name = "NAME")
    private String name;

    //服务IP地址
    @Column(name = "IP")
    private String ip;

    //服务器端口
    @Column(name = "PORT")
    private Integer port;

    //服务器登录用户名
    @Column(name = "USERNAME")
    private String username;

    //服务器登录密码
    @Column(name = "PASSWORD")
    private String password;

    //nginx执行命令的文件路径
    @Column(name = "NGINX_CMD_PATH")
    private String nginxCmdPath;

    //nginx存放子站nginx配置文件路径
    @Column(name = "NGINX_FILE_PATH")
    private String nginxFilePath;

    //nginx执行重启的命令
    @Column(name = "NGINX_CMD")
    private String nginxCmd;

    //模板
    @Column(name = "TEMPLATE")
    private String template;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNginxCmdPath() {
        return nginxCmdPath;
    }

    public void setNginxCmdPath(String nginxCmdPath) {
        this.nginxCmdPath = nginxCmdPath;
    }

    public String getNginxFilePath() {
        return nginxFilePath;
    }

    public void setNginxFilePath(String nginxFilePath) {
        this.nginxFilePath = nginxFilePath;
    }

    public String getNginxCmd() {
        return nginxCmd;
    }

    public void setNginxCmd(String nginxCmd) {
        this.nginxCmd = nginxCmd;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}

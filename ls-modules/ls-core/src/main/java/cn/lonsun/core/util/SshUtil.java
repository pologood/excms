package cn.lonsun.core.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @author gu.fei
 * @version 2017-08-01 11:04
 * ssh工具类
 */
public class SshUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Session session;

    public SshUtil(String host, Integer port, String user, String password) {
        connect(host, port, user, password);
    }
	

    /**
     * 连接sftp服务器
     * @param host     远程主机ip地址
     * @param port     sftp连接端口，null  时为默认端口
     * @param user     用户名
     * @param password 密码
     * @return
     * @throws JSchException
     */
    private Session connect(String host, Integer port, String user, String password) {
        try {
            JSch jsch = new JSch();
            if (port != null) {
                session = jsch.getSession(user, host, port.intValue());
            } else {
                session = jsch.getSession(user, host);
            }
            session.setPassword(password);
            //设置第一次登陆的时候提示，可选值:(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("userauth.gssapi-with-mic", "no");
            //连接
            session.connect();
        } catch (JSchException e) {
            e.printStackTrace();
            logger.error("SFTPUitl获取连接发生错误");
            throw new RuntimeException("SFTPUitl获取连接发生错误");
        }
        return session;
    }

    /**
     * 发送命令
     * @param command
     * @return
     * @throws Exception
     */
    public SSHResInfo sendCmd(String command) throws Exception {
        return sendCmd(command, 200);
    }

    /*
    * 执行命令，返回执行结果
    * @param command 命令
    * @param delay 估计shell命令执行时间
    * @return String 执行命令后的返回
    * @throws Exception
    */
    public SSHResInfo sendCmd(String command, int delay) throws Exception {
        if (delay < 50) {
            delay = 50;
        }
        SSHResInfo result = null;
        byte[] tmp = new byte[1024]; //读数据缓存
        StringBuffer strBuffer = new StringBuffer();  //执行SSH返回的结果
        StringBuffer errResult = new StringBuffer();

        Channel channel = session.openChannel("exec");
        ChannelExec ssh = (ChannelExec) channel;
        //返回的结果可能是标准信息,也可能是错误信息,所以两种输出都要获取
        //一般情况下只会有一种输出.
        //但并不是说错误信息就是执行命令出错的信息,如获得远程java JDK版本就以
        //ErrStream来获得.
        InputStream stdStream = ssh.getInputStream();
        InputStream errStream = ssh.getErrStream();

        ssh.setCommand(command);
        ssh.connect();

        try {
            //开始获得SSH命令的结果
            while (true) {
                //获得错误输出
                while (errStream.available() > 0) {
                    int i = errStream.read(tmp, 0, 1024);
                    if (i < 0) break;
                    errResult.append(new String(tmp, 0, i));
                }
                //获得标准输出
                while (stdStream.available() > 0) {
                    int i = stdStream.read(tmp, 0, 1024);
                    if (i < 0) break;
                    strBuffer.append(new String(tmp, 0, i));
                }
                if (ssh.isClosed()) {
                    int code = ssh.getExitStatus();
                    logger.info("exit-status: " + code);
                    result = new SSHResInfo(code, strBuffer.toString(), errResult.toString());
                    break;
                }
                try {
                    Thread.sleep(delay);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        } finally {
            // TODO: handle finally clause
            channel.disconnect();
        }

        return result;
    }

    /**
     * 配置文件上传
     * @param is 文件流
     * @param path 上传文件的路径
     * @param fileName 上传文件名称
     */
    public void fileUpload(InputStream is,String path,String fileName) {
        if(null == is) {
            throw new BaseRunTimeException("文件内容为空!");
        }

        if(null == path) {
            throw new BaseRunTimeException("文件路径为空!");
        }

        if(null == fileName) {
            throw new BaseRunTimeException("文件名称为空!");
        }

        try {
            ChannelSftp channelSftp = (ChannelSftp)session.openChannel("sftp");
            channelSftp.connect();
            //这里设置无用，需要改源码
            channelSftp.setFilenameEncoding("UTF-8");
            channelSftp.put(is,getRemoteFile(path,fileName));
        } catch (JSchException e) {
            e.printStackTrace();
            logger.error("上传配置文件失败，{}",e.getMessage());
            throw new BaseRunTimeException("上传配置文件失败");
        } catch (SftpException e) {
            e.printStackTrace();
            throw new BaseRunTimeException("上传配置文件失败");
        }
    }

    /**
     * 解析返回字符串
     * @param in
     * @param charset
     * @return
     * @throws Exception
     */
    private String processStream(InputStream in, String charset) throws Exception {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    /**
     * 删除远程文件
     * @param path 上传文件的路径
     * @param fileName 上传文件名称
     * @return
     */
    public boolean deleteRemoteFileOrDir(String path,String fileName) {
        ChannelSftp channel = null;
        try {
            String remoteFile = getRemoteFile(path,fileName);
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS = channel.lstat(remoteFile);
            if (sftpATTRS.isDir()) {
                //目录
                logger.debug("remote File:dir");
                channel.rmdir(remoteFile);
                return true;
            } else if (sftpATTRS.isReg()) {
                //文件
                logger.debug("remote File:file");
                channel.rm(remoteFile);
                return true;
            } else {
                logger.debug("remote File:unkown");
                return false;
            }
        } catch (JSchException e) {
            if (channel != null) {
                channel.disconnect();
                session.disconnect();
            }
            logger.error("error", e);
            return false;
        } catch (SftpException e) {
            logger.info("meg" + e.getMessage());
            logger.error("SftpException", e);
            return false;
        }

    }

    /**
     * 判断linux下 某文件是否存在
     * @param path 上传文件的路径
     * @param fileName 上传文件名称
     * @return
     */
    public boolean detectedFileExist(String path,String fileName) {
        ChannelSftp channel = null;
        try {
            String remoteFile = getRemoteFile(path,fileName);
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            SftpATTRS sftpATTRS = channel.lstat(remoteFile);
            if (sftpATTRS.isDir() || sftpATTRS.isReg()) {
                //目录 和文件
                logger.info("remote File:dir");
                return true;
            } else {
                logger.info("remote File:unkown");
                return false;
            }
        } catch (JSchException e) {
            if (channel != null) {
                channel.disconnect();
                session.disconnect();
            }
            return false;
        } catch (SftpException e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 获取文件地址
     * @param path
     * @param fileName
     * @return
     */
    public String getRemoteFile(String path,String fileName) {
        StringBuffer dst = new StringBuffer(path);
        if(!path.endsWith("/")) {
            dst.append("/");
        }
        dst.append(fileName);

        return dst.toString();
    }

    /**
     * 关闭
     */
    public void close() {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 获取ssh链接状态
     * @return
     */
    public boolean isConnected() {
        return session.isConnected();
    }
}

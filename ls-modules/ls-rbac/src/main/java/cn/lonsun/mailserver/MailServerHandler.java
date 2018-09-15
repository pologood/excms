package cn.lonsun.mailserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;

/**
 * Created by zhusy on 2015-3-15.
 */
public class MailServerHandler {

    private static Logger logger = LoggerFactory.getLogger(MailServerHandler.class);

    /**
     * 添加邮件用户
     * @param userName  uid+organId
     * @return
     */
    public static boolean addMailUser(String userName){
        if(AppUtil.isEmpty(userName)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"用户名不能为空");
        }
        String host = MailServerConfig.SENDMAIL_SERVERHOST;
        if(AppUtil.isEmpty(host)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"邮件服务器主机地址配置错误");
        }
        String serverAccount = MailServerConfig.SENDMAIL_SERVERACCOUNT;
        if(AppUtil.isEmpty(serverAccount)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"邮件服务器用户配置错误");
        }
        String serverPassword = MailServerConfig.SENDMAIL_SERVERPASSWORD;
        if(AppUtil.isEmpty(serverPassword)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"邮件服务器密码配置错误");
        }

        Connection connection = new Connection(host);

        Session ssh = null;
        try {
            //连接到主机
            connection.connect();
            //使用用户名和密码校验
            boolean isConnected = connection.authenticateWithPassword(serverAccount, serverPassword);
            if(!isConnected){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"用户名称或者是密码不正确");
            }else{
                ssh = connection.openSession();
                String command = "#!/bin/bash\n" +
                        "adduser "+userName+" -g mailuser -s /sbin/nologin \n" +
                        "echo \""+userName+"\" | passwd --stdin "+userName+"\n" +
                        "chmod 600 /var/spool/mail/"+userName;

                logger.info(command);
                ssh.execCommand(command);//执行命令

                InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                String resultStr = "";
                String line = "";
                while(true){
                    line = brs.readLine();
                    if(line==null){
                        break;
                    }
                    resultStr+=line;
                }
                brs.close();
                logger.info(resultStr);
                if(AppUtil.isEmpty(resultStr)){
                    logger.info(userName+"导入失败");
                    return false;
                }else if(resultStr.indexOf("success") > -1){
                    logger.info(userName+"导入成功");
                    return true;
                }
            }
            //连接的Session和Connection对象都需要关闭
            ssh.close();
            connection.close();

        } catch (IOException e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"邮件服务器IO异常");
        }
        return false;
    }

    /**
     * 添加邮件用户
     * @return
     */
    public static boolean addMailUsers(String userNames){


        Connection connection = new Connection(MailServerConfig.SENDMAIL_SERVERHOST);

        Session ssh = null;
        try {
            //连接到主机
            connection.connect();
            //使用用户名和密码校验
            boolean isConnected = connection.authenticateWithPassword(MailServerConfig.SENDMAIL_SERVERACCOUNT, MailServerConfig.SENDMAIL_SERVERPASSWORD);
            if(!isConnected){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"用户名称或者是密码不正确");
            }else{
                ssh = connection.openSession();

                 String command = "#!/bin/bash\n" +
                        "for name in "+userNames+"\n" +
                        "do\n" +
                        "useradd $name\n" +
                        "echo $name | passwd --stdin $name\n" +
                        "chmod 600 /var/spool/mail/$name\n"+
                        "done\n";

                logger.info(command);
                ssh.execCommand(command);//执行命令

                InputStream is = new StreamGobbler(ssh.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                String resultStr = "";
                String line = "";
                while(true){
                    line = brs.readLine();
                    if(line==null){
                        break;
                    }
                    resultStr+=line;
                }
                logger.info(resultStr);
                if(AppUtil.isEmpty(resultStr)){
                    logger.info(userNames+"导入失败");
                    return false;
                }else if(resultStr.indexOf("success") > -1 || resultStr.indexOf("成功") > -1){
                    logger.info(userNames+"导入成功");
                    return true;
                }
            }
            //连接的Session和Connection对象都需要关闭
            ssh.close();
            connection.close();

        } catch (IOException e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"邮件服务器IO异常");
        }
        return false;
    }


}

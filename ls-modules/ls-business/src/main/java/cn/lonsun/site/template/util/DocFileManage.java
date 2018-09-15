package cn.lonsun.site.template.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.base.impl.MongoDbFileServerImpl;
import cn.lonsun.mongodb.vo.MongoFileVO;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author gu.fei
 * @version 2015-8-26 14:09
 */
public class DocFileManage {

    private static Logger logger = LoggerFactory.getLogger(DocFileManage.class);
    private static IMongoDbFileServer fileServer = new MongoDbFileServerImpl();
    /*
    * 读取文件
    * */
    public static String readFile(String path) {

        String content = null;
        try {
            GridFSDBFile gf = fileServer.getGridFSDBFile(path,"template");

            InputStream is = gf.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n"); //appende a new line
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            content = new String(builder.toString().getBytes(), "UTF-8");
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return content;
    }

    /*
    * 保存文件
    * */
    public static String writeFile(String fileName ,String content,Long id) {

        MongoFileVO fileVO = null;
        try {
            fileVO = fileServer.uploadByteFile(content.getBytes("UTF-8"), fileName, id ,"template");
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return fileVO.getMongoId();
    }

    /*
    * 删除文件
    * */
    public static boolean delFile(String path) {

        if(AppUtil.isEmpty(path))
            return false;

        boolean rst = false;
        try {
            rst = fileServer.delete(path,"template");
        } catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        return rst;
    }

    public static void main(String[] args) {}

}

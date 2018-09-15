package cn.lonsun.mongodb.base;


import cn.lonsun.mongodb.vo.MongoFileVO;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * mongoDb 附件处理接口
 *
 * @author zhangchao
 */
public interface IMongoDbFileServer {

    /**
     * 存储文件 （多文件）
     *
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @param files          文件
     * @param caseId         外键
     */
    public List<MongoFileVO> uploadMultipartFile(MultipartFile[] files, Long caseId, String collectionName);

    /**
     * 存储文件 (单文件)
     *
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @param file           文件
     * @param caseId         外键
     */
    public MongoFileVO uploadMultipartFile(MultipartFile file, String collectionName);


    /**
     * 存储文件 (单文件)
     *
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @param (byte[])
     * @param caseId         外键
     */
    public MongoFileVO uploadByteFile(byte[] filebytes, String fileName, Long caseId, String collectionName);
    public MongoFileVO uploadByteFile(InputStream inputStream, String fileName, Long caseId, String collectionName);
    public MongoFileVO uploadMultipartFile(MultipartFile file, String collectionName, String _id);

    /**
     * 获取集合
     *
     * @param caseId
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public List<MongoFileVO> getList(Long caseId, String collectionName);

    /**
     * 获取文件
     *
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public DBCursor GridFSDBFileList(DBObject query, String collectionName);

    /**
     * 获取文件
     *
     * @param mongoDbId
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public GridFSDBFile getGridFSDBFile(String mongoDbId, String collectionName);

    /**
     * 下载
     *
     * @param mongoDbId
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public void downloadFile(HttpServletResponse response, String mongoDbId, String collectionName);

    /**
     * pdf预览
     *
     * @param mongoDbId
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public void viewFile(HttpServletResponse response, GridFSDBFile gridFSDBFile);

    /**
     * @param caseId
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public boolean deleteByCaseId(Long caseId, String collectionName);


    /**
     * @param mongoDbId      mongoDb主键
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    public boolean delete(String mongoDbId, String collectionName);


    /**
     * 更新文件属性 (单文件)
     *
     * @param (mongoId)      mongoId id
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     */
    public MongoFileVO updateByteFile(String mongoId, MongoFileVO vo, String collectionName);

    public DBCursor MongoAllList(DBObject ref, DBObject keys);
}

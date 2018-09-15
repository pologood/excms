package cn.lonsun.mongodb.base.impl;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.util.LoginPersonUtil;
import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("mongoDbFileServer")
public class MongoDbFileServerImpl implements IMongoDbFileServer {

    private static String defaultCollectionName = "fs";

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param files          文件
     * @param caseId         外键
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    @Override
    public List<MongoFileVO> uploadMultipartFile(MultipartFile[] files, Long caseId, String collectionName) {
        if (caseId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "外键不能为空");
        }
        collectionName = getCollectionName(collectionName);
        GridFSFile gfile = null;
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        List<MongoFileVO> mfs = new ArrayList<MongoFileVO>(files.length);
        for (MultipartFile file : files) {
            // 判断文件是否为空
            if (!file.isEmpty()) {
                // 后缀名
                String suffix = getSuffix(file.getOriginalFilename());
                try {
                    gfile = gridFS.createFile(file.getBytes());
                    gfile.put("filename", file.getOriginalFilename());
                    //gfile.put("filename", RandomFileName(suffix));
                    gfile.put("caseId", caseId);
                    gfile.put("contentType", suffix);
                    gfile.put("metadata", MongoMetaData(file.getOriginalFilename()));
                    gfile.save();
                    mfs.add(getMongoFileVO(gfile, caseId));
                } catch (Exception e) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb保存文件出错");
                }
            }
        }
        return mfs;
    }

    @Override
    public MongoFileVO uploadMultipartFile(MultipartFile file, String collectionName) {
        collectionName = getCollectionName(collectionName);
        GridFSFile gfile = null;
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        MongoFileVO mf = null;
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                gfile = gridFS.createFile(file.getBytes());
//                gfile.put("filename", file.getOriginalFilename());
                // 后缀名
                String suffix = getSuffix(file.getOriginalFilename());
                gfile.put("filename", RandomFileName(suffix));
                gfile.put("caseId", "");
                gfile.put("contentType", suffix);
                gfile.put("metadata", MongoMetaData(file.getOriginalFilename()));
                gfile.save();
                // objectId = gfile.getId() == null?"":gfile.getId().toString();
                mf = getMongoFileVO(gfile, null);
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb保存文件出错");
            }
        }
        return mf;
    }

    @Override
    public MongoFileVO uploadMultipartFile(MultipartFile file, String collectionName, String _id) {
        GridFS myFS = new GridFS(mongoTemplate.getDb(), collectionName);
        MongoFileVO mf = null;
        DBObject query = new BasicDBObject("_id", _id);
        GridFSDBFile gridFSDBFile = myFS.findOne(query);
        if (gridFSDBFile != null) {
            myFS.remove(new ObjectId(_id));
        }
        GridFSInputFile gridFSInputFile = null;
        try {
            gridFSInputFile = myFS.createFile(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        gridFSInputFile.put("_id", _id);
        // 后缀名
        String suffix = getSuffix(file.getOriginalFilename());
        gridFSInputFile.put("filename", RandomFileName(suffix));
//        gridFSInputFile.put("filename", file.getOriginalFilename());
        //gridFSInputFile.put("filename", RandomFileName(suffix));
        gridFSInputFile.put("caseId", "");
        gridFSInputFile.put("contentType", suffix);
        gridFSInputFile.put("metadata", MongoMetaData(file.getOriginalFilename()));
        gridFSInputFile.save();
        mf = getMongoFileVO(gridFSInputFile, null);
        return mf;
    }

    /**
     * doing
     *
     * @param filebytes
     * @param fileName
     * @param caseId         外键
     * @param collectionName 集合名    默认值： fs , 如果想分清楚模块集合可自行设置
     * @return
     */
    @Override
    public MongoFileVO uploadByteFile(byte[] filebytes, String fileName, Long caseId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        if (filebytes == null || filebytes.length == 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "filebytes不能为空");
        }
        GridFSFile gfile = null;
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        MongoFileVO mf = null;
        // 判断文件是否为空
        try {
            gfile = gridFS.createFile(filebytes);
//            gfile.put("filename", fileName);
            // 后缀名
            String suffix = getSuffix(fileName);
            gfile.put("filename", RandomFileName(suffix));
            gfile.put("caseId", caseId);
            gfile.put("contentType", suffix);
            gfile.put("metadata", MongoMetaData(fileName));
            gfile.save();
            mf = getMongoFileVO(gfile, null);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb保存文件出错");
        }
        return mf;
    }

    @Override
    public MongoFileVO uploadByteFile(InputStream inputStream, String fileName, Long caseId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        if (inputStream == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "inputStream不能为空");
        }
        GridFSFile gfile = null;
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        MongoFileVO mf = null;
        // 判断文件是否为空
        try {
            gfile = gridFS.createFile(inputStream);
//            gfile.put("filename", fileName);
            // 后缀名
            String suffix = getSuffix(fileName);
            gfile.put("filename", RandomFileName(suffix));
            gfile.put("caseId", caseId);
            gfile.put("contentType", suffix);
            gfile.put("metadata", MongoMetaData(fileName));
            gfile.save();
            mf = getMongoFileVO(gfile, null);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb保存文件出错");
        }
        return mf;
    }

    @Override
    public List<MongoFileVO> getList(Long caseId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        DBObject query = new BasicDBObject("caseId", caseId);
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        List<GridFSDBFile> gridFSDBFileList = gridFS.find(query);
        List<MongoFileVO> list = new ArrayList<MongoFileVO>(gridFSDBFileList.size());
        if (gridFSDBFileList != null && gridFSDBFileList.size() > 0) {
            for (GridFSDBFile gfile : gridFSDBFileList) {
                list.add(getMongoFileVO(gfile, caseId));
            }
        }
        return list;
    }

    @Override
    public DBCursor GridFSDBFileList(DBObject query, String collectionName) {
        collectionName = getCollectionName(collectionName);
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        DBCursor gridFSDBFile = gridFS.getFileList(query);
        return gridFSDBFile;
    }

    @Override
    public GridFSDBFile getGridFSDBFile(String mongoDbId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        //如果是文件名称
        if (mongoDbId.indexOf(".") != -1) {
            GridFSDBFile gridFSDBFile = (GridFSDBFile) gridFS.findOne(mongoDbId);
            return gridFSDBFile;
        }
        ObjectId objId = new ObjectId(mongoDbId);
        GridFSDBFile gridFSDBFile = (GridFSDBFile) gridFS.findOne(objId);
        return gridFSDBFile;
    }

    @Override
    public void downloadFile(HttpServletResponse response, String mongoDbId, String collectionName) {
        GridFSDBFile gridFSDBFile = getGridFSDBFile(mongoDbId, collectionName);
        java.io.OutputStream sos = null;
        if (gridFSDBFile != null) {
            try {
                sos = response.getOutputStream();
                // 获取原文件名
//                String fileName = gridFSDBFile.getFilename();
                String fileName = String.valueOf(gridFSDBFile.getMetaData().get("OriginalFilename"));
                // String outFile = URLEncoder.encode(fileName,
                // "UTF-8").concat(".").concat(gridFSDBFile.getContentType());
                response.setContentType("application/octet-stream");
                String agent = LoginPersonUtil.getRequest().getHeader("User-Agent").toLowerCase();
                if (!StringUtils.isEmpty(agent) && agent.indexOf("firefox") > 0) {
                    response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
                } else {
                    response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
                }
                // 向客户端输出文件
                gridFSDBFile.writeTo(sos);
                sos.flush();
                sos.close();
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb下载文件出错");
            }
        }
    }

    @Override
    public void viewFile(HttpServletResponse response, GridFSDBFile gridFSDBFile) {
        java.io.OutputStream sos = null;
        if (gridFSDBFile != null) {
            try {
                sos = response.getOutputStream();
                // 获取原文件名
//                String fileName = gridFSDBFile.getFilename();
                String fileName = String.valueOf(gridFSDBFile.getMetaData().get("OriginalFilename"));
                String agent = LoginPersonUtil.getRequest().getHeader("User-Agent").toLowerCase();
                // String outFile = URLEncoder.encode(fileName,
                // "UTF-8").concat(".").concat(gridFSDBFile.getContentType());
                response.setContentType("application/pdf");
                if (!StringUtils.isEmpty(agent) && agent.indexOf("firefox") > 0) {
                    response.addHeader("Content-Disposition", "filename=" + new String(fileName.getBytes("GB2312"), "ISO-8859-1"));
                } else {
                    response.addHeader("Content-Disposition", "filename=" + fileName);
                }
                // 向客户端输出文件
                gridFSDBFile.writeTo(sos);
                sos.flush();
                sos.close();
            } catch (Exception e) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb下载文件出错");
            }
        }
    }

    @Override
    public boolean deleteByCaseId(Long caseId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        DBObject query = new BasicDBObject("caseId", caseId);
        try {
            gridFS.remove(query);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb删除文件出错");
        }
        return true;
    }

    @Override
    public boolean delete(String mongoDbId, String collectionName) {
        collectionName = getCollectionName(collectionName);
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        ObjectId objId = new ObjectId(mongoDbId);
        try {
            gridFS.remove(objId);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb删除文件出错");
        }
        return true;
    }

    //自定义metadata
    private DBObject MongoMetaData(String OriginalFilename) {
        DBObject metaData = new BasicDBObject();
        metaData.put("OriginalFilename", OriginalFilename);
        return metaData;
    }

    //生成uuid
    private String RandomFileName(String suffix) {
        return UUID.randomUUID().toString().replaceAll("\\-", "").concat(".").concat(suffix);
    }

    private MongoFileVO getMongoFileVO(GridFSFile gfile, Long caseId) {
        MongoFileVO mf = new MongoFileVO();
        mf.setMongoId(gfile.getId() == null ? null : gfile.getId().toString());
        mf.setCaseId(caseId);
        mf.setContentType(gfile.getContentType());
        mf.setFileName(gfile.getFilename());
        mf.setUploadDate(gfile.getUploadDate());
        mf.setMd5(gfile.getMD5());
        mf.setMetaData(gfile.getMetaData());
        return mf;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return
     */
    private String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            String suffix = fileName.substring(index + 1);// 后缀
            return suffix;
        } else {
            return "";
        }
    }

    private String getCollectionName(String collectionName) {
        return (StringUtils.isEmpty(collectionName) ? MongoDbFileServerImpl.defaultCollectionName : collectionName);
    }

    @Override
    public MongoFileVO updateByteFile(String mongoId, MongoFileVO vo, String collectionName) {
        collectionName = getCollectionName(collectionName);
        if (mongoId == null || mongoId.equals("")) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoId 不能为空");
        }
        GridFSFile gfile = null;
        GridFS gridFS = new GridFS(mongoTemplate.getDb(), collectionName);
        MongoFileVO mf = null;
        // 判断文件是否为空
        try {
            gfile = gridFS.findOne(new ObjectId(mongoId));
//            gfile.put("filename", vo.getFileName());
            // 后缀名
            String suffix = getSuffix(vo.getFileName());
            gfile.put("filename", RandomFileName(suffix));
            gfile.put("caseId", vo.getCaseId());
            gfile.put("contentType", suffix);
            gfile.put("metadata", vo.getMetaData());
            gfile.save();
            mf = getMongoFileVO(gfile, null);
        } catch (Exception e) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "mongoDb 更新文件出错");
        }
        return mf;
    }

    @Override
    public DBCursor MongoAllList(DBObject ref, DBObject keys) {
        DB db = mongoTemplate.getDb();
        DBCollection collection = db.getCollection("contentMongoEO");//集合名
        DBObject query = new BasicDBObject();
        query.put("_id", "39695");
        DBCursor cursor = collection.find(ref, keys);//.limit(1000);//.skip(76001);//.sort(new BasicDBObject("title", -1));
        return cursor;
    }

}

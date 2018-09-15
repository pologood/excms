package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.RequestUtil;
import cn.lonsun.govbbs.internal.dao.IBbsFileDao;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.govbbs.internal.entity.BbsPostEO;
import cn.lonsun.govbbs.internal.service.IBbsFileService;
import cn.lonsun.govbbs.internal.service.IBbsPostService;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛附件Service实现类<br/>
 *
 * @version v1.0 <br/>
 */
@Service("bbsFileService")
public class BbsFileServiceImpl extends BaseService<BbsFileEO> implements IBbsFileService {

    @Autowired
    private IBbsFileDao bbsFileDao;

    @Autowired
    private IBbsPostService bbsPostService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Override
    public Pagination getPage(FileCenterVO fileVO) {
        if (AppUtil.isEmpty(fileVO.getSiteId())) {
            fileVO.setSiteId(LoginPersonUtil.getSiteId());
        }
        return bbsFileDao.getPage(fileVO);
    }

    @Override
    public void deleteFiles(Long[] ids, Integer isDel) {
        try {
            List<String> mongodbIds = new ArrayList<String>();
            List<BbsFileEO> list = getEntities(BbsFileEO.class, ids);
            for (BbsFileEO file : list) {
                if (isDel == 0) {
                    file.setRecordStatus(BbsFileEO.RecordStatus.Removed.toString());
                } else {
                    if (!StringUtils.isEmpty(file.getMongoId())) {
                        mongodbIds.add(file.getMongoId());
                    }
                }
            }
            if (isDel == 0) {
                updateEntities(list);
            } else {
                bbsFileDao.deleteIds(ids);
                if (mongodbIds != null && mongodbIds.size() > 0) {
                    try {
                        for (int i = 0; i < mongodbIds.size(); i++) {
                            mongoDbFileServer.delete(mongodbIds.get(i), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteWebFiles(Long[] ids) {
        try {
            List<String> mongodbIds = new ArrayList<String>();
            List<Long> delIds = new ArrayList<Long>();
            List<BbsFileEO> list = getEntities(BbsFileEO.class, ids);
            for (BbsFileEO file : list) {
                if(file.getRecordStatus().equals(BbsFileEO.RecordStatus.Removed.toString())){
                    delIds.add(file.getId());
                    if (!StringUtils.isEmpty(file.getMongoId())) {
                        mongodbIds.add(file.getMongoId());
                    }
                }
            }
            if(delIds != null && delIds.size() > 0){
                bbsFileDao.deleteIds(ids);
            }
            if (mongodbIds != null && mongodbIds.size() > 0) {
                try {
                    for (int i = 0; i < mongodbIds.size(); i++) {
                        mongoDbFileServer.delete(mongodbIds.get(i), null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean removeFromDir(Long[] ids) {
        try {
            List<BbsFileEO> list = getEntities(BbsFileEO.class, ids);
            for (BbsFileEO li : list) {
                try {
                    mongoDbFileServer.delete(li.getMongoId(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            delete(BbsFileEO.class, ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void setFilesStatus(Long[] ids, Integer auditStatus, Long caseId, Long postId, Long plateId) {
        try {
            if (ids != null && ids.length > 0) {
                List<BbsFileEO> list = getEntities(BbsFileEO.class, ids);
                for (BbsFileEO li : list) {
                    li.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
                    li.setStatus(1);
                    li.setAuditStatus(auditStatus);
                    li.setPlateId(plateId);
                    li.setCaseId(caseId);
                    li.setPostId(postId);
                }
                updateEntities(list);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public List<BbsFileEO> getBbsFiles(Long caseId) {
        return bbsFileDao.getBbsFiles(caseId);
    }

    @Override
    public BbsFileEO fileUpload(MultipartFile filedata, HttpServletRequest request, BbsFileEO file) {
        MongoFileVO mongo = null;

        mongo = mongoDbFileServer.uploadMultipartFile(filedata, null);

        try {
            file.setMd5(MD5Util.getMd5ByByte(filedata.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.setFileSize(filedata.getSize());
        if (filedata.getSize() != 0) {
            try {
                java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
                Long size = filedata.getSize();
                Integer t = 1024;
                file.setFileSizeKb(df.format(size.doubleValue() / t.doubleValue()));
            } catch (Exception e) {
            }
        }
        file.setFileName(mongo.getFileName());
        file.setSuffix(mongo.getContentType());
        file.setMongoId(mongo.getMongoId());
        file.setStatus(0);
        if (AppUtil.isEmpty(file.getSiteId())) {
            file.setSiteId(LoginPersonUtil.getSiteId());
        }
        if(AppUtil.isEmpty(file.getCreateUserId())){
            file.setCreateUserId(LoginPersonUtil.getUserId());
        }
        if(AppUtil.isEmpty(file.getCreateOrganId())){
            file.setCreateOrganId(LoginPersonUtil.getOrganId());
        }
        if(AppUtil.isEmpty(file.getCreateUserName())){
            file.setCreateUserName(LoginPersonUtil.getUserName());
        }
        file.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
        file.setType(FileCenterEO.Type.NotDefined.toString());
        file.setCode(BbsFileEO.Code.Govbbs.toString());
        file.setIp(RequestUtil.getIpAddr(request));
        saveEntity(file);
        return file;
    }

    @Override
    public void deleteBycaseId(Long[] caseIds, Integer isDel) {
        List<String> mongodbIds = new ArrayList<String>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseIds);
        List<BbsFileEO> files = getEntities(BbsFileEO.class, params);
        if (files != null && files.size() > 0) {
            for (BbsFileEO file : files) {
                if (isDel == 0) {
                    file.setRecordStatus(BbsFileEO.RecordStatus.Removed.toString());
                } else {
                    if (!StringUtils.isEmpty(file.getMongoId())) {
                        mongodbIds.add(file.getMongoId());
                    }
                }
            }
            if (isDel == 0) {
                updateEntities(files);
            } else {
                delete(files);
                if (mongodbIds != null && mongodbIds.size() > 0) {
                    try {
                        for (int i = 0; i < mongodbIds.size(); i++) {
                            mongoDbFileServer.delete(mongodbIds.get(i), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void restoreFiles(Long[] caseIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", caseIds);
        List<BbsFileEO> files = getEntities(BbsFileEO.class, params);
        if (files != null && files.size() > 0) {
            for (BbsFileEO file : files) {
                file.setRecordStatus(BbsFileEO.RecordStatus.Normal.toString());
            }
            updateEntities(files);
        }
    }

    @Override
    public void updateFilesPlateId(BbsPostEO bbsPost) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("postId", bbsPost.getPostId());
        List<BbsFileEO> files = getEntities(BbsFileEO.class, params);
        if (files != null && files.size() > 0) {
            for (BbsFileEO file : files) {
                file.setPlateId(bbsPost.getPlateId());
            }
            updateEntities(files);
        }
    }

    @Override
    public void setFilesAuditStatus(Long[] postId, Integer auditStatus) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("caseId", postId);
        List<BbsFileEO> files = getEntities(BbsFileEO.class, params);
        if (files != null && files.size() > 0) {
            for (BbsFileEO file : files) {
                file.setAuditStatus(auditStatus);
            }
            updateEntities(files);
        }
    }


    /**
     * 是否有图片或者附件
     * @param fileIds
     * @return
     */
    @Override
    public Integer getFileSuffix(Long[] fileIds) {
        Integer type = 0;
        if(fileIds != null && fileIds.length > 0){
            List<BbsFileEO> files = getEntities(BbsFileEO.class, fileIds);
            if (files != null && files.size() > 0) {
                Boolean hasFile = false;
                Boolean hasImg = false;
                String imgs="*.png;*.jpg;*.jpeg;*.gif;";
                String fs="*.doc;*.docx;*.xls;*.xlsx;*.txt";
                for (BbsFileEO file : files) {
                    if(!StringUtils.isEmpty(file.getSuffix())){
                        if(imgs.indexOf(file.getSuffix().toLowerCase()) > -1){
                            hasImg = true;
                        }else if(fs.indexOf(file.getSuffix().toLowerCase()) > -1){
                            hasFile = true;
                        }
                    }
                }
                if(hasFile){
                    type = 1;
                }
                if(hasImg){
                    type = 2;
                }
                if(hasImg && hasFile){
                    type = 3;
                }

            }
        }
        return type;
    }

    @Override
    public void updatePostFilesSuffix(Long postId) {
        if(postId != null){
            BbsPostEO post = bbsPostService.getEntity(BbsPostEO.class,postId);
            if(post != null){
                Integer type = 0;
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("caseId", postId);
                params.put("recordStatus", "Normal");
                params.put("auditStatus", 1);
                params.put("status", 1);
                List<BbsFileEO> files = getEntities(BbsFileEO.class, params);
                if (files != null && files.size() > 0) {
                    Boolean hasFile = false;
                    Boolean hasImg = false;
                    String imgs="*.png;*.jpg;*.jpeg;*.gif;";
                    String fs="*.doc;*.docx;*.xls;*.xlsx;*.txt";
                    for (BbsFileEO file : files) {
                        if(!StringUtils.isEmpty(file.getSuffix())){
                            if(imgs.indexOf(file.getSuffix().toLowerCase()) > -1){
                                hasImg = true;
                            }else if(fs.indexOf(file.getSuffix().toLowerCase()) > -1){
                                hasFile = true;
                            }
                        }
                    }
                    if(hasFile){   type = 1; }
                    if(hasImg){    type = 2; }
                    if(hasImg && hasFile){ type = 3;  }

                }
                post.setHasFile(type);
                bbsPostService.updateEntity(post);
            }

        }
    }
}


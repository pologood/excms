package cn.lonsun.special.internal.service.impl;

import cn.lonsun.GlobalConfig;
import cn.lonsun.activemq.MessageSender;
import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.FileUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.core.util.ZipUtil;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.mongodb.base.IMongoDbFileServer;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.special.internal.dao.ISpecialThemeDao;
import cn.lonsun.special.internal.entity.SpecialDownloadLogEO;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import cn.lonsun.special.internal.service.ISpecialDownloadLogService;
import cn.lonsun.special.internal.service.ISpecialSkinsService;
import cn.lonsun.special.internal.service.ISpecialThemeService;
import cn.lonsun.special.internal.vo.SpecialCloudThemeVO;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;
import cn.lonsun.special.internal.vo.SpecialThumbVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import cn.lonsun.webservice.special.ISpecialWebService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.FileUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created by zhushouyong on 2016-10-15.
 */
@Service
public class SpecialThemeServiceImpl extends MockService<SpecialThemeEO> implements ISpecialThemeService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    @Autowired
    private ISpecialThemeDao specialThemeDao;

    @Autowired
    private ISpecialSkinsService specialSkinsService;

    @Autowired
    private IMongoDbFileServer mongoDbFileServer;

    @Autowired
    private ISpecialWebService specialWebService;

    @Autowired
    private ISpecialDownloadLogService specialDownloadLogService;

    @Autowired
    private TaskExecutor executor;
    /**
     * 分析样式
     *
     * @param themeId
     */
    public List<Element> parseSkins(Element elm, Long themeId) {
        return parseSkins(elm, themeId, LoginPersonUtil.getSiteId());
    }
    /**
     * 分析样式
     * @param themeId
     */
    public List<Element> parseSkins(Element elm, Long themeId, Long siteId) {

        //提取配置模板的节点
        List<Element> item = elm.selectNodes(SpecialConfig.SKINS_NODE);
        for (Element ele : item) {

            String name = ele.selectSingleNode("Name").getText();
            String color = ele.selectSingleNode("Color").getText();
            String cssPath = ele.selectSingleNode("CssPath").getText();
            Integer defaults = Integer.valueOf(ele.selectSingleNode("Defaults").getText());
            if (name != null) {

                //创建
                SpecialSkinsEO eo = new SpecialSkinsEO();
                eo.setName(name);
                eo.setSiteId(siteId);
                eo.setColor(color);
                eo.setPath(cssPath);
                eo.setDefaults(defaults);
                eo.setThemeId(themeId);
                eo.setSpecialID(0L);
                specialSkinsService.saveSpecialSkins(eo);

            }

        }
        return item;
    }

    /**
     * 分析缩略图
     *
     * @param specialPath
     */
    public List<Map<String, Object>> parseThumbnail(Element elm, String specialPath) {
        return parseThumbnail(elm, specialPath, LoginPersonUtil.getSiteId(),LoginPersonUtil.getUserId(), LoginPersonUtil.getOrganId());
    }
    /**
     * 分析缩略图
     *
     * @param specialPath
     */
    public List<Map<String, Object>> parseThumbnail(Element elm, String specialPath, Long siteId, Long userId, Long organId) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        //提取缩略图的节点
        List<Element> item = elm.selectNodes(SpecialConfig.THUMB_NODE);
        for (Element ele : item) {

            String name = ele.selectSingleNode("Name").getText();
            String path = ele.selectSingleNode("Path").getText();
            Integer defaults = Integer.valueOf(ele.selectSingleNode("Defaults").getText());
            if (name != null) {

                //读取文件
                File file = new File(config.getSpecialPath().concat(File.separator).concat(specialPath).concat(File.separator).concat(path));
                if (file.exists()) {
                    // 上传
                    byte[] fileByte = new byte[0];
                    try {
                        fileByte = FileUtils.readFileToByteArray(file);
                        FileCenterEO fileCenter = new FileCenterEO();
                        fileCenter.setStatus(1);
                        fileCenter.setFileName(name);
                        fileCenter.setSuffix(FileUtil.getSuffix(path));
                        fileCenter.setType(FileCenterEO.Type.Image.toString());
                        fileCenter.setCode(BaseContentEO.TypeCode.specialNews.toString());
                        fileCenter.setStatus(0);
                        fileCenter.setSiteId(siteId);
                        fileCenter.setCreateDate(new Date());
                        fileCenter.setCreateUserId(userId);
                        fileCenter.setCreateOrganId(organId);
                        fileCenter.setUpdateDate(new Date());
                        fileCenter.setDesc("视频上传");
                        fileCenter.setRecordStatus("Normal");
                        MongoFileVO mongvo = FileUploadUtil.uploadUtil(fileByte, fileCenter);

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", name);
                        map.put("path", mongvo.getFileName());
                        map.put("defaults", defaults);
                        list.add(map);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
        return list;
    }


    @Override
    public Pagination getCloudList(Map<String, Object> param) {
        param.put("siteId", LoginPersonUtil.getSiteId());
        Pagination page = specialWebService.getSpecialThemeList(JSONObject.toJSONString(param));
        if(page != null && page.getData() != null){
            List<SpecialCloudThemeVO> data = new ArrayList<SpecialCloudThemeVO>();
            List<Long> ids = new ArrayList<Long>();
            for(Object obj : page.getData()){
                SpecialCloudThemeVO theme = JSONObject.parseObject(JSONObject.toJSONString(obj),SpecialCloudThemeVO.class);
                try {
                    theme.setHot((Boolean) ((Map<String, Object>)obj).get("hot"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ids.add(theme.getId());
                data.add(theme);
            }
            //获取已下载的主题
            List<SpecialDownloadLogEO> exits = specialDownloadLogService.getByCloudIds(ids.toArray(new Long[]{}), LoginPersonUtil.getSiteId());
            Map<Long, SpecialDownloadLogEO> map = new HashMap<Long, SpecialDownloadLogEO>();
            for(SpecialDownloadLogEO log : exits){
                if(!map.containsKey(log.getCloudId())){
                    map.put(log.getCloudId(), log);
                } else if(map.get(log.getCloudId()).getVersion() < log.getVersion()){
                    map.put(log.getCloudId(), log);
                }
            }
            for(SpecialCloudThemeVO item : data){
                SpecialDownloadLogEO log = map.get(item.getId());
                if(log != null){
                    item.setLocalVersion(log.getVersion());
                    item.setLocalId(log.getSpecialThemeId());
                    item.setDownloadStatus(log.getStatus());
                }
            }
            page.setData(data);
        }
        return page;
    }

    /**
     * 保存专题主题
     *
     * @param specialTheme
     */
    @Override
    public void saveSpecialTheme(SpecialThemeEO specialTheme) {

        if (AppUtil.isEmpty(config.getSpecialPath())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "主题模板路径不存在或未配置！");
        }

        Long themeId = specialTheme.getId();
        if (null == specialTheme.getId()) {
            themeId = saveEntity(specialTheme);

            //主题保存路径
            String themePath = new File(config.getSpecialPath()).getPath().concat(File.separator);
            //主题保存成文件路径
            String themeZIP = themePath.concat(specialTheme.getPath());
            String unzipPath = themePath.concat(specialTheme.getPath().replaceAll("\\.","_")).concat(File.separator);
            //保存文件至磁盘
            GridFSDBFile file = mongoDbFileServer.getGridFSDBFile(specialTheme.getPath(), null);
            try {
                //保存文件
                file.writeTo(themeZIP);
                //解压文件
                ZipUtil.unZip(themeZIP, unzipPath);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseRunTimeException(TipsMode.Message.toString(), "主题文件保存失败！");
            }

            //root节点
            Element root = null;
            try {

                root = Dom4jUtils.getXmlRoot(unzipPath + SpecialConfig.SETTING_XML);

                //解析添加主题样式
                List<Element> skinsItem = parseSkins(root, themeId);

            } catch (DocumentException e) {
                e.printStackTrace();
            }

        } else {
            updateEntity(specialTheme);
        }

    }
    /**
     * 保存专题主题
     */
    @Override
    public void downloadSpecialTheme(Long id, Integer version) {
        if(version == null || version == 0){
            version = 1;
        }
        final Long siteId = LoginPersonUtil.getSiteId();
        final Long userId = LoginPersonUtil.getUserId();
        final Long organId = LoginPersonUtil.getOrganId();
        //检查是否正在下载，如果正在下载抛出异常提醒
        List<SpecialDownloadLogEO> downloadLogs = specialDownloadLogService.getByStatus(id, siteId,new Long[]{0l});
        if(downloadLogs != null && !downloadLogs.isEmpty()){
            throw new BaseRunTimeException("专题下载中，请勿重复操作！");
        }
        //检查是否已下载，如果已下载抛出异常提醒
        downloadLogs = specialDownloadLogService.getByStatus(id, siteId,new Long[]{1l});
        if(downloadLogs != null && !downloadLogs.isEmpty()){
            //如果比所有历史版本中的版本号都大，说明是更新操作
            for(SpecialDownloadLogEO item : downloadLogs){
                if(version <= item.getVersion()){
                    throw new BaseRunTimeException("您已下载该专题,请勿重复操作！");
                }
            }
        }

        final Long themeId;
        if(downloadLogs != null && !downloadLogs.isEmpty()){
            themeId = downloadLogs.get(0).getSpecialThemeId();
        }else{
            themeId = null;
        }

        final SpecialCloudThemeVO cloudTheme;
        try {
            cloudTheme = specialWebService.downloadSpecialTheme(id, siteId, SpecialCloudThemeVO.class);
        } catch (BaseRunTimeException e) {
            throw new BaseRunTimeException(e.getTipsMessage());
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                HibernateSessionUtil.execute(new HibernateHandler<SpecialThemeEO>() {

                    private SpecialDownloadLogEO downloadLog;

                    @Override
                    @Transactional
                    public SpecialThemeEO execute() throws Throwable {

                        SpecialThemeEO specialTheme = new SpecialThemeEO();
                        BeanUtils.copyProperties(cloudTheme, specialTheme);
                        specialTheme.setId(themeId);

                        if (AppUtil.isEmpty(config.getSpecialPath())) {
                            throw new BaseRunTimeException("主题模板路径不存在或未配置！");
                        }
                        saveOrUpdateEntity(specialTheme);
                        final Long themeId = specialTheme.getId();

                        //保存下载日志
                        downloadLog = new SpecialDownloadLogEO();
                        downloadLog.setCloudId(cloudTheme.getId());
                        downloadLog.setSiteId(siteId);
                        downloadLog.setStatus(0l);
                        downloadLog.setVersion(cloudTheme.getVersion());
                        downloadLog.setSpecialThemeId(themeId);
                        specialDownloadLogService.saveEntity(downloadLog);

                        final StringBuilder fileName = new StringBuilder();
                        fileName.append(cloudTheme.getPath().substring(cloudTheme.getPath().lastIndexOf("/") + 1, cloudTheme.getPath().length()));
                        if(fileName.indexOf(".") <= 0){
                            fileName.append(".zip");
                        }
                        URL url = new URL(cloudTheme.getPath());
//                        URL url = new URL("http://ex8yanshi.lonsun.cn/mongo/59eea351e4b00b50bf5bcae6");
                        MongoFileVO mongoVo = mongoDbFileServer.uploadByteFile(url.openStream(), fileName.toString(), null, null);
                        specialTheme.setPath(mongoVo.getFileName());

                        //主题保存路径
                        String themePath = new File(config.getSpecialPath()).getPath().concat(File.separator);
                        //主题保存成文件路径
                        String themeZIP = themePath.concat(specialTheme.getPath());
                        //保存文件至磁盘
                        GridFSDBFile file = mongoDbFileServer.getGridFSDBFile(specialTheme.getPath(), null);
                        //保存文件
                        file.writeTo(themeZIP);
                        String unZipFileName = specialTheme.getPath().replaceAll("\\.","_");
                        String unZipFilePath = themePath.concat(File.separator).concat(unZipFileName).concat(File.separator);
                        //解压文件
                        ZipUtil.unZip(themeZIP, unZipFilePath);
                        //root节点
                        Element root = Dom4jUtils.getXmlRoot(unZipFilePath + SpecialConfig.SETTING_XML);
                        List<Map<String, Object>> thumbnail = parseThumbnail(root, unZipFileName, siteId,userId,organId);
                        specialTheme.setImgPath(JSONObject.toJSONString(thumbnail));
                        //解析添加主题样式
                        List<Element> skinsItem = parseSkins(root, themeId, siteId);
                        specialThemeDao.update(specialTheme);
                        return specialTheme;
                    }

                    @Override
                    public SpecialThemeEO complete(SpecialThemeEO result, Throwable exception) {
                        MessageSystemEO message = new MessageSystemEO();
                        message.setMessageType(MessageSystemEO.TIP);
                        message.setTodb(true);// 不需要入库
                        message.setSiteId(siteId);
                        message.setData(true);
                        message.setRecUserIds(String.valueOf(userId));
                        message.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                        if(exception != null){
                            downloadLog.setStatus(2l);
                            logger.error("专题下载失败", exception);
                            message.setTitle("专题《" + cloudTheme.getName() + "》下载失败！");
                            String msg = exception instanceof BaseRunTimeException?((BaseRunTimeException) exception).getTipsMessage():exception.getMessage();
                            message.setContent("专题下载失败原因：" + msg);
                            message.setData(false);
                            message.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                        }else{
                            message.setTitle("专题《" + cloudTheme.getName() + "》下载成功！");
                            message.setContent("下载已完成");
                            downloadLog.setStatus(1l);
                        }
                        //更新下载状态
                        specialDownloadLogService.updateEntity(downloadLog);
                        MessageSender.sendMessage(message);
                        return null;
                    }
                });
            }
        });
    }

    @Override
    public void updateSpecialTheme(Long id) {
        //TODO 更新专题
    }

    /**
     * 获取专题对应的皮肤样式
     *
     * @param queryVO
     */
    @Override
    public Pagination getPagination(SpecialThemeQueryVO queryVO) {

        Pagination page = specialThemeDao.getPagination(queryVO);
        List<SpecialThemeEO> pageList = (List<SpecialThemeEO>) page.getData();
        for (SpecialThemeEO eo : pageList) {
            logger.info("缩略图：" + eo.getImgPath());
            if (!AppUtil.isEmpty(eo.getImgPath())) {
                eo.setThumb(JSONArray.parseArray(eo.getImgPath(),SpecialThumbVO.class));
            }
            eo.setSkins(specialSkinsService.getSkinsItem(queryVO.getSiteId(), eo.getId()));
        }
        return page;
    }

    /**
     * 获取专题缩略图
     *
     * @param queryVO
     */
    @Override
    public SpecialThemeEO getSpecialThumb(SpecialThemeQueryVO queryVO) {
        SpecialThemeEO specialThemeEO = specialThemeDao.getEntity(SpecialThemeEO.class, queryVO.getId());
        if (AppUtil.isEmpty(specialThemeEO)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取的信息不存在！");
        }
        if (!AppUtil.isEmpty(specialThemeEO.getImgPath())) {
            specialThemeEO.setThumb((List<SpecialThumbVO>) JSONArray.parse(specialThemeEO.getImgPath()));
        }
        specialThemeEO.setSkins(specialSkinsService.getSkinsItem(queryVO.getSiteId(), specialThemeEO.getId()));
        return specialThemeEO;
    }


    @Override
    public List<Map<String, Object>> saveSpecialThumb(String path) {

        if (AppUtil.isEmpty(config.getSpecialPath())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "主题模板路径不存在或未配置！");
        }

        //主题保存路径
        String themePath = new File(config.getSpecialPath()).getPath().concat(File.separator);

        //主题保存成文件路径
        String themeZIP = themePath.concat(path);
        String unZipFileName = path.replaceAll("\\.","_");
        String unzipPath = themePath.concat(unZipFileName).concat(File.separator);
        //保存文件至磁盘
        GridFSDBFile file = mongoDbFileServer.getGridFSDBFile(path, null);
        try {
            //保存文件
            file.writeTo(themeZIP);

            //解压文件
            ZipUtil.unZip(themeZIP, unzipPath);

        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "主题文件保存失败！");
        }

        //root节点
        Element root = null;
        try {
            root = Dom4jUtils.getXmlRoot(unzipPath + SpecialConfig.SETTING_XML);
            //解析缩略图,生成JSON字符串
            return parseThumbnail(root, unZipFileName);

        } catch (DocumentException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Message.toString(), "获取缩略图失败！");
        }

    }

}

/*
 * GenerateRunnable.java         2015年9月7日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.staticcenter.generate.thread;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.staticcenter.eo.FileEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.GenerateRecord;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.tag.impl.link.LinkListJsBeanService;
import cn.lonsun.staticcenter.generate.tag.impl.publicInfo.PublicCatalogBeanService;
import cn.lonsun.staticcenter.generate.util.MapUtil;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.util.FileUtil;
import cn.lonsun.staticcenter.util.GenerateUtil;
import cn.lonsun.statictask.internal.entity.TaskInfoEO;
import cn.lonsun.statictask.internal.service.ITaskInfoService;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 静态文件生成 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月7日 <br/>
 */
public class GenerateRunnable implements Runnable {

    private Logger log = LoggerFactory.getLogger("staticGen");

    private Context context;// 上下文对象
    private boolean todb;// 是否入库
    private CountDownLatch countDownLatch; // 线程计数器
    private ITaskInfoService taskInfoService = SpringContextHolder.getBean(ITaskInfoService.class); // 错误任务入库
    private BeanService contentService = SpringContextHolder.getBean(LinkListJsBeanService.class);// 链接管理
    private BeanService publicService = SpringContextHolder.getBean(PublicCatalogBeanService.class);// 信息公开目录

    /**
     * Creates a new instance of GenerateRunnable.
     *
     * @param context
     * @param todb
     * @param countDownLatch
     */
    public GenerateRunnable(Context context, boolean todb, CountDownLatch countDownLatch) {
        this.context = context;
        this.todb = todb;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        // 判断是否异常
        final GenerateRecord record = context.getGenerateRecord();
        if (record.isException()) {
            countDownLatch.countDown();// 这里计数器要减一
            return;// 其他线程发生异常，本线程直接退出
        }
        // 设置上下文
        ContextHolder.setContext(context);
        // 取出上下文信息
        final Long siteId = context.getSiteId();// 站点id
        final Long source = context.getSource();// 来源 信息公开or内容协同
        final String fileType = context.getFileType();// 文件类型 html or js
        final Long columnId = context.getColumnId();// 栏目id
        final Long type = context.getType();// 类型 发布or取消发布
        // 执行hibernate方法，获取文件信息
        HibernateSessionUtil.execute(new HibernateHandler<Boolean>() {

            @Override
            public Boolean execute() throws Throwable {
                // 判断生成上下文是否正常
                if (!StringUtils.isEmpty(context.getErrorMsg())) {
                    throw new GenerateException(context.getErrorMsg());
                }
                // 生成js文件，没有模板，直接调取类
                if (Context.FileType.JS.toString().equals(fileType) && MessageEnum.PUBLISH.value().equals(type)) {
                    List<FileEO> fileList = this.generateJavaScript(new FileEO());
                    boolean flag = true;
                    for(FileEO eo : fileList){
                        flag = flag && FileUtil.generateFile(eo);
                        if(!flag){
                            return false;
                        }
                    }
                }
                // 文件分发
                FileEO fileEO = new FileEO();
                fileEO.setPath(PathUtil.getFilePath());
                //如果不是取消发布栏目页
                if(!(MessageEnum.UNPUBLISH.value().equals(type) &&
                        columnId != null && context.getContentId() == null)){
                    fileEO.setName(PathUtil.getFileName());
                    // 生成的是html文件，发布的话需要调取模板生成内容
                    if (Context.FileType.HTML.toString().equals(fileType) && MessageEnum.PUBLISH.value().equals(type)) {
                        fileEO.setContent(GenerateUtil.generate(context));
                    }
                    // 设置文件路径，主要用来消息推送时，在页面显示
                    context.setLink(fileEO.getPath() + PathUtil.SEPARATOR + fileEO.getName());
                }else{
                    fileEO.setName("");
                    // 设置文件路径，主要用来消息推送时，在页面显示
                    context.setLink(fileEO.getPath() + PathUtil.SEPARATOR);
                }
                return FileUtil.generateFile(fileEO);
            }

            @Override
            public Boolean complete(Boolean result, Throwable exception) {
                try {
                    record.complete(exception); // 计数
                    this.changeGenerateInfo(exception);// 更新任务信息
                    return result;
                } catch (Throwable e) {
                    return false;
                } finally {
                    countDownLatch.countDown();// 这里计数器要减一
                }
            }

            private void changeGenerateInfo(Throwable exception) {

                if (null != exception) {// 更新任务信息
                    StringBuilder sb = new StringBuilder();
                    log.error("生成静态异常", exception);
                    if(todb){
                        TaskInfoEO taskInfoEO = new TaskInfoEO();
                        taskInfoEO.setColumnId(context.getColumnId());
                        taskInfoEO.setArticleId(context.getContentId());
                        taskInfoEO.setTaskId(context.getTaskId());
                        taskInfoEO.setTitle(context.getTitle());
                        taskInfoEO.setLog(HibernateSessionUtil.getStackTrace(exception, GenerateException.class));
                        StringBuilder errorMsg = new StringBuilder();
                        errorMsg.append("<div class='well'>");
                        errorMsg.append(JSONObject.toJSONString(context));
                        errorMsg.append("</div></br>");
                        errorMsg.append(HibernateSessionUtil.getStackTrace(exception, null));
                        taskInfoEO.setDetail(errorMsg.toString());// 打印堆栈信息
                        taskInfoEO.setLink(context.getLink());
                        taskInfoService.saveEntity(taskInfoEO);
                    }
                }
            }

//            /**
//             * 生成js文件
//             *
//             * @author fangtinghua
//             * @param fileEO
//             * @throws GenerateException
//             */
//            private void generateJavaScript(FileEO fileEO) throws GenerateException {
//                JSONObject paramObj = new JSONObject();// 标签解析参数
//                MapUtil.unionContextToJson(paramObj);// 合并上下文参数到标签参数中
//                // 生成文件分发对象
//                if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开，生成目录与部门对应关系js文件，传入的是站点id
//                    fileEO.setName(siteId + ".js");// js文件
//                    paramObj.put(GenerateConstant.ID, siteId);
//                    Object resultObj = publicService.getObject(paramObj);// 查询结果
//                    fileEO.setContent(publicService.objToStr(null, resultObj, paramObj)); // 设置内容
//                } else if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同传入的是栏目id
//                    fileEO.setPath(PathUtil.getFilePath());
//                    fileEO.setName(columnId + ".js");// js文件
//                    if (MessageEnum.PUBLISH.value().equals(type)) {// 发布的话需要重新生成js内容
//                        // 获取栏目配置信息
//                        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
//                        // 设置栏目配置信息参数
//                        paramObj.put("isLogo", columnConfigEO.getIsLogo());
//                        paramObj.put("width", columnConfigEO.getWidth());
//                        paramObj.put("height", columnConfigEO.getHeight());
//                        paramObj.put("num", columnConfigEO.getNum());
//                        // 查询结果
//                        Object resultObj = contentService.getObject(paramObj);
//                        // 设置内容，替换换行符
//                        fileEO.setContent(contentService.objToStr(null, resultObj, paramObj));
//                    }
//                }
//            }

            /**
             * 生成js文件
             *
             * @author fangtinghua
             * @param fileEO
             * @throws GenerateException
             */
            private List<FileEO> generateJavaScript(FileEO fileEO) throws GenerateException {
                List<FileEO> result = new ArrayList<FileEO>();
                result.add(fileEO);
                JSONObject paramObj = new JSONObject();// 标签解析参数
                MapUtil.unionContextToJson(paramObj);// 合并上下文参数到标签参数中
                // 生成文件分发对象
                if (MessageEnum.PUBLICINFO.value().equals(source)) {// 信息公开，生成目录与部门对应关系js文件，传入的是站点id
                    fileEO.setName(siteId + ".js");// js文件
                    paramObj.put(GenerateConstant.ID, siteId);
                    Object resultObj = publicService.getObject(paramObj);// 查询结果
                    fileEO.setContent(publicService.objToStr(null, resultObj, paramObj)); // 设置内容
                    //循环生成各个单位的目录js
                    Map<Long, Object> catalogs = (Map<Long, Object>)resultObj;
                    for(Map.Entry<Long, Object> item : catalogs.entrySet()){
                        FileEO f = new FileEO();
                        BeanUtils.copyProperties(fileEO, f);
                        fileEO.setName(siteId +"/"+ item.getKey() + ".js");// js文件
                        paramObj.put(PublicCatalogBeanService.subOrgan, item.getKey());
                        fileEO.setContent(publicService.objToStr(null, resultObj, paramObj)); // 设置内容
                        result.add(f);
                    }
                } else if (MessageEnum.CONTENTINFO.value().equals(source)) {// 内容协同传入的是栏目id
                    fileEO.setPath(PathUtil.getFilePath());
                    fileEO.setName(columnId + ".js");// js文件
                    if (MessageEnum.PUBLISH.value().equals(type)) {// 发布的话需要重新生成js内容
                        // 获取栏目配置信息
                        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
                        // 设置栏目配置信息参数
                        paramObj.put("isLogo", columnConfigEO.getIsLogo());
                        paramObj.put("width", columnConfigEO.getWidth());
                        paramObj.put("height", columnConfigEO.getHeight());
                        paramObj.put("num", columnConfigEO.getNum());
                        // 查询结果
                        Object resultObj = contentService.getObject(paramObj);
                        // 设置内容，替换换行符
                        fileEO.setContent(contentService.objToStr(null, resultObj, paramObj));
                    }
                }
                return result;
            }
        });
    }
}
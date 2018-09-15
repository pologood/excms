/*
 * Context.java         2015年9月11日 <br/>
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

import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.template.internal.entity.TemplateConfEO;
import cn.lonsun.staticcenter.generate.GenerateRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * 上下文对象 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年9月11日 <br/>
 */
public class Context {

    /**
     * 文件类型 <br/>
     *
     * @author fangtinghua
     * @date: 2016年6月3日 上午8:24:07 <br/>
     */
    public enum FileType {
        HTML, JS
    }

    /**
     * 模板来源 <br/>
     *
     * @author fangtinghua
     * @date: 2016年6月3日 上午8:24:07 <br/>
     */
    public enum From {
        PC, WAP
    }

    private Long siteId = null;// 站点id
    private String siteType = null;// 站点类型
    private String uri = null;// 站点域名
    private Long columnId = null;// 栏目id或者部门id
    private String urlPath;//渲染后的完整地址
    private Long contentId = null;// 内容id
    private String typeCode = null;// 文章类型
    private TemplateConfEO templateConfEO = null;// 模板
    private Long pageIndex = 1l;// 当前的页数
    private Long type = null;// 类型1.发布 0.取消发布
    private Long scope = null;// 范围1.首页 2.栏目页 3.文章页 针对全站生成情况
    private Long source = null;// 来源
    private Map<String, String> paramMap = new HashMap<String, String>();// 动态生成请求时，url传进来的参数
    private String module = null;// 模块动态请求时，传入
    private String action = null;// 模块动态请求时，传入
    private String fileType = FileType.HTML.toString();// 文件类型 js、html
    private String path = null;// 动态请求路径
    private String param = null;// 动态请求参数，不带分页参数
    private GenerateRecord generateRecord = null;// 生成记录
    private Long userId = null;// 生成用户id
    private String errorMsg = null;// 用来存放当前生成错误信息
    private Long taskId = null;// 生成任务id
    private String title = null;// 当前生成栏目或者文章标题
    private String link = null;// 文件路径
    private long time = 0L;// 时间
    private String from = From.PC.toString();// pc、wap
    private Object userData;// 用户自定义数据，用来在页面判断
    private Boolean linkType = true;//标签的链接类型 true 为静态地址，false为动态地址

    public Long getSiteId() {
        return siteId;
    }

    public Context setSiteId(Long siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getSiteType() {
        return siteType;
    }

    public Context setSiteType(String siteType) {
        this.siteType = siteType;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public Context setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Long getColumnId() {
        return columnId;
    }

    public Context setColumnId(Long columnId) {
        this.columnId = columnId;
        return this;
    }

    public Long getContentId() {
        return contentId;
    }

    public Context setContentId(Long contentId) {
        this.contentId = contentId;
        return this;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public Context setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public TemplateConfEO getTemplateConfEO() {
        return templateConfEO;
    }

    public Context setTemplateConfEO(TemplateConfEO templateConfEO) {
        this.templateConfEO = templateConfEO;
        return this;
    }

    public Long getPageIndex() {
        return pageIndex;
    }

    public Context setPageIndex(Long pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public Long getType() {
        return type;
    }

    public Context setType(Long type) {
        this.type = type;
        return this;
    }

    public Long getScope() {
        return scope;
    }

    public Context setScope(Long scope) {
        this.scope = scope;
        return this;
    }

    public Long getSource() {
        return source;
    }

    public Context setSource(Long source) {
        this.source = source;
        return this;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public Context setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
        return this;
    }

    public String getModule() {
        return module;
    }

    public Context setModule(String module) {
        this.module = module;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Context setAction(String action) {
        this.action = action;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public Context setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getPath() {
        return path;
    }

    public Context setPath(String path) {
        this.path = path;
        return this;
    }

    public String getParam() {
        return param;
    }

    public Context setParam(String param) {
        this.param = param;
        return this;
    }

    public GenerateRecord getGenerateRecord() {
        return generateRecord;
    }

    public Context setGenerateRecord(GenerateRecord generateRecord) {
        this.generateRecord = generateRecord;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Context setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Context setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Context setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Context setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getLink() {
        return link;
    }

    public Context setLink(String link) {
        this.link = link;
        return this;
    }

    public long getTime() {
        return time;
    }

    public Context setTime(long time) {
        this.time = time;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public Context setFrom(String from) {
        this.from = from;
        return this;
    }

    public Object getUserData() {
        return userData;
    }

    public Context setUserData(Object userData) {
        this.userData = userData;
        return this;
    }

    public Boolean getLinkType() {
        return linkType;
    }

    public void setLinkType(Boolean linkType) {
        this.linkType = linkType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnId == null) ? 0 : columnId.hashCode());
        result = prime * result + ((contentId == null) ? 0 : contentId.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Context other = (Context) obj;
        if (columnId == null) {
            if (other.columnId != null)
                return false;
        } else if (!columnId.equals(other.columnId))
            return false;
        if (contentId == null) {
            if (other.contentId != null)
                return false;
        } else if (!contentId.equals(other.contentId))
            return false;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (scope == null) {
            if (other.scope != null)
                return false;
        } else if (!scope.equals(other.scope))
            return false;
        if (siteId == null) {
            if (other.siteId != null)
                return false;
        } else if (!siteId.equals(other.siteId))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("站点:").append(siteId);
        if (null != columnId) {
            if (MessageEnum.PUBLICINFO.value().equals(source)) {
                sb.append(",部门:").append(columnId);
            } else if (MessageEnum.CONTENTINFO.value().equals(source)) {
                sb.append(",栏目:").append(columnId);
            }
        }
        if (null != contentId) {
            sb.append(",文章:").append(contentId);
        }
        if (null != scope) {// 范围
            if (MessageEnum.INDEX.value().equals(scope)) {
                sb.append(",范围:").append(MessageEnum.INDEX.getName());
            } else if (MessageEnum.COLUMN.value().equals(scope)) {
                sb.append(",范围:").append(MessageEnum.COLUMN.getName());
            } else if (MessageEnum.CONTENT.value().equals(scope)) {
                sb.append(",范围:").append(MessageEnum.CONTENT.getName());
            }
        }
        if (null != templateConfEO) {
            sb.append(",模板:").append(templateConfEO.getId());
        }
        if (null != paramMap && !paramMap.isEmpty()) {
            sb.append(",参数列表:{");
            int index = 0;// 下标
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                if (index++ > 0) {
                    sb.append(",");
                }
                sb.append(entry.getKey()).append(":").append(entry.getValue());
            }
            sb.append("}");
        }
        return sb.append("}").toString();
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
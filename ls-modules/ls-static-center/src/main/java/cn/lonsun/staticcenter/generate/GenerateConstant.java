/*
 * GenerateConstant.java         2015年11月25日 <br/>
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

package cn.lonsun.staticcenter.generate;

/**
 * 常量工具类<br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月25日 <br/>
 */
public class GenerateConstant {
    /**
     * 标签名称，不是当做参数传入，因此需要特殊定义名称，防止返回的json对象列表覆盖现象
     */
    public static final String LABEL_NAME = "_label_name_";
    /**
     * 是否查询子节点
     */
    public static final String IS_CHILD = "isChild";
    /**
     * 是否需要分页
     */
    public static final String IS_PAGE = "isPage";
    /**
     * 栏目id或者文章id
     */
    public static final String ID = "id";
    /**
     * 条数
     */
    public static final String NUM = "num";
    /**
     * 是否要写标题
     */
    public static final String IS_TITLE = "isTitle";
    /**
     * 条件
     */
    public static final String WHERE = "where";
    /**
     * 排序
     */
    public static final String ORDER = "order";
    /**
     * 类型
     */
    public static final String TYPE = "type";
    /**
     * 动作
     */
    public static final String ACTION = "action";
    /**
     * 高级功能，根据传入的自定义模板文件进行解析
     */
    public static final String FILE = "file";
    /**
     * 分页大小
     */
    public static final String PAGE_SIZE = "pageSize";

    public static final String TIME_STR = "timeStr";

    public static final String DATE_FORMAT = "dateFormat";

    public static final String START_TIME = "startTime";

    public static final String END_TIME = "endTime";

    public static final String ORGAN_ID = "organId";

    /**
     * 在线访谈区分字段 1 访谈预告 2 正在访谈 3 历史回顾
     */
    public static final String ITYPE = "itype";
    /**
     * 排除字段
     */
    public static final String EXCLUDE = "exclude";

    /**
     * 排除条数
     */
    public static final String ROWNUM = "rowNum";

    /**
     * 标签ID
     */
    public static final String LABEL_ID = "labelId";

    /**
     * 关联标签
     */
    public static final String REFER_LABEL = "referLabel";

}
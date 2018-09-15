/*
 * PublicTypeMap.java         2016年3月1日 <br/>
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

package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;

/**
 * 信息公开常量 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年3月1日 <br/>
 */
public class PublicConstant {

    public static final String CID = "cId";// 所属分类id
    public static final String NAME = "name";// 导航提示名称
    public static final String CATID = "catId";// 目录id
    public static final String RELCATIDS = "relCatIds";// 查询目录和部门对应关系时，需要查询的目录id
    public static final String INCLUDEIDS = "includeIds";// 包含的目录id
    public static final String EXCLUDEIDS = "excludeIds";// 排除的目录id
    public static final String CATNAME = "catName";// 目录名称
    public static final String ACTION_REL = "rel";// 查询目录和部门对应关系
    public static final String ACTION_NUM = "num";// 查询条数
    public static final String ACTION_LIST = "list";// 查询分页列表
    public static final String APPLYFILE = "applyFile";// 依申请公开文件名
    private static Map<String, String> TYPEMAP = null;// 所有类型map

    /**
     * 信息公开类型 ADD REASON. <br/>
     *
     * @author fangtinghua
     * @date: 2016年7月7日 上午8:48:33 <br/>
     */
    public enum PublicTypeEnum {
        PUBLIC_INSTITUTION("1", PublicContentEO.Type.PUBLIC_INSTITUTION.toString()), // 公开制度
        PUBLIC_GUIDE("2", PublicContentEO.Type.PUBLIC_GUIDE.toString()), // 公开指南
        PUBLIC_ANNUAL_REPORT("3", PublicContentEO.Type.PUBLIC_ANNUAL_REPORT.toString()), // 公开年报
        DRIVING_PUBLIC("4", PublicContentEO.Type.DRIVING_PUBLIC.toString()), // 主动公开
        PUBLIC_APPLY("5", PublicApplyEO.PUBLIC_APPLY), // 依申请公开

        PUBLIC_POINTS("6", PublicContentEO.Type.PUBLIC_POINTS.toString()),
        PUBLIC_TREND("7", PublicContentEO.Type.PUBLIC_TREND.toString());

        private String key;
        private String value;

        private PublicTypeEnum(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public static String getType(String type) {
            mapInit();
            return TYPEMAP.get(type);
        }

        public static String getTypeByValue(String value) {
            mapInit();
            for (Entry<String, String> e : TYPEMAP.entrySet()) {
                if (e.getValue().equals(value)) {
                    return e.getKey();
                }
            }
            return "";
        }

        public static boolean containsKey(String type) {
            mapInit();
            return TYPEMAP.containsKey(type);
        }

        private static void mapInit() {
            // map初始化
            if (null == TYPEMAP) {
                synchronized (PublicTypeEnum.class) {
                    if (null == TYPEMAP) {
                        TYPEMAP = new HashMap<String, String>();
                        TYPEMAP.put(PUBLIC_INSTITUTION.getKey(), PUBLIC_INSTITUTION.getValue());
                        TYPEMAP.put(PUBLIC_GUIDE.getKey(), PUBLIC_GUIDE.getValue());
                        TYPEMAP.put(PUBLIC_ANNUAL_REPORT.getKey(), PUBLIC_ANNUAL_REPORT.getValue());
                        TYPEMAP.put(DRIVING_PUBLIC.getKey(), DRIVING_PUBLIC.getValue());
                        TYPEMAP.put(PUBLIC_APPLY.getKey(), PUBLIC_APPLY.getValue());
                        TYPEMAP.put(PUBLIC_POINTS.getKey(), PUBLIC_POINTS.getValue());
                        TYPEMAP.put(PUBLIC_TREND.getKey(), PUBLIC_TREND.getValue());
                    }
                }
            }
        }
    }
}
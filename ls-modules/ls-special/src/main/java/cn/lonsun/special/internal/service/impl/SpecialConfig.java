package cn.lonsun.special.internal.service.impl;

import cn.lonsun.GlobalConfig;
import cn.lonsun.core.util.SpringContextHolder;

import java.io.File;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.service.impl
 * @description: ${todo}
 * @date 2017/1/17
 */
public class SpecialConfig {

    private final static GlobalConfig config = SpringContextHolder.getBean(GlobalConfig.class);

    public enum modelCode {
        specialVirtual // 虚拟栏目
    }

    public enum tplCode {
        specialVirtual_Column,// 虚拟模型栏目模板
        specialVirtual_Content,// 虚拟模型详细页模板
        special_column,// 专题栏目页模板类型
        special_content// 专题详细页模板类型
    }

    /**
     * 主题配置文件
     */
    public static final String SETTING_XML = "setting.xml";

    /**
     * 主题打包扩展名带点
     */
    public static final String THEME_SUFFIX = ".zip";

    /**
     * 公共模板节点
     */
    public static final String COMMONTEMPLATE_NODE = "//CommonTemplate//Item";

    /**
     * 模板节点
     */
    public static final String TEMPLATE_NODE = "//Template//Item";

    /**
     * 模型节点
     */
    public static final String MODEL_NODE = "//Model//Item";

    /**
     * 栏目节点
     */
    public static final String COLUMN_NODE = "//Column//Item";

    /**
     * 皮肤节点
     */
    public static final String SKINS_NODE = "//Skins//Item";

    /**
     * 缩略图节点
     */
    public static final String THUMB_NODE = "//Thumbnails//Item";

    public static String getUnzipFilePath(String zipFileName){
        //主题保存路径
        String themePath = new File(config.getSpecialPath()).getPath().concat(File.separator);

        String unZipFileName = zipFileName.replaceAll("\\.","_");
        return themePath.concat(File.separator).concat(unZipFileName).concat(File.separator);
    }

    public static String getSettingPath(String zipFileName){
        return getUnzipFilePath(zipFileName) + SETTING_XML;
    }

}

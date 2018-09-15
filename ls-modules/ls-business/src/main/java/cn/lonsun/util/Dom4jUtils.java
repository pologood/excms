package cn.lonsun.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.util
 * @description: ${todo}
 * @date 2017/1/17
 */
public class Dom4jUtils {

    public static Element getXmlRoot(String path) throws DocumentException {

        if (AppUtil.isEmpty(path)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "XML 路径不能为空！");
        }

        SAXReader reader = new SAXReader();
        Document document = null;

        //读取配置文件
        File xmlPath = new File(path);
        if (xmlPath.exists()) {

            //读取xml
            document = reader.read(xmlPath);

            //root节点
            Element root = document.getRootElement();

            return root;

        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "XML 文件不存在！");
        }

    }

}

package cn.lonsun.special.util;

import cn.lonsun.common.util.AppUtil;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.util
 * @description: ${todo}
 * @date 2017/4/24
 */
public class Dom4jUtil {

    public static String selectSingleNode(Element ele, String nodeName) {
        String nodeVal = "";
        if (!AppUtil.isEmpty(ele) && !AppUtil.isEmpty(nodeName)) {
            Node node = ele.selectSingleNode(nodeName);
            if (!AppUtil.isEmpty(node)) {
                nodeVal = node.getText();
            }
        }
        return nodeVal;
    }

}

package cn.lonsun.special.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.service
 * @description: ${todo}
 * @date 2016/12/28
 */
@Component
public class SimpleLabelParseUtil {

    private static ContentMongoServiceImpl contentMongoService = SpringContextHolder.getBean(ContentMongoServiceImpl.class);

    // 该正则表达式支持两种闭合方式、支持标签嵌套、支持取单独属性，暂不支持相同标签嵌套
    private static final String LIST_REGEX = "\\{ls:([\\w]+)([^\\}]*)(?:/\\}|\\}(.*?)\\{/ls:\\1\\})";

    // 两种模式，为了应对换行的匹配
    private static final Pattern LIST_PATTERN = Pattern.compile(LIST_REGEX, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * 标签体 ADD REASON. <br/>
     *
     * @author fangtinghua
     * @date: 2016年8月22日 上午11:02:56 <br/>
     */
    private static class LabelEntry {
        private String labelName;
        private String param;

        public LabelEntry(String labelName, String param) {
            super();
            this.labelName = labelName;
            this.param = param;
        }
    }

    /**
     * 解析参数，针对相同参数做缓存处理
     *
     * @param param
     * @return
     * @author fangtinghua
     */
    public static Map<String, String> parseText(String param) {
        Map<String, String> map = new HashMap<String, String>();// 参数map
        // 判断空
        if (StringUtils.isEmpty(param)) {
            return map;
        }
        try {
            Document document = DocumentHelper.parseText("<Regex " + param.replaceAll("&", "&amp;") + "></Regex>");
            Element element = (Element) document.selectSingleNode("Regex");
            Iterator<?> it = element.attributeIterator();
            while (it.hasNext()) {
                Attribute attr = (Attribute) it.next();
                map.put(attr.getName(), attr.getValue());
            }
            return map;
        } catch (Throwable e) {
            throw new BaseRunTimeException("参数[" + param + "]解析错误.");
        }
    }

    public static String parseLabel(String content) {

        int index = 0;
        // 结果
        StringBuffer sb = new StringBuffer();
        // 合并标签解析
        List<LabelEntry> labelEntryList = new ArrayList<LabelEntry>();
        // 正则匹配，当解析指定标签时， 不预处理文章类型，防止文章标题被替换
        Matcher m = LIST_PATTERN.matcher(content);
        // 查找
        while (m.find()) {
            // 标签名称
            String labelName = m.group(1).trim();
            // 当有指定标签时，必须包含指定标签才解析
            if (labelName.equals("include")) {
                // 参数
                String param = m.group(2).trim();
                // 放入list中
                labelEntryList.add(new SimpleLabelParseUtil.LabelEntry(labelName, param));
                // 替换
                m.appendReplacement(sb, "\\{" + labelName + ":" + index++ + "\\}");
            }
        }

        m.appendTail(sb);
        //返回字符串
        String result = sb.toString();

        // 解析
        if (!labelEntryList.isEmpty()) {
            index = 0;// 重新计数
            for (LabelEntry entry : labelEntryList) {

                //标签名称
                String labelName = entry.labelName;

                // 转换参数并检查类型
                Map<String, String> paramObj = parseText(entry.param);

                String r = "";
                if (!AppUtil.isEmpty(paramObj.get("id"))) {
                    Long tplId = Long.valueOf(paramObj.get("id"));
                    ContentMongoEO eo = contentMongoService.queryById(tplId);
                    // 查询结果
                    if (eo != null && !"".equals(eo)) {
                        r = StringUtils.trim(eo.getContent());
                    }
                }

                // 由于$出现在replacement中时，表示对捕获组的反向引用，所以要对上面替换内容中的$进行替换
                result = result.replaceFirst("\\{" + labelName + ":" + index++ + "\\}", Matcher.quoteReplacement(r));
            }
        }

        return result;
    }
}

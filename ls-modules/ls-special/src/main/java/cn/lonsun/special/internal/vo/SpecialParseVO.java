package cn.lonsun.special.internal.vo;

import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.entity.SpecialThemeEO;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Doocal
 * @title: ${todo}
 * @package cn.lonsun.special.internal.vo
 * @description: ${todo}
 * @date 2016/12/9
 */
public class SpecialParseVO {

    // 模板xml根对象
    private Element elementRoot = null;

    // 模板 xml 对象
    private Element element = null;

    // 模板对象
    private Map<String, SpecialTemplateVO> templateMap = new HashMap<String, SpecialTemplateVO>();

    // 专题模型
    private Map<String, SpecialModelVO> modelMap = new HashMap<String, SpecialModelVO>();

    // 解析添加栏目
    private Map<String, SpecialColumnVO> columnMap = new HashMap<String, SpecialColumnVO>();

    // 当前专题对象
    private SpecialEO specialEO = new SpecialEO();

    // 当前模板主题对象
    private SpecialThemeEO specialThemeEO = new SpecialThemeEO();

    public Element getElementRoot() {
        return elementRoot;
    }

    public void setElementRoot(Element elementRoot) {
        this.elementRoot = elementRoot;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Map<String, SpecialTemplateVO> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, SpecialTemplateVO> templateMap) {
        this.templateMap = templateMap;
    }

    public Map<String, SpecialModelVO> getModelMap() {
        return modelMap;
    }

    public void setModelMap(Map<String, SpecialModelVO> modelMap) {
        this.modelMap = modelMap;
    }

    public Map<String, SpecialColumnVO> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, SpecialColumnVO> columnMap) {
        this.columnMap = columnMap;
    }

    public SpecialEO getSpecialEO() {
        return specialEO;
    }

    public void setSpecialEO(SpecialEO specialEO) {
        this.specialEO = specialEO;
    }

    public SpecialThemeEO getSpecialThemeEO() {
        return specialThemeEO;
    }

    public void setSpecialThemeEO(SpecialThemeEO specialThemeEO) {
        this.specialThemeEO = specialThemeEO;
    }
}

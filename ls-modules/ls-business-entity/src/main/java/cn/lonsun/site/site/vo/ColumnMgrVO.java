package cn.lonsun.site.site.vo;

import cn.lonsun.indicator.internal.entity.FunctionEO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lonsun on 2017-2-13.
 */
public class ColumnMgrVO {
    private Long indicatorId;
    // 名称
    private String name;
    private String shortName;
    //树结构名称
    private String  treeName;
    //父节点Id
    private Long parentId;
    //排序号
    private Integer sortNum;
    //是否为父节点
    private Integer isParent = Integer.valueOf(0);
    //站点ID
    private Long siteId;
    //类型
    private String type;
    private String opt = "super";
    private List<FunctionEO> functions =new ArrayList<FunctionEO>();
    private String columnTypeCode;

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public Long getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(Long indicatorId) {
        this.indicatorId = indicatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getIsParent() {
        return isParent;
    }

    public void setIsParent(Integer isParent) {
        this.isParent = isParent;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpt() {
        return opt;
    }

    public void setOpt(String opt) {
        this.opt = opt;
    }

    public List<FunctionEO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionEO> functions) {
        this.functions = functions;
    }
}

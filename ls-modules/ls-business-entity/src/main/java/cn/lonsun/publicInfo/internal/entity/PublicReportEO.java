package cn.lonsun.publicInfo.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**
 * Created by fth on 2017/1/19.
 */
@Entity
@Table(name = "CMS_PUBLIC_REPORT")
public class PublicReportEO extends AMockEntity {

    /**
     * 统计表
     */
    public enum TableType {
        DRIVING_PUBLIC, // 主动公开
        PUBLIC_APPLY //依申请公开
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "SITE_ID")
    private Long siteId;// 站点id
    @Column(name = "TITLE")
    private String title;//统计标题
    @Column(name = "TABLE_TYPE")
    private String tableType;//统计的表类型
    @Column(name = "IS_TITLE")
    private Boolean isTitle;//只是标题，无其他意义
    @Column(name = "UNIT")
    private String unit;//计量单位
    @Column(name = "PARENT_ID")
    private Long parentId;//父id
    @Column(name = "RESULT")
    private Long result;//统计结果
    @Column(name = "SORT_NUM")
    private Long sortNum;//排序
    @Column(name = "IS_SHOW")
    private Boolean isShow = Boolean.TRUE;//是否显示
    @Column(name = "IS_FILL")
    private Boolean isFill = Boolean.FALSE;//是否自己填写值
    @Column(name = "IS_SQL")
    private Boolean isSql = Boolean.FALSE;//是否sql语句
    @Column(name = "KEY_IDS")
    private String keyIds;//统计的目录id或者状态，手動輸入
    @Column(name = "SQL")
    private String sql;//sql语句，手動輸入
    @Column(name = "BACKUP1")
    private String backup1;//备用字段
    @Column(name = "BACKUP2")
    private String backup2;//备用字段

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(Boolean isTitle) {
        this.isTitle = isTitle;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getResult() {
        return result;
    }

    public void setResult(Long result) {
        this.result = result;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Boolean getIsFill() {
        return isFill;
    }

    public void setIsFill(Boolean isFill) {
        this.isFill = isFill;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Boolean getIsSql() {
        return isSql;
    }

    public void setIsSql(Boolean isSql) {
        this.isSql = isSql;
    }

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }

    public String getBackup2() {
        return backup2;
    }

    public void setBackup2(String backup2) {
        this.backup2 = backup2;
    }

    public String getKeyIds() {
        return keyIds;
    }

    public void setKeyIds(String keyIds) {
        this.keyIds = keyIds;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }
}

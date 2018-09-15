package cn.lonsun.monitor.config.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**监测平台栏目配置
 *
 */
@Entity
@Table(name="EX_MONITORED_COLUMN_CONFIG")
public class MonitoredColumnConfigEO extends AMockEntity {

    public enum TypeCode {
        columnType_index,  //首页栏目
        columnType_update,//应更新栏目
        columnType_DTYW,//动态要问类栏目
        columnType_TZZC,//通知公告、政策文件类栏目
        columnType_RSGH,//人事、规划计划类栏目
        columnType_ZWZX,//政务咨询类栏目
        columnType_DCZJ,//调查征集类栏目
        columnType_BLANK,//空白类栏目
        columnType_HDFT//互动访谈类栏目

    }

    public enum Status{
       start(1),
       stop(0);
        private Integer sta;
        private Status(Integer sta){
            this.sta=sta;
        }
        public Integer getStatus(){
            return sta;
        }
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TYPE_ID")
    /**
     * 类别ID
     */
    private Long typeId;
    /**
     * 类别名称
     */
    @Column(name = "TYPE_NAME")
    private String typeName;
    /**
     * 类别编码
     */
    @Column(name = "TYPE_CODE")
    private String typeCode;
    /**
     * 状态(0:停用,1启用)
     */
    @Column(name = "STATUES")
    private Integer status=0;
    @Column(name = "SITE_ID")
    private Long siteId;
    @Column(name = "PUBLIC_CATE_ID_NUM")
    private Long publicCateIdNum=0L;
    @Column(name = "COLUMN_ID_NUM")
    private Long columnIdNum=0L;

    @Column(name = "PUBLIC_CATE_ID")
    private String synOrganCatIds;
    @Column(name = "COLUMN_ID")
    private String synColumnIds;

    public Long getColumnIdNum() {
        return columnIdNum;
    }

    public void setColumnIdNum(Long columnIdNum) {
        this.columnIdNum = columnIdNum;
    }

    public Long getPublicCateIdNum() {
        return publicCateIdNum;
    }

    public void setPublicCateIdNum(Long publicCateIdNum) {
        this.publicCateIdNum = publicCateIdNum;
    }

    public String getSynOrganCatIds() {
        return synOrganCatIds;
    }

    public void setSynOrganCatIds(String synOrganCatIds) {
        this.synOrganCatIds = synOrganCatIds;
    }

    public String getSynColumnIds() {
        return synColumnIds;
    }

    public void setSynColumnIds(String synColumnIds) {
        this.synColumnIds = synColumnIds;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}

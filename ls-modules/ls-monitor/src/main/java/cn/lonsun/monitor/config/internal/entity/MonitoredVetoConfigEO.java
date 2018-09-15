package cn.lonsun.monitor.config.internal.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;

/**单项否决项
 * Created by lonsun on 2017-9-25.
 */
@Entity
@Table(name="EX_MONITORED_VETO_CONFIG")
public class MonitoredVetoConfigEO extends AMockEntity {
    public enum  CodeType{
        siteDeny("站点无法访问"),//单项否决-站点无法访问
        siteUpdate("网站不更新"),//单项否决-站点更新
        columnUpdate("栏目不更新"),//单项否决-栏目更新
        reply("互动回应差"),//单项否决-互动回应
        siteUse("网站可用性"),//综合评分-网站可用性
        infoUpdate("信息更新情况"),//综合评分-信息更新
        replyScope("互动回应情况"),//综合评分-互动回应
        error("严重错误"),//单项否决-严重错误
        service("实时服务")//综合评分-实时服务
        ;

        CodeType(String text) {
            this.text = text;
        }

        private String text;

        /**
         * 提给velocity的get方法
         * @return
         */
        public String getName() {
            return this.name();
        }

        public String getText() {
            return text;
        }
    }

    public  enum StandardCode{
        updateNum,//更新信息数量
        notUpdateColumnNum,//未更新栏目数量
        notOpenSite,//网站打不开
        blankColumnNum,//空白栏目
        replyMothNum//未回复留言时长
    }

    public enum BaseCode {
        vote(new CodeType[]{CodeType.siteDeny, CodeType.siteUpdate, CodeType.columnUpdate, CodeType.reply}, "单项否决"),//单项否决
        scop(new CodeType[]{CodeType.siteUse, CodeType.infoUpdate, CodeType.replyScope}, "综合评分");//综合评分


        BaseCode(CodeType[] type, String text) {
            this.type = type;
            this.text = text;
        }

        private CodeType[] type;

        private String text;

        /**
         * 根据子类型找到父类型
         *
         * @param subType
         * @return
         */
        public static BaseCode getCodeBySubType(CodeType subType) {
            for (BaseCode b : BaseCode.values()) {
                for (MonitoredVetoConfigEO.CodeType codeTypeb : b.getType()) {
                    if (codeTypeb == subType) {
                        return b;
                    }
                }
            }
            return null;
        }

        /**
         * 提给velocity的get方法
         *
         * @return
         */
        public String getName() {
            return this.name();
        }

        public CodeType[] getType() {
            return type;
        }

        public String getText() {
            return text;
        }

    }

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DENIED_ID")
    private Long  deniedId ;
    /**
     * 单项否决项类别
     */
    @Column(name = "VETO_CODE_TYPE")
    private  String  codeType;
    @Column(name = "content")
    private  String  content;

    /**
     * 栏目类别编码
     */
    @Column(name = "COLUMN_TYPE_CODE")
    private String columnTypeCode;
    /**
     * 栏目类别名称
     */
    @Column(name = "COLUMN_TYPE_NAME")
    private String  columnTypeName;
    @Column(name = "BASE_CODE")
    private String baseCode;
    @Column(name = "SITE_ID")
    private Long siteId;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public Long getDeniedId() {
        return deniedId;
    }

    public void setDeniedId(Long deniedId) {
        this.deniedId = deniedId;
    }

    public String getColumnTypeName() {
        return columnTypeName;
    }

    public void setColumnTypeName(String columnTypeName) {
        this.columnTypeName = columnTypeName;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }
}

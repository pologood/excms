package cn.lonsun.content.survey.vo;

public class QuestionOptionsVO implements java.io.Serializable {


    public enum Type {
        Question,
        Option
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long pid;

    private String title;

    // 1 单选  2 多选  3 radio  4 checkbox
    private Integer options;

    private Integer optionsCount;

    private Long votesCount;

    private String type;

    private String idField;// 主键字段
    private String parentField;// 父键字段

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOptions() {
        return options;
    }

    public void setOptions(Integer options) {
        this.options = options;
    }

    public Integer getOptionsCount() {
        return optionsCount;
    }

    public void setOptionsCount(Integer optionsCount) {
        this.optionsCount = optionsCount;
    }

    public Long getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(Long votesCount) {
        this.votesCount = votesCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }
}

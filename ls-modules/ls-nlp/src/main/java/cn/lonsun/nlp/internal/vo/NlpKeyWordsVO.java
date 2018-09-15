package cn.lonsun.nlp.internal.vo;

/**
 * @author: liuk
 * @version: v1.0
 * @date:2018/6/29 11:07
 */
public class NlpKeyWordsVO {

    private Long keyWordId;

    private String keyWordName;

    private Long readCounts;

    /**
     * 是否已经关注 0-未关注 1-已关注
     */
    private Integer isLabel = 0;

    public NlpKeyWordsVO() {}

    public NlpKeyWordsVO(Long keyWordId, String keyWordName, Long readCounts) {
        this.keyWordId = keyWordId;
        this.keyWordName = keyWordName;
        this.readCounts = readCounts;
    }

    public NlpKeyWordsVO(Long keyWordId, String keyWordName) {
        this.keyWordId = keyWordId;
        this.keyWordName = keyWordName;
    }

    public Long getKeyWordId() {
        return keyWordId;
    }

    public void setKeyWordId(Long keyWordId) {
        this.keyWordId = keyWordId;
    }

    public String getKeyWordName() {
        return keyWordName;
    }

    public void setKeyWordName(String keyWordName) {
        this.keyWordName = keyWordName;
    }

    public Long getReadCounts() {
        return readCounts;
    }

    public void setReadCounts(Long readCounts) {
        this.readCounts = readCounts;
    }

    public Integer getIsLabel() {
        return isLabel;
    }

    public void setIsLabel(Integer isLabel) {
        this.isLabel = isLabel;
    }
}

package cn.lonsun.content.survey.internal.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.lonsun.core.base.entity.ABaseEntity;


@Entity
@Table(name="CMS_SURVEY_QUESTION")
public class SurveyQuestionEO extends ABaseEntity{
	
	
	public enum Options {
		One(1), // 单选
		More(2),// 多选
		Text(3);//文本
		private Integer op;
		private Options(Integer op){
			this.op=op;
		}
		public Integer getOptions(){
			return op;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="QUESTION_ID")
	private Long questionId;

	//(主题id)
	@Column(name="THEME_ID")
	private Long themeId;

	//(标题)
	@Column(name="TITLE")
	private String title;
	
	//(选项)
	@Column(name="OPTIONS_")
	private Integer options = Options.One.getOptions();
	
	//(最多选多少)
	@Column(name="OPTIONS_COUNT")
	private Integer optionsCount;
	
	
	@Transient
	private List<SurveyOptionsEO> optionsList;
	
	@Transient
	private List<SurveyReplyEO> replys;
	
	

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getThemeId() {
		return themeId;
	}

	public void setThemeId(Long themeId) {
		this.themeId = themeId;
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

	public List<SurveyOptionsEO> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<SurveyOptionsEO> optionsList) {
		this.optionsList = optionsList;
	}

	public List<SurveyReplyEO> getReplys() {
		return replys;
	}

	public void setReplys(List<SurveyReplyEO> replys) {
		this.replys = replys;
	}
	
}

/*
 * Powered By zhongjun
 * createtime 2017-11-17 09:14:47
 */

package cn.lonsun.search.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zhongjun
 * @createtime 2017-11-17 09:14:47
 */
@Entity
@Table(name = "cms_global_search_result")
public class SearchResultEO extends ABaseEntity {
	
	public static SearchResultEO getEmptyInstence(){
        SearchResultEO ins = new SearchResultEO();
		//set default value this place
        return ins;
    }
	/**无参数构造函数*/
	public SearchResultEO(){ }
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="result_id")
	private Long resultId;
	/**查询任务Id*/	
	@Column(name="task_id")
	private Long taskId;

	/**搜索内容的id*/
	@Field
	@Column(name="id")
	private Long id;
	/**columnId*/
	@Field
	@Column(name="column_Id")
	private Long columnId;
	/**文档所在栏目路径，完整路径*/	
	@Column(name="column_path")
	private String columnPath;
	/**标题*/
	@Field
	@Column(name="title")
	private String title;
	/**内容*/
	@Field
	@Column(name="content")
	private String content;
	/**类型编码*/
	@Field
	@Column(name="type_Code")
	private String typeCode;
	/**所属站点*/
	@Field
	@Column(name="site_id")
	private Long siteId;
	/**创建时间*/
	@Field
	@Column(name="content_create_Date")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date contentCreateDate;
	/**作者*/
	@Field
	@Column(name="author")
	private String author;
	
	/**序号*/	
	public Long getResultId(){
		return resultId;
	}
	/**序号*/	
	public void setResultId(Long resultId){
		this.resultId = resultId;
	}
	/**查询任务Id*/	
	public Long getTaskId(){
		return taskId;
	}
	/**查询任务Id*/	
	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}
	/**搜索内容的id*/	
	public Long getId(){
		return id;
	}
	/**搜索内容的id*/	
	public void setId(Long id){
		this.id = id;
	}
	/**columnId*/	
	public Long getColumnId(){
		return columnId;
	}
	/**columnId*/	
	public void setColumnId(Long columnId){
		this.columnId = columnId;
	}
	/**文档所在栏目路径，完整路径*/	
	public String getColumnPath(){
		return columnPath;
	}
	/**文档所在栏目路径，完整路径*/	
	public void setColumnPath(String columnPath){
		this.columnPath = columnPath;
	}
	/**标题*/	
	public String getTitle(){
		return title;
	}
	/**标题*/	
	public void setTitle(String title){
		this.title = title;
	}
	/**内容*/	
	public String getContent(){
		return content;
	}
	/**内容*/	
	public void setContent(String content){
		this.content = content;
	}
	/**类型编码*/	
	public String getTypeCode(){
		return typeCode;
	}
	/**类型编码*/	
	public void setTypeCode(String typeCode){
		this.typeCode = typeCode;
	}
	/**所属站点*/	
	public Long getSiteId(){
		return siteId;
	}
	/**所属站点*/	
	public void setSiteId(Long siteId){
		this.siteId = siteId;
	}
	/**创建时间*/	
	public Date getContentCreateDate(){
		return contentCreateDate;
	}
	/**创建时间*/	
	public void setContentCreateDate(Date contentCreateDate){
		this.contentCreateDate = contentCreateDate;
	}
	/**作者*/	
	public String getAuthor(){
		return author;
	}
	/**作者*/	
	public void setAuthor(String author){
		this.author = author;
	}
}



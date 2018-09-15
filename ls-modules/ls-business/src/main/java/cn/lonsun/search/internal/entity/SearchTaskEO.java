/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * @author zhongjun
 * @createtime 2017-11-16 17:55:00
 */
@Entity
@Table(name = "cms_global_search_task")
public class SearchTaskEO extends ABaseEntity {


	public static SearchTaskEO getEmptyInstence(){
        SearchTaskEO ins = new SearchTaskEO();
		//set default value this place
        return ins;
    }
	/**无参数构造函数*/
	public SearchTaskEO(){ }

	public SearchTaskEO(String searchKey) {
		this.searchKey = searchKey;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	/**搜索关键字*/	
	@Column(name="search_key")
	private String searchKey;
	/**查询结果*/	
	@Column(name="search_result")
	private String searchResult;
	/**完成时间*/	
	@Column(name="finish_time")
	private String finishTime;
	
	/**序号*/	
	public Long getId(){
		return id;
	}
	/**序号*/	
	public void setId(Long id){
		this.id = id;
	}
	/**搜索关键字*/	
	public String getSearchKey(){
		return searchKey;
	}
	/**搜索关键字*/	
	public void setSearchKey(String searchKey){
		this.searchKey = searchKey;
	}
	/**查询结果*/	
	public String getSearchResult(){
		return searchResult;
	}
	/**查询结果*/	
	public void setSearchResult(String searchResult){
		this.searchResult = searchResult;
	}
	/**完成时间*/	
	public String getFinishTime(){
		return finishTime;
	}
	/**完成时间*/	
	public void setFinishTime(String finishTime){
		this.finishTime = finishTime;
	}
}



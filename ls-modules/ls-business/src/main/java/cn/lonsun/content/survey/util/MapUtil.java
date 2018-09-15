package cn.lonsun.content.survey.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.survey.internal.entity.SurveyOptionsEO;
import cn.lonsun.content.survey.internal.entity.SurveyReplyEO;

public class MapUtil {

	public static Map<Long, List<SurveyOptionsEO>> getOptionsMap(List<SurveyOptionsEO> options) {
		Map<Long,List<SurveyOptionsEO>> map = null;
		if(options !=null && options.size()>0){
			map = new HashMap<Long,List<SurveyOptionsEO>>();
			List<SurveyOptionsEO> soList = null;
			for(SurveyOptionsEO so:options){
				Long questionId = so.getQuestionId();
				soList = map.get(questionId);
				if(!(soList != null && soList.size() > 0)){
					soList = new ArrayList<SurveyOptionsEO>();
				}
				soList.add(so);
				map.put(questionId, soList);
			}
		}
		return map;
	}

	public static  Map<Long, List<SurveyReplyEO>> getReplysMap(List<SurveyReplyEO> replys) {
		Map<Long,List<SurveyReplyEO>> map = null;
		if(replys !=null && replys.size()>0){
			map = new HashMap<Long,List<SurveyReplyEO>>();
			List<SurveyReplyEO> soList = null;
			for(SurveyReplyEO so:replys){
				Long questionId = so.getQuestionId();
				soList = map.get(questionId);
				if(!(soList != null && soList.size() > 0)){
					soList = new ArrayList<SurveyReplyEO>();
				}
				soList.add(so);
				map.put(questionId, soList);
			}
		}
		return map;
	}


	public static  Map<Long, List<LeaderInfoVO>> getLeadersMap(List<LeaderInfoVO> leaders) {
		Map<Long,List<LeaderInfoVO>> map = null;
		if(leaders !=null && leaders.size()>0){
			map = new HashMap<Long,List<LeaderInfoVO>>();
			List<LeaderInfoVO> list = null;
			for(LeaderInfoVO leader:leaders){
				Long typeId = leader.getLeaderTypeId();
				list = map.get(typeId);
				if(!(list != null && list.size() > 0)){
					list = new ArrayList<LeaderInfoVO>();
				}
				list.add(leader);
				map.put(typeId, list);
			}
		}
		return map;
	}
}

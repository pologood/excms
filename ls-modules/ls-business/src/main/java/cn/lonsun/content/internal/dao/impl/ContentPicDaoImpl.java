package cn.lonsun.content.internal.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.entity.AMockEntity;
import org.springframework.stereotype.Repository;

import cn.lonsun.content.internal.dao.IContentPicDao;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.core.base.dao.impl.MockDao;

/**
 * 
 * @ClassName: ContentPicDaoImpl
 * @Description: picture news Data persistence layer
 * @author Hewbing
 * @date 2015年10月15日 上午11:25:54
 *
 */
@Repository("contentPicDao")
public class ContentPicDaoImpl extends MockDao<ContentPicEO> implements IContentPicDao {

	@Override
	public List<ContentPicEO> getPicsList(Long contentId) {
		String hql="from ContentPicEO where contentId=? and recordStatus='Normal' order by sortNum asc,createDate asc";
		return getEntitiesByHql(hql, new Object[]{contentId});
	}

	@Override
	public void updatePicPath(String path,String thumbPath,Long picId) {
		String hql="update ContentPicEO set path=?,thumbPath=? where picId=?";
		executeUpdateByHql(hql, new Object[]{path,thumbPath,picId});
	}

	@Override
	public void updatePicInfo(ContentPicEO picEO) {
		String hql="update ContentPicEO set picTitle=?,picAuthor=?,description=?,updateDate=?,updateUserId=? where picId=?";
		executeUpdateByHql(hql, new Object[]{picEO.getPicTitle(),picEO.getPicAuthor(),picEO.getDescription(),picEO.getUpdateDate(),picEO.getUpdateUserId(),picEO.getPicId()});
	}

	@Override
	public void updatePic(ContentPicEO picEO,Long contentId) {
		String hql="update ContentPicEO set contentId=?, picTitle=?,description=?,sortNum=? where picId=?";
		executeUpdateByHql(hql, new Object[]{contentId,picEO.getPicTitle(),picEO.getDescription(),picEO.getSortNum(),picEO.getPicId()});
	}

	@Override
	public void updatePic(ContentPicEO picEO) {
		String hql="update ContentPicEO set picTitle=?,description=?,sortNum=? where picId=?";
		executeUpdateByHql(hql, new Object[]{picEO.getPicTitle(),picEO.getDescription(),picEO.getSortNum(),picEO.getPicId()});
	}

	@Override
	public List<ContentPicEO> getListByPath(String[] paths) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("paths", paths);
		String hql="from ContentPicEO where path in(:paths) and recordStatus='Normal' order by sortNum asc,createDate asc";
		return getEntitiesByHql(hql, map);
	}

	@Override
	public void removePic(Long[] contentIds) {
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("contentId", contentIds);
		String hql="delete ContentPicEO where contentId in(:contentId)";
		executeUpdateByJpql(hql, map);
	}



}

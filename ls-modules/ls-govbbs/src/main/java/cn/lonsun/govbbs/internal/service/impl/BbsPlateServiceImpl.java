package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.govbbs.internal.dao.IBbsPlateDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateEO;
import cn.lonsun.govbbs.internal.service.IBbsPlateService;
import cn.lonsun.govbbs.internal.service.IBbsPlateUnitService;
import cn.lonsun.govbbs.internal.vo.PlateShowVO;
import cn.lonsun.govbbs.util.PlateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bbsPlateService")
public class BbsPlateServiceImpl extends MockService<BbsPlateEO> implements IBbsPlateService {

	@Autowired
	private IBbsPlateDao bbsPlateDao;

	@Autowired
	private IBbsPlateUnitService bbsPlateUnitService;

	@Override
	public List<BbsPlateEO> getPlates(Long parentId, Long siteId) {

		return bbsPlateDao.getPlates(parentId,siteId);
	}

	@Override
	public Long getMaxSortNum(Long parentId,Long siteId) {
		return bbsPlateDao.getMaxSortNum(parentId,siteId);
	}

	@Override
	public void updateHasChildren(Long parentId) {
		if(parentId==null||parentId<=0){
			return;
		}
		BbsPlateEO plate = getEntity(BbsPlateEO.class, parentId);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("parentId", parentId);
		params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
		List<BbsPlateEO> childrens = getEntities(BbsPlateEO.class, params);
		Integer hasChild = Integer.valueOf(0);
		if(childrens!=null&&childrens.size()>0){
			hasChild = 1;
		}
		plate.setHasChild(hasChild);
		updateEntity(plate);
	}

	@Override
	public void delete(Long plateId) {
		delete(BbsPlateEO.class, plateId);
		bbsPlateUnitService.deleteByPlateId(plateId);
	}

	@Override
	public List<PlateShowVO> getPlatesByPids(String topPlateParent, Long siteId) {
		return bbsPlateDao.getPlatesByPids(topPlateParent,siteId);
	}

	@Override
	public String getParentIds(Long parentId) {
		String topStr = PlateUtil.topPlate;
		//获取最大父节点下的id的版块
		BbsPlateEO plateChlid = bbsPlateDao.getMaxBbsPlate(parentId);
		if(parentId == null && plateChlid ==  null){
			return topStr + "," + "001";
		}if(parentId != null && plateChlid ==  null){
			BbsPlateEO plate = getEntity(BbsPlateEO.class,parentId);
			if(plate == null){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "父节点不存在");
			}
			return plate.getParentIds()  + "," + "001";
		}else{
			String pids = plateChlid.getParentIds();
			if(StringUtils.isEmpty(pids)){
				return null;
			}
			String pidStr = pids.substring(0,pids.lastIndexOf(","));
			String childStr = pids.substring(pids.lastIndexOf(",")+1,pids.length());
			Integer idNum = Integer.parseInt(childStr);
			if(idNum >= 999){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "版块子节点数最大为999");
			}
			String idNumStr = String.valueOf(idNum+1);
			String zeroStr = "";
			for(int k = 0;k<(3-idNumStr.length());k++){
				zeroStr = zeroStr +"0";
			}
			return pidStr+","+(zeroStr+idNumStr);
		}
	}
}

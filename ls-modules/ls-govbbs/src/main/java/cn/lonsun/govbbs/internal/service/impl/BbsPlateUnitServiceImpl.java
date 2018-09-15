package cn.lonsun.govbbs.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.govbbs.internal.dao.IBbsPlateUnitDao;
import cn.lonsun.govbbs.internal.entity.BbsPlateUnitEO;
import cn.lonsun.govbbs.internal.service.IBbsPlateUnitService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BbsPlateUnitServiceImpl extends BaseService<BbsPlateUnitEO> implements IBbsPlateUnitService {

	@Autowired
	private IBbsPlateUnitDao bbsPlateUnitDao;

	@Override
	public List<BbsPlateUnitEO> getUnits(Long plateId) {
		return bbsPlateUnitDao.getUnits(plateId);
	}

	@Override
	public void deleteByPlateId(Long plateId) {
		bbsPlateUnitDao.deleteByPlateId(plateId);
	}

	@Override
	public void deleteByPlateId(Long plateId, String unitIds, String unitNames) {
		//		List<BbsPlateUnitEO> plateUnits = getPlateUnits(plateId);
		//		List<Long> ids = new ArrayList<Long>();
		//		List<String> names = new ArrayList<String>();;
		deleteByPlateId(plateId);
		if(!StringUtils.isEmpty(unitIds)){
			//			if(plateUnits!=null && plateUnits.size()>0){}
			String[] idStrs = unitIds.split(",");
			String[] nameStrs = unitNames.split(",");
			if(idStrs !=null && idStrs.length>0){
				for(int i = 0;i<idStrs.length;i++){
					try{
						BbsPlateUnitEO bpu = new BbsPlateUnitEO();
						bpu.setPlateId(plateId);
						bpu.setUnitId(Long.parseLong(idStrs[i]));
						bpu.setUnitName(nameStrs[i]);
						saveEntity(bpu);
					}catch(Exception e){}
					//						ids.add(Long.parseLong(idStrs[i]));
					//						names.add(nameStrs[i]);
				}
			}
		}
		//		if(plateUnits!=null&&plateUnits.size()>0){
		//			Iterator<BbsPlateUnitEO>  iterator = plateUnits.iterator();
		//			while(iterator.hasNext()){
		//				BbsPlateUnitEO ra = iterator.next();
		//				if(ids.contains(ra.getUnitId())){
		//					//去除待更新的与数据库中重复的内容，其余的需要进行删除
		//					iterator.remove();
		//					//去除已存在的,其余的需要新增到数据库中
		//					ids.remove(ra.getUnitId());
		//					names.remove(ra.getUnitName());
		//				}
		//				if(names.contains(ra.getUnitName())){
		//					//去除待更新的与数据库中重复的内容，其余的需要进行删除
		//					iterator.remove();
		//					//去除已存在的,其余的需要新增到数据库中
		//					ids.remove(ra.getUnitId());
		//					names.remove(ra.getUnitName());
		//				}
		//			}
		//		}
		//		if(ids !=null && ids.size()>0){
		//			for(Long unitId:ids){
		//				BbsPlateUnitEO bpu = new BbsPlateUnitEO();
		//				bpu.setPlateId(plateId);
		//				bpu.setUnitId(unitId);
		//			}
		//		}
	}

	@Override
	public void savePlateUnits(Long columnConfigId, String unitIds,
			String unitNames) {
		if(!StringUtils.isEmpty(unitIds)){
			//			if(plateUnits!=null && plateUnits.size()>0){}
			String[] idStrs = unitIds.split(",");
			String[] nameStrs = unitNames.split(",");
			if(idStrs !=null && idStrs.length>0){
				for(int i = 0;i<idStrs.length;i++){
					try{
						BbsPlateUnitEO bpu = new BbsPlateUnitEO();
						bpu.setPlateId(columnConfigId);
						bpu.setUnitId(Long.parseLong(idStrs[i]));
						bpu.setUnitName(nameStrs[i]);
						saveEntity(bpu);
					}catch(Exception e){}
					//						ids.add(Long.parseLong(idStrs[i]));
					//						names.add(nameStrs[i]);
				}
			}
		}
	}

	//	private List<BbsPlateUnitEO> getPlateUnits(Long plateId) {
	//		Map<String,Object> params = new HashMap<String,Object>();
	//		params.put("plateId", plateId);
	//		return getEntities(BbsPlateUnitEO.class, params);
	//	}
}

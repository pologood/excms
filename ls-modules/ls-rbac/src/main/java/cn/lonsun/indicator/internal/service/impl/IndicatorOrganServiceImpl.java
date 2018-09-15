package cn.lonsun.indicator.internal.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.dao.IIndicatorOrganDao;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.entity.IndicatorOrganEO;
import cn.lonsun.indicator.internal.service.IIndicatorOrganService;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.indicator.vo.IndicatorOrganVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;

@Service
public class IndicatorOrganServiceImpl extends BaseService<IndicatorOrganEO> implements
		IIndicatorOrganService {
	@Autowired
	private IIndicatorOrganDao indicatorOrganDao;
	@Autowired
	private IIndicatorService indicatorService;
	@Autowired
	private IOrganService organService;

	@Override
	public int getMaxSortNum() {
		return indicatorOrganDao.getMaxSortNum();
	}

	@Override
	public IndicatorOrganEO getIndicatorOrgans(Long shortCutId, Long menuId,
			Long unitId) {
		Map<String,Object> params = new HashMap<String,Object>(3);
		params.put("shortCutId", shortCutId);
		if(menuId!=null){
			params.put("menuId", menuId);
		}
		params.put("unitId", unitId);
		return getEntity(IndicatorOrganEO.class, params);
	}

	@Override
	public Pagination getPagination(PageQueryVO query) {
		Pagination page = indicatorOrganDao.getPagination(query);
		List<?> list = page.getData();
		//信息补充
		if(list!=null&&list.size()>0){
			int size = list.size();
			List<Object> vos = new ArrayList<Object>(size);
			Map<Long,List<IndicatorOrganVO>> scMap = new HashMap<Long, List<IndicatorOrganVO>>();
			Map<Long,List<IndicatorOrganVO>> mMap = new HashMap<Long, List<IndicatorOrganVO>>();
			Map<Long,List<IndicatorOrganVO>> uMap = new HashMap<Long, List<IndicatorOrganVO>>();
			List<Long> shortCutIds = new ArrayList<Long>(size);
			List<Long> menuIds = new ArrayList<Long>(size);
			List<Long> unitIds = new ArrayList<Long>(size);
			for(Object obj:list){
				IndicatorOrganEO io = (IndicatorOrganEO)obj;
				IndicatorOrganVO vo = new IndicatorOrganVO();
				BeanUtils.copyProperties(io, vo);
				vos.add(vo);
				Long shortCutId = io.getShortCutId();
				if(shortCutId!=null){
					shortCutIds.add(shortCutId);
					List<IndicatorOrganVO> is = scMap.get(shortCutId);
					if(is==null){
						is = new ArrayList<IndicatorOrganVO>();
					}
					is.add(vo);
					scMap.put(shortCutId, is);
				}
				Long menuId = io.getMenuId();
				if(menuId!=null){
					menuIds.add(menuId);
					List<IndicatorOrganVO> is = mMap.get(shortCutId);
					if(is==null){
						is = new ArrayList<IndicatorOrganVO>();
					}
					is.add(vo);
					mMap.put(menuId, is);
				}
				Long unitId = io.getUnitId();
				if(unitId!=null){
					unitIds.add(unitId);
					List<IndicatorOrganVO> is = uMap.get(shortCutId);
					if(is==null){
						is = new ArrayList<IndicatorOrganVO>();
					}
					is.add(vo);
					uMap.put(unitId, is);
				}
			}
			//设置应用名称
			Map<String,Object> params = new HashMap<String, Object>();
			if(shortCutIds.size()>0){
				params.put("indicatorId", shortCutIds);
				List<IndicatorEO> shortCuts = indicatorService.getEntities(IndicatorEO.class, params);
				if(shortCuts!=null&&shortCuts.size()>0){
					for(IndicatorEO shortCut:shortCuts){
						Long shortCutId = shortCut.getIndicatorId();
						List<IndicatorOrganVO> targets = scMap.get(shortCutId);
						if(targets!=null&&targets.size()>0){
							for(IndicatorOrganVO target:targets){
								target.setShortCutName(shortCut.getName());
							}
						}
					}
				}
			}
			//设置菜单名称
			if(menuIds.size()>0){
				params.clear();
				params.put("indicatorId", menuIds);
				List<IndicatorEO> menus = indicatorService.getEntities(IndicatorEO.class, params);
				if(menus!=null&&menus.size()>0){
					for(IndicatorEO menu:menus){
						Long menuId = menu.getIndicatorId();
						List<IndicatorOrganVO> targets = mMap.get(menuId);
						if(targets!=null&&targets.size()>0){
							for(IndicatorOrganVO target:targets){
								target.setMenuName(menu.getName());
							}
						}
					}
				}
			}
			//设置单位名称
			if(unitIds.size()>0){
				params.clear();
				params.put("organId", unitIds);
				List<OrganEO> units = organService.getEntities(OrganEO.class, params);
				if(units!=null&&units.size()>0){
					for(OrganEO unit:units){
						Long unitId = unit.getOrganId();
						List<IndicatorOrganVO> targets = uMap.get(unitId);
						if(targets!=null&&targets.size()>0){
							for(IndicatorOrganVO target:targets){
								target.setUnitName(unit.getName());
							}
						}
					}
				}
			}
			page.setData(vos);
		}
		return page;
	}


}

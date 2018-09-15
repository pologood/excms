package cn.lonsun.rbac.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.entity.IndicatorEO.Type;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.indicator.internal.vo.IndicatorVO;
import cn.lonsun.rbac.utils.IndicatorRelationMap;

/**
 * 角色权限树初始化Servlet
 * 
 * @author xujh
 * @date 2014年10月11日 上午9:43:05
 * @version V1.0
 */
public class RoleIndicatorServlet extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6473704352223182218L;

	String[] menuTypes = new String[] { IndicatorEO.Type.Menu.toString() };
	String[] rightTypes = new String[] {
			IndicatorEO.Type.ToolBarButton.toString(),
			IndicatorEO.Type.Other.toString() };
	// 系统权限树key
	public static String SYSTEM_RIGHT_KEY = "sysIndicatorsTree";
	public static String APP_RIGHT_KEY = "appIndicatorsTree";
	private IIndicatorService indicatorService;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(config.getServletContext());
		this.indicatorService = (IIndicatorService) ctx.getBean("indicatorService");
		// 获取系统管理应用
		List<IndicatorVO> rightTree = new ArrayList<IndicatorVO>();
		//获取除系统管理外的应用权限
		List<IndicatorVO> appRightTree = new ArrayList<IndicatorVO>();
		String[] types = new String[] { IndicatorEO.Type.Shortcut.toString() };
		List<IndicatorEO> shortCuts = indicatorService.getEnableIndicators(null, types, null, null,true);
		if (shortCuts != null && shortCuts.size() > 0) {
			for (IndicatorEO shortCut : shortCuts) {
				getEnableMenus(rightTree, shortCut, null, null,true);
				if(!SystemCodes.systemMgr.toString().equals(shortCut.getSystemCode())){
					getEnableMenus(appRightTree, shortCut, null, null,true);
				}
			}
		}
		IndicatorRelationMap.map.put(RoleIndicatorServlet.SYSTEM_RIGHT_KEY,rightTree);
		IndicatorRelationMap.map.put(RoleIndicatorServlet.APP_RIGHT_KEY,appRightTree);
	}

	/**
	 * 从菜单开始获取所有的权限
	 *
	 * @param container
	 * @param indicator
	 * @param isShown4Developer
	 * @param isShown4Admin
	 */
	private void getEnableMenus(List<IndicatorVO> container,IndicatorEO indicator, 
			Boolean isShown4Developer,Boolean isShown4Admin,Boolean isShown4SystemManager) {
		if (indicator == null) {
			return;
		}
		// 转换成VO
		IndicatorVO menu = getVO(indicator);
		container.add(menu);
		// 获取类型子菜单
		String[] types = new String[]{Type.Menu.toString()};
		Long parentId = indicator.getIndicatorId();
		List<IndicatorEO> menus = indicatorService.getEnableIndicators(parentId, types, isShown4Developer, isShown4Admin,isShown4SystemManager);
		if (menus != null && menus.size() > 0) {
			for (IndicatorEO i : menus) {
				// 菜单下可能存在子菜单
				getEnableMenus(container, i, isShown4Developer, isShown4Admin,isShown4SystemManager);
			}
		}else{
			types = new String[] {Type.ToolBarButton.toString(),Type.Other.toString() };
			List<IndicatorEO> rights = indicatorService.getEnableIndicators(parentId, types, isShown4Developer, isShown4Admin,isShown4SystemManager);
			if (rights != null && rights.size() > 0) {
				List<IndicatorVO> vos = new ArrayList<IndicatorVO>(rights.size());
				for (IndicatorEO eo : rights) {
					IndicatorVO vo = getVO(eo);
					vos.add(vo);
				}
				menu.setRights(vos);
			}
		}
	}

	private IndicatorVO getVO(IndicatorEO eo) {
		IndicatorVO vo = new IndicatorVO();
		BeanUtils.copyProperties(eo, vo);
		vo.setIsEnable(eo.getIsEnable() ? "1" : "0");
		vo.setIsShowSons(eo.getIsShowSons() ? "1" : "0");
		vo.setId(eo.getIndicatorId());
		vo.setPid(eo.getParentId());
		vo.setIsParent(eo.getIsParent() != null && eo.getIsParent() == 1 ? true
				: false);
		return vo;
	}
}

package cn.lonsun.demo.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.demo.entity.DemoEO;
import cn.lonsun.demo.vo.DemoQueryVO;

import java.util.List;

/**
 * demo Dao层<br/>
 *
 * @author wangshibao <br/>
 * @version v1.0 <br/>
 * @date 2018-8-2<br/>
 */
public interface IDemoDao extends IMockDao<DemoEO>{

    /**
     * 根据搜索条件获取分页列表
     * @param queryVO
     * @return
     */
    public Pagination getDemoPage(DemoQueryVO queryVO);

    /**
     * 根据条件获取列表
     * @param code
     * @param name
     * @return
     */
    public List<DemoEO> getDemoListByCodeAndName(String code, String name);

    /**
     * 根据条件获取信息
     * @param id
     * @return
     */
    public DemoEO getDemoInfoById(Long id);

    /**
     * 保存信息
     * @param eo
     * @return
     */
    public Object saveDemoInfo(DemoEO eo);

    /**
     * 修改信息
     * @param eo
     * @return
     */
    public void updateDemoInfo(DemoEO eo);

    /**
     * 删除信息
     * @param id
     * @return
     */
    public void deleteDemoInfo(Long id);

    /**
     * 更新全部
     * @param eo
     * @return
     */
    public void update(DemoEO eo);
}

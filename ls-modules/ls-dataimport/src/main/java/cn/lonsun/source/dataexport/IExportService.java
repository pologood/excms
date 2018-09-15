package cn.lonsun.source.dataexport;

import cn.lonsun.engine.vo.ExportQueryVO;

import java.util.List;

/**
 * 数据导出服务
 * Created by zhushouyong on 2018-1-16.
 */
public interface IExportService<E, P extends ExportQueryVO> {

    /**
     * 该方法已由 abstractExportService 实现
     * @param queryVO
     * @return
     */
    public List<E> getDataList(P queryVO);


    /**
     * 根据id查询数据，用于错误重导，以及导入某个单位
     * @param queryVO
     * @return
     */
    public List<E> getDataByIds(P queryVO, Object... ids);


}

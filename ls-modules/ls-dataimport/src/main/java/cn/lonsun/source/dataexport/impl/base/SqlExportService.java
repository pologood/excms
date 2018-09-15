package cn.lonsun.source.dataexport.impl.base;

import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.IExportService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 执行sql获取数据
 * @author zhongjun
 */
@Component("sqlExportService")
public class SqlExportService extends JdbcAble<Map<String,Object>> implements IExportService<Map<String,Object>, ExportQueryVO> {

    @Override
    public List<Map<String,Object>> getDataList(ExportQueryVO queryVO) {
        return queryMap( queryVO.getSqlConfig(), null);
    }

    @Override
    public List<Map<String, Object>> getDataByIds(ExportQueryVO queryVO, Object... ids) {
        return null;
    }
}

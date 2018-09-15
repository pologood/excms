package cn.lonsun.source.dataexport.impl.ex7.content;

import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.IExportService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 数据获取类
 * @author zhongjun
 */
@Component("ex7ArticleNewsExportService")
//解析为多实例，不能使用单利，datasource可能会出现多线程问题
@Scope("prototype")
public class Ex7ArticleNewsExportService extends JdbcAble<Long> implements IExportService<Long, ExportQueryVO> {

    @Override
    public List<Long> getDataList(ExportQueryVO queryVO) {
        return queryList("");
    }

    @Override
    public List<Long> getDataByIds(ExportQueryVO queryVO, Object... ids) {
        return null;
    }
}

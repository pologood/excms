package cn.lonsun.source.dataexport.impl.ex7.content;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.source.dataexport.content.ICollectInfoExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.CollectInfoVO;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author caohaitao
 * @Title: Ex7CollectInfoExportService
 * @Package cn.lonsun.source.dataexport.impl.ex7.content
 * @Description: 民意征集
 * @date 2018/3/9 8:36
 */
@Service("ex7CollectInfoExportService")
public class Ex7CollectInfoExportService extends JdbcAble<CollectInfoVO> implements ICollectInfoExportService {

    private static final String QUERY_COLLECT_SQL = "select c.CC_Title as title,c.CC_Contents as content,c.CC_CheckIN as isIssued ,c.CC_Begindate as startTime,c.CC_Enddate as endTime,c.CC_date as issuedTime from CollectContents c where c.CC_Isdel = '0' and c.SS_ID = ?";

    @Override
    public List<CollectInfoVO> getDataList(ContentQueryVO queryVO) {
        return queryList(QUERY_COLLECT_SQL, queryVO.getOldColumnId());
    }

    @Override
    public List<CollectInfoVO> getDataByIds(ContentQueryVO queryVO, Object... ids) {
        return null;
    }
}

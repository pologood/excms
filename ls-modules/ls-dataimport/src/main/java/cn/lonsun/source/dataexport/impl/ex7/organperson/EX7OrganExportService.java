package cn.lonsun.source.dataexport.impl.ex7.organperson;

import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.organperson.IOrganExportService;
import cn.lonsun.target.datamodel.organperson.OrganExportQueryVO;
import cn.lonsun.target.datamodel.organperson.OrganVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2018-3-9.
 */
@Service
public class EX7OrganExportService  extends JdbcAble<OrganVO> implements IOrganExportService {
    @Override
    public List<OrganVO> getDataList(OrganExportQueryVO queryVO) {
        return null;
    }

    @Override
    public List<OrganVO> getDataByIds(OrganExportQueryVO queryVO, Object... ids) {
        return null;
    }
}

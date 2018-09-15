package cn.lonsun.source.dataexport.impl.ex7.organperson;

import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.source.dataexport.organperson.IUserExportService;
import cn.lonsun.target.datamodel.organperson.UserExportQueryVO;
import cn.lonsun.target.datamodel.organperson.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lonsun on 2018-3-7.
 */
@Service("ex7UserExportService")
public class Ex7UserExportService extends JdbcAble<UserVO> implements IUserExportService {


    @Override
    public List<UserVO> getDataList(UserExportQueryVO queryVO) {
        return null;
    }

    @Override
    public List<UserVO> getDataByIds(UserExportQueryVO queryVO, Object... ids) {
        return null;
    }
}

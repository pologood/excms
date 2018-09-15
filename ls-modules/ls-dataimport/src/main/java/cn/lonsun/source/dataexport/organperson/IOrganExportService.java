package cn.lonsun.source.dataexport.organperson;

import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.target.datamodel.organperson.OrganExportQueryVO;
import cn.lonsun.target.datamodel.organperson.OrganVO;
import org.springframework.stereotype.Service;

/**
 * Created by lonsun on 2018-3-9.
 */

public interface IOrganExportService extends IExportService<OrganVO,OrganExportQueryVO> {
}

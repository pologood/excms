package cn.lonsun.monitor.util;

import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.util.DateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 *
 * 导入操作
 * Created by zhangchao on 2016/7/7.
 */
public class PoiExcelUtil {


    private static IOrganService organService = SpringContextHolder.getBean("organService");

    /**
     * 后缀
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName)
    {
        int index = fileName.lastIndexOf(".");
        if (index != -1)
        {
            String suffix = fileName.substring(index + 1);
            return suffix;
        }
        return "";
    }

    /**
     * 导出多sheet表格
     * @param titles
     * @param fileName
     * @param suffix
     * @param multiHeaders
     * @param multiValues
     * @param response
     */
    public static void exportMultiSheetExcel(String[] titles,String fileName, String suffix,
                                             List<String[]> multiHeaders, List<List<Object[]>> multiValues, HttpServletResponse response){
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();

        //遍历添加多sheet
        for (int i = 0 ; i < titles.length ; i++) {
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
            HSSFSheet sheet = wb.createSheet(titles[i]);

            // 第四步，创建设置值表头属性
            HSSFCellStyle headStyle = wb.createCellStyle();
            // 第四步，创建设置值body属性
            HSSFCellStyle bodyStyle = wb.createCellStyle();
            headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            bodyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //字体设计
            HSSFFont font = wb.createFont();

            //添加sheet数据
            setSheetHeadAndValues(multiHeaders.get(i),multiValues.get(i),sheet,headStyle,bodyStyle,font);
        }

        // 下载
        try{
            OutputStream os = response.getOutputStream();// 取得输出流
            response.reset();// 清空输出流
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1").
                    concat(".").concat(suffix == null?"xls":suffix));
            response.setContentType("application/msexcel");// 定义输出类型
            wb.write(os);
            wb.close();
            os.flush();
            os.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param  title   sheet 名称
     * @param headers  exl  头部表格 标题属性
     * @param values  实体数据
     * @param      response
     *
     */
    public static void exportExcel(String title,String fileName, String suffix,String[] headers, List<Object[]> values, HttpServletResponse response){
        // 第一步，创建一个webbook，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(title);

        // 第四步，创建设置值表头属性
        HSSFCellStyle headStyle = wb.createCellStyle();
        // 第四步，创建设置值body属性
        HSSFCellStyle bodyStyle = wb.createCellStyle();
        headStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        bodyStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //字体设计
        HSSFFont font = wb.createFont();

        //添加sheet数据
        setSheetHeadAndValues(headers,values,sheet,headStyle,bodyStyle,font);

        // 下载
        try{
            OutputStream os = response.getOutputStream();// 取得输出流
            response.reset();// 清空输出流
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1").
                    concat(".").concat(suffix == null?"xls":suffix));
            response.setContentType("application/msexcel");// 定义输出类型
            wb.write(os);
            wb.close();
            os.flush();
            os.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //填充每个sheet的数据
    private static void setSheetHeadAndValues(String[] headers, List<Object[]> values,HSSFSheet sheet,HSSFCellStyle headStyle,HSSFCellStyle bodyStyle,HSSFFont font) {
        sheet.setDefaultColumnWidth(15);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);

        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        headStyle.setFont(font);
        //设置标题
        HSSFCell cell = null;
        for (int i = 0; i < headers.length; i++) {
            cell = row.createCell(i);
            String sheetTitle = "";
            try {
                sheetTitle = headers[i];
            }catch(Exception e){ }
            cell.setCellValue(sheetTitle);
            cell.setCellStyle(headStyle);
        }
        // 第五步，写入实体数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(values != null && values.size() >0){
            for (int i = 0; i < values.size(); i++){
                Object[] object =values.get(i);
                row = sheet.createRow(i + 1);
                for (int j = 0; j < object.length; j++) {
                    //设置行数据 第二行开始
                    String sheetValue = "";
                    try {
                        if (object[j] != null) {
                            if (object[j] instanceof Date) {
                                sheetValue = sdf.format(object[j]);
                            }
                            sheetValue = object[j].toString();
                        }
                    }catch (Exception e){}
                    cell = row.createCell(j);
                    cell.setCellValue(sheetValue);
                    cell.setCellStyle(bodyStyle);
                }
            }
        }
    }

    public static List<OrganEO> getOrgans(InputStream is,Long pid) throws Exception {
        List<OrganEO> organs = null;

        Workbook wb = create(is);
        if(wb != null){
            organs = new ArrayList<OrganEO>();
            String ptype = null;
            if(pid != null){
                OrganEO o = organService.getEntity(OrganEO.class,pid);
                ptype = (o == null?null:o.getType());
            }
            //（Sheet）的个数
            int countSheet = wb.getNumberOfSheets();
            for (int k = 0; k < countSheet; k++) {
                //（Sheet）对象数组
                Sheet sheet = wb.getSheetAt(k);
                //行
                int rowSize = sheet.getLastRowNum();
                if (rowSize < 0) continue;
                //第二行开始
                for (int i = 1; i <= rowSize; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)  continue;
                    int j = i+1;
                    OrganEO organ = new OrganEO();
                    organ.setParentId(pid);
                    //单位名称
                    String name = getCellValue(row.getCell(0));
                    name = name.trim();
                    if(name.length() > 30 || name.length() <=1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第A列错误，单位名称{字段不能为空、长度在[1-30]}");
                    }
                    //判断单位是否可用
                    if(!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(name)){
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "第"+j+"行，第A列错误，单位名称仅支持中文、英文数字和部分中文标点符号的组合");
                    }
                    organ.setName(name);
                    //类型
                    String type = getCellValue(row.getCell(1));
                    if(!(type.equals("单位") || type.equals("部门"))){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第B列错误,单位类型{字段不能为空，[单位、部门]}");
                    }
                    type = type.equals("单位")?OrganEO.Type.Organ.toString():OrganEO.Type.OrganUnit.toString();
                    if(pid != null && ptype != null &&
                            ptype.equals(OrganEO.Type.OrganUnit.toString()) &&
                            type.equals(OrganEO.Type.Organ.toString())){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第B列错误,单位类型{字段不能为空，[单位、部门]，部门下不可以有单位}");
                    }
                    organ.setType(type);

                    //简称
                    String simpleName = getCellValue(row.getCell(2));
                    if(!StringUtils.isEmpty(simpleName)) {
                        simpleName = simpleName.trim();
                        if (!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(simpleName)) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "第" + j + "行，第C列错误，单位简称仅支持中文、英文和数字的组合");
                        }
                        if (simpleName.length() > 20) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "第" + j + "行，第C列错误，单位简称{字段长度[~20]}");
                        }
                    }
                    organ.setSimpleName(simpleName);

                    //编码
                    String code = getCellValue(row.getCell(3));
                    if(!StringUtils.isEmpty(code) && code.length() > 20) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),"第"+j+"行，第D列错误，编码（字段长度[1~20]）");
                    }
                    organ.setCode(code);
                    //编码
                    String isPublic = getCellValue(row.getCell(4));
                    if(!StringUtils.isEmpty(isPublic)){
                        if(!(isPublic.equals("是") || isPublic.equals("否"))){
                            throw new BaseRunTimeException(TipsMode.Message.toString(),
                                    "第"+j+"行，第E列错误,开启信息公开{类型只能为[是、否]}");
                        }
                        organ.setIsPublic(isPublic.equals("是")?1:0);
                    }
                    //排序
                    String sortNumStr = getCellValue(row.getCell(5));
                    Long sortNum = null;
                    try {
                        sortNum = Long.parseLong(sortNumStr);
                        if(sortNum.longValue()<0){
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能小于0");
                        }
                        if(sortNum>999999){
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能大于999999");
                        }
                    }catch (Exception e){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第F列错误,排序号{字段不能为空，[0~999999]}");
                    }
                    organ.setSortNum(sortNum);
                    //联系电话1
                    String officePhone = getCellValue(row.getCell(6));
                    if(!StringUtils.isEmpty(officePhone) && officePhone.length() > 20 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第G列错误，联系电话1（字段长度[~20]）");
                    }
                    organ.setOfficePhone(officePhone);
                    //联系电话2
                    String servePhone = getCellValue(row.getCell(7));
                    if(!StringUtils.isEmpty(servePhone) && servePhone.length() > 20 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第H列错误，联系电话2（字段长度[~20]）");
                    }
                    organ.setServePhone(servePhone);
                    //联系地址1
                    String officeAddress= getCellValue(row.getCell(8));
                    if(!StringUtils.isEmpty(officeAddress) && officeAddress.length() > 100 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第I列错误，联系地址1（字段长度[~100]）");
                    }
                    organ.setOfficeAddress(officeAddress);
                    //联系地址2
                    String serveAddress= getCellValue(row.getCell(9));
                    if(!StringUtils.isEmpty(serveAddress) && serveAddress.length() > 100 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第J列错误，联系地址2（字段长度[~100]）");
                    }
                    organ.setServeAddress(serveAddress);
                    //单位网址
                    String organUrl= getCellValue(row.getCell(10));
                    if(!StringUtils.isEmpty(organUrl) && organUrl.length() > 30 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第K列错误，单位网址（字段长度[~30]）");
                    }
                    organ.setOrganUrl(organUrl);
                    //科室
                    String organUnitName= getCellValue(row.getCell(11));
                    if(!StringUtils.isEmpty(organUnitName)) {
                        if(organUnitName.length() > 30 || organUnitName.length() <=1) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(),
                                    "第"+j+"行，第L列错误，科室名称{长度在[1-30]}");
                        }
                        //判断单位是否可用
                        if(!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(organUnitName)){
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "第"+j+"行，第L列错误，科室名称仅支持中文、英文数字和部分中文标点符号的组合");
                        }
                        //设置部门名称
                        organ.setActive(organUnitName);
                    }
                    organs.add(organ);
                }
            }
        }

        return organs;

    }

    public static List<MemberEO> getMembers(InputStream is, Long siteId) throws Exception {
        List<MemberEO> members = null;

        Workbook wb = create(is);
        if(wb != null){
            members = new ArrayList<MemberEO>();

            //（Sheet）的个数
            int countSheet = wb.getNumberOfSheets();
            for (int k = 0; k < countSheet; k++) {
                //（Sheet）对象数组
                Sheet sheet = wb.getSheetAt(k);
                //行
                int rowSize = sheet.getLastRowNum();
                if (rowSize < 0) continue;
                //第二行开始
                for (int i = 1; i <= rowSize; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)  continue;
                    int j = i+1;
                    MemberEO member = new MemberEO();

                    //用户名
                    String uid = getCellValue(row.getCell(0));
                    uid = uid.trim();
                    if( uid.length() <1){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行错误，用户名不能为空");
                    }
                    member.setUid(uid);
                    //密码
                    String plainpw= getCellValue(row.getCell(1));
                    plainpw=plainpw.trim();
                    if( plainpw.length() <1){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行错误，密码不能为空");
                    }
                    member.setPlainpw(plainpw);
                    //MD5加密
                    String password = DigestUtils.md5Hex(plainpw);
                    member.setPassword(password);

                    //姓名
                    String name = getCellValue(row.getCell(2));
                    name=name.trim();
                    if( name.length() <1){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行错误，姓名不能为空");
                    }
                    member.setName(name);

                    //性别
                    String sex= getCellValue(row.getCell(3));
                    sex=sex.trim();
                    if(sex.length()>0){
                        Integer sexInt=sex.equals("男") ? 1 : 0;
                        member.setSex(sexInt);}

                    //邮箱
                    String email= getCellValue(row.getCell(4));
                    email=email.trim();
                    member.setEmail(email);

                    //手机号
                    String phone= getCellValue(row.getCell(5));
                    phone=phone.trim();
                    member.setPhone(phone);

                    //身份证号
                    String idCard= getCellValue(row.getCell(6));
                    idCard=idCard.trim();
                    member.setIdCard(idCard);

                    //地址
                    String address= getCellValue(row.getCell(7));
                    address=address.trim();
                    member.setAddress(address);

                    //问题
                    String question= getCellValue(row.getCell(8));
                    question=question.trim();
                    member.setQuestion(question);

                    //答案
                    String answer= getCellValue(row.getCell(9));
                    answer=answer.trim();
                    member.setAnswer(answer);


                    member.setSiteId(siteId);
                    members.add(member);
                }
            }
        }
        return members;

    }
    public static List<PersonNodeVO> getPersons(InputStream is) throws Exception {
        List<PersonNodeVO> persons = null;
        Workbook wb = create(is);
        if(wb != null){
            persons = new ArrayList<PersonNodeVO>();

            //（Sheet）的个数
            int countSheet = wb.getNumberOfSheets();
            for (int k = 0; k < countSheet; k++) {
                //（Sheet）对象数组
                Sheet sheet = wb.getSheetAt(k);
                //行
                int rowSize = sheet.getLastRowNum();
                if (rowSize < 0) continue;
                //第二行开始
                for (int i = 1; i <= rowSize; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null)  continue;
                    int j = i+1;
                    PersonNodeVO vo = new PersonNodeVO();

                    //部门id
                    String organIdStr = getCellValue(row.getCell(0));
                    Long organId = null;
                    try {
                        organId = Long.parseLong(organIdStr);
                    }catch (Exception e){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第A列错误，部门Id{字段不能为空}");
                    }
                    vo.setOrganId(organId);

                    //部门名称
                    String organName = getCellValue(row.getCell(1));
                    if(StringUtils.isEmpty(organName)){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第B列错误,所属部门{字段不能为空}");
                    }

                    //排序号
                    String sortNumStr = getCellValue(row.getCell(2));
                    Long sortNum = null;
                    try {
                        sortNum = Long.parseLong(sortNumStr);
                        if(sortNum.longValue()<0){
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能小于0");
                        }
                        if(sortNum>999999){
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能大于999999");
                        }
                    }catch (Exception e){
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第C列错误,排序号{字段不能为空，[0~999999]}");
                    }
                    vo.setSortNum(sortNum);

                    //用户名称
                    String name = getCellValue(row.getCell(3));
                    if(name.length() > 10 || name.length() < 2) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第D列错误，用户名称{字段不能为空、长度在[2-10]}");
                    }
                    name = name.trim();
                    if(!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(name)){
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "第"+j+"行，第D列错误，用户名称仅支持中文、英文数字和部分中文标点符号的组合");
                    }
                    vo.setName(name);

                    //用户账号
                    String uid = getCellValue(row.getCell(4));
                    if(uid.length() > 20 || uid.length() < 2) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第E列错误，用户账号{字段不能为空、长度在[2-20]}");
                    }
                    uid = uid.trim();
                    if(!RegexUtil.isCombinationOfCharactersAndNumbers(uid)){
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "第"+j+"行，第E列错误，用户账号仅字母和数字的组合");
                    }
                    vo.setUid(uid);

                    //密码
                    String password = getCellValue(row.getCell(5));
                    if(password.length() > 30 || password.length() < 3) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第F列错误，密码{字段不能为空、长度在[3-30]}");
                    }
                    vo.setPassword(password);

                    //职务
                    String positions = getCellValue(row.getCell(6));
                    if(!StringUtils.isEmpty(positions) && positions.length() > 30 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第G列错误，职务{字段长度在[~30]}");
                    }
                    vo.setPositions(positions);

                    //手机号
                    String mobile = getCellValue(row.getCell(7));
                    if(!StringUtils.isEmpty(mobile) && mobile.length() > 13 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第H列错误，手机号{字段长度在[~13]}");
                    }
                    vo.setMobile(mobile);

                    //办公电话
                    String officePhone = getCellValue(row.getCell(8));
                    if(!StringUtils.isEmpty(officePhone) && officePhone.length() > 20 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第I列错误，办公电话{字段长度在[~20]}");
                    }
                    vo.setOfficePhone(officePhone);

                    //办公地址
                    String officeAddress = getCellValue(row.getCell(9));
                    if(!StringUtils.isEmpty(officeAddress) && officeAddress.length() > 60 ) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第"+j+"行，第J列错误，办公地址{字段长度在[~60]}");
                    }
                    vo.setOfficeAddress(officeAddress);

                    //角色IDS
                    String roleIds = getCellValue(row.getCell(10));
                    vo.setRoleIds(roleIds);
                    persons.add(vo);
                }
            }
        }

        return persons;

    }
    /**
     * @param columnId   栏目id
     * @param columnType  栏目类型
     * @param siteId  站点id
     * 获取execl表格的项目规划信息
     *
     */

    public static List<ProjectInformationEO> getProjectInfos(InputStream is, Long siteId,Long columnId,String columnType) throws Exception {
        List<ProjectInformationEO> projectInformationEOs = null;

        Workbook wb = create(is);
        if(wb != null){
            projectInformationEOs = new ArrayList<ProjectInformationEO>();

            //（Sheet）的个数
            int countSheet = wb.getNumberOfSheets();
            for (int k = 0; k < countSheet; k++) {
                //（Sheet）对象数组
                Sheet sheet = wb.getSheetAt(k);
                //行
                int rowSize = sheet.getLastRowNum();
                if (rowSize < 0) continue;
                //第二行开始
                for (int i = 1; i <= rowSize; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;
                    int j = i + 1;
                    ProjectInformationEO projectInformationEO = new ProjectInformationEO();

                    //建设单位
                    String buildUnitName = getCellValue(row.getCell(0));
                    buildUnitName = buildUnitName.trim();
                    if (buildUnitName.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，建设单位不能为空");
                    }
                    projectInformationEO.setBuildUnitName(buildUnitName);

                    //项目名称
                    String projectName = getCellValue(row.getCell(1));
                    projectName = projectName.trim();
                    if (projectName.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，项目名称不能为空");
                    }
                    projectInformationEO.setProjectName(projectName);

                    //项目地址
                    String projectAddress = getCellValue(row.getCell(2));
                    projectAddress = projectAddress.trim();
                    if (projectAddress.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，项目地址不能为空");
                    }
                    projectInformationEO.setProjectAddress(projectAddress);

                    //项目面积
                    String area = getCellValue(row.getCell(3));
                    area = area.trim();
                    if (area.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，项目面积不能为空");
                    }
                    projectInformationEO.setArea(area);
                    //发证文号
                    String certificationNum = getCellValue(row.getCell(4));
                    certificationNum = certificationNum.trim();
                    if (certificationNum.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，发证文号不能为空");
                    }
                    projectInformationEO.setCertificationNum(certificationNum);

                    //发证日期
                    String certificationDate = getCellValue(row.getCell(5));
                    certificationDate = certificationDate.trim();
                    if (certificationDate.length() < 1) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(),
                                "第" + j + "行错误，发证日期不能为空");
                    }
                    projectInformationEO.setCertificationDate(DateUtil.getStrToDate(certificationDate, new SimpleDateFormat("yyyy-MM-dd")));
                    projectInformationEO.setSiteId(siteId);
                    projectInformationEO.setColumnId(columnId);
                    projectInformationEO.setInformationType(columnType);
                    projectInformationEOs.add(projectInformationEO);
                }
            }
        }
        return projectInformationEOs;
    }


    private static Workbook create(InputStream is){
        Workbook wb = null;
        try {
            //创建Workbook对象
            wb = new WorkbookFactory().create(is);
        }catch (Exception e){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"文档读取错误");
        }
        return wb;
    }

    private static String getCellValue(Cell cell) throws Exception{
        String strCell = "";
        if(cell != null){
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    strCell = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    /*DecimalFormat df = new DecimalFormat("#");
                    Double d = cell.getNumericCellValue();
                    if(d !=null){
                        strCell = String.valueOf(df.format(d));
                    }
                    break;*/
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                        SimpleDateFormat sdf = null;
                        if (cell.getCellStyle().getDataFormat() == HSSFDataFormat
                                .getBuiltinFormat("h:mm")) {
                            sdf = new SimpleDateFormat("HH:mm");
                        } else {// 日期
                            sdf = new SimpleDateFormat("yyyy-MM-dd");
                        }
                        Date date = cell.getDateCellValue();
                        strCell = sdf.format(date);
                    } else if (cell.getCellStyle().getDataFormat() == 58) {
                        // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil
                                .getJavaDate(value);
                        strCell = sdf.format(date);
                    } else {
                        double value = cell.getNumericCellValue();
                        CellStyle style = cell.getCellStyle();
                        DecimalFormat format = new DecimalFormat();
                        String temp = style.getDataFormatString();
                        // 单元格设置成常规
                        if (temp.equals("General")) {
                            format.applyPattern("#");
                        }
                        strCell = format.format(value);
                    }
                    break;
                case Cell.CELL_TYPE_BLANK:
                    strCell = "";
                    break;
                default:
                    strCell = "";
                    break;
            }
        }
        return strCell;
    }

}

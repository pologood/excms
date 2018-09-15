package ewebeditor.server;

import cn.lonsun.GlobalConfig;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class upload_jsp {

    protected HttpServletRequest m_request;
    protected HttpServletResponse m_response;
    private HttpSession m_session;
    protected ServletContext m_application;
    protected PageContext m_pagecontext;
    protected JspWriter m_out;

    private long nAllowSize;
    private String sAllowExt, sUploadDir, sBaseUrl, sContentPath, sSetContentPath, sAutoDir;
    private int nUploadObject;
    private String sSYText, sSYFontColor, sSYFontName, sSYPicPath, sSLTSYExt, sSYShadowColor;
    private int nSLTFlag, nSLTMode, nSLTCheckFlag, nSLTMinSize, nSLTOkSize, nSYWZFlag, nSYFontSize, nSLTSYObject, nSYWZMinWidth, nSYShadowOffset, nSYWZMinHeight, nSYWZPosition, nSYWZTextWidth, nSYWZTextHeight, nSYWZPaddingH, nSYWZPaddingV, nSYTPFlag, nSYTPMinWidth, nSYTPMinHeight, nSYTPPosition, nSYTPPaddingH, nSYTPPaddingV, nSYTPImageWidth, nSYTPImageHeight;
    private float nSYTPOpacity;
    private String sSpaceSize, sSpacePath, sMFUMode;
    private String sParamBlockFile, sParamBlockFlag;
    private String sFileNameMode, sFileNameSameFix, sAutoDirOrderFlag, sAutoTypeDir;
    private String sSYValidNormal, sSYValidLocal, sSYValidRemote;


    // param
    private String sAction;
    private String sType;
    private String sStyleName;
    private String sCusDir;
    private String sParamSYFlag;
    private String sParamRnd;
    private String sWSRootUrl;

    // interface
    private String sOriginalFileName;
    private String sSaveFileName;
    private String sPathFileName;

    private String sRealUploadPath;
    private String sFileSeparator;

    private util myUtil;
    private static GlobalConfig globalConfig = SpringContextHolder.getBean(GlobalConfig.class);
    private static final String downloadPrefix = "download";


    public upload_jsp() {
        sOriginalFileName = "";
        sSaveFileName = "";
        sPathFileName = "";
        myUtil = new util();

    }


    public final void Load(PageContext pagecontext) throws ServletException, IOException {
        m_pagecontext = pagecontext;
        m_application = pagecontext.getServletContext();
        m_request = (HttpServletRequest) pagecontext.getRequest();
        m_response = (HttpServletResponse) pagecontext.getResponse();
        m_session = (HttpSession) pagecontext.getSession();
        m_out = pagecontext.getOut();
        myUtil.InitServer(pagecontext);

        InitUpload();
    }


    private void InitUpload() throws ServletException, IOException {
        sFileSeparator = System.getProperty("file.separator");

        String sConfig = myUtil.getConfigText();
        ;
        ArrayList aStyle = myUtil.getConfigArray("Style", sConfig);

        // param
        String s_QueryStr = m_request.getQueryString();
        String[] a_Params = myUtil.split(s_QueryStr, "&");
        HashMap params = new HashMap();
        for (int i = 0; i < a_Params.length; i++) {
            String[] a_Param = myUtil.split(a_Params[i], "=");
            if (a_Param.length == 2) {
                params.put(a_Param[0], a_Param[1]);
            }
        }

        sAction = myUtil.dealNull((String) params.get("action")).toUpperCase();
        sWSRootUrl = myUtil.GetSafeUrl(myUtil.dealNull((String) params.get("ws")));
        sType = myUtil.dealNull((String) params.get("type")).toUpperCase();
        sStyleName = myUtil.dealNull((String) params.get("style"));
        sCusDir = myUtil.dealNull((String) params.get("cusdir"));
        sParamSYFlag = myUtil.dealNull((String) params.get("syflag"));
        sParamRnd = myUtil.dealNull((String) params.get("rnd"));

        String s_SKey = myUtil.dealNull((String) params.get("skey"));
        String s_SParams = myUtil.dealNull((String) params.get("sparams"));

        sParamBlockFile = myUtil.dealNull((String) params.get("blockfile"));
        sParamBlockFlag = myUtil.dealNull((String) params.get("blockflag"));
        if (!sParamBlockFile.equals("")) {
            sParamBlockFile = URLDecoder.decode(sParamBlockFile, "UTF-8");
            if (!IsFileNameFormat(sParamBlockFile)) {
                OutError("blockfile");
                return;
            }
        }


        // InitUpload


        String[] aStyleConfig = new String[1];
        boolean bValidStyle = false;

        for (int i = 0; i < aStyle.size(); i++) {
            aStyleConfig = myUtil.split(aStyle.get(i).toString(), "|||");
            if (sStyleName.toLowerCase().equals(aStyleConfig[0].toLowerCase())) {
                bValidStyle = true;
                break;
            }
        }

        if (!bValidStyle) {
            OutError("style");
            return;
        }

        if ((!sWSRootUrl.equals("")) && aStyleConfig[112].equals("")) {
            OutError("ws");
            return;
        }

        if (!aStyleConfig[61].equals("1")) {
            sCusDir = "";
        }

        String ss_FileSize = "", ss_FileBrowse = "", ss_SpaceSize = "", ss_SpacePath = "", ss_PathMode = "", ss_PathUpload = "", ss_PathCusDir = "", ss_PathCode = "", ss_PathView = "";
        if ((aStyleConfig[61].equals("2")) && ((!s_SKey.equals("")) || (myUtil.IsOkSParams(s_SParams, aStyleConfig[70])))) {
            if (!s_SKey.equals("")) {
                ss_FileSize = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_FileSize"));
                ss_FileBrowse = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_FileBrowse"));
                ss_SpaceSize = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_SpaceSize"));
                ss_SpacePath = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_SpacePath"));
                ss_PathMode = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathMode"));
                ss_PathUpload = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathUpload"));
                ss_PathCusDir = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathCusDir"));
                ss_PathCode = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathCode"));
                ss_PathView = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathView"));
            } else {
                String[] a_SParams = myUtil.split(s_SParams, "|");
                ss_FileSize = a_SParams[1];
                ss_FileBrowse = a_SParams[2];
                ss_SpaceSize = a_SParams[3];
                ss_SpacePath = a_SParams[4];
                ss_PathMode = a_SParams[5];
                ss_PathUpload = a_SParams[6];
                ss_PathCusDir = a_SParams[7];
                ss_PathCode = a_SParams[8];
                ss_PathView = a_SParams[9];
            }

            if (myUtil.IsInt(ss_FileSize)) {
                aStyleConfig[11] = ss_FileSize;
                aStyleConfig[12] = ss_FileSize;
                aStyleConfig[13] = ss_FileSize;
                aStyleConfig[14] = ss_FileSize;
                aStyleConfig[15] = ss_FileSize;
                aStyleConfig[45] = ss_FileSize;
            } else {
                ss_FileSize = "";
            }
            if (ss_FileBrowse.equals("0") || ss_FileBrowse.equals("1")) {
                aStyleConfig[43] = ss_FileBrowse;
            } else {
                ss_FileBrowse = "";
            }
            if (myUtil.IsInt(ss_SpaceSize)) {
                aStyleConfig[78] = ss_SpaceSize;
            } else {
                ss_SpaceSize = "";
            }
            if (!ss_PathMode.equals("")) {
                aStyleConfig[19] = ss_PathMode;
            }
            if (!ss_PathUpload.equals("")) {
                aStyleConfig[3] = ss_PathUpload;
            }
            if (!ss_PathCode.equals("")) {
                aStyleConfig[23] = ss_PathCode;
            }
            if (!ss_PathView.equals("")) {
                aStyleConfig[22] = ss_PathView;
            }

            sCusDir = ss_PathCusDir;
            sSpacePath = ss_SpacePath;
        } else {
            sSpacePath = "";
        }


        sBaseUrl = aStyleConfig[19];
        nUploadObject = Integer.valueOf(aStyleConfig[20]).intValue();
        sAutoDir = aStyleConfig[71];

        sUploadDir = aStyleConfig[3];

        if (sBaseUrl.equals("1")) {
            /**
             * 改造返回地址路径
             */
            String fileServerPath = globalConfig.getFileServerNamePath();
            if (null != fileServerPath) {
                fileServerPath = fileServerPath.endsWith("/") ? fileServerPath : fileServerPath + "/";
                sContentPath = fileServerPath;
            } else {
                sContentPath = RelativePath2RootPath(sUploadDir);
            }
        } else if (sBaseUrl.equals("2")) {
            sContentPath = RootPath2DomainPath(RelativePath2RootPath(sUploadDir));
        } else {
            //0,3
            sContentPath = aStyleConfig[23];
        }
        sSetContentPath = sContentPath;


        if (sType.equals("REMOTE")) {
            sAllowExt = aStyleConfig[10];
            nAllowSize = Long.valueOf(aStyleConfig[15]).longValue();
            sAutoTypeDir = aStyleConfig[93];
        } else if (sType.equals("FILE")) {
            sAllowExt = aStyleConfig[6];
            nAllowSize = Long.valueOf(aStyleConfig[11]).longValue();
            sAutoTypeDir = aStyleConfig[92];
        } else if (sType.equals("MEDIA")) {
            sAllowExt = aStyleConfig[9];
            nAllowSize = Long.valueOf(aStyleConfig[14]).longValue();
            sAutoTypeDir = aStyleConfig[91];
        } else if (sType.equals("FLASH")) {
            sAllowExt = aStyleConfig[7];
            nAllowSize = Long.valueOf(aStyleConfig[12]).longValue();
            sAutoTypeDir = aStyleConfig[90];
        } else if (sType.equals("LOCAL")) {
            sAllowExt = aStyleConfig[44];
            nAllowSize = Long.valueOf(aStyleConfig[45]).longValue();
            sAutoTypeDir = aStyleConfig[94];
        } else {
            sAllowExt = aStyleConfig[8];
            nAllowSize = Long.valueOf(aStyleConfig[13]).longValue();
            sAutoTypeDir = aStyleConfig[89];
        }

        nSLTFlag = Integer.valueOf(aStyleConfig[29]).intValue();
        nSLTMode = Integer.valueOf(aStyleConfig[69]).intValue();
        nSLTCheckFlag = Integer.valueOf(aStyleConfig[77]).intValue();
        nSLTMinSize = Integer.valueOf(aStyleConfig[30]).intValue();
        nSLTOkSize = Integer.valueOf(aStyleConfig[31]).intValue();
        nSYWZFlag = Integer.valueOf(aStyleConfig[32]).intValue();
        sSYText = aStyleConfig[33];
        sSYFontColor = aStyleConfig[34];
        nSYFontSize = Integer.valueOf(aStyleConfig[35]).intValue();
        sSYFontName = aStyleConfig[36];
        sSYPicPath = aStyleConfig[37];
        nSLTSYObject = Integer.valueOf(aStyleConfig[38]).intValue();
        sSLTSYExt = aStyleConfig[39];
        nSYWZMinWidth = Integer.valueOf(aStyleConfig[40]).intValue();
        sSYShadowColor = aStyleConfig[41];
        nSYShadowOffset = Integer.valueOf(aStyleConfig[42]).intValue();
        nSYWZMinHeight = Integer.valueOf(aStyleConfig[46]).intValue();
        nSYWZPosition = Integer.valueOf(aStyleConfig[47]).intValue();
        nSYWZTextWidth = Integer.valueOf(aStyleConfig[48]).intValue();
        nSYWZTextHeight = Integer.valueOf(aStyleConfig[49]).intValue();
        nSYWZPaddingH = Integer.valueOf(aStyleConfig[50]).intValue();
        nSYWZPaddingV = Integer.valueOf(aStyleConfig[51]).intValue();
        nSYTPFlag = Integer.valueOf(aStyleConfig[52]).intValue();
        nSYTPMinWidth = Integer.valueOf(aStyleConfig[53]).intValue();
        nSYTPMinHeight = Integer.valueOf(aStyleConfig[54]).intValue();
        nSYTPPosition = Integer.valueOf(aStyleConfig[55]).intValue();
        nSYTPPaddingH = Integer.valueOf(aStyleConfig[56]).intValue();
        nSYTPPaddingV = Integer.valueOf(aStyleConfig[57]).intValue();
        nSYTPImageWidth = Integer.valueOf(aStyleConfig[58]).intValue();
        nSYTPImageHeight = Integer.valueOf(aStyleConfig[59]).intValue();
        nSYTPOpacity = Float.valueOf(aStyleConfig[60]).floatValue();
        sSpaceSize = aStyleConfig[78];
        sMFUMode = aStyleConfig[79];
        sFileNameMode = aStyleConfig[68];
        sFileNameSameFix = aStyleConfig[87];
        sAutoDirOrderFlag = aStyleConfig[88];
        sSYValidNormal = aStyleConfig[99];
        sSYValidLocal = aStyleConfig[100];
        sSYValidRemote = aStyleConfig[101];


        if (((sAction.equals("SAVE") || sAction.equals("MFU")) && !sSYValidNormal.equals("1")) || (sAction.equals("LOCAL") && !sSYValidLocal.equals("1")) || (sAction.equals("REMOTE") && !sSYValidRemote.equals("1"))) {
            nSYWZFlag = 0;
            nSYTPFlag = 0;
        }

        if (nSYWZFlag == 2) {
            if (sParamSYFlag.equals("1")) {
                nSYWZFlag = 1;
            } else {
                nSYWZFlag = 0;
            }
        }
        if (nSYTPFlag == 2) {
            if (sParamSYFlag.equals("1")) {
                nSYTPFlag = 1;
            } else {
                nSYTPFlag = 0;
            }
        }

        if (!myUtil.IsInt(sParamRnd)) {
            sParamRnd = "";
        }

        if (!sBaseUrl.equals("3")) {
            sRealUploadPath = myUtil.getRealPathFromRelative(sUploadDir);
        } else {
            sRealUploadPath = sUploadDir;
        }
        if (!sRealUploadPath.endsWith(sFileSeparator)) {
            sRealUploadPath += sFileSeparator;
        }

        if (sSYPicPath.startsWith("../")) {
            sSYPicPath = sSYPicPath.substring(3);
        } else if (sSYPicPath.startsWith("./")) {
            sSYPicPath = "jsp/" + sSYPicPath.substring(2);
        } else if (!sSYPicPath.startsWith("/")) {
            sSYPicPath = "jsp/" + sSYPicPath;
        }
        if (!sSYPicPath.equals("")) {
            sSYPicPath = myUtil.getRealPathFromRelative(sSYPicPath);
        }


        if (!sCusDir.equals("")) {
            sCusDir = myUtil.replace(sCusDir, "\\", "/");
            if ((sCusDir.startsWith("/")) || (sCusDir.indexOf("//") >= 0) || (myUtil.RegExpTest(sCusDir, "[\\.\\;\\,\\:\\*\\?\\\"\\|\\<\\>\\r\\n\\t]+"))) {
                sCusDir = "";
            } else {
                if (!sCusDir.endsWith("/")) {
                    sCusDir = sCusDir + "/";
                }
            }
        }

        DoCreateNewDir();

        if (sAction.equals("REMOTE")) {
            DoRemote();

        } else if (sAction.equals("SAVE")) {
            DoSave();

        } else if (sAction.equals("LOCAL")) {
            DoLocal();

        } else if (sAction.equals("MFU")) {
            DoMFU();

        }

    }

    private void DoCreateNewDir() throws IOException {
        if (!sCusDir.equals("")) {
            String[] aTmp = myUtil.split(sCusDir, "/");
            for (int i = 0; i < aTmp.length; i++) {
                if (!aTmp[i].equals("")) {
                    sRealUploadPath += aTmp[i] + sFileSeparator;
                    sContentPath += aTmp[i] + "/";
                    Mkdir(sRealUploadPath);
                }
            }
        }

        if (!CheckSpaceSize()) {
            return;
        }

        if (sAutoDirOrderFlag.equals("0")) {
            DoCreateNewTypeDir();
            DoCreateNewDateDir();
        } else if (sAutoDirOrderFlag.equals("1")) {
            DoCreateNewDateDir();
            DoCreateNewTypeDir();
        }
    }

    private void DoCreateNewDateDir() {
        if (sAutoDir.equals("")) {
            return;
        }

        String s_NewDir = myUtil.ReplaceTime(new Date(), sAutoDir);
        String[] aTmp = myUtil.split(s_NewDir, "/");
        for (int i = 0; i < aTmp.length; i++) {
            if (!aTmp[i].equals("")) {
                sRealUploadPath += aTmp[i] + sFileSeparator;
                sContentPath += aTmp[i] + "/";
                Mkdir(sRealUploadPath);
            }
        }
    }

    private void DoCreateNewTypeDir() {
        if (sAutoTypeDir.equals("")) {
            return;
        }

        String[] aTmp = myUtil.split(sAutoTypeDir, "/");
        for (int i = 0; i < aTmp.length; i++) {
            if (!aTmp[i].equals("")) {
                sRealUploadPath += aTmp[i] + sFileSeparator;
                sContentPath += aTmp[i] + "/";
                Mkdir(sRealUploadPath);
            }
        }
    }


    private void DoRemote() throws IOException {
        String sRemoteContent = myUtil.dealNull(m_request.getParameter("eWebEditor_UploadText"));
        if (!sAllowExt.equals("") && !sRemoteContent.equals("")) {
            Pattern p = Pattern.compile("((http|https|ftp|rtsp|mms):(\\/\\/|\\\\\\\\){1}(([A-Za-z0-9_-])+[.]){1,}([A-Za-z0-9]{1,5})(:[0-9]+?)?\\/(\\S+\\.(" + sAllowExt + ")))", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(sRemoteContent);
            ArrayList a_RemoteUrl = new ArrayList();
            String sRemoteurl = "";
            boolean bFind = false;

            String s_SameSiteDomain = "";
            if (sBaseUrl.equals("3")) {
                s_SameSiteDomain = GetDomainFromUrl(sSetContentPath);
            }
            if (s_SameSiteDomain.equals("")) {
                s_SameSiteDomain = m_request.getServerName().toLowerCase();
            }

            while (m.find()) {
                sRemoteurl = sRemoteContent.substring(m.start(), m.end());
                boolean b_SameSiteUrl = false;

                if (GetDomainFromUrl(sRemoteurl).equals(s_SameSiteDomain)) {
                    b_SameSiteUrl = true;
                }

                if (!b_SameSiteUrl) {
                    bFind = false;
                    for (int i = 0; i < a_RemoteUrl.size(); i++) {
                        if (sRemoteurl.equals(a_RemoteUrl.get(i).toString())) {
                            bFind = true;
                        }
                    }
                    if (bFind == false) {
                        a_RemoteUrl.add(sRemoteurl);
                    }
                }
            }

            String SaveFileType = "";
            String SaveFileName = "";
            for (int i = 0; i < a_RemoteUrl.size(); i++) {
                sRemoteurl = a_RemoteUrl.get(i).toString();
                SaveFileType = sRemoteurl.substring(sRemoteurl.lastIndexOf(".") + 1);
                SaveFileName = GetRndFileName(SaveFileType);
                MongoFileVO fileVO = SaveRemoteFile_ex8(SaveFileName, sRemoteurl);
                //if (SaveRemoteFile(SaveFileName, sRemoteurl, sRealUploadPath)){
                if (null != fileVO) {
                    SaveFileName = fileVO.getFileName();
                    makeImageSY(sRealUploadPath + SaveFileName);
                    if (!sOriginalFileName.equals("")) {
                        sOriginalFileName = sOriginalFileName + "|";
                        sSaveFileName = sSaveFileName + "|";
                        sPathFileName = sPathFileName + "|";
                    }
                    sOriginalFileName = sOriginalFileName + sRemoteurl.substring(sRemoteurl.lastIndexOf("/") + 1);
                    sSaveFileName = sSaveFileName + SaveFileName;
                    sPathFileName = sPathFileName + sContentPath + SaveFileName;
                    sRemoteContent = myUtil.replace(sRemoteContent, sRemoteurl, sContentPath + SaveFileName);
                }
            }
        }


        String s_FormItem = "", s_Script = "";
        s_FormItem = myUtil.GetHideInputHtml("d_content", sRemoteContent) + myUtil.GetHideInputHtml("d_ori", sOriginalFileName) + myUtil.GetHideInputHtml("d_save", sPathFileName);
        s_Script = "parent.setHTML(document.getElementById('d_content').value); " +
            "try{parent.addUploadFiles(document.getElementById('d_ori').value, document.getElementById('d_save').value);} catch(e){} " +
            "parent.remoteUploadOK();";

        OutScript("uploadremote", s_FormItem, s_Script);
    }


    private void DoSave() throws ServletException, IOException {
        //m_out.print("<html><head><title>eWebEditor</title><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head><body>");

        String s_Flag = doingUpload();
        if (!s_Flag.equals("ok")) {
            OutError(s_Flag);
            return;
        }

        sOriginalFileName = myUtil.replace(sOriginalFileName, "'", "\\'");
        sOriginalFileName = myUtil.replace(sOriginalFileName, "\"", "\\\"");

        String s_SmallImageFile, s_SmallImagePathFile, s_SmallImageScript;
        boolean b_SY;
        s_SmallImageFile = getSmallImageFile(sSaveFileName);
        s_SmallImagePathFile = "";
        s_SmallImageScript = "";
        if (makeImageSLT(sRealUploadPath, sSaveFileName, s_SmallImageFile)) {
            switch (nSLTMode) {
                case 1:
                    b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                    b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
                    s_SmallImagePathFile = sContentPath + s_SmallImageFile;
                    break;
                case 2:
                    b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                    DelFile(sRealUploadPath + sSaveFileName);
                    sSaveFileName = s_SmallImageFile;
                    break;
                default:
                    b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                    b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
                    s_SmallImagePathFile = sContentPath + s_SmallImageFile;
                    break;
            }

        } else {
            s_SmallImageFile = "";
            b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
        }

        sPathFileName = sContentPath + sSaveFileName;
        if (null != sSaveFileName && (sSaveFileName.toLowerCase().endsWith(".pdf"))) {
            sPathFileName = sSaveFileName;
        }

        String s_FormItem = "", s_Script = "";
        s_FormItem = myUtil.GetHideInputHtml("d_ori", sOriginalFileName) + myUtil.GetHideInputHtml("d_save", sPathFileName) + myUtil.GetHideInputHtml("d_thumb", s_SmallImagePathFile);
        s_Script = "parent.UploadSaved(document.getElementById('d_ori').value, document.getElementById('d_save').value, document.getElementById('d_thumb').value);";
        OutScript("uploadsave", s_FormItem, s_Script);
    }


    private void DoLocal() throws ServletException, IOException {
        String s_Flag = doingUpload();
        if (!s_Flag.equals("ok")) {
            return;
        }
        makeImageSY(sRealUploadPath + sSaveFileName);

        sPathFileName = sContentPath + sSaveFileName;
        if (null != sSaveFileName && (sSaveFileName.toLowerCase().endsWith(".pdf"))) {
            sPathFileName = sSaveFileName;
        }
        m_out.print(sPathFileName);
    }


    private void DoMFU() throws ServletException, IOException {
        if (sParamBlockFlag.equals("cancel")) {
            if (!sParamBlockFile.equals("")) {
                DelFile(sRealUploadPath + sParamBlockFile + ".tmp1");
            }
            m_out.print("ok");
            return;
        }

        String s_Flag = doingUpload();
        if (!s_Flag.equals("ok")) {
            return;
        }

        if (sParamBlockFlag.equals("end")) {

            String s_SmallImageFile, s_SmallImagePathFile, s_SmallImageScript;
            boolean b_SY;
            s_SmallImageFile = sSaveFileName;//getSmallImageFile(sSaveFileName);
            s_SmallImagePathFile = "";
            s_SmallImageScript = "";
            if (makeImageSLT(sRealUploadPath, sSaveFileName, s_SmallImageFile)) {
                switch (nSLTMode) {
                    case 1:
                        b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                        b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
                        s_SmallImagePathFile = sContentPath + s_SmallImageFile;
                        s_SmallImageScript = "try{obj.addUploadFile('" + sOriginalFileName + "', '" + s_SmallImagePathFile + "');} catch(e){} ";
                        s_SmallImagePathFile = "";
                        break;
                    case 2:
                        b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                        DelFile(sRealUploadPath + sSaveFileName);
                        sSaveFileName = s_SmallImageFile;
                        break;
                    default:
                        b_SY = makeImageSY(sRealUploadPath + s_SmallImageFile);
                        b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
                        s_SmallImagePathFile = sContentPath + s_SmallImageFile;
                        s_SmallImageScript = "try{obj.addUploadFile('" + sOriginalFileName + "', '" + s_SmallImagePathFile + "');} catch(e){} ";
                        break;
                }

            } else {
                s_SmallImageFile = "";
                b_SY = makeImageSY(sRealUploadPath + sSaveFileName);
            }

            sPathFileName = sContentPath + sSaveFileName;
            if (null != sSaveFileName && (sSaveFileName.toLowerCase().endsWith(".pdf"))) {
                sPathFileName = sSaveFileName;
            }
            m_out.print(sPathFileName + "::" + s_SmallImagePathFile);
        } else {
            m_out.print(sSaveFileName.substring(0, sSaveFileName.lastIndexOf(".")));
        }
    }


    private String doingUpload() throws ServletException, IOException {
        File f = new File(sRealUploadPath);
		/*if (!f.isDirectory()){
			return "dir";
		}
		if (!f.canWrite()){
			return "write";
		}*/

        switch (nUploadObject) {
            /**case 3:
             return doingUpload_WebWork2();
             case 2:
             return doingUpload_Struts2();
             case 4:
             return doingUpload_Struts2dot5();**/
            case 1:
                return doingUpload_CommonsFileUpload();
            case 0:
                return doingUpload_JspSmartUpload();
        }
        return "param";
    }

    private String doingUpload_CommonsFileUpload() throws ServletException {
        if (m_request instanceof ShiroHttpServletRequest) {
            return doingUpload_lonsunEx8();
        }
        // Create a factory for disk-based file items
        org.apache.commons.fileupload.disk.DiskFileItemFactory factory = new org.apache.commons.fileupload.disk.DiskFileItemFactory();

        // Set factory constraints
        factory.setSizeThreshold(4096);
        factory.setRepository(new File(sRealUploadPath));

        // Create a new file upload handler
        org.apache.commons.fileupload.servlet.ServletFileUpload upload = new org.apache.commons.fileupload.servlet.ServletFileUpload(factory);

        // Set overall request size constraint
        upload.setSizeMax(nAllowSize * 1024);
        //upload.setFileSizeMax(nAllowSize*1024);

        upload.setHeaderEncoding("UTF-8");

        try {
            if (m_request instanceof ShiroHttpServletRequest) {
                ShiroHttpServletRequest shiroRequest = (ShiroHttpServletRequest) m_request;
                m_request = (HttpServletRequest) shiroRequest.getRequest();
                if (m_request instanceof AbstractMultipartHttpServletRequest) {
                    m_request = ((AbstractMultipartHttpServletRequest) m_request).getRequest();
                }
            }
            // Parse the request
            java.util.List items = upload.parseRequest(m_request);
            // Process the uploaded items
            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                org.apache.commons.fileupload.FileItem item = (org.apache.commons.fileupload.FileItem) iter.next();
                if (item.isFormField()) {
                    continue;
                }    // Process a regular form field

                String s_FileName = item.getName();        // include path
                if (s_FileName != null) {
                    s_FileName = org.apache.commons.io.FilenameUtils.getName(s_FileName);
                }

                long n_FileSize = item.getSize();
                if (s_FileName == null || s_FileName.equals("") || n_FileSize == 0) {
                    continue;
                }

                String s_FileExt = s_FileName.substring(s_FileName.lastIndexOf(".") + 1);
                if (!CheckValidExt(s_FileExt)) {
                    return "ext";
                }

                sOriginalFileName = s_FileName;
                sSaveFileName = GetRndFileName(s_FileExt);

                String s_MapPath = sRealUploadPath + sSaveFileName;
                String s_TrueSavePath = MFU_GetSavePath(s_MapPath);

                item.write(new File(s_TrueSavePath));

                MFU_DoUploadAfter(s_MapPath);
            }
        } catch (Exception e) {
            return "size";
        }
        return "ok";
    }

    private String doingUpload_lonsunEx8() {
        try {
            if (m_request instanceof ShiroHttpServletRequest) {
                ShiroHttpServletRequest shiroRequest = (ShiroHttpServletRequest) m_request;
                //CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
                //MultipartHttpServletRequest multipartRequest = commonsMultipartResolver.resolveMultipart((HttpServletRequest) shiroRequest.getRequest());
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) shiroRequest.getRequest();
                Long siteId = null;
                Long columnId = null;
                String cusdir = m_request.getParameter("cusdir");
                if (StringUtils.isNotEmpty(cusdir)) {
                    int startIndex = 0;
                    if (cusdir.startsWith("/")) {
                        startIndex = 1;
                    }
                    String[] params = cusdir.split("/");
                    //少于两个参数无法判断哪个参数是站点id，哪个是栏目id
                    if (params.length > 1) {
                        siteId = Long.valueOf(params[startIndex + 0]);
                        columnId = Long.valueOf(params[startIndex + 1]);
                    }
                }
                int contentWidth = 0;
                if (null != siteId && null != columnId) {
                    ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
                    if (configVO != null && !AppUtil.isEmpty(configVO.getContentWidth())) {
                        contentWidth = configVO.getContentWidth();
                    }
                }
                Iterator<String> itr = multipartRequest.getFileNames();
                MultipartFile item = null;
                MongoFileVO mgvo = null;
                MongoFileVO originalMgvo = null;//无压缩大图
                while (itr.hasNext()) {
                    item = multipartRequest.getFile(itr.next());

                    String s_FileName = item.getOriginalFilename();        // include path
                    if (s_FileName != null) {
                        s_FileName = org.apache.commons.io.FilenameUtils.getName(s_FileName);
                    }

                    long n_FileSize = item.getSize();
                    if (s_FileName == null || s_FileName.equals("") || n_FileSize == 0) {
                        continue;
                    }

                    String s_FileExt = s_FileName.substring(s_FileName.lastIndexOf(".") + 1);
                    if (!CheckValidExt(s_FileExt)) {
                        return "ext";
                    }
                    sOriginalFileName = s_FileName;
                    try {
                        if (!AppUtil.isEmpty(siteId) && EditorUploadUtil.checkPic(s_FileExt) && contentWidth > 0) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImgHander.ImgTrans(item.getInputStream(), contentWidth, 0, baos, s_FileExt);
                            byte[] b = baos.toByteArray();
                            byte[] bt = WaterMarkUtil.createWaterMark(new ByteArrayInputStream(b), siteId, s_FileExt);
                            mgvo = FileUploadUtil.editorUpload(bt, s_FileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", m_request);
                            originalMgvo = FileUploadUtil.editorUpload(item, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", m_request);
                        } else {
                            mgvo = FileUploadUtil.editorUpload(item, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", m_request);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "size";
                    }

                    sSaveFileName = mgvo.getFileName();//GetRndFileName(s_FileExt);
                    if (null != originalMgvo) {
                        String fileServerPath = globalConfig.getFileServerNamePath();
                        if (null != fileServerPath) {
                            fileServerPath = fileServerPath.endsWith("/") ? fileServerPath : fileServerPath + "/";
                        }
                        sSaveFileName += "[~~]" + fileServerPath + originalMgvo.getFileName();
                    }
                    if (null != mgvo.getContentType() && "pdf".equals(mgvo.getContentType().toLowerCase())) {
                        String fileServerPath = globalConfig.getFileServerNamePath();
                        fileServerPath = getFileServerPathPrefix(fileServerPath);
                        sSaveFileName = fileServerPath + "/" + downloadPrefix + "/" + mgvo.getMongoId() + "." + mgvo.getContentType();
                        System.out.println("==========PDF返回给前台地址=================" + sSaveFileName);
                    }

					/*String s_MapPath = sRealUploadPath+sSaveFileName;
					String s_TrueSavePath = MFU_GetSavePath(s_MapPath);


					item.transferTo(new File(s_TrueSavePath));

					MFU_DoUploadAfter(s_MapPath);*/
                }

            }
        } catch (Exception e) {
            return "size";
        }
        return "ok";

    }

    private String getFileServerPathPrefix(String fileServerPath) {
        if (null != fileServerPath) {
            fileServerPath = fileServerPath.trim();
            if (fileServerPath.indexOf("http://") > -1) {
                fileServerPath = fileServerPath.replace("http://", "");
                if (fileServerPath.indexOf("/") > -1) {
                    fileServerPath = fileServerPath.substring(0, fileServerPath.indexOf("/"));
                }
                fileServerPath = "http://" + fileServerPath;
            } else {
                fileServerPath = "";
            }
        } else {
            fileServerPath = "";
        }
        return fileServerPath;
    }

    private MongoFileVO doingUpload_lonsunEx8(String fileName, InputStream inputStream) {
        Long siteId = null;
        Long columnId = null;
        if (!AppUtil.isEmpty(m_request.getParameter("siteId"))) {
            siteId = Long.parseLong(m_request.getParameter("siteId"));
        }
        if (!AppUtil.isEmpty(m_request.getParameter("columnId"))) {
            columnId = Long.parseLong(m_request.getParameter("columnId"));
        }
        int contentWidth = 0;
        if (null != siteId && null != columnId) {
            ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(columnId, siteId);
            if (configVO != null && !AppUtil.isEmpty(configVO.getContentWidth())) {
                contentWidth = configVO.getContentWidth();
            }
        }
        String s_FileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
        MongoFileVO mgvo = null;
        try {
            if (!AppUtil.isEmpty(siteId) && EditorUploadUtil.checkPic(s_FileExt) && contentWidth > 0) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImgHander.ImgTrans(inputStream, contentWidth, 0, baos, s_FileExt);
                byte[] b = baos.toByteArray();
                byte[] bt = WaterMarkUtil.createWaterMark(new ByteArrayInputStream(b), siteId, s_FileExt);
                mgvo = FileUploadUtil.editorUpload(bt, fileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", m_request);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buff = new byte[100];
                int rc = 0;
                while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                    baos.write(buff, 0, rc);
                }
                byte[] b = baos.toByteArray();
                mgvo = FileUploadUtil.editorUpload(b, fileName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", m_request);
            }
        } catch (Exception e) {
        }
        return mgvo;

    }

    private String doingUpload_JspSmartUpload() throws ServletException {
        com.jspsmart.upload.SmartUpload mySmartUpload = new com.jspsmart.upload.SmartUpload();
        mySmartUpload.initialize(m_pagecontext);
        mySmartUpload.setMaxFileSize(nAllowSize * 1024);
        mySmartUpload.setCharset("UTF-8");
        //String sAllowedFilesList = myUtil.replace(sAllowExt, "|", ",");
        //mySmartUpload.setAllowedFilesList(sAllowedFilesList);
        try {
            mySmartUpload.upload();
            com.jspsmart.upload.File oFile = mySmartUpload.getFiles().getFile(0);
            String s_FileExt = oFile.getFileExt().toLowerCase();
            if (!CheckValidExt(s_FileExt)) {
                return "ext";
            }
            sOriginalFileName = oFile.getFileName();
            sSaveFileName = GetRndFileName(s_FileExt);

            String s_MapPath = sRealUploadPath + sSaveFileName;
            String s_TrueSavePath = MFU_GetSavePath(s_MapPath);
            oFile.saveAs(s_TrueSavePath, oFile.SAVEAS_PHYSICAL);

            MFU_DoUploadAfter(s_MapPath);
        } catch (Exception e) {
            return "size";
        }
        return "ok";
    }


    /*private String doingUpload_Struts2() throws IOException {
        org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper wrapper = (org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper) m_request;
        File file = wrapper.getFiles("uploadfile")[0];
        sOriginalFileName = wrapper.getFileNames("uploadfile")[0];
        String s_FileExt = sOriginalFileName.substring(sOriginalFileName.lastIndexOf(".") + 1).toLowerCase();
        if (!CheckValidExt(s_FileExt)) {
            return "ext";
        }
        if (file.length() > nAllowSize * 1024) {
            return "size";
        }
        sSaveFileName = GetRndFileName(s_FileExt);

        String s_MapPath = sRealUploadPath + sSaveFileName;
        String s_TrueSavePath = MFU_GetSavePath(s_MapPath);

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(s_TrueSavePath);
        InputStream in = new FileInputStream(file);
        try {
            int num = 0;
            while ((num = in.read(buffer)) > 0) {
                fos.write(buffer, 0, num);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            in.close();
            fos.close();
        }

        MFU_DoUploadAfter(s_MapPath);
        return "ok";
    }

    private String doingUpload_Struts2dot5() throws IOException {
        ewebeditor.struts2dot5.upload up = new ewebeditor.struts2dot5.upload();
        up.Init(m_request);
        up.setFieldName("uploadfile");
        if (!up.isFile()) {
            return "isfile";
        }
        sOriginalFileName = up.getOriginalName();
        String s_FileExt = sOriginalFileName.substring(sOriginalFileName.lastIndexOf(".") + 1).toLowerCase();
        if (!CheckValidExt(s_FileExt)) {
            return "ext";
        }
        if (up.length().longValue() > nAllowSize * 1024) {
            return "size";
        }
        sSaveFileName = GetRndFileName(s_FileExt);

        String s_MapPath = sRealUploadPath + sSaveFileName;
        String s_TrueSavePath = MFU_GetSavePath(s_MapPath);

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(s_TrueSavePath);
        InputStream in = new FileInputStream(new File(up.getAbsolutePath()));
        try {
            int num = 0;
            while ((num = in.read(buffer)) > 0) {
                fos.write(buffer, 0, num);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            in.close();
            fos.close();
        }

        MFU_DoUploadAfter(s_MapPath);
        return "ok";
    }

    private String doingUpload_WebWork2() throws IOException {
        com.opensymphony.webwork.dispatcher.multipart.MultiPartRequestWrapper wrapper = (com.opensymphony.webwork.dispatcher.multipart.MultiPartRequestWrapper) m_request;
        File file = wrapper.getFiles("uploadfile")[0];
        sOriginalFileName = wrapper.getFileNames("uploadfile")[0];
        String s_FileExt = sOriginalFileName.substring(sOriginalFileName.lastIndexOf(".") + 1).toLowerCase();
        if (!CheckValidExt(s_FileExt)) {
            return "ext";
        }
        if (file.length() > nAllowSize * 1024) {
            return "size";
        }
        sSaveFileName = GetRndFileName(s_FileExt);

        String s_MapPath = sRealUploadPath + sSaveFileName;
        String s_TrueSavePath = MFU_GetSavePath(s_MapPath);

        byte[] buffer = new byte[1024];
        FileOutputStream fos = new FileOutputStream(s_TrueSavePath);
        InputStream in = new FileInputStream(file);
        try {
            int num = 0;
            while ((num = in.read(buffer)) > 0) {
                fos.write(buffer, 0, num);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            in.close();
            fos.close();
        }

        MFU_DoUploadAfter(s_MapPath);
        return "ok";
    }*/

    private String MFU_GetSavePath(String s_Path) throws IOException {
        if (!sAction.equals("MFU")) {
            return s_Path;
        }

        String s_Ret = "";
        if (sParamBlockFile.equals("")) {
            //new
            if (sParamBlockFlag.equals("end")) {
                s_Ret = s_Path;
            } else {
                s_Ret = s_Path + ".tmp1";
            }
        } else {
            if (!IsFileExists(s_Path + ".tmp1")) {
                OutError("file");
                return "";
            }

            //append
            s_Ret = s_Path + ".tmp2";
        }
        return s_Ret;
    }

    private void MFU_DoUploadAfter(String s_Path) throws IOException {
        if (!sAction.equals("MFU")) {
            return;
        }

        if (!sParamBlockFile.equals("")) {
            //if (sMFUMode.equals("1")){
            //	MFU_DoMergeFile_Adv(s_Path);
            //}else{
            MFU_DoMergeFile_Normal(s_Path);
            //}

            if (sParamBlockFlag.equals("end")) {
                //rename
                RenameFile(s_Path + ".tmp1", s_Path);
            }
        }
    }


    private void MFU_DoMergeFile_Normal(String s_File) throws IOException {
        String s_File1 = s_File + ".tmp1";
        String s_File2 = s_File + ".tmp2";

        FileOutputStream fos = new FileOutputStream(s_File1, true);
        FileInputStream fis = new FileInputStream(s_File2);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = fis.read(buffer, 0, 1024)) != -1) {
            fos.write(buffer, 0, len);
            fos.flush();
        }
        fis.close();
        fos.close();


        //del tmp2
        DelFile(s_File2);
    }


    private String GetRndFileName(String s_Ext) {
        s_Ext = s_Ext.toLowerCase();
        if (sAction.equals("MFU") && !sParamBlockFile.equals("")) {
            return sParamBlockFile + "." + s_Ext;
        }

        int i_Rnd = (int) (Math.random() * 900) + 100;
        String s_Rnd = String.valueOf(i_Rnd) + sParamRnd;

        String s_RndTime = myUtil.formatDate(new Date(), 4) + s_Rnd;
        String s_FileName = s_RndTime + "." + s_Ext;

        if (sAction.equals("SAVE") || sAction.equals("MFU")) {
            String s_Pre = GetOriginalFileNamePre(sOriginalFileName, s_Ext);
            if (!s_Pre.equals("")) {
                if (sFileNameMode.equals("1") || (sFileNameMode.equals("2") && sType.equals("FILE"))) {
                    s_FileName = s_Pre + "." + s_Ext;

                    if (!sFileNameSameFix.equals("")) {
                        if (IsFileExists(sRealUploadPath + s_FileName)) {
                            s_FileName = myUtil.replace(sFileNameSameFix, "{time}", s_RndTime);
                            if (s_FileName.indexOf("{sn}") >= 0) {
                                int i = 0;
                                while (true) {
                                    i++;
                                    String s = myUtil.replace(s_FileName, "{sn}", String.valueOf(i));
                                    s = myUtil.replace(s, "{name}", s_Pre);
                                    if (!IsFileExists(sRealUploadPath + s + "." + s_Ext)) {
                                        s_FileName = s;
                                        break;
                                    }
                                }
                            } else {
                                s_FileName = myUtil.replace(s_FileName, "{name}", s_Pre);
                            }
                            s_FileName = s_FileName + "." + s_Ext;
                        }
                    } else {
                        if (IsFileExists(sRealUploadPath + s_FileName)) {
                            DelFile(sRealUploadPath + s_FileName);
                        }
                        return s_FileName;
                    }
                }
            }
        }

        if (IsFileExists(sRealUploadPath + s_FileName)) {
            return GetRndFileName(s_Ext);
        } else {
            return s_FileName;
        }
    }

    private String GetOriginalFileNamePre(String s_FileName, String s_OkExt) {
        s_FileName = myUtil.replace(s_FileName, "%20", " ");
        s_FileName = s_FileName.trim();
        if (s_FileName.equals("")) {
            return "";
        }

        s_FileName = myUtil.replace(s_FileName, "/", "\\");
        int n = s_FileName.lastIndexOf("\\");
        if (n >= 0) {
            s_FileName = s_FileName.substring(n + 1);
        }

        n = s_FileName.lastIndexOf(".");
        if (n <= 0) {
            return "";
        }

        String s_Ext = s_FileName.substring(n + 1);
        if (!s_Ext.toLowerCase().equals(s_OkExt.toLowerCase())) {
            return "";
        }

        String s_Pre = s_FileName.substring(0, n);
        if (!IsFileNameFormat(s_Pre)) {
            s_Pre = "";
        }

        return s_Pre.trim();
    }


    private void OutError(String s_ErrCode) throws IOException {
        if (sAction.equals("SAVE")) {
            String s_FormItem = "", s_Script = "";
            s_FormItem = myUtil.GetHideInputHtml("d_errcode", s_ErrCode);
            s_Script = "parent.UploadError(document.getElementById('d_errcode').value);";
            OutScript("uploaderr", s_FormItem, s_Script);
        }
    }

    private void OutScript(String s_Action, String s_FormItem, String s_Script) throws IOException {
        String s_Form1 = "", s_Form2 = "";

        if (!sWSRootUrl.equals("")) {
            s_Form1 = "<form name=\"myform\" action=\"" + sWSRootUrl + "jsp/fs.jsp?act=" + s_Action + "\" method=\"post\" target=\"_self\">";
            s_Form2 = "</form>";
            s_Script = "document.myform.submit();";
        }

        m_response.setHeader("X-XSS-Protection", "0");
        m_out.print("<html><head><title>eWebEditor</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n" +
            "<script type=\"text/javascript\">\r\n" +
            "window.onload = _Onload; var bRun = false; function _Onload(){if(bRun){return;}; bRun = true; " + s_Script + "}\r\n" +
            "</script>\r\n" +
            "</head><body>\r\n" + s_Form1 + "\r\n" + s_FormItem + "\r\n" + s_Form2 + "\r\n</body></html>");
    }


    private boolean CheckValidExt(String sExt) {
        String[] aExt = myUtil.split(sAllowExt, "|");
        for (int i = 0; i < aExt.length; i++) {
            if (aExt[i].toLowerCase().equals(sExt.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void Mkdir(String path) {
        File dir = new File(path);
        if (dir == null) {
            return;
        }
        if (dir.isFile()) {
            return;
        }
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
        }
    }

    private String RelativePath2RootPath(String url) {
        String sTempUrl = url;

        if (sTempUrl.substring(0, 1).equals("/")) {
            return m_request.getContextPath() + sTempUrl;
        }

        String sWebEditorPath = m_request.getRequestURI();
        sWebEditorPath = sWebEditorPath.substring(0, sWebEditorPath.lastIndexOf("/"));
        sWebEditorPath = sWebEditorPath.substring(0, sWebEditorPath.lastIndexOf("/"));
        while (sTempUrl.startsWith("../")) {
            sTempUrl = sTempUrl.substring(3);
            sWebEditorPath = sWebEditorPath.substring(0, sWebEditorPath.lastIndexOf("/"));
        }
        return sWebEditorPath + "/" + sTempUrl;
    }

    private String RootPath2DomainPath(String url) {
        String s_ReqUrl = String.valueOf(m_request.getRequestURL());
        String s_Host = "";
        int n = s_ReqUrl.indexOf("://");
        if (n > 0) {
            s_Host = s_ReqUrl.substring(0, n + 3);
            s_ReqUrl = s_ReqUrl.substring(n + 3);
            n = s_ReqUrl.indexOf("/");
            if (n > 0) {
                s_ReqUrl = s_ReqUrl.substring(0, n);
            }
            s_Host = s_Host + s_ReqUrl;
        } else {
            s_Host = m_request.getScheme() + "://" + m_request.getServerName();
            String s_Port = String.valueOf(m_request.getServerPort());
            if (!s_Port.equals("80") && !s_Port.equals("443")) {
                s_Host = s_Host + ":" + s_Port;
            }
        }

        return s_Host + url;
    }


    private MongoFileVO SaveRemoteFile_ex8(String s_LocalFileName, String s_RemoteFileUrl) {
        MongoFileVO fileVO = null;
        InputStream is = null;
        try {
            int httpStatusCode;
            URL url = new URL(s_RemoteFileUrl);
            URLConnection conn = url.openConnection();
            // 添加响应头，模拟请求增加采集成功系数
            conn.setRequestProperty("Accept", "text/html");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("Accept-Encoding", "gzip");
            conn.setRequestProperty("Accept-Language", "en-US,en");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:50.0) Gecko/20100101 Firefox/50.0");
            conn.connect();
            HttpURLConnection httpconn = (HttpURLConnection) conn;
            httpStatusCode = httpconn.getResponseCode();
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            is = conn.getInputStream();
            if (null != is) {
                fileVO = doingUpload_lonsunEx8(s_LocalFileName, is);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileVO;
    }

    private boolean SaveRemoteFile(String s_LocalFileName, String s_RemoteFileUrl, String s_RealUploadPath) {
        try {
            int httpStatusCode;
            URL url = new URL(s_RemoteFileUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            HttpURLConnection httpconn = (HttpURLConnection) conn;
            httpStatusCode = httpconn.getResponseCode();
            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                //file://HttpURLConnection return an error code
                //System.out.println("Connect to "+s_RemoteFileUrl+" failed,return code:"+httpStatusCode);
                return false;
            }
            int filelen = conn.getContentLength();
            InputStream is = conn.getInputStream();
            byte[] tmpbuf = new byte[1024];
            File savefile = new File(s_RealUploadPath + s_LocalFileName);
            if (!savefile.exists())
                savefile.createNewFile();
            FileOutputStream fos = new FileOutputStream(savefile);
            int readnum = 0;
            if (filelen < 0) {
                while (readnum > -1) {
                    readnum = is.read(tmpbuf);
                    if (readnum > 0) {
                        fos.write(tmpbuf, 0, readnum);
                    }
                }
            } else {
                int readcount = 0;
                while (readcount < filelen && readnum != -1) {
                    readnum = is.read(tmpbuf);
                    if (readnum > 0) {
                        fos.write(tmpbuf, 0, readnum);
                        readcount = readcount + readnum;
                    }
                }
                if (readcount < filelen) {
                    System.out.println("download error");
                    is.close();
                    fos.close();
                    savefile.delete();
                    return false;
                }
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getSmallImageFile(String s_File) {
        return s_File.substring(0, s_File.lastIndexOf(".")) + "_s" + s_File.substring(s_File.lastIndexOf("."));
    }

    private boolean isValidSLTSYExt(String s_File) {
        String sExt = s_File.substring(s_File.lastIndexOf(".") + 1).toLowerCase();
        String[] aExt = myUtil.split(sSLTSYExt.toLowerCase(), "|");
        for (int i = 0; i < aExt.length; i++) {
            if (aExt[i].equals(sExt)) {
                return true;
            }
        }
        return false;
    }


    private boolean makeImageSY(String s_PathFile) throws IOException {
        if ((nSYWZFlag == 0) && (nSYTPFlag == 0)) {
            return false;
        }
        if (!isValidSLTSYExt(s_PathFile)) {
            return false;
        }

        //String readFormats[] = ImageIO.getReaderFormatNames();
        //System.out.println(Arrays.asList(readFormats));

        String s_Ext = s_PathFile.substring(s_PathFile.lastIndexOf(".") + 1).toLowerCase();

        File fi = new File(s_PathFile);
        if (!fi.exists()) {
            return false;
        }

        //Support for BMP images wasn't added to ImageIO until Java 5.
        BufferedImage theImg = ImageIO.read(fi);
        if (theImg == null) {
            return false;
        }

        int width = theImg.getWidth(null);
        int height = theImg.getHeight(null);
        int posX, posY;
        BufferedImage bimage;
        if (s_Ext.equals("png")) {
            bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        } else {
            bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g = bimage.createGraphics();
        g.drawImage(theImg, 0, 0, null);
        if (nSYWZFlag == 1) {
            if ((width < nSYWZMinWidth) || (height < nSYWZMinHeight)) {
                return false;
            }
            posX = getSYPosX(nSYWZPosition, width, nSYWZTextWidth + nSYShadowOffset, nSYWZPaddingH);
            posY = getSYPosY(nSYWZPosition, height, nSYWZTextHeight + nSYShadowOffset, nSYWZPaddingV);

            Font wordFont = new Font(sSYFontName, Font.PLAIN, nSYFontSize);
            g.setFont(wordFont);
            FontMetrics fm = g.getFontMetrics();
            int a = fm.getAscent();

            //g.setBackground(Color.white);
            g.setColor(new Color(Integer.parseInt(sSYShadowColor, 16)));
            g.drawString(sSYText, posX + nSYShadowOffset, posY + a + nSYShadowOffset);
            g.setColor(new Color(Integer.parseInt(sSYFontColor, 16)));
            g.drawString(sSYText, posX, posY + a);
        }
        if (nSYTPFlag == 1) {
            if ((width < nSYTPMinWidth) || (height < nSYTPMinHeight)) {
                return false;
            }
            posX = getSYPosX(nSYTPPosition, width, nSYTPImageWidth, nSYTPPaddingH);
            posY = getSYPosY(nSYTPPosition, height, nSYTPImageHeight, nSYTPPaddingV);

            ImageIcon waterIcon = new ImageIcon(sSYPicPath);
            Image waterImg = waterIcon.getImage();
            //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, nSYTPOpacity));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nSYTPOpacity));
            g.drawImage(waterImg, posX, posY, null);
        }
        g.dispose();

        if (!SaveImage(s_PathFile, bimage)) {
            return false;
        }

        return true;
    }

    private boolean SaveImage(String s_PathFile, BufferedImage bufImage) throws IOException {
        String s_Ext = s_PathFile.substring(s_PathFile.lastIndexOf(".") + 1).toLowerCase();

        try {
            if (s_Ext.equals("png")) {
                File fo = new File(s_PathFile);
                ImageIO.write(bufImage, s_Ext, fo);
            } else {
                ImageWriter iw = (ImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();

                ImageWriteParam iwp = iw.getDefaultWriteParam();
                iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwp.setCompressionQuality(1); // 0-1, float


                IIOImage iIamge = new IIOImage(bufImage, null, null);
                FileOutputStream fos = new FileOutputStream(s_PathFile);
                ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
                iw.setOutput(ios);

                iw.write(null, iIamge, iwp);

                ios.close();
                fos.close();
                iw.dispose();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }

        return true;
    }


    private int getSYPosX(int posFlag, int originalW, int syW, int paddingH) {
        switch (posFlag) {
            case 1:
            case 2:
            case 3:
                return paddingH;
            case 4:
            case 5:
            case 6:
                return (int) Math.floor((originalW - syW) / 2);
            case 7:
            case 8:
            case 9:
                return (originalW - paddingH - syW);
            default:
                return 0;
        }
    }

    private int getSYPosY(int posFlag, int originalH, int syH, int paddingV) {
        switch (posFlag) {
            case 1:
            case 4:
            case 7:
                return paddingV;
            case 2:
            case 5:
            case 8:
                return (int) Math.floor((originalH - syH) / 2);
            case 3:
            case 6:
            case 9:
                return (originalH - paddingV - syH);
            default:
                return 0;
        }
    }

    private boolean makeImageSLT(String s_Path, String s_File, String s_SmallFile) {
        if (nSLTFlag != 1) {
            return false;
        }
        if (!isValidSLTSYExt(s_Path + s_File)) {
            return false;
        }

        try {
            File fi = new File(s_Path + s_File);
            BufferedImage bis = ImageIO.read(fi);
            int w = bis.getWidth();
            int h = bis.getHeight();

            int nw = 0, nh = 0;

            switch (nSLTCheckFlag) {
                case 0:
                    if (w <= nSLTMinSize) {
                        return false;
                    }
                    nw = nSLTOkSize;
                    nh = (int) ((double) nSLTOkSize / (double) w * (double) h);
                    break;
                case 1:
                    if (h <= nSLTMinSize) {
                        return false;
                    }
                    nh = nSLTOkSize;
                    nw = (int) ((double) nSLTOkSize / (double) h * (double) w);
                    break;
                case 2:
                    if (w <= nSLTMinSize && h <= nSLTMinSize) {
                        return false;
                    }
                    if (w > h) {
                        nw = nSLTOkSize;
                        nh = (int) ((double) nSLTOkSize / (double) w * (double) h);
                    } else {
                        nh = nSLTOkSize;
                        nw = (int) ((double) nSLTOkSize / (double) h * (double) w);
                    }
                    break;
            }

            String s_Ext = s_File.substring(s_File.lastIndexOf(".") + 1).toLowerCase();

            BufferedImage bimage;
            if (s_Ext.equals("png")) {
                bimage = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
            } else {
                bimage = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D g = bimage.createGraphics();
            g.drawImage(bis.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            if (!SaveImage(s_Path + s_SmallFile, bimage)) {
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }


    private void DelFile(String s_MapFile) {
        File f = new File(s_MapFile);
        if (f.exists()) {
            f.delete();
        }
    }

    private void RenameFile(String s_File1, String s_File2) {
        File f1 = new File(s_File1);
        File f2 = new File(s_File2);
        boolean b = f1.renameTo(f2);
    }

    private boolean IsFileExists(String s_MapFile) {
        File f = new File(s_MapFile);
        if (f.exists()) {
            return true;
        }
        return false;
    }

    private String GetDomainFromUrl(String s_Url) {
        String s = s_Url.toLowerCase();
        int n = s.indexOf("://");
        if (n >= 0) {
            s = s.substring(n + 3);
        } else {
            return "";
        }
        n = s.indexOf("/");
        if (n >= 0) {
            s = s.substring(0, n - 1);
        }
        n = s.indexOf(":");
        if (n >= 0) {
            s = s.substring(0, n - 1);
        }
        return s;
    }

    private boolean CheckSpaceSize() throws IOException {
        if (sSpaceSize.equals("")) {
            return true;
        }

        String s_Dir = "";
        if (sSpacePath.equals("")) {
            s_Dir = sRealUploadPath;
        } else {
            s_Dir = sSpacePath;
        }

        if (GetFolderSize(new File(s_Dir)) >= (Long.parseLong(sSpaceSize) * 1024 * 1024)) {
            OutError("space");
            return false;
        }
        return true;
    }

    private long GetFolderSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += GetFolderSize(file);
            }
        }
        return dirSize;
    }


    private boolean IsFileNameFormat(String s_Name) {
        if (s_Name.startsWith(".")) {
            return false;
        }

        Pattern p = Pattern.compile("[\\/\\\\\\:\\*\\?\"\\<\\>\\|\\r\\n\\t]+");
        if (p.matcher(s_Name).matches()) {
            return false;
        }
        return true;
    }


}

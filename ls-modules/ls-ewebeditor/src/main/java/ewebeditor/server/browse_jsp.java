package ewebeditor.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class browse_jsp {

    protected HttpServletRequest m_request;
    protected HttpServletResponse m_response;
    private HttpSession m_session;
    protected ServletContext m_application;
    protected PageContext m_pagecontext;
    protected JspWriter m_out;

    private ewebeditor.server.util myUtil;

    private String sFileSeparator;
    private String sWSRootUrl;
    int nTreeIndex;

    public browse_jsp() {
        myUtil = new ewebeditor.server.util();
        sFileSeparator = System.getProperty("file.separator");
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
        String sConfig = myUtil.getConfigText();
        ;
        ArrayList aStyle = myUtil.getConfigArray("Style", sConfig);

        String sAllowExt, sUploadDir, sBaseUrl, sContentPath;
        String sCurrDir, sDir;
        int nAllowBrowse;
        String sPathShareImage, sPathShareFlash, sPathShareMedia, sPathShareOther;

        // param
        String sType = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("type")).toUpperCase());
        String sStyleName = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("style")));
        String sCusDir = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("cusdir")));
        String sAction = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("action")).toUpperCase());
        sWSRootUrl = myUtil.GetSafeUrl(myUtil.dealNull(m_request.getParameter("ws")));

        String s_SKey = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("skey")));

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
        if ((aStyleConfig[61].equals("2")) && (!s_SKey.equals(""))) {
            ss_FileSize = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_FileSize"));
            ss_FileBrowse = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_FileBrowse"));
            ss_SpaceSize = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_SpaceSize"));
            ss_SpacePath = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_SpacePath"));
            ss_PathMode = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathMode"));
            ss_PathUpload = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathUpload"));
            ss_PathCusDir = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathCusDir"));
            ss_PathCode = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathCode"));
            ss_PathView = (String) myUtil.dealNull(m_session.getAttribute("eWebEditor_" + s_SKey + "_PathView"));

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
        }


        sBaseUrl = aStyleConfig[19];
        nAllowBrowse = Integer.valueOf(aStyleConfig[43]).intValue();


        if (nAllowBrowse != 1) {
            OutError("notallow");
            return;
        }

        if (!sCusDir.equals("")) {
            sCusDir = myUtil.replace(sCusDir, "\\", "/");
            if ((sCusDir.startsWith("/")) || (sCusDir.startsWith(".")) || (sCusDir.endsWith(".")) || (sCusDir.indexOf("./") >= 0) || (sCusDir.indexOf("/.") >= 0) || (sCusDir.indexOf("//") >= 0) || (sCusDir.indexOf("..") >= 0)) {
                sCusDir = "";
            } else {
                if (!sCusDir.endsWith("/")) {
                    sCusDir = sCusDir + "/";
                }
            }
        }

        sUploadDir = aStyleConfig[3];
        if (!sBaseUrl.equals("3")) {
            sUploadDir = myUtil.getRealPathFromRelative(sUploadDir);
        }
        sUploadDir = GetSlashPath(sUploadDir);
        sUploadDir = sUploadDir + myUtil.replace(myUtil.replace(sCusDir, "/", sFileSeparator), "\\", sFileSeparator);

        if (sType.equals("FILE")) {
            sAllowExt = aStyleConfig[6];
        } else if (sType.equals("MEDIA")) {
            sAllowExt = aStyleConfig[9];
        } else if (sType.equals("FLASH")) {
            sAllowExt = aStyleConfig[7];
        } else {
            sAllowExt = aStyleConfig[8];
        }

        sPathShareImage = GetSlashPath(myUtil.getRealPathFromRelative("sharefile/image/"));
        sPathShareFlash = GetSlashPath(myUtil.getRealPathFromRelative("sharefile/flash/"));
        sPathShareMedia = GetSlashPath(myUtil.getRealPathFromRelative("sharefile/media/"));
        sPathShareOther = GetSlashPath(myUtil.getRealPathFromRelative("sharefile/other/"));


        String s_Out = "";
        if (sAction.equals("FILE")) {

            String s_ReturnFlag = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("returnflag")));
            String s_FolderType = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("foldertype")));
            String s_Dir = myUtil.GetSafeStr(myUtil.dealNull(m_request.getParameter("dir")));
            s_Dir = java.net.URLDecoder.decode(s_Dir, "UTF-" + "8");

            String s_CurrDir = "";
            if (s_FolderType.equals("upload")) {
                s_CurrDir = sUploadDir;
            } else if (s_FolderType.equals("shareimage")) {
                sAllowExt = "";
                s_CurrDir = sPathShareImage;
            } else if (s_FolderType.equals("shareflash")) {
                sAllowExt = "";
                s_CurrDir = sPathShareFlash;
            } else if (s_FolderType.equals("sharemedia")) {
                sAllowExt = "";
                s_CurrDir = sPathShareMedia;
            } else {
                s_FolderType = "shareother";
                sAllowExt = "";
                s_CurrDir = sPathShareOther;
            }

            s_Dir = myUtil.replace(s_Dir, "\\", "/");
            if ((s_Dir.startsWith("/")) || (s_Dir.startsWith(".")) || (s_Dir.endsWith(".")) || (s_Dir.indexOf("./") >= 0) || (s_Dir.indexOf("/.") >= 0) || (s_Dir.indexOf("//") >= 0) || (s_Dir.indexOf("..") >= 0)) {
                s_Dir = "";
            }

            String s_Dir2 = myUtil.replace(s_Dir, "/", sFileSeparator);
            s_Dir2 = myUtil.replace(s_Dir2, "\\", sFileSeparator);

            if (!s_Dir.equals("")) {
                if (CheckValidDir(s_CurrDir + s_Dir2)) {
                    s_CurrDir += s_Dir2;
                } else {
                    s_Dir = "";
                }
            }

            if (CheckValidDir(s_CurrDir)) {
                File file = new File(s_CurrDir);
                File[] filelist = file.listFiles();
                if (filelist != null && filelist.length > 0) {
                    int n = -1;
                    for (int i = 0; i < filelist.length; i++) {
                        if (filelist[i].isFile()) {
                            String s_FileName = filelist[i].getName();
                            String s_FileExt = s_FileName.substring(s_FileName.lastIndexOf(".") + 1);
                            s_FileExt = s_FileExt.toLowerCase();
                            if (CheckValidExt(sAllowExt, s_FileExt)) {
                                n++;
                                if (n > 0) {
                                    s_Out = s_Out + "||";
                                }
                                s_Out = s_Out + s_FileName + "|" + String.valueOf(convertFileSize(filelist[i].length())) + "|" + formatDate(new Date(filelist[i].lastModified()));
                            }
                        }
                    }
                }
            }
            if (s_Out.equals("")) {
                s_Out = "0";
            }

            s_Out = s_ReturnFlag + "|||" + s_FolderType + "|||" + s_Dir + "|||" + s_Out;
            OutData("browsefile", s_Out);

        } else {
            String s_ArrUpload = "", s_ArrShareImage = "", s_ArrShareFlash = "", s_ArrShareMedia = "", s_ArrShareOther = "";

            nTreeIndex = 0;
            s_ArrUpload = GetFolderTree(sUploadDir, "Upload", 1);

            sAllowExt = "";
            if (sType.equals("FILE")) {
                nTreeIndex = 0;
                s_ArrShareImage = GetFolderTree(sPathShareImage, "ShareImage", 1);
                nTreeIndex = 0;
                s_ArrShareFlash = GetFolderTree(sPathShareFlash, "ShareFlash", 1);
                nTreeIndex = 0;
                s_ArrShareMedia = GetFolderTree(sPathShareMedia, "ShareMedia", 1);
                nTreeIndex = 0;
                s_ArrShareOther = GetFolderTree(sPathShareOther, "ShareOther", 1);
            } else if (sType.equals("MEDIA")) {
                nTreeIndex = 0;
                s_ArrShareImage = "0";
                s_ArrShareFlash = "0";
                s_ArrShareMedia = GetFolderTree(sPathShareMedia, "ShareMedia", 1);
                s_ArrShareOther = "0";
            } else if (sType.equals("FLASH")) {
                nTreeIndex = 0;
                s_ArrShareImage = "0";
                s_ArrShareFlash = GetFolderTree(sPathShareFlash, "ShareFlash", 1);
                s_ArrShareMedia = "0";
                s_ArrShareOther = "0";
            } else {
                nTreeIndex = 0;
                s_ArrShareImage = GetFolderTree(sPathShareImage, "ShareImage", 1);
                s_ArrShareFlash = "0";
                s_ArrShareMedia = "0";
                s_ArrShareOther = "0";
            }

            s_Out = s_ArrUpload + "|||" + s_ArrShareImage + "|||" + s_ArrShareFlash + "|||" + s_ArrShareMedia + "|||" + s_ArrShareOther;
            OutData("browsefolder", s_Out);
        }
    }


    private String GetSlashPath(String str) {
        if (!str.endsWith(sFileSeparator)) {
            return str + sFileSeparator;
        }
        return str;
    }


    private void OutError(String s_ErrCode) throws IOException {
        OutData("browseerr", s_ErrCode);
    }

    private void OutData(String s_Action, String s_Data) throws IOException {
        String s_FormItem = "", s_Script = "";
        s_FormItem = myUtil.GetHideInputHtml("d_data", s_Data);
        s_Script = "var d=document.getElementById(\"d_data\").value; ";

        if (s_Action.equals("browsefile")) {
            s_Script = s_Script + "parent.setFileList(d);";
        } else if (s_Action.equals("browsefolder")) {
            s_Script = s_Script + "parent.setFolderList(d);";
        } else if (s_Action.equals("browseerr")) {
            s_Script = s_Script + "parent.ServerError(d);";
        }

        OutScript(s_Action, s_FormItem, s_Script);
    }

    private void OutScript(String s_Action, String s_FormItem, String s_Script) throws IOException {
        String s_Form1 = "", s_Form2 = "";

        if (!sWSRootUrl.equals("")) {
            s_Form1 = "<form name=\"myform\" action=\"" + sWSRootUrl + "jsp/fs.jsp?act=" + s_Action + "\" method=\"post\" target=\"_self\">";
            s_Form2 = "</form>";
            s_Script = "document.myform.submit();";
        }

        m_out.print("<html><head><title>eWebEditor</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n" +
            "<script type=\"text/javascript\">\r\n" +
            "window.onload = _Onload; var bRun = false; function _Onload(){if(bRun){return;}; bRun = true; " + s_Script + "}\r\n" +
            "</script>\r\n" +
            "</head><body>\r\n" + s_Form1 + "\r\n" + s_FormItem + "\r\n" + s_Form2 + "\r\n</body></html>");
    }


    private boolean CheckValidExt(String s_AllowExt, String sExt) {
        if (s_AllowExt.equals("")) {
            return true;
        }
        String[] aExt = myUtil.split(s_AllowExt, "|");
        for (int i = 0; i < aExt.length; i++) {
            if (aExt[i].toLowerCase().equals(sExt)) {
                return true;
            }
        }
        return false;
    }


    private String GetFolderTree(String s_Dir, String s_Flag, int n_Indent) {
        String s_List = "";
        ArrayList aSubFolders = new ArrayList();

        File file = new File(s_Dir);
        File[] filelist = file.listFiles();

        if (filelist != null && filelist.length > 0) {
            for (int i = 0; i < filelist.length; i++) {
                if (filelist[i].isDirectory()) {
                    aSubFolders.add(filelist[i].getName());
                }
            }

            int n_Count = aSubFolders.size();
            if (n_Count > 1) {
                Collections.sort(aSubFolders);
            }
            String s_LastFlag = "";
            String s_Folder = "";
            for (int i = 1; i <= n_Count; i++) {
                if (i < n_Count) {
                    s_LastFlag = "0";
                } else {
                    s_LastFlag = "1";
                }

                s_Folder = aSubFolders.get(i - 1).toString();
                if (nTreeIndex > 0) {
                    s_List = s_List + "||";
                }
                s_List = s_List + s_Folder + "|" + String.valueOf(n_Indent) + "|" + s_LastFlag;
                nTreeIndex = nTreeIndex + 1;
                s_List = s_List + GetFolderTree(s_Dir + s_Folder + sFileSeparator, s_Flag, n_Indent + 1);
            }
        }
        if (s_List.equals("")) {
            s_List = "0";
        }

        return s_List;
    }

    private boolean CheckValidDir(String path) {
        java.io.File dir = new java.io.File(path);
        if (dir == null) {
            return false;
        }
        if (dir.isFile()) {
            return false;
        }
        if (!dir.exists()) {
            return false;
        }
        return true;
    }


    private String convertFileSize(long size) {
        int divisor = 1024;
        String unit = "K";
        if (divisor == 1) return size / divisor + " " + unit;
        String aftercomma = "" + 100 * (size % divisor) / divisor;
        if (aftercomma.length() == 1) aftercomma = "0" + aftercomma;
        return size / divisor + "." + aftercomma + " " + unit;
    }

    private String formatDate(Date myDate) {
        String strFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(strFormat);
        String strDate = formatter.format(myDate);
        return strDate;
    }


}

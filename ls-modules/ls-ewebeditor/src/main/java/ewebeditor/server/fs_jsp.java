package ewebeditor.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class fs_jsp {

    protected HttpServletRequest m_request;
    protected HttpServletResponse m_response;
    private HttpSession m_session;
    protected ServletContext m_application;
    protected PageContext m_pagecontext;
    protected JspWriter m_out;

    private ewebeditor.server.util myUtil;


    public fs_jsp() {
        myUtil = new ewebeditor.server.util();
    }


    public final void Load(PageContext pagecontext) throws ServletException, IOException {
        m_pagecontext = pagecontext;
        m_application = pagecontext.getServletContext();
        m_request = (HttpServletRequest) pagecontext.getRequest();
        m_response = (HttpServletResponse) pagecontext.getResponse();
        m_session = (HttpSession) pagecontext.getSession();
        m_out = pagecontext.getOut();
        myUtil.InitServer(pagecontext);

        String sAction = myUtil.dealNull(m_request.getParameter("act"));

        String s_ErrCode = "", s_Ori = "", s_Save = "", s_Thumb = "", s_Content = "", s_Data = "";
        String s_FormItem = "", s_Script = "";
        if (sAction.equals("uploadsave")) {
            s_Ori = myUtil.dealNull(m_request.getParameter("d_ori"));
            s_Save = myUtil.dealNull(m_request.getParameter("d_save"));
            s_Thumb = myUtil.dealNull(m_request.getParameter("d_thumb"));
            s_FormItem = myUtil.GetHideInputHtml("d_ori", s_Ori) + myUtil.GetHideInputHtml("d_save", s_Save) + myUtil.GetHideInputHtml("d_thumb", s_Thumb);
            s_Script = "parent.UploadSaved(document.getElementById('d_ori').value, document.getElementById('d_save').value, document.getElementById('d_thumb').value);";

        } else if (sAction.equals("uploadremote")) {
            s_Content = myUtil.dealNull(m_request.getParameter("d_content"));
            s_Ori = myUtil.dealNull(m_request.getParameter("d_ori"));
            s_Save = myUtil.dealNull(m_request.getParameter("d_save"));
            s_FormItem = myUtil.GetHideInputHtml("d_content", s_Content) + myUtil.GetHideInputHtml("d_ori", s_Ori) + myUtil.GetHideInputHtml("d_save", s_Save);
            s_Script = "parent.setHTML(document.getElementById('d_content').value); " +
                "try{parent.addUploadFiles(document.getElementById('d_ori').value, document.getElementById('d_save').value);} catch(e){} " +
                "parent.remoteUploadOK();";

        } else if (sAction.equals("uploaderr")) {
            s_ErrCode = myUtil.dealNull(m_request.getParameter("d_errcode"));
            s_FormItem = myUtil.GetHideInputHtml("d_errcode", s_ErrCode);
            s_Script = "parent.UploadError(document.getElementById('d_errcode').value);";

        } else {
            s_Data = myUtil.dealNull(m_request.getParameter("d_data"));
            s_FormItem = myUtil.GetHideInputHtml("d_data", s_Data);
            s_Script = "var d=document.getElementById(\"d_data\").value; ";

            if (sAction.equals("browsefile")) {
                s_Script = s_Script + "parent.setFileList(d);";
            } else if (sAction.equals("browsefolder")) {
                s_Script = s_Script + "parent.setFolderList(d);";
            } else if (sAction.equals("browseerr")) {
                s_Script = s_Script + "parent.ServerError(d);";
            }
        }

        OutScript(s_FormItem, s_Script);
    }


    private void OutScript(String s_FormItem, String s_Script) throws IOException {
        m_response.setHeader("X-XSS-Protection", "0");
        m_out.print("<html><head><title>eWebEditor</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n" +
            "<script type=\"text/javascript\">\r\n" +
            "window.onload = _Onload; var bRun = false; function _Onload(){if(bRun){return;}; bRun = true; " + s_Script + "}\r\n" +
            "</script>\r\n" +
            "</head><body>\r\n" + s_FormItem + "\r\n</body></html>");
    }


}

package ewebeditor.admin;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;


public class default_jsp {

    private HttpServletRequest m_request;
    private HttpServletResponse m_response;
    private HttpSession m_session;
    private ServletContext m_application;
    private PageContext m_pagecontext;
    private JspWriter m_out;

    private ewebeditor.admin.util myUtil;


    public default_jsp() {
        myUtil = new ewebeditor.admin.util();
    }


    public final void Load(PageContext pagecontext) throws ServletException, IOException {
        m_pagecontext = pagecontext;
        m_application = pagecontext.getServletContext();
        m_request = (HttpServletRequest) pagecontext.getRequest();
        m_response = (HttpServletResponse) pagecontext.getResponse();
        m_session = (HttpSession) pagecontext.getSession();
        m_out = pagecontext.getOut();


        if (!myUtil.InitAdmin(pagecontext, true)) {
            return;
        }


    }


}

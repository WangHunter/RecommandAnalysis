package com.wonder.servlet;

import com.wonder.service.GetRecommandResultService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GetItemIDServlet extends HttpServlet {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(GetItemIDServlet.class
            .getName());
    @Autowired
    private GetRecommandResultService getRecommandResultService;
    private static final long serialVersionUID = 1L;


    @Override
    public void init() throws ServletException {
        super.init();
        WebApplicationContext context = WebApplicationContextUtils
                .getRequiredWebApplicationContext(getServletContext());
        getRecommandResultService = (GetRecommandResultService) context
                .getBean("getRecommandResultService");
    }


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        new ArrayList<String>();
        String getUserid = request.getParameter("userid");
        log.info("根据用户id来获取推荐标签,用户id为"+getUserid);
        try {
            String getItemID = getRecommandResultService.getItemByUserID(getUserid);
            PrintWriter out = response.getWriter();
            out.println(getItemID);
            out.flush();
            out.close();
        } catch (JSONException e) {
            log.error(e.getStackTrace());
        }

    }
}

package com.taobao.pamirs.schedule;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

public class WebInitial extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            ConsoleManager.initial();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

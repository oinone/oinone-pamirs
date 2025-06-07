<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="java.io.StringWriter" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (ConsoleManager.isInitial() == false) {
        response.sendRedirect("config.jsp");
    }
%>
<html>
<body style="font-size:12px;">
<%
    String path = request.getParameter("path");
    if (path == null) {
        path = ConsoleManager.getScheduleStrategyManager().getRootPath();
    }
    StringWriter writer = new StringWriter();
    ConsoleManager.getScheduleStrategyManager().printTree(
            path, writer, "<br/>");
%>
<pre>
<%=writer.getBuffer().toString()%>
</pre>
</body>
</html>

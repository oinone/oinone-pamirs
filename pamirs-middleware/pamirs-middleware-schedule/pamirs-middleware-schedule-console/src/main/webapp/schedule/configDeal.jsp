<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.zk.ZKManager" %>
<%@page import="java.util.Properties" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%

    Properties p = new Properties();
    p.setProperty(ZKManager.keys.zkConnectString.toString(),
            request.getParameter(ZKManager.keys.zkConnectString.toString()));
    p.setProperty(ZKManager.keys.rootPath.toString(), request.getParameter(ZKManager.keys.rootPath.toString()));
    p.setProperty(ZKManager.keys.userName.toString(), request.getParameter(ZKManager.keys.userName.toString()));
    p.setProperty(ZKManager.keys.password.toString(), request.getParameter(ZKManager.keys.password.toString()));
    p.setProperty(ZKManager.keys.zkSessionTimeout.toString(),
            request.getParameter(ZKManager.keys.zkSessionTimeout.toString()));
    try {
        ConsoleManager.saveConfigInfo(p);
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("config.jsp?error=" + e.getMessage());
    }
%>
<script>
  if (parent != null) {
    parent.location.href = "index.jsp?manager=true";
  } else {
    window.location.href = "index.jsp?manager=true";
  }
</script>

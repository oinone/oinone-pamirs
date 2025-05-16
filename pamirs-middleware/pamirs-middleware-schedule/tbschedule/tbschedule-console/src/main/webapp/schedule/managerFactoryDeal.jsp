<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>
        创建调度任务
    </title>
</head>
<body bgcolor="#ffffff">

<%
    boolean isRefreshParent = false;
    String result = "";
    String action = request.getParameter("action");
    String uuid = request.getParameter("uuid");
    try {
        if (action.equalsIgnoreCase("startManagerFactory")) {
            ConsoleManager.getScheduleStrategyManager().updateManagerFactoryInfo(uuid, true);
            isRefreshParent = true;
        } else if (action.equalsIgnoreCase("stopManagerFactory")) {
            ConsoleManager.getScheduleStrategyManager().updateManagerFactoryInfo(uuid, false);
            isRefreshParent = true;
        } else {
            throw new Exception("不支持的操作：" + action);
        }
    } catch (Throwable e) {
        e.printStackTrace();
        result = "ERROR:" + e.getMessage();
        isRefreshParent = false;
    }
%>
<%=result%>
</body>
</html>
<% if (isRefreshParent == true) { %>
<script>
  parent.location.reload();
</script>
<%}%>
<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    try {
        ConsoleManager.initial();
        ConsoleManager.getScheduleDataManager();
        String isManager = request.getParameter("manager");
%>
<html>
<head>
    <title>
        Schedule调度管理
    </title>
    <STYLE type=text/css>

        TH {
            height: 20px;
            color: #5371BA;
            font-weight: bold;
            font-size: 12px;
            text-align: center;
            border: #8CB2E3 solid;
            border-width: 0 1 1 0;
            background-color: #E4EFF1;
            display: block;
        }

        TD {
            background-color: ;
            border: #8CB2E3 solid;
            border-width: 0 1 1 0;
            font-size: 12px;
        }

    </STYLE>

</head>
<body style="font-size:12px;">
<h1 align="center">TaobaoSchedule调度管理控制台</h1>
<a id="strategyList" onclick="linkOnclick(this);" target="content"
   href="scheduleStrategyList.jsp?manager=<%=isManager%>" style="color:#0000CD">调度策略</a>
<a id="taskTypeList" onclick="linkOnclick(this);" target="content" href="taskTypeList.jsp?manager=<%=isManager%>"
   style="color:#0000CD">任务管理</a>
<a id="managerFactoryList" onclick="linkOnclick(this);" target="content"
   href="managerFactoryList.jsp?manager=<%=isManager%>" style="color:#0000CD">机器管理</a>
<a id="serverList" onclick="linkOnclick(this);" target="content" href="serverList.jsp" style="color:#0000CD">处理线程组列表</a>

<%
    if ("true".equals(isManager)) {
%>
<a id="config" onclick="linkOnclick(this);" target="content" href="config.jsp" style="color:#0000CD">Zookeeper连接配置</a>
<a id="zookeeperData" onclick="linkOnclick(this);" target="content" href="zookeeperData.jsp" style="color:#0000CD">Zookeeper数据</a>
<a id="zookeeperDataExport" onclick="linkOnclick(this);" target="content" href="exportConfig.jsp" style="color:#0000CD">Export配置数据</a>
<a id="zookeeperDataImport" onclick="linkOnclick(this);" target="content" href="importConfig.jsp" style="color:#0000CD">Import配置数据</a>
<%
    }
%>
&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp请参考：<a
        href="https://github.com/nmyphp/tbschedule">Github开源项目</a></font>
<iframe name="content" FRAMEBORDER="0" height="85%" width="100%"
        src="scheduleStrategyList.jsp?manager=<%=isManager%>"></iframe>
</body>
</html>

<script>
  function linkOnclick(obj) {
    taskTypeList.style.backgroundColor = "";
    strategyList.style.backgroundColor = "";
    managerFactoryList.style.backgroundColor = "";
    serverList.style.backgroundColor = "";
    config.style.backgroundColor = "";
    zookeeperData.style.backgroundColor = "";
    zookeeperDataExport.style.backgroundColor = "";
    zookeeperDataImport.style.backgroundColor = "";

    obj.style.backgroundColor = "#FF0000";
  }

</script>
<%
    } catch (Exception e) {
        response.sendRedirect("config.jsp?error=" + e.getMessage());
    }
%>
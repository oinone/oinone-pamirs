<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.strategy.ManagerFactoryInfo" %>
<%@page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String isManager = request.getParameter("manager");
%>
<html>
<head>
    <title>
        调度策略管理
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
            white-space: nowrap;
            overflow: hidden;
        }

        TD {
            background-color: ;
            border: #8CB2E3 1px solid;
            border-width: 0 1 1 0;
            font-size: 12px;
        }

        table {
            border-collapse: collapse
        }
    </STYLE>

</head>
<body style="font-size:12px;">

<table id="contentTable" border="1">
    <tr>
        <th width="50">序号</th>
        <%if ("true".equals(isManager)) {%>
        <th width="100">管理</th>
        <%}%>
        <th>任务处理机</th>
        <th width="50">状态</th>
    </tr>
    <%
        List<ManagerFactoryInfo> list = ConsoleManager.getScheduleStrategyManager().loadAllManagerFactoryInfo();
        String sts = "";
        String action;
        String actionName;
        for (int i = 0; i < list.size(); i++) {
            ManagerFactoryInfo info = list.get(i);
            if (info.isStart() == true) {
                sts = "运行";
                action = "stopManagerFactory";
                actionName = "停止";
            } else {
                sts = "休眠";
                action = "startManagerFactory";
                actionName = "启动";
            }
    %>
    <tr onclick="openDetail(this,'<%=info.getUuid()%>')">
        <td align="center"><%=(i + 1)%>
        </td>
        <%if ("true".equals(isManager)) {%>
        <td align="center">
            <a target="scheduleStrategyRuntime"
               href="managerFactoryDeal.jsp?action=<%=action%>&uuid=<%=info.getUuid()%>"
               style="color:#0000CD"><%=actionName%>
            </a>
        </td>
        <%}%>
        <td><%=info.getUuid()%>
        </td>
        <td><%=sts%>
        </td>
    </tr>
    <%
        }
    %>
</table>
<br/>
此调度器上的任务分配情况：
<iframe name="scheduleStrategyRuntime" height="150" width="100%"></iframe>
此调度器上的服务情况
<iframe name="servlerList" height="230" width="100%"></iframe>
</body>
</html>
<script>

  var oldSelectRow = null;

  function openDetail(obj, uuid) {
    if (oldSelectRow != null) {
      oldSelectRow.bgColor = "";
    }
    obj.bgColor = "#FFD700";
    oldSelectRow = obj;
    document.all("servlerList").src = "serverList.jsp?managerFactoryUUID=" + uuid;
    document.all("scheduleStrategyRuntime").src = "scheduleStrategyRuntime.jsp?uuid=" + uuid;
  }

  if (contentTable.rows.length > 1) {
    contentTable.rows[1].click();
  }

</script>
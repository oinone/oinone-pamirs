<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.strategy.ScheduleStrategyRunntime" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html>
<head>
    <title>
        调度动态分配情况
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
    <%
        String strategyName = request.getParameter("strategyName");
        String uuid = request.getParameter("uuid");

    %>
</head>
<body style="font-size:12px;">
<table border="1">
    <tr>
        <th>序号</th>
        <th>任务类型</th>
        <th>处理机器</th>
        <th>线程组数量</th>
        <th>错误信息</th>
    </tr>
    <%
        List<ScheduleStrategyRunntime> runntimeList = null;
        if (strategyName != null && strategyName.trim().length() > 0) {
            runntimeList = ConsoleManager.getScheduleStrategyManager()
                    .loadAllScheduleStrategyRunntimeByTaskType(strategyName);
        } else if (uuid != null && uuid.trim().length() > 0) {
            runntimeList = ConsoleManager.getScheduleStrategyManager().loadAllScheduleStrategyRunntimeByUUID(uuid);
        } else {
            runntimeList = new ArrayList<ScheduleStrategyRunntime>();
        }

        for (int i = 0; i < runntimeList.size(); i++) {
            ScheduleStrategyRunntime run = runntimeList.get(i);
    %>
    <tr>
        <td><%=(i + 1)%>
        </td>
        <td><%=run.getStrategyName()%>
        </td>
        <td align="center"><%=run.getUuid()%>
        </td>
        <td align="center"><%=run.getRequestNum()%>
        </td>
        <td align="center" style=""><p style="color:red"><%=run.getMessage()%>
        </p></td>
    </tr>
    <%
        }
    %>
</table>
</body>
</html>
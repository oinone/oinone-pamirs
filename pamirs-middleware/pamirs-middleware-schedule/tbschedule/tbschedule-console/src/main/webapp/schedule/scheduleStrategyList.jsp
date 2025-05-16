<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.strategy.ScheduleStrategy" %>
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
        <th>序号</th>
        <%if ("true".equals(isManager)) {%>
        <th>管理</th>
        <%}%>
        <th>策略名称</th>
        <th>任务状态</th>
        <th>任务类型</th>
        <th>任务名称</th>
        <th>任务参数</th>
        <th>单JVM最大线程组数量</th>
        <th>最大线程组数量</th>
        <th>IP地址(逗号分隔)</th>
    </tr>
    <%
        List<ScheduleStrategy> scheduleStrategyList = ConsoleManager.getScheduleStrategyManager()
                .loadAllScheduleStrategy();
        String ipIds = "";
        for (int i = 0; i < scheduleStrategyList.size(); i++) {
            ScheduleStrategy scheduleStrategy = scheduleStrategyList.get(i);
            String[] ipList = scheduleStrategy.getIPList();
            ipIds = "";
            for (int j = 0; ipList != null && j < ipList.length; j++) {
                if (j > 0) {
                    ipIds = ipIds + ",";
                }
                ipIds = ipIds + ipList[j];
            }

            String pauseOrResumeAction = "pauseTaskType";
            String pauseOrResumeActionName = "停止";
            String stsName = "正常";
            if (ScheduleStrategy.STS_PAUSE.equals(scheduleStrategyList.get(i).getSts())) {
                pauseOrResumeAction = "resumeTaskType";
                pauseOrResumeActionName = "恢复";
                stsName = "停止";
            }


    %>
    <tr onclick="openDetail(this,'<%=scheduleStrategy.getStrategyName()%>')">
        <td><%=(i + 1)%>
        </td>
        <%if ("true".equals(isManager)) {%>
        <td width="100" align="center">
            <a target="strategyDetail" href="scheduleStrategyEdit.jsp?taskType=<%=scheduleStrategy.getStrategyName()%>"
               style="color:#0000CD">编辑</a>
            <a target="strategyDetail" href="javascript:void(0)"
               onclick="validateDel('<%=scheduleStrategy.getStrategyName()%>')">删除</a>
            <a target="strategyDetail"
               href="scheduleStrategyDeal.jsp?action=<%=pauseOrResumeAction%>&strategyName=<%=scheduleStrategy.getStrategyName()%>"
               style="color:#0000CD"><%=pauseOrResumeActionName%>
            </a>
        </td>
        <%}%>
        <td><%=scheduleStrategy.getStrategyName()%>
        </td>
        <td><%=stsName%>
        </td>
        <td><%=scheduleStrategy.getKind()%>
        </td>
        <td><%=scheduleStrategy.getTaskName()%>
        </td>
        <td><%=scheduleStrategy.getTaskParameter()%>
        </td>

        <td align="center"><%=scheduleStrategy.getNumOfSingleServer()%>
        </td>
        <td align="center"><%=scheduleStrategy.getAssignNum()%>
        </td>
        <td><%=ipIds%>
        </td>
    </tr>
    <%
        }
    %>
</table>
<br/>
<%if ("true".equals(isManager)) {%>
<a target="strategyDetail" href="scheduleStrategyEdit.jsp?taskType=-1" style="color:#0000CD">创建新策略...</a>
<%}%>
任务在各个机器上的分配情况：
<iframe id="showStrategyDetail" name="strategyDetail" height="80%" width="100%"></iframe>
</body>
</html>
<script>

  var oldSelectRow = null;

  function openDetail(obj, strategyName) {
    if (oldSelectRow != null) {
      oldSelectRow.bgColor = "";
    }
    obj.bgColor = "#FFD700";
    oldSelectRow = obj;
    document.getElementById("showStrategyDetail").src = "scheduleStrategyRuntime.jsp?strategyName=" + strategyName;
  }

  if (contentTable.rows.length > 1) {
    contentTable.rows[1].click();
  }

  function validateDel(str) {
    var flag = window.confirm("确认删除策略" + str + "?");
    if (flag) {
      window.location.href = "scheduleStrategyDeal.jsp?action=deleteScheduleStrategy&strategyName=" + str;
    }
  }
</script>
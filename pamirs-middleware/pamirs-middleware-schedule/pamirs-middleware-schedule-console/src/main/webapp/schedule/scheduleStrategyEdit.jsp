<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.strategy.ScheduleStrategy" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String isManager = request.getParameter("manager");
    String taskTypeName = request.getParameter("taskType");
    ScheduleStrategy scheduleStrategy = ConsoleManager.getScheduleStrategyManager().loadStrategy(taskTypeName);
    boolean isNew = false;
    String actionName = "editScheduleStrategy";
    String editSts = "";
    String ips = "";
    if (scheduleStrategy != null) {
        String[] ipList = scheduleStrategy.getIPList();
        for (int i = 0; ipList != null && i < ipList.length; i++) {
            if (i > 0) {
                ips = ips + ",";
            }
            ips = ips + ipList[i];
        }
        editSts = "style=\"background-color: blue\" readonly=\"readonly\"";
    } else {
        scheduleStrategy = new ScheduleStrategy();
        scheduleStrategy.setStrategyName("");
        scheduleStrategy.setKind(ScheduleStrategy.Kind.Schedule);
        scheduleStrategy.setTaskName("");
        scheduleStrategy.setTaskParameter("");
        scheduleStrategy.setNumOfSingleServer(0);
        scheduleStrategy.setAssignNum(2);
        ips = "127.0.0.1";

        isNew = true;
        actionName = "createScheduleStrategy";
    }

%>
<html>
<head>
    <STYLE type=text/css>

        TH {
            color: #5371BA;
            font-weight: bold;
            font-size: 12px;
            background-color: #E4EFF1;
            display: block;
        }

        TD {
            font-size: 12px;
        }

    </STYLE>
</head>
<body>
<form id="scheduleStrategyForm" method="get" name="scheduleStrategyForm" action="scheduleStrategyDeal.jsp">
    <input type="hidden" name="action" value="<%=actionName%>"/>
    <table>
        <tr>
            <td>策略名称:</td>
            <td><input type="text" id="strategyName" name="strategyName"  <%=editSts%>
                       value="<%=scheduleStrategy.getStrategyName()%>" width="30"></td>
            <td>必须填写，不能有中文和特殊字符</td>
        </tr>
        <tr>
            <td>任务类型:</td>
            <td><input type="text" id="kind" name="kind" value="<%=scheduleStrategy.getKind().toString()%>" width="30">
            </td>
            <td>可选类型：Schedule,Java,Bean 大小写敏感</td>
        </tr>
        <tr>
            <td>任务名称:</td>
            <td><input type="text" id="taskName" name="taskName" value="<%=scheduleStrategy.getTaskName()%>" width="30">
            </td>
            <td>与任务类型匹配的名称例如：1、任务管理中配置的任务名称(对应Schedule) 2、Class名称(对应java) 3、Bean的名称(对应Bean)</td>
        </tr>
        <tr>
            <td>任务参数:</td>
            <td><input type="text" id="taskParameter" name="taskParameter"
                       value="<%=scheduleStrategy.getTaskParameter()%>" width="30"></td>
            <td>逗号分隔的Key-Value。 对任务类型为Schedule的无效，需要通过任务管理来配置的</td>
        </tr>

        <tr>
            <td>单JVM最大线程组数量:</td>
            <td><input type="text" name="numOfSingleServer" value="<%=scheduleStrategy.getNumOfSingleServer() %>"
                       width="30"></td>
            <td>单JVM最大线程组数量，如果是0，则表示没有限制.每台机器运行的线程组数量 =总量/机器数</td>
        </tr>
        <tr>
            <td>最大线程组数量：</td>
            <td><input type="text" name="assignNum" value="<%=scheduleStrategy.getAssignNum()%>" width="30"></td>
            <td>所有服务器总共运行的最大数量</td>
        </tr>
        <tr>
            <td>IP地址(逗号分隔)：</td>
            <td><input type="text" name="ips" value="<%=ips%>" width="30"></td>
            <td>127.0.0.1或者localhost会在所有机器上运行</td>
        </tr>
    </table>
    <br/>
    <input type="button" value="保存" onclick="save();" style="width:100px">

</form>

</body>
</html>

<script>
  function save() {
    var strategyName = document.all("strategyName").value;
    var reg = /.*[\u4e00-\u9fa5]+.*$/;
    if (reg.test(strategyName)) {
      alert('任务类型不能含中文');
      return;
    }
    if (strategyName == null || strategyName == '' || isContainSpace(strategyName)) {
      alert('任务类型不能为空或存在空格');
      return;
    }
    document.getElementById("scheduleStrategyForm").submit();
  }

  function isContainSpace(array) {
    if (array.indexOf(' ') >= 0) {
      return true;
    }
    return false;
  }
</script>
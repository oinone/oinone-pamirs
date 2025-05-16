<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.taskmanager.ScheduleServer" %>
<%@page import="com.taobao.pamirs.schedule.taskmanager.ScheduleTaskItem" %>
<%@page import="com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType" %>
<%@page import="com.taobao.pamirs.schedule.taskmanager.ScheduleTaskTypeRunningInfo" %>
<%@page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>
        调度任务详细信息
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

<%
    String baseTaskType = request.getParameter("baseTaskType");
    String ownSign = request.getParameter("ownSign");
    List<ScheduleTaskTypeRunningInfo> taskTypeRunningInfoList = ConsoleManager.getScheduleDataManager()
            .getAllTaskTypeRunningInfo(baseTaskType);
    if (taskTypeRunningInfoList.size() == 0) {
%>
任务 <%=baseTaskType%>：还没有运行期信息
<%
} else {
%>
<table border="1">
    <%
        for (int i = 0; i < taskTypeRunningInfoList.size(); i++) {
            if (ownSign != null && taskTypeRunningInfoList.get(i).getOwnSign().equals(ownSign) == false) {
                continue;
            }
    %>
    <tr style="background-color:#F3F5F8;color:#013299;">
        <td style="font-size:14px;font-weight:bold">
            <%=taskTypeRunningInfoList.get(i).getTaskType()%> -- <%=taskTypeRunningInfoList.get(i).getOwnSign()%>
        </td>
    </tr>
    <tr>
        <td>
            <table border="1" style="border-COLLAPSE: collapse;display:block;">
                <tr>
                    <th nowrap>序号</th>
                    <th>线程组编号</th>
                    <th>域</th>
                    <th>IP地址</th>
                    <th>主机名称</th>
                    <th nowrap>线程</th>
                    <th>注册时间</th>
                    <th>心跳时间</th>
                    <th>取数时间</th>
                    <th nowrap>版本</th>
                    <th nowrap>下次开始</th>
                    <th nowrap>下次结束</th>
                    <th>处理详情</th>
                    <th>处理机器</th>
                </tr>
                <%
                    List<ScheduleServer> serverList = ConsoleManager.getScheduleDataManager()
                            .selectAllValidScheduleServer(taskTypeRunningInfoList.get(i).getTaskType());
                    for (int j = 0; j < serverList.size(); j++) {
                        String bgColor = "";
                        ScheduleTaskType base = ConsoleManager.getScheduleDataManager()
                                .loadTaskTypeBaseInfo(serverList.get(j).getBaseTaskType());
                        if (serverList.get(j).getCenterServerTime().getTime() - serverList.get(j).getHeartBeatTime()
                                .getTime() > base.getJudgeDeadInterval()) {
                            bgColor = "BGCOLOR='#A9A9A9'";
                        } else if (serverList.get(j).getLastFetchDataTime() == null ||
                                serverList.get(j).getCenterServerTime().getTime() - serverList.get(j)
                                        .getLastFetchDataTime().getTime() > base.getHeartBeatRate() * 20) {
                            bgColor = "BGCOLOR='#FF0000'";
                        }
                %>
                <tr <%=bgColor%>>
                    <td><%=(j + 1)%>
                    </td>
                    <td nowrap><%=serverList.get(j).getUuid()%>
                    </td>
                    <td><%=serverList.get(j).getOwnSign()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getIp()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getHostName()%>
                    </td>
                    <td><%=serverList.get(j).getThreadNum()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getRegisterTime()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getHeartBeatTime()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getLastFetchDataTime() == null ? "--"
                            : serverList.get(j).getLastFetchDataTime()%>
                    </td>
                    <td><%=serverList.get(j).getVersion()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getNextRunStartTime() == null ? "--"
                            : serverList.get(j).getNextRunStartTime()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getNextRunEndTime() == null ? "--"
                            : serverList.get(j).getNextRunEndTime()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getDealInfoDesc()%>
                    </td>
                    <td nowrap><%=serverList.get(j).getManagerFactoryUUID()%>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </td>
    </tr>
    <!-- 队列信息 -->
    <tr>
        <td>
            <table border="1" style="border-COLLAPSE: collapse;display:block;">
                <tr>
                    <th>任务项</th>
                    <th>当前线程组</th>
                    <th>请求线程组</th>
                    <th>任务状态</th>
                    <th>任务参数</th>
                    <th>处理描述</th>

                </tr>
                <%
                    List<ScheduleTaskItem> taskItemList = ConsoleManager.getScheduleDataManager()
                            .loadAllTaskItem(taskTypeRunningInfoList.get(i).getTaskType());
                    for (int j = 0; j < taskItemList.size(); j++) {
                %>
                <tr>
                    <td><%=taskItemList.get(j).getTaskItem()%>
                    </td>
                    <td><%=taskItemList.get(j).getCurrentScheduleServer() == null ? "--"
                            : taskItemList.get(j).getCurrentScheduleServer()%>
                    </td>
                    <td><%=taskItemList.get(j).getRequestScheduleServer() == null ? "--"
                            : taskItemList.get(j).getRequestScheduleServer()%>
                    </td>
                    <td><%=taskItemList.get(j).getSts()%>
                    </td>
                    <td><%=taskItemList.get(j).getDealParameter() == null ? ""
                            : taskItemList.get(j).getDealParameter()%>
                    </td>
                    <td><%=taskItemList.get(j).getDealDesc() == null ? "" : taskItemList.get(j).getDealDesc()%>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </td>
    </tr>
    <%
        }
    %>
</table>
<%
    }
%>
</body>
</html>

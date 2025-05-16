<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="com.taobao.pamirs.schedule.taskmanager.ScheduleTaskType" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String isManager = request.getParameter("manager");
    String taskTypeName = request.getParameter("taskType");
    ScheduleTaskType taskType = ConsoleManager.getScheduleDataManager().loadTaskTypeBaseInfo(taskTypeName);
    String taskItems = "";
    boolean isNew = false;
    String actionName = "editTaskType";
    String editSts = "";
    if (taskType != null) {
        String[] taskItemList = taskType.getTaskItems();
        for (int j = 0; j < taskItemList.length; j++) {
            if (j > 0) {
                taskItems = taskItems + ",";
            }
            taskItems = taskItems + taskItemList[j];
        }
        editSts = "style=\"background-color: blue\" readonly=\"readonly\"";
    } else {
        taskType = new ScheduleTaskType();
        taskType.setBaseTaskType("请输入新的任务类型...");
        taskType.setDealBeanName("");
        isNew = true;
        actionName = "createTaskType";
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
<body style="font-size:12px;">
<form id="taskTypeForm" method="get" name="taskTypeForm" action="taskTypeDeal.jsp">
    <input type="hidden" name="action" value="<%=actionName%>"/>
    <input type="hidden" name="sts" value="<%=taskType.getSts()%>"/>

    <table>
        <tr>
            <td>任务名称:</td>
            <td><input type="text" id="taskType" name="taskType"  <%=editSts%> value="<%=taskType.getBaseTaskType()%>"
                       width="30"></td>
            <td>任务处理的SpringBean:</td>
            <td><input type="text" id="dealBean" name="dealBean" value="<%=taskType.getDealBeanName()%>" width="30">
            </td>
        </tr>
        <tr>
            <td>心跳频率(秒):</td>
            <td><input type="text" name="heartBeatRate" value="<%=taskType.getHeartBeatRate()/1000.0 %>" width="30">
            </td>
            <td>假定服务死亡间隔(秒):</td>
            <td><input type="text" name="judgeDeadInterval" value="<%=taskType.getJudgeDeadInterval()/1000.0 %>"
                       width="30"></td>
        </tr>
        <tr>
            <td>线程数：</td>
            <td><input type="text" name="threadNumber" value="<%=taskType.getThreadNumber()%>" width="30"></td>
            <td>处理模式：</td>
            <td><input type="text" name="processType" value="<%=taskType.getProcessorType()%>" width="30">
                SLEEP 和 NOTSLEEP
            </td>
        </tr>
        <tr>
            <td>每次获取数据量：</td>
            <td><input type="text" name="fetchNumber" value="<%=taskType.getFetchDataNumber() %>" width="30"></td>
            <td>每次执行数量：</td>
            <td><input type="text" name="executeNumber" value="<%=taskType.getExecuteNumber() %>" width="30">
                只在bean实现IScheduleTaskDealMulti才生效
            </td>
        </tr>
        <tr>
            <td>没有数据时休眠时长(秒)：</td>
            <td><input type="text" name="sleepTimeNoData" value="<%=taskType.getSleepTimeNoData()/1000.0%>" width="30">
            </td>
            <td>每次处理完数据后休眠时间(秒)：</td>
            <td><input type="text" name="sleepTimeInterval" value="<%= taskType.getSleepTimeInterval()/1000.0%>"
                       width="30"></td>
        </tr>
        <tr>
            <td>执行开始时间：</td>
            <td><input type="text" name="permitRunStartTime"
                       value="<%=taskType.getPermitRunStartTime()==null?"":taskType.getPermitRunStartTime()%>"
                       width="30"></td>
            <td>执行结束时间：</td>
            <td><input type="text" name="permitRunEndTime"
                       value="<%=taskType.getPermitRunEndTime()==null?"":taskType.getPermitRunEndTime()%>" width="30">
            </td>
        </tr>
        <tr>
            <td>单线程组最大任务项：</td>
            <td><input type="text" name="maxTaskItemsOfOneThreadGroup"
                       value="<%=taskType.getMaxTaskItemsOfOneThreadGroup()%>" width="30"></td>
            <td>单次调度执行次数：</td>
            <td><input type="text" name="fetchDataCountEachSchedule" value="<%=taskType.getFetchDataCountEachSchedule() %>" width="30">
            默认为-1，表示没有执行次数限制
            </td>
        </tr>
        <tr>
            <td>自定义参数(字符串):</td>
            <td colspan="3"><input type="text" id="taskParameter" name="taskParameter"
                                   value="<%=taskType.getTaskParameter()==null?"":taskType.getTaskParameter()%>"
                                   style="width:657"></td>
        </tr>
        <tr>
            <td>任务项(","分隔):</td>
            <td colspan="3"><TEXTAREA type="textarea" rows="5" , id="taskItems" name="taskItems"
                                      style="width:657"><%=taskItems%> </TEXTAREA></td>
        </tr>

    </table>
    <br/>
    <input type="button" value="保存" onclick="save();" style="width:100px">

</form>
<b>执行开始时间说明：</b><br/>
1.允许执行时段的开始时间crontab的时间格式.'0 * * * * ?' 表示在每分钟的0秒开始<br/>
2.以startrun:开始，则表示开机立即启动调度.<br/>
3.格式参见： http://dogstar.javaeye.com/blog/116130<br/><br/>
<b>执行结束时间说明：</b><br/>
1.允许执行时段的结束时间crontab的时间格式,'20 * * * * ?' 表示在每分钟的20秒终止<br/>
2.如果不设置，表示取不到数据就停止 <br/>
3.格式参见：http://dogstar.javaeye.com/blog/116130<br/><br/>
<b>任务项的说明：</b><br/>
1、将一个数据表中所有数据的ID按10取模，就将数据划分成了0、1、2、3、4、5、6、7、8、9供10个任务项。<br/>
2、将一个目录下的所有文件按文件名称的首字母(不区分大小写)， 就划分成了A、B、C、D、E、F、G、H、I、J、K、L、M、N、O、P、Q、R、S、T、U、V、W、X、Y、Z供26个任务项。<br/>
3、将一个数据表的数据ID哈希后按1000取模作为最后的HASHCODE,我们就可以将数据按[0,100)、[100,200) 、[200,300)、[300,400)
、[400,500)、[500,600)、[600,700)、[700,800)、[800,900)、 [900,1000)划分为十个任务项，
当然你也可以划分为100个任务项，最多是1000个项。<br/>
4、任务项是进行任务分配的最小单位。一个任务队列只能由一个ScheduleServer来进行处理。但一个Server可以处理任意数量的任务项。
</body>
</html>

<script>
  function save() {
    var taskType = document.all("taskType").value;
    var reg = /.*[\u4e00-\u9fa5]+.*$/;
    if (reg.test(taskType)) {
      alert('任务类型不能含中文');
      return;
    }
    if (taskType == null || taskType == '' || isContainSpace(taskType)) {
      alert('任务类型不能为空或存在空格');
      return;
    }
    var str = document.all("dealBean").value;
    if (str == null || str.length == 0) {
      alert("请输入处理任务的bean名称！！");
      return;
    }
    if (isContainSpace(str)) {
      alert('处理任务的bean名称不能存在空格');
      return;
    }
    if (reg.test(str)) {
      alert('bean名称不能含中文');
      return;
    }
    str = document.all("taskItems").value;
    if (str == null || str.length == 0) {
      alert("请输入任务项！！");
      return;
    }
    document.getElementById("taskTypeForm").submit();
  }

  function isContainSpace(array) {
    if (array.indexOf(' ') >= 0) {
      return true;
    }
    return false;
  }
</script>
<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="java.io.BufferedReader" %>
<%@page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringReader" %>
<%@ page import="java.io.StringWriter" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (ConsoleManager.isInitial() == false) {
        response.sendRedirect("config.jsp");
    }
    StringWriter writer = new StringWriter();
    boolean isUpdate = false;
    String configContent = "";
    try {
        if ("POST".equals(request.getMethod())) {
            configContent = request.getParameter("configContent");
            StringReader strReader = new StringReader(configContent);
            BufferedReader bufReader = new BufferedReader(strReader);
            String line = null;
            boolean isUploadConfig = false;
            isUpdate = Boolean
                    .valueOf(request.getParameter("isUpdate"));
            while ((line = bufReader.readLine()) != null) {
                isUploadConfig = true;
                if (line.contains("strategy")
                        || line.contains("baseTaskType")) {
                    ConsoleManager.getScheduleStrategyManager()
                            .importConfig(line, writer, isUpdate);
                } else {
                    writer.write("<h3><font color=\"red\">非法配置信息：\n\t\t</font>"
                            + line + "</h3>");
                }
            }
            if (!isUploadConfig) {
                writer.append("<h3><font color=\"red\">错误信息：\n\t</font>没有选择导入的配置文件</h3>");
            }
        }
    } catch (Exception e) {
        StringWriter strWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(strWriter);
        e.printStackTrace(printWriter);
        writer.append("<h3><font color=\"red\">错误信息堆栈：\n\t\t</font>"
                + e.getMessage() + "\n" + strWriter.toString()
                + "</h3>");
    }
%>
<!-- encType="multipart/form-data" -->
<html>
<body style="font-size: 12px;">
<form id="taskTypeForm" method="post" name="taskTypeForm"
      action="importConfig.jsp"><pre
        style="width: 100px; float: left;">配置文本信息：</pre>
    <textarea
            name="configContent" style="width: 1000px; height: 150px;"><%=configContent%></textarea>
    <br/>
    是否强制更新：&nbsp;&nbsp; <select name="isUpdate">
        <option value="true" <%if (isUpdate) {%> selected <%}%>>是</option>
        <option value="false" <%if (!isUpdate) {%> selected <%}%>>否</option>
    </select> <input type="button" onclick="importConfig();" value="导入配置"/></form>
<pre>
	<h3>
<%=writer.toString()%>
	</h3>
</pre>
<script>
  // 导入配置文件
  function importConfig() {
    document.getElementById("taskTypeForm").submit();
  }

  function insertTitle(tValue) {
    var t1 = tValue.lastIndexOf("\\");
    var t2 = tValue.lastIndexOf(".");
    if (t1 >= 0 && t1 < t2 && t1 < tValue.length) {
      document.getElementById("fileName").value = tValue
      .substring(t1 + 1);
    }
  }
</script>
</body>
</html>

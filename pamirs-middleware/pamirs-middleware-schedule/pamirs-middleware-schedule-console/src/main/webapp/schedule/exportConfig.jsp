<%@page import="com.taobao.pamirs.schedule.ConsoleManager" %>
<%@page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%
    if (ConsoleManager.isInitial() == false) {
        response.sendRedirect("config.jsp");
    }
    StringWriter confWriter = new StringWriter();
    StringWriter errWriter = new StringWriter();
    String rootPath = ConsoleManager.getScheduleStrategyManager().getRootPath();
    String type = (String) request.getParameter("type");
    if ("POST".equals(request.getMethod())) {
        StringWriter tmpWriter = new StringWriter();
        try {
            StringBuffer buffer = null;
            if (rootPath != null && rootPath.length() > 0) {
                buffer = ConsoleManager.getScheduleStrategyManager()
                        .exportConfig(rootPath, confWriter);
            } else {
                tmpWriter.write("没有设置导出配置信息的路径");
            }
            // 导出文件
            if (type != null && type.equals("1")) {
                // 导出进行保存
                if (buffer != null) {
                    response.setContentType("text/plain;charset=GBK");
                    response.setHeader("Content-disposition",
                            "attachment; filename=config.txt");
                    PrintWriter out_ = response.getWriter();
                    out_.print(buffer.toString());
                    out_.close();
                }
            }
            if (tmpWriter != null && tmpWriter.toString().length() > 0) {
                errWriter.write("<font color=\"red\">错误信息：</font>\n\t");
                errWriter.write(tmpWriter.toString());
            }
        } catch (Exception e) {
            if (tmpWriter != null && tmpWriter.toString().length() > 0) {
                errWriter.write("<font color=\"red\">错误信息：</font>\n\t");
                errWriter.write(tmpWriter.toString());
            }
            StringWriter strWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(strWriter);
            e.printStackTrace(printWriter);
            errWriter.write("<font color=\"red\">错误的堆栈信息:</font>\n\t" + e.getMessage() + "\n" + strWriter);
        }
    }
%>
<html>
<body style="font-size: 12px;">
<form id="taskTypeForm" method="post" name="taskTypeForm"
      action="exportConfig.jsp"><input type="hidden" id="type"
                                       name="type" value="1"/> 配置文件路径：<input type="text" name="rootPath"
                                                                             value="<%=rootPath == null ? "" : rootPath%>"
                                                                             style="width:330px;"/>
    <input type="button" onclick="viewConfig();" value="查看"/> <input
            type="button" onclick="saveConfig();" value="导出"/>
    <pre>
<%if (errWriter == null || errWriter.getBuffer().length() == 0) { %>
<%=confWriter%>
<%} %><h3>
<%=errWriter == null ? "" : errWriter%>
</h3>
</pre>
</form>
<script>
  // 查看配置文件
  function viewConfig() {
    document.getElementById("type").value = "0";
    document.getElementById("taskTypeForm").submit();
  }

  // 导出配置文件
  function saveConfig() {
    document.getElementById("type").value = "1";
    document.getElementById("taskTypeForm").submit();
  }
</script>
</body>
</html>

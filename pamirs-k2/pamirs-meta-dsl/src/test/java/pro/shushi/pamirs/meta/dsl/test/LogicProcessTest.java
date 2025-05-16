package pro.shushi.pamirs.meta.dsl.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.core.io.ClassPathResource;
import pro.shushi.pamirs.meta.dsl.definition.helper.DefinitionHelper;
import pro.shushi.pamirs.meta.dsl.definition.helper.XStreamHelper;
import pro.shushi.pamirs.meta.dsl.definition.node.Exception;
import pro.shushi.pamirs.meta.dsl.definition.node.*;
import pro.shushi.pamirs.meta.dsl.process.AutoProcess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LogicProcessTest extends TestCase{

	public void setUp(){
		DefinitionHelper.deployProcess(new ClassPathResource("/process/logic_1.xml"));
	}

	public void testLogicProcessRun(){
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("oil", "6");
		context.put("a", "Hello");
		context.put("b", "world");
		context = AutoProcess.run("pro.shushi.pamirs.base.model.meta.Functions.test", context, Boolean.TRUE);
		Assert.assertTrue(true);
		Assert.assertEquals("shot", context.get("r"));
	}

	public void testXML(){
		Logic logic = new Logic("逻辑", 1);
		logic.setDesc("这是一个逻辑");
		logic.setStart(new Begin("start1"));
		logic.getStart().setTo(new To("action2"));
		logic.setNodes(new ArrayList<>());

		Action action2 = new Action("action2");
		action2.setDesc("模型操作2");
		action2.setModel("pro.shushi.pamirs.base.model.resource.ResourceGroup");
		action2.setName("create");
		action2.setArg("abc");
		action2.setTos( new ArrayList<>() );
		To transition = new To("if3");
		action2.getTos().add(transition);
		logic.getNodes().add(action2);

		If if3 = new If("if3");
		if3.setDesc("条件判断3");
		if3.setExp("action2.model == \"pro.shushi.pamirs.base.model.meta.Model\"");
		if3.setTos( new ArrayList<>());
		To transition2 = new To("foreach4");
		if3.getTos().add(transition2);
		To transition3 = new To("fun9");
		if3.setEls(transition3);
		logic.getNodes().add(if3);

		Foreach foreach4 = new Foreach("foreach4");
		foreach4.setDesc("遍历集合4");
		foreach4.setList("action2.modelFields");
		foreach4.setStart("2");
		foreach4.setEnd("8");
		foreach4.setTos( new ArrayList<>() );
		To transition4 = new To("new5");
		foreach4.getTos().add(transition4);
		To transition5 = new To("fun8");
		foreach4.setEach(transition5);
		logic.getNodes().add(foreach4);

		New new10 = new New("new5");
		new10.setDesc("新纪录5");
		new10.setModel("pro.shushi.pamirs.model.Model");
		new10.setFields(new ArrayList<>());
		Arg arg12 = new Arg();
		arg12.setName("name");
		arg12.setExp("abc");
		new10.getFields().add(arg12);
		Arg arg11 = new Arg();
		arg11.setName("gogogo");
		arg11.setExp("ddd");
		new10.getFields().add(arg11);

		new10.setTos( new ArrayList<>() );
		To transition10 = new To("assign6");
		new10.getTos().add(transition10);
		logic.getNodes().add(new10);

		Assign assign8 = new Assign("assign6");
		assign8.setDesc("模型赋值6");
		assign8.setArg("new10");
		assign8.setFields(new ArrayList<>());
		Arg arg2 = new Arg();
		arg2.setName("name");
		arg2.setExp("abc");
		assign8.getFields().add(arg2);

		Arg arg3 = new Arg();
		arg3.setName("description");
		arg3.setExp("abc");
		assign8.getFields().add(arg3);

		Arg arg4 = new Arg();
		arg4.setName("isTransient");
		arg4.setExp("true");
		assign8.getFields().add(arg4);

		assign8.setTos(new ArrayList<>());
		To transition6 = new To("action7");
		assign8.getTos().add(transition6);
		logic.getNodes().add(assign8);

		Action action9 = new Action("action7");
		action9.setDesc("动作调用7");
		action9.setModel("pro.shushi.pamirs.base.model.meta.Model");
		action9.setName("test");
		action9.setArg("abc");
		logic.getNodes().add(action9);

		Fun fun7 = new Fun("fun8");
		fun7.setDesc("函数调用8");
		fun7.setNamespace("pamirs");
		fun7.setName("test");
		fun7.setArgs(new ArrayList<>());

		Arg arg6 = new Arg();
		arg6.setName("a");
		arg6.setExp("1+2");
		fun7.getArgs().add(arg6);

		Arg arg7 = new Arg();
		arg7.setName("b");
		arg7.setExp("action2.isEnhance");
		fun7.getArgs().add(arg7);

		logic.getNodes().add(fun7);


		Fun fun5 = new Fun("fun9");
		fun5.setDesc("函数调用9");
		fun5.setNamespace("pamirs");
		fun5.setName("test2");
		fun5.setArgs(new ArrayList<>());

		Arg arg8 = new Arg();
		arg8.setName("a");
		arg8.setExp("1+2");
		fun5.getArgs().add(arg8);

		Arg arg9 = new Arg();
		arg9.setName("b");
		arg9.setExp("action2.info");
		fun5.getArgs().add(arg9);

		To transition7 = new To("exception10");
		fun5.setEx(transition7);

		fun5.setTos(new ArrayList<>());
		To transition8 = new To("create11");
		fun5.getTos().add(transition8);
		logic.getNodes().add(fun5);

		Exception exception6 = new Exception("exception10");
		exception6.setDesc("异常处理10");
		To transition9 = new To("code == \"E-00001\"","fun15");
		exception6.setTos(Arrays.asList(transition9));
		logic.getNodes().add(exception6);

		Create create10 = new Create("create11");
		create10.setDesc("新增记录11");
		create10.setModel("pro.shushi.pamirs.base.model.meta.Model");
		create10.setArg("abc");

		create10.setTos(new ArrayList<>());
		To transition11 = new To("query12");
		create10.getTos().add(transition11);
		logic.getNodes().add(create10);

		Query query11 = new Query("query12");
		query11.setDesc("查询记录12");
		query11.setModel("pro.shushi.pamirs.base.model.meta.Model");
		query11.setRsql("id>0");
		query11.setAggs("id=count=countId");
		query11.setPage(1);
		query11.setSize(20);
		query11.setGroupBy("name");

		query11.setTos(new ArrayList<>());
		To transition12 = new To("update13");
		query11.getTos().add(transition12);
		logic.getNodes().add(query11);

		Update update12 = new Update("update13");
		update12.setDesc("更新记录13");
		update12.setModel("pro.shushi.pamirs.base.model.meta.Model");
		update12.setArg("abc");

		update12.setTos(new ArrayList<>());
		To transition13 = new To("delete14");
		update12.getTos().add(transition13);
		logic.getNodes().add(update12);

		Delete delete13 = new Delete("delete14");
		delete13.setDesc("删除记录14");
		delete13.setModel("pro.shushi.pamirs.base.model.meta.Model");
		delete13.setArg("abc");

		logic.getNodes().add(delete13);

		Fun fun15 = new Fun("fun15");
		fun15.setDesc("函数调用15");
		fun15.setNamespace("pamirs");
		fun15.setName("test2");
		fun15.setArgs(new ArrayList<>());

		Arg arg10 = new Arg();
		arg10.setName("a");
		arg10.setExp("1+2");
		fun15.getArgs().add(arg10);

		Arg arg13 = new Arg();
		arg13.setName("b");
		arg13.setExp("action2.info");
		fun15.getArgs().add(arg13);

		logic.getNodes().add(fun15);

		System.out.println(XStreamHelper.getInstance().toXML(logic));
	}
}

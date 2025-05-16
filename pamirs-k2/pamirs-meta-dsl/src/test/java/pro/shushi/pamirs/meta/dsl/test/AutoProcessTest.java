package pro.shushi.pamirs.meta.dsl.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.springframework.core.io.ClassPathResource;
import pro.shushi.pamirs.meta.dsl.definition.helper.DefinitionHelper;
import pro.shushi.pamirs.meta.dsl.process.AutoProcess;

import java.util.HashMap;
import java.util.Map;

public class AutoProcessTest extends TestCase{

	public void setUp(){
		DefinitionHelper.deployProcess(new ClassPathResource("/process/trade_1.xml"));
	}
	
	public void testAutoProcessRun(){
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("oil", "6");
		context.put("a", "Hello");
		context.put("b", "world");
		context = AutoProcess.run("trade", context);
		Assert.assertTrue(true);
		Assert.assertEquals("shot", context.get("r"));
	}
}
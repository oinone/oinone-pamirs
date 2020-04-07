package pro.shushi.pamirs.meta.dsl.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import pro.shushi.pamirs.meta.dsl.definition.helper.DefinitionHelper;
import pro.shushi.pamirs.meta.dsl.enumeration.EventExecutableStatus;
import pro.shushi.pamirs.meta.dsl.process.EventProcess;
import pro.shushi.pamirs.meta.dsl.process.EventProcessContext;

public class EventProcessTest extends TestCase{

	public void setUp(){
		new ClassPathXmlApplicationContext(new String[]{"classpath:spring.xml"});
		DefinitionHelper.deployProcess(new ClassPathResource("/process/event_1.xml"));
		DefinitionHelper.deployProcess(new ClassPathResource("/process/muti_spring_1.xml"));
	}
	
	public void testMutiSpring(){
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("a", "Hello");
		context.put("b", "world");
		EventProcessContext eventProcessContext = new EventProcessContext();
		eventProcessContext.init("muti_spring", 1, "park", context);
		if (EventProcess.isEventExecutable(eventProcessContext).equals(EventExecutableStatus.NORMAL)) {
			eventProcessContext = EventProcess.fireEvent(eventProcessContext);
			Assert.assertEquals("shot", eventProcessContext.context.get("r"));
		}else {
			Assert.assertTrue(false);
		}
	}
	
	public void testFireEvent(){
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("a", "Hello");
		context.put("b", "world");
		EventProcessContext eventProcessContext = new EventProcessContext();
		eventProcessContext.init("event", 1, "park", context);
		if (EventProcess.isEventExecutable(eventProcessContext).equals(EventExecutableStatus.NORMAL)) {
			eventProcessContext = EventProcess.fireEvent(eventProcessContext);
			Assert.assertEquals("shot", eventProcessContext.context.get("r"));
		}else {
			Assert.assertTrue(false);
		}
		
		List<String> path = null;
		path = Arrays.asList("park");
		eventProcessContext = new EventProcessContext();
		eventProcessContext.init("event", 1, "repair", path, context);
		if (EventProcess.isEventExecutable(eventProcessContext).equals(EventExecutableStatus.NORMAL)) {
			eventProcessContext = EventProcess.fireEvent(eventProcessContext);
			Assert.assertEquals("shot", context.get("r"));
		}else {
			Assert.assertTrue(false);
		}

		path = Arrays.asList("park", "repair");
		eventProcessContext = new EventProcessContext();
		eventProcessContext.init("event", 1, "shopping", path, context);
		if (EventProcess.isEventExecutable(eventProcessContext).equals(EventExecutableStatus.NORMAL)) {
			eventProcessContext = EventProcess.fireEvent(eventProcessContext);
			Assert.assertEquals("shot", context.get("r"));
		}else {
			Assert.assertTrue(false);
		}
	}
	
	public void testFireEventCheckFalse(){
		Map<String, Object> context = new HashMap<String, Object>();
		context.put("a", "Hello");
		context.put("b", "world");
		List<String> path = null;
		path = Arrays.asList("parke", "repaire");
		EventProcessContext eventProcessContext = new EventProcessContext();
		eventProcessContext.init("event", 1, "shopping", path, context);
		if (!EventProcess.isEventExecutable(eventProcessContext).equals(EventExecutableStatus.NORMAL)) {
			Assert.assertTrue(true);
		}
	}
}

package pro.shushi.pamirs.meta.dsl.definition.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.dsl.definition.exception.DefinitionException;
import pro.shushi.pamirs.meta.dsl.init.InitFramework;
import pro.shushi.pamirs.meta.dsl.model.Place;
import pro.shushi.pamirs.meta.dsl.model.Process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DefinitionHelper {

	final static Logger logger = LoggerFactory.getLogger(DefinitionHelper.class);

	/**
	 * deploy a process by file path
	 *
	 * @param resource
	 */
	public static void deployProcess(Resource resource) {
		Object definition = getDefinition(resource);
		List<Process> processList = InitFramework.getTransformer(definition.getClass().getName()).transform(definition);
		for(Process process : processList) {
			if (ValidationHelper.validateProcessNameAndVersion(process, resource.getFilename())) {
				Place.putProcess(process);
			} else {
				throw new DefinitionException(
						"check process name and version failed. file name should be <processName>_<processVersion>.xml");
			}
		}
	}

	/**
	 * deploy a process by definition
	 *
	 * @param content
	 */
	public static List<Process> readProcess(String content) {
		return readProcess(content, null);
	}

	public static List<Process> readProcess(String content, List<String> names) {
		Object definition = getDefinition(content);
		List<Process> processList = InitFramework.getTransformer(definition.getClass().getName()).transform(definition);
		boolean useName = !CollectionUtils.isEmpty(names) && names.size() == processList.size();
		int i = 0;
		for(Process process : processList) {
			if(useName){
				process.setName(names.get(i));
				i++;
			}
			Place.putProcess(process);
		}
		return processList;
	}

	/**
	 * deploy processes by folder path
	 *
	 * @param processResources
	 */
	public static void deployProcessFolder(Resource[] processResources) {
		for (Resource resource : filterResource(processResources)) {
			deployProcess(resource);
		}
	}

	private static List<Resource> filterResource(Resource[] processResources) {
		List<Resource> list = new ArrayList<Resource>();
		for(Resource resource : processResources) {
			if(resource.getFilename().toLowerCase().endsWith(".xml")) {
				list.add(resource);
			}
		}
		return list;
	}

	private static <T> T getDefinition(Resource resource) {
		try {
			InputStream is = resource.getInputStream();
			if (null == is) {
				logger.error("definition not found exception on path : " + resource.getURL());
				throw new DefinitionException("definition not found on path: " + resource.getURL());
			}
			return ParseHelper.parse(is);
		} catch (Exception e) {
			throw new DefinitionException("getDefinition fail", e);
		}
	}

	private static <T> T getDefinition(String definition) {
		try {
			return ParseHelper.parse(definition);
		} catch (Exception e) {
			throw new DefinitionException("getDefinition fail", e);
		}
	}

}

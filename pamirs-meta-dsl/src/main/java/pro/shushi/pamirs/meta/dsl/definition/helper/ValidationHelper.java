package pro.shushi.pamirs.meta.dsl.definition.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import pro.shushi.pamirs.meta.dsl.model.Process;

public class ValidationHelper {

	public static boolean validateProcessNameAndVersion(Process process, String path){
		String fileName = null;
		Pattern p = Pattern.compile("(\\w+)\\.xml$");
		Matcher m = p.matcher(path);
		while (m.find()) {
			fileName = m.group(1);
		}
		if(StringUtils.isBlank(fileName)){
			return false;
		}
		String[] strs = fileName.split("_");
		if (strs.length < 2) {
			return false;
		}
		int processVersion = Integer.valueOf(strs[strs.length-1]);
		if (process.getVersion() == processVersion) {
			return true;
		}
		return false;
	}
	
	public static String validateFolderPath(String path){
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path += "/";
		}
		return path;
	}
}

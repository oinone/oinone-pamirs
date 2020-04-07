package pro.shushi.pamirs.meta.dsl.utils;

import java.util.List;

public class ListUtils {

	public static boolean isEmpty(List<?> list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static String List2String(List list) {
		StringBuffer buf = new StringBuffer();
		if(null == list) {
			return buf.toString();
		}
		for(Object obj : list) {
			buf.append(obj.toString()).append(";");
		}
		return buf.toString();
	}
}

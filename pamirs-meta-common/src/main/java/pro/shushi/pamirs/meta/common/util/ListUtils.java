package pro.shushi.pamirs.meta.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/13 2:23 下午
 */
public class ListUtils {

    public static <T> List<T> asList(T... ts){
        if(null == ts || 0 == ts.length){
            return null;
        }
        List<T> list = new ArrayList<>();
        for(T t : ts){
            list.add(t);
        }
        return list;
    }

    public static boolean isEmptyArray(Object... array){
        if(null == array || 0 == array.length){
            return Boolean.TRUE;
        }
        boolean isEmpty = Boolean.TRUE;
        for(Object arg : array){
            if(null != arg){
                isEmpty = Boolean.FALSE;
            }
        }
        return isEmpty;
    }

}

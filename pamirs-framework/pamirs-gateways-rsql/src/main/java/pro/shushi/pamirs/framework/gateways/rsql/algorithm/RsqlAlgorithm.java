package pro.shushi.pamirs.framework.gateways.rsql.algorithm;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author shier
 * date  2021/3/31 11:31 上午
 */
@Data
public class RsqlAlgorithm<T> {

    List<T> temp = new ArrayList<>();

    List<List<T>> result = new ArrayList<>();

    public void composition(List<List<T>> list, List<T> temp, int n) {
        if (n >= list.size()) {
            return;
        } else {
            for (int i = 0; i < list.get(n).size(); i++) {
                if (n < list.size() - 1) {
                    temp.add(list.get(n).get(i));
                    composition(list, temp, n + 1);
                } else if (n == list.size() - 1) {
                    temp.add(list.get(n).get(i));
                    List<T> list1 = new ArrayList<>();
                    for (T integer : temp) {
                        list1.add(integer);
                    }
                    result.add(list1);
                }
                temp.remove(n);
            }
        }
    }

    public List<List<T>> removeBigger(List<List<T>> origin) {
        List<List<T>> result = new ArrayList<>();
        for (List<T> t : origin) {
            Iterator<List<T>> iterator = result.iterator();
            Boolean needAdd = Boolean.FALSE;
            /**
             * 1.result中没有的时候
             */
            while (iterator.hasNext()) {
                List<T> value = iterator.next();
                if (value.containsAll(t)) {
                    iterator.remove();
                    needAdd = Boolean.TRUE;
                } else if (t.containsAll(value)) {
                    needAdd = Boolean.FALSE;
                    break;
                } else {
                    needAdd = Boolean.TRUE;
                }
            }
            if (needAdd || result.size() == 0) {
                result.add(t);
            }
        }
        return result;
    }
}

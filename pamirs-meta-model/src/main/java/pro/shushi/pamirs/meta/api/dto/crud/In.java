package pro.shushi.pamirs.meta.api.dto.crud;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

/**
 * in
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
public class In extends HashMap<String, Collection<?>> implements Serializable {

    private static final long serialVersionUID = -8542846556585462587L;

    public static final String singlePkPlaceholder = "singlePk";

    public In() {
    }

    public In in(String column, Object... values){
        this.put(column, Lists.newArrayList(values));
        return this;
    }

    public In in(String column, Collection<?> values){
        this.put(column, values);
        return this;
    }

    public Collection<?> values(String column){
        return this.get(column);
    }

    public In pkIn(Object... values){
        this.put(singlePkPlaceholder, Lists.newArrayList(values));
        return this;
    }

    @SuppressWarnings("unused")
    public In pkIn(Collection<?> values){
        this.put(singlePkPlaceholder, values);
        return this;
    }

    public Collection<?> pkValues(){
        return this.get(singlePkPlaceholder);
    }

}

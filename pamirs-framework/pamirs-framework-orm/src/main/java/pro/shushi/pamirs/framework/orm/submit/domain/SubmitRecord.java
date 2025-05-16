package pro.shushi.pamirs.framework.orm.submit.domain;

import pro.shushi.pamirs.meta.annotation.fun.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 待处理记录
 * <p>
 * 2022/5/11 5:16 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Data
public class SubmitRecord<T> {

    private String model;

    private List<T> data;

    private List<Function<T, ?>> referenceHandler;

    private List<BiFunction<T, ?, ?>> throughHandler;

    public SubmitRecord(String model) {
        this.setModel(model);
        this.setData(new ArrayList<>());
        this.setReferenceHandler(new ArrayList<>());
        this.setThroughHandler(new ArrayList<>());
    }

}

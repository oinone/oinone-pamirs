package pro.shushi.pamirs.core.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.D;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static pro.shushi.pamirs.core.common.FetchUtil.cast;

/**
 * 拷贝工具类
 *
 * @author Adamancy Zhang on 2021-04-01 20:45
 */
public class CopyHelper {

    public static String createUserName = "createUserName";
    public static String writeUserName = "writeUserName";

    private CopyHelper() {
        //reject create object
    }

    /**
     * 同源对象修改对象类型（非拷贝）
     *
     * @param origin 源对象
     * @param target 目标对象
     * @return 目标对象
     */
    public static <T extends D, R extends T> R transfer(T origin, R target) {
        String model = Models.api().getModel(target);
        String dataModel = Models.api().getDataModel(target);
        target.set_d(origin.get_d());
        Models.api().setModel(target, model);
        Models.api().setDataModel(dataModel, target);
        return target;
    }

    /**
     * 同源对象浅拷贝
     *
     * @param origin 源对象
     * @param target 目标对象
     * @return 目标对象
     */
    public static <T extends D, R extends T> R simpleReplace(T origin, R target) {
        String model = Models.api().getModel(target);
        String dataModel = Models.api().getDataModel(target);
        target.set_d(new HashMap<>(origin.get_d()));
        Models.api().setModel(target, model);
        Models.api().setDataModel(dataModel, target);
        return target;
    }

    public static <T extends D, R extends T> List<R> simpleReplaces(Iterable<T> iterable, Supplier<R> supplier) {
        return simpleReplaces(iterable, supplier, new ArrayList<>());
    }

    private static <T extends D, R extends T, RC extends Collection<R>> RC simpleReplaces(Iterable<T> iterable, Supplier<R> supplier, RC collection) {
        for (T item : iterable) {
            collection.add(simpleReplace(item, supplier.get()));
        }
        return collection;
    }

    public static <T extends D> void unsetDataId(T data) {
        if (data instanceof IdModel) {
            IdModel idData = (IdModel) data;
            idData.unsetId()
                    .unsetCreateUid()
                    .unsetCreateDate()
                    .unsetWriteUid()
                    .unsetWriteDate();
        }
        if (data instanceof CodeModel) {
            CodeModel codeData = (CodeModel) data;
            codeData.unsetCode()
                    .unsetCreateUid()
                    .unsetCreateDate()
                    .unsetWriteUid()
                    .unsetWriteDate();
        }
        if (FieldUtils.containsFieldValue(data, createUserName)) {
            FieldUtils.setFieldValue(data, createUserName, null);
        }
        if (FieldUtils.containsFieldValue(data, writeUserName)) {
            FieldUtils.setFieldValue(data, writeUserName, null);
        }
    }

    public static <T extends D> void unsetDataIdByIteration(String model, T data) {
        if (data == null || StringUtils.isBlank(model)) {
            return;
        }
        unsetDataId(data);
        relationDataProcess(model, data, (modelFieldConfig, relationData) -> {
            if (!TtypeEnum.O2M.value().equals(modelFieldConfig.getTtype())) {
                return;
            }
            unsetDataIdByIteration(modelFieldConfig.getReferences(), relationData);
        });
    }

    private static <T extends D> void relationDataProcess(String model, T data, BiConsumer<ModelFieldConfig, D> process) {
        ModelConfig modelConfig = PamirsSession.getContext().getModelConfig(model);
        Map<String, Object> dMap = data.get_d();
        for (ModelFieldConfig modelFieldConfig : modelConfig.getModelFieldConfigList()) {
            String ttype = modelFieldConfig.getTtype();
            if (TtypeEnum.isRelationType(ttype)) {
                Object relationDataObject = dMap.get(modelFieldConfig.getField());
                if (relationDataObject == null) {
                    continue;
                }
                if (TtypeEnum.O2O.value().equals(ttype) || TtypeEnum.M2O.value().equals(ttype)) {
                    process.accept(modelFieldConfig, (D) relationDataObject);
                } else if (TtypeEnum.O2M.value().equals(ttype) || TtypeEnum.M2M.value().equals(ttype)) {
                    List<D> relationDataList = cast(dMap.get(modelFieldConfig.getField()));
                    if (CollectionUtils.isEmpty(relationDataList)) {
                        continue;
                    }
                    for (D relationData : relationDataList) {
                        process.accept(modelFieldConfig, relationData);
                    }
                }
            }
        }
    }
}

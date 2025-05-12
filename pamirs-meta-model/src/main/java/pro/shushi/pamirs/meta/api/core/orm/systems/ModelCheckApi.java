package pro.shushi.pamirs.meta.api.core.orm.systems;

import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;

import static pro.shushi.pamirs.meta.enmu.MetaExpEnumerate.*;

/**
 * 模型检查接口
 * <p>
 * 2020/6/30 2:55 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface ModelCheckApi {

    /**
     * 请求校验
     * <p>
     * 默认参数名为data
     *
     * @param model 模型编码
     * @param obj   参数数据
     * @param <T>   参数类型
     * @return 参数数据
     */
    default <T> T checkRequest(String model, T obj) {
        return obj;
    }

    /**
     * 请求校验
     *
     * @param model   模型编码
     * @param argName 参数名
     * @param obj     参数数据
     * @param <T>     参数类型
     * @return 参数数据
     */
    default <T> T checkRequest(String model, String argName, T obj) {
        return obj;
    }

    /**
     * 根据唯一索引或主键判断对象是否包含至少一个非空唯一键值
     *
     * @param obj 对象
     */
    default void checkPkOrUniqueKeyValueValid(Object obj) {
        if (!Models.compute().isUniqueKeyValueValid(obj) && !Models.compute().isPkValueValid(obj)) {
            throw PamirsException.construct(BASE_UNIQUE_KEY_VALUE_INVALID_ERROR).errThrow();
        }
    }

    /**
     * 根据唯一索引或主键判断列表中是否所有元素包含至少一个非空唯一键值
     *
     * @param list 列表
     */
    default void checkListPkOrUniqueKeyValueValid(List<?> list) {
        if (!Models.compute().isListPkOrUniqueKeyValueValid(list)) {
            throw PamirsException.construct(BASE_LIST_PK_OR_UNIQUE_KEY_VALUE_INVALID_ERROR).errThrow();
        }
    }

    /**
     * 根据唯一索引判断对象是否包含至少一个非空唯一键值
     *
     * @param obj 对象
     */
    default void checkUniqueKeyValueValid(Object obj) {
        if (!Models.compute().isUniqueKeyValueValid(obj)) {
            throw PamirsException.construct(BASE_OBJ_UNIQUE_KEY_VALUE_INVALID_ERROR).errThrow();
        }
    }

    /**
     * 根据唯一索引判断列表中是否所有元素包含至少一个非空唯一键值
     *
     * @param list 列表
     */
    default void checkListUniqueKeyValueValid(List<?> list) {
        if (!Models.compute().isListUniqueKeyValueValid(list)) {
            throw PamirsException.construct(BASE_LIST_UNIQUE_KEY_VALUE_INVALID_ERROR).errThrow();
        }
    }

    /**
     * 根据唯一索引判断对象主键值非空
     *
     * @param obj 对象
     */
    default void checkPkValueValid(Object obj) {
        if (!Models.compute().isPkValueValid(obj)) {
            throw PamirsException.construct(BASE_PK_VALUE_INVALID_ERROR).errThrow();
        }
    }

    /**
     * 根据唯一索引判断列表中是否所有元素主键值非空
     *
     * @param list 列表
     */
    default void checkListPkValueValid(List<?> list) {
        if (!Models.compute().isListPkValueValid(list)) {
            throw PamirsException.construct(BASE_LIST_PK_VALUE_INVALID_ERROR).errThrow();
        }
    }

}

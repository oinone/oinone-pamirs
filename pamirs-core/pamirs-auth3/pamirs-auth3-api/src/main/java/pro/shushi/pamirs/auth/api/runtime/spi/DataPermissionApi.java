package pro.shushi.pamirs.auth.api.runtime.spi;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 数据权限API
 *
 * @author Adamancy Zhang at 16:21 on 2024-01-06
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataPermissionApi {

    /**
     * 判断是否可以访问指定模型
     *
     * @param model        指定模型
     * @param functionType 函数类型
     * @return 是否可访问
     */
    AuthResult<Boolean> isAccessModel(String model, List<FunctionTypeEnum> functionType);

    /**
     * 获取模型字段权限
     *
     * @param model 指定模型
     * @return 字段权限
     */
    AuthResult<Map<String, Long>> fetchFieldPermissions(String model);

    /**
     * 获取模型过滤表达式 - 读
     *
     * @param model 指定模型
     * @return 过滤表达式
     */
    AuthResult<String> fetchModelFilterForRead(String model);

    /**
     * 获取模型过滤表达式 - 写
     *
     * @param model 指定模型
     * @return 过滤表达式
     */
    AuthResult<String> fetchModelFilterForWrite(String model);

    /**
     * 获取模型过滤表达式 - 删除
     *
     * @param model 指定模型
     * @return 过滤表达式
     */
    AuthResult<String> fetchModelFilterForDelete(String model);
}

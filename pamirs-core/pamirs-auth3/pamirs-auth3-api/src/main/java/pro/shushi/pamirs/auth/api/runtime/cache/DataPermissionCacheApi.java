package pro.shushi.pamirs.auth.api.runtime.cache;

import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.factory.SpringServiceLoaderFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据权限缓存API
 *
 * @author Adamancy Zhang at 20:03 on 2024-01-04
 */
@SPI(factory = SpringServiceLoaderFactory.class)
public interface DataPermissionCacheApi {

    /**
     * 获取模型权限
     *
     * @param model 模型
     * @return 模型授权值
     */
    AuthResult<Long> fetchModelPermission(Set<Long> roleIds, String model);

    /**
     * 批量获取模型权限
     *
     * @param models 模型列表
     * @return 模型授权值
     */
    AuthResult<Map<String, Long>> fetchModelPermissionBatch(Set<Long> roleIds, List<String> models);

    /**
     * 获取字段权限
     *
     * @param model 模型
     * @return 字段授权值Map
     */
    AuthResult<Map<String, Long>> fetchFieldPermissions(Set<Long> roleIds, String model);

    /**
     * 批量获取字段权限
     *
     * @param models 模型列表
     * @return 字段授权值
     */
    AuthResult<Map<String, Map<String, Long>>> fetchFieldPermissionsBatch(Set<Long> roleIds, List<String> models);

    /**
     * 获取行权限 - 读
     *
     * @param model 模型
     * @return 行权限过滤表达式集合
     */
    AuthResult<Set<String>> fetchRowPermissionsForRead(Set<Long> roleIds, String model);

    /**
     * 获取行权限 - 写
     *
     * @param model 模型
     * @return 行权限过滤表达式集合
     */
    AuthResult<Set<String>> fetchRowPermissionsForWrite(Set<Long> roleIds, String model);

    /**
     * 获取行权限 - 删除
     *
     * @param model 模型
     * @return 行权限过滤表达式集合
     */
    AuthResult<Set<String>> fetchRowPermissionsForDelete(Set<Long> roleIds, String model);
}

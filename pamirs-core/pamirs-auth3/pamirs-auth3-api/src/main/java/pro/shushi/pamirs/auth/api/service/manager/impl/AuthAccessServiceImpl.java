package pro.shushi.pamirs.auth.api.service.manager.impl;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.auth.api.constants.AuthConstants;
import pro.shushi.pamirs.auth.api.debug.AuthVerificationDebugTrace;
import pro.shushi.pamirs.auth.api.entity.AuthResult;
import pro.shushi.pamirs.auth.api.enumeration.authorized.FieldAuthorizedValueEnum;
import pro.shushi.pamirs.auth.api.holder.AuthApiHolder;
import pro.shushi.pamirs.auth.api.runtime.executor.DataPermissionExecutor;
import pro.shushi.pamirs.auth.api.runtime.spi.DataPermissionApi;
import pro.shushi.pamirs.auth.api.service.manager.AuthAccessService;
import pro.shushi.pamirs.auth.api.utils.AuthVerificationHelper;
import pro.shushi.pamirs.core.common.FetchUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.config.ModelFieldConfig;
import pro.shushi.pamirs.meta.api.session.PamirsSession;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 权限扩展服务实现
 *
 * @author Adamancy Zhang at 09:32 on 2024-01-12
 */
@Slf4j
@Service
@Order(10)
public class AuthAccessServiceImpl implements AuthAccessService {

    @Override
    public Result<Void> canAccessModule(String module) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessModule(module)),
                result -> AuthVerificationDebugTrace.debugAccessModule(result, module));
    }

    @Override
    public Result<Void> canAccessHomepage(String module) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessHomepage(module)),
                result -> AuthVerificationDebugTrace.debugAccessHomepage(result, module));
    }

    @Override
    public Result<Void> canAccessMenu(String module, String name) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessMenu(module, name)),
                result -> AuthVerificationDebugTrace.debugAccessMenu(result, module, name));
    }

    @Override
    public Result<Void> canAccessAction(String model, String name) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessAction(model, name)),
                result -> AuthVerificationDebugTrace.debugAccessAction(result, model, name));
    }

    @Override
    public Result<Void> canAccessAction(String path) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessAction(path)),
                result -> AuthVerificationDebugTrace.debugAccessAction(result, path));
    }

    @Override
    public Result<Void> canAccessFunction(String namespace, String fun) {
        return debug(() -> convertBooleanResult(AuthApiHolder.getAccessPermissionApi().isAccessFunction(namespace, fun)),
                result -> AuthVerificationDebugTrace.debugAccessFunction(result, namespace, fun));
    }

    @Override
    public Result<String> canReadableData(String model) {
        return fetchModelFilters(model, api -> api::fetchModelFilterForRead);
    }

    @Override
    public Result<String> canWritableData(String model) {
        return fetchModelFilters(model, api -> api::fetchModelFilterForWrite);
    }

    @Override
    public Result<String> canDeletableData(String model) {
        return fetchModelFilters(model, api -> api::fetchModelFilterForDelete);
    }

    @Override
    public Result<Set<String>> canReadableFields(String model) {
        return debug(() -> fetchFieldPermissions(model, FieldAuthorizedValueEnum::readable),
                result -> AuthVerificationDebugTrace.debugReadableFields(result, model));
    }

    @Override
    public Result<Set<String>> canWritableFields(String model) {
        return debug(() -> fetchFieldPermissions(model, FieldAuthorizedValueEnum::writable),
                result -> AuthVerificationDebugTrace.debugWritableFields(result, model));
    }

    @Override
    public String getDataFilter(String namespace, String fun) {
        return DataPermissionExecutor.getFilter(namespace, fun);
    }

    @Override
    public Result<Set<String>> canAccessModules() {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessModules());
    }

    @Override
    public Result<Set<String>> canAccessHomepages() {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessHomepages());
    }

    @Override
    public Result<Set<String>> canAccessMenus(String module) {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessMenus(module));
    }

    @Override
    public Result<Set<String>> canAccessActions() {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessActions());
    }

    @Override
    public Result<Set<String>> canAccessActions(String model) {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessActions(model));
    }

    @Override
    public Boolean checkModuleAccess(String module) {
        return AuthAccessService.super.checkModuleAccess(module);
    }

    @Override
    public Result<String> canReadAccessData(String model) {
        return AuthAccessService.super.canReadAccessData(model);
    }

    @Override
    public Result<List<String>> canReadAccessField(String model) {
        return AuthAccessService.super.canReadAccessField(model);
    }

    @Override
    public Result<List<String>> canUpdateAccessField(String model) {
        return AuthAccessService.super.canUpdateAccessField(model);
    }

    @Override
    public Result<Void> canManagementModule(String module) {
        return convertBooleanResult(AuthApiHolder.getManagementPermissionApi().isManagementModule(module));
    }

    @Override
    public Result<Void> canManagementHomepage(String module) {
        return convertBooleanResult(AuthApiHolder.getManagementPermissionApi().isManagementHomepage(module));
    }

    @Override
    public Result<Void> canManagementMenu(String module, String name) {
        return convertBooleanResult(AuthApiHolder.getManagementPermissionApi().isManagementMenu(module, name));
    }

    @Override
    public Result<Set<String>> canManagementModules() {
        return convertResult(AuthApiHolder.getManagementPermissionApi().fetchManagementModules());
    }

    @Override
    public Result<Set<String>> canManagementHomepages() {
        return convertResult(AuthApiHolder.getManagementPermissionApi().fetchManagementHomepages());
    }

    @Override
    public Result<Set<String>> canManagementMenus(String module) {
        return convertResult(AuthApiHolder.getManagementPermissionApi().fetchManagementMenus(module));
    }

    @Override
    public Result<Map<String, Set<String>>> canAccessMenus(Set<String> modules) {
        return convertResult(AuthApiHolder.getAccessPermissionApi().fetchAccessMenus(modules));
    }

    @Override
    public Result<Map<String, Set<String>>> canManagementMenus(Set<String> modules) {
        return convertResult(AuthApiHolder.getManagementPermissionApi().fetchManagementMenus(modules));
    }

    @Override
    public Result<Void> canManagementAction(String model, String name) {
        return convertBooleanResult(AuthApiHolder.getManagementPermissionApi().isManagementAction(model, name));
    }

    @Override
    public Result<Void> canManagementAction(String path) {
        return convertBooleanResult(AuthApiHolder.getManagementPermissionApi().isManagementAction(path));
    }

    private Result<Void> convertBooleanResult(AuthResult<Boolean> result) {
        return new Result<Void>().setSuccess(AuthVerificationHelper.isAccessResource(result));
    }

    private <R> Result<R> convertResult(AuthResult<R> result) {
        if (result != null && result.isFetch()) {
            return new Result<R>().setData(result.getData());
        }
        return new Result<R>().setSuccess(Boolean.FALSE);
    }

    private Result<String> fetchModelFilters(String model, DataPermissionFetcher fetcher) {
        AuthResult<String> result = fetcher.apply(AuthApiHolder.getDataPermissionApi()).apply(model);
        if (result != null && result.isFetch()) {
            String rsql = result.getData();
            if (StringUtils.isBlank(rsql)) {
                return new Result<>();
            }
            try {
                return new Result<String>().setData(FetchUtil.rsqlToSql(model, rsql));
            } catch (Exception e) {
                log.error("Row permission parser error. rsql = {}", rsql, e);
            }
        }
        return new Result<String>().setSuccess(Boolean.FALSE);
    }

    private Result<Set<String>> fetchFieldPermissions(String model, Predicate<Long> authorizedPredict) {
        AuthResult<Map<String, Long>> result = AuthApiHolder.getDataPermissionApi().fetchFieldPermissions(model);
        if (result != null && result.isFetch()) {
            Map<String, Long> data = result.getData();
            if (MapUtils.isNotEmpty(data)) {
                boolean isAllFields = Optional.ofNullable(data.get(AuthConstants.ALL_FLAG_STRING))
                        .map(authorizedPredict::test)
                        .orElse(false);
                if (isAllFields) {
                    return new Result<Set<String>>().setSuccess(Boolean.TRUE).setData(Optional.ofNullable(PamirsSession.getContext().getModelConfig(model))
                            .map(ModelConfig::getModelFieldConfigList)
                            .map(v -> v.stream().map(ModelFieldConfig::getField).collect(Collectors.toSet()))
                            .orElse(Collections.emptySet()));
                }
                Set<String> fields = new HashSet<>(data.size());
                for (Map.Entry<String, Long> entry : data.entrySet()) {
                    if (authorizedPredict.test(entry.getValue())) {
                        fields.add(entry.getKey());
                    }
                }
                return new Result<Set<String>>().setSuccess(Boolean.TRUE).setData(fields);
            }
            return new Result<Set<String>>().setSuccess(Boolean.TRUE);
        }
        return new Result<Set<String>>().setSuccess(Boolean.FALSE);
    }

    private <R> Result<R> debug(Supplier<Result<R>> supplier, Consumer<Result<R>> debugConsumer) {
        Result<R> result = supplier.get();
        debugConsumer.accept(result);
        return result;
    }

    @FunctionalInterface
    private interface DataPermissionFetcher {

        DataPermissionApply apply(DataPermissionApi api);
    }

    @FunctionalInterface
    private interface DataPermissionApply {

        AuthResult<String> apply(String model);
    }
}

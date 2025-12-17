package pro.shushi.pamirs.meta.api.core.auth;

import pro.shushi.pamirs.meta.api.CommonApi;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.common.spi.HoldKeeper;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 权限接口
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/15 2:41 下午
 */
@SPI
public interface AuthApi extends CommonApi {

    /**
     * check module access permission.
     *
     * @param module specify module for check
     * @return is allow access module
     */
    Result<Void> canAccessModule(String module);

    /**
     * check module homepage access permission.
     *
     * @param module specify module for check
     * @return is allow access module homepage
     */
    Result<Void> canAccessHomepage(String module);

    /**
     * check module menu access permission.
     *
     * @param module specify module
     * @param name   specify menu name
     * @return access menu names
     */
    Result<Void> canAccessMenu(String module, String name);

    /**
     * check action access permission.
     *
     * @param model specify action model
     * @param name  specify action name
     * @return is allow access action
     */
    Result<Void> canAccessAction(String model, String name);

    /**
     * check action access permission.
     *
     * @param path specify action path
     * @return is allow access action
     */
    Result<Void> canAccessAction(String path);

    /**
     * check function access permission.
     *
     * @param namespace specify function namespace
     * @param fun       specify function fun
     * @return is allow access function
     */
    Result<Void> canAccessFunction(String namespace, String fun);

    /**
     * get model readable data filter.
     *
     * @param model specify model
     * @return readable data filter
     */
    Result<String> canReadableData(String model);

    /**
     * get model writable data filter.
     *
     * @param model specify model
     * @return writable data filter
     */
    Result<String> canWritableData(String model);

    /**
     * get model deletable data filter.
     *
     * @param model specify model
     * @return deletable data filter
     */
    Result<String> canDeletableData(String model);

    /**
     * get model readable fields.
     *
     * @param model specify model
     * @return model readable fields
     */
    Result<Set<String>> canReadableFields(String model);

    /**
     * get model writable fields.
     *
     * @param model specify model
     * @return model writable fields
     */
    Result<Set<String>> canWritableFields(String model);

    /**
     * get data filter
     *
     * @param namespace specify function namespace
     * @param fun       specify function fun
     * @return specify model data filter
     */
    String getDataFilter(String namespace, String fun);

    /**
     * get access modules.
     *
     * @return access modules
     */
    @Deprecated
    Result<Set<String>> canAccessModules();

    /**
     * get access homepages.
     *
     * @return access homepages
     */
    @Deprecated
    Result<Set<String>> canAccessHomepages();

    /**
     * get access menu names.
     *
     * @param module specify module
     * @return access menu names
     */
    @Deprecated
    Result<Set<String>> canAccessMenus(String module);

    /**
     * get access actions by session path.
     *
     * @return access action paths
     */
    @Deprecated
    Result<Set<String>> canAccessActions();

    /**
     * get access actions by model.
     *
     * @return access actions
     */
    @Deprecated
    Result<Set<String>> canAccessActions(String model);

    /**
     * @deprecated please using canAccessModule
     */
    @Deprecated
    default Boolean checkModuleAccess(String module) {
        return canAccessModule(module).getSuccess();
    }

    /**
     * @deprecated please using canReadableData
     */
    @Deprecated
    default Result<String> canReadAccessData(String model) {
        return canReadableData(model);
    }

    /**
     * @deprecated please using canReadableFields
     */
    @Deprecated
    default Result<List<String>> canReadAccessField(String model) {
        Result<Set<String>> readableFields = canReadableFields(model);
        if (readableFields == null) {
            return null;
        }
        Result<List<String>> result = new Result<List<String>>().setSuccess(readableFields.getSuccess());
        Set<String> data = readableFields.getData();
        if (data != null) {
            result.setData(new ArrayList<>(data));
        }
        return result;
    }

    /**
     * @deprecated please using canWritableFields
     */
    @Deprecated
    default Result<List<String>> canUpdateAccessField(String model) {
        Result<Set<String>> writableFields = canWritableFields(model);
        if (writableFields == null) {
            return null;
        }
        Result<List<String>> result = new Result<List<String>>().setSuccess(writableFields.getSuccess());
        Set<String> data = writableFields.getData();
        if (data != null) {
            result.setData(new ArrayList<>(data));
        }
        return result;
    }

    HoldKeeper<AuthApi> holder = new HoldKeeper<>();

    static AuthApi get() {
        return holder.supply(() -> Spider.getDefaultExtension(AuthApi.class));
    }
}
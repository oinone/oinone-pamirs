package pro.shushi.pamirs.boot.web.action;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import pro.shushi.pamirs.boot.base.model.AppConfig;
import pro.shushi.pamirs.boot.web.enmu.BootUxdExpEnumerate;
import pro.shushi.pamirs.boot.web.extend.AppConfigLoaderExtendApi;
import pro.shushi.pamirs.boot.web.service.AppConfigService;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import javax.annotation.Resource;
import java.util.List;

import static pro.shushi.pamirs.meta.enmu.FunctionOpenEnum.*;

/**
 * 应用配置服务器动作
 * <p>
 * 2022/6/10 3:10 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Base
@Service
@Model.model(AppConfig.MODEL_MODEL)
public class AppConfigAction {

    @Resource
    private AppConfigService appConfigService;

    @Function.Advanced(displayName = "根据条件查询记录列表", type = FunctionTypeEnum.QUERY, managed = true)
    @Function(openLevel = {LOCAL, REMOTE, API})
    public List<AppConfig> queryListByWrapper(IWrapper<AppConfig> queryWrapper) {
        List<AppConfig> appConfigs = Models.origin().queryListByWrapper(queryWrapper);
        List<AppConfigLoaderExtendApi> extendApis = Spider.getLoader(AppConfigLoaderExtendApi.class).getOrderedExtensions();
        if (CollectionUtils.isNotEmpty(extendApis)) {
            for (AppConfigLoaderExtendApi extendApi : extendApis) {
                appConfigs = extendApi.queryAfterProperties(appConfigs);
            }
        }
        return appConfigs;
    }

    @Function.Advanced(displayName = "保存应用配置")
    @Function(openLevel = {FunctionOpenEnum.API})
    public AppConfig save(AppConfig data) {
        try {
            return appConfigService.save(data);
        } catch (PamirsException e) {
            throw e;
        } catch (Exception e) {
            log.error(BootUxdExpEnumerate.BASE_SAVE_APP_CONFIG_ERROR.msg(), e);
            throw PamirsException.construct(BootUxdExpEnumerate.BASE_SAVE_APP_CONFIG_ERROR).errThrow();
        }
    }

}

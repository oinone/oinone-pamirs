package pro.shushi.pamirs.boot.orm.spi;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModelPrepareApi;
import pro.shushi.pamirs.boot.common.spi.api.boot.BootModulesApi;
import pro.shushi.pamirs.boot.orm.configure.BootConfiguration;
import pro.shushi.pamirs.boot.orm.configure.NoCodeModuleConfiguration;
import pro.shushi.pamirs.framework.configure.db.service.ModuleService;
import pro.shushi.pamirs.framework.connectors.data.infrastructure.api.LogicSchemaService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.sql.query.QueryWrapper;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.lambda.LambdaUtil;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.PStringUtils;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;
import pro.shushi.pamirs.meta.enmu.ModuleStateEnum;

import javax.annotation.Resource;
import java.util.*;

/**
 * 启动获取模块列表接口
 * <p>
 * 2020/8/27 5:05 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Slf4j
@Order(88)
@Component
@SPI.Service
public class OrmBootModules implements BootModulesApi {

    @Resource
    private BootConfiguration bootConfiguration;

    @Resource
    private ModuleService moduleService;

    @Override
    public Set<String> modules() {
        return bootConfiguration.getModules();
    }

    @Override
    public Set<String> excludeModules() {
        return bootConfiguration.getExcludeModules();
    }

    @Override
    public Set<String> distributionModules() {
        return bootConfiguration.getDistributionModules();
    }

    @Override
    public Boolean initNoCodeModules() {
        NoCodeModuleConfiguration noCodeModuleConfiguration = bootConfiguration.getNoCodeModule();
        return Optional.ofNullable(noCodeModuleConfiguration).map(NoCodeModuleConfiguration::getInit).orElse(null);
    }

    @Override
    public Set<String> noCodeModules() {
        NoCodeModuleConfiguration noCodeModuleConfiguration = bootConfiguration.getNoCodeModule();
        return Optional.ofNullable(noCodeModuleConfiguration).map(NoCodeModuleConfiguration::getModules).orElse(null);
    }

    @Override
    public Map<String, ModuleDefinition> dataModules(AppLifecycleCommand command) {
        boolean updateModule = command.getOptions().isUpdateModule();
        boolean updateMeta = command.getOptions().isUpdateMeta();
        boolean rebuildTable = command.getOptions().isRebuildTable();
        boolean diffTable = command.getOptions().isDiffTable();

        Map<String/*model*/, String/*simulate model*/> modelMap = new HashMap<>();
        Spider.getDefaultExtension(BootModelPrepareApi.class).prepare(modelMap);

        QueryWrapper<ModuleDefinition> queryWrapper = Pops.<ModuleDefinition>query().from(ModuleDefinition.MODEL_MODEL);
        queryWrapper.eq(
                PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(ModuleDefinition::getSys)), Boolean.FALSE
        );
        queryWrapper.in(
                PStringUtils.fieldName2Column(LambdaUtil.fetchFieldName(ModuleDefinition::getState)), Arrays.asList(ModuleStateEnum.INSTALLED, ModuleStateEnum.TOUPGRADE)
        );

        Map<String, ModuleDefinition> result = moduleService.fetchModuleMapFromDB(modelMap, queryWrapper,
                (modulesDefinition) -> {
                    if (!rebuildTable || !updateModule && !updateMeta) {
                        return;
                    }
                    List<ModelDefinition> modelDefinitions = new ArrayList<>();
                    modelDefinitions.add(modulesDefinition);
                    LogicSchemaService.get().buildTable(modelDefinitions, false, diffTable);
                });
        log.info("dataModules:{}", String.join(",", result.keySet()));
        return result;
    }
}

package pro.shushi.pamirs.boot.standard.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.LifecycleBeginInit;
import pro.shushi.pamirs.boot.common.api.init.LifecycleCompletedInit;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckContext;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentCheckResult;
import pro.shushi.pamirs.boot.standard.entity.EnvironmentKey;
import pro.shushi.pamirs.boot.standard.printer.Printer;
import pro.shushi.pamirs.boot.standard.printer.Slf4jPrinter;
import pro.shushi.pamirs.boot.standard.printer.StdPrinter;
import pro.shushi.pamirs.framework.common.utils.DataShardingHelper;
import pro.shushi.pamirs.framework.configure.simulate.api.MetaSimulateService;
import pro.shushi.pamirs.framework.configure.simulate.service.MetaSimulator;
import pro.shushi.pamirs.framework.connectors.data.dialect.Dialects;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.TableMetaDialectService;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.data.DsApi;
import pro.shushi.pamirs.meta.api.prefix.DataPrefixManager;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.constants.ModuleConstants;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;
import pro.shushi.pamirs.meta.domain.PlatformEnvironmentHistoryRecord;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.module.ModuleDefinition;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 环境保护检查
 *
 * @author Adamancy Zhang at 14:14 on 2024-08-07
 */
@Slf4j
@Order(120)
@Component
public class EnvironmentProtectedChecker implements LifecycleBeginInit, LifecycleCompletedInit {

    @Resource
    private MetaSimulateService metaSimulateService;

    @Autowired
    private EnvironmentHistoryRecordSaver environmentHistoryRecordSaver;

    @Autowired
    private Environment environment;

    private Runnable saver;

    private Runnable clear;

    @Override
    public void process(AppLifecycleCommand command, Set<String> runModules, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        Map<String, PlatformEnvironmentChecker> checkerMap = collectionCheckers();
        EnvironmentCheckContext context = new EnvironmentCheckContext(checkerMap);
        Map<String, List<PlatformEnvironment>> currentEnvironmentMap = collectionCurrentEnvironments(checkerMap.values());
        Map<String, List<PlatformEnvironment>> historyEnvironmentMap = collectionHistoryEnvironments(context);
        Map<String, List<PlatformEnvironment>> recordHistoryEnvironmentMap = new HashMap<>(historyEnvironmentMap);
        if (!context.checkBefore(currentEnvironmentMap, historyEnvironmentMap) && context.isCheckEnvironment()) {
            printCheckMessage(context);
            throw throwInterruptedException();
        }
        List<PlatformEnvironment> environments = new ArrayList<>();
        for (Map.Entry<String, List<PlatformEnvironment>> currentEnvironmentEntry : currentEnvironmentMap.entrySet()) {
            String type = currentEnvironmentEntry.getKey();
            List<PlatformEnvironment> currentEnvironments = currentEnvironmentEntry.getValue();
            List<PlatformEnvironment> historyEnvironments = historyEnvironmentMap.remove(type);
            environments.addAll(context.check(type, currentEnvironments, historyEnvironments));
        }
        environments.addAll(context.checkAfter(environments, currentEnvironmentMap, historyEnvironmentMap));
        boolean isInterrupted = !context.getError().isEmpty() && context.isCheckEnvironment();
        printCheckMessage(context);
        if (isInterrupted) {
            throw throwInterruptedException();
        }
        saver = () -> {
            context.save(environments, currentEnvironmentMap, historyEnvironmentMap);
            saveEnvironmentsRecords(context, environments, recordHistoryEnvironmentMap);
            saveEnvironments(context, environments);
        };
        clear = () -> {
            context.clear();
            environments.clear();
        };
    }

    private UnsupportedOperationException throwInterruptedException() {
        return new UnsupportedOperationException("环境信息检查不通过，请根据以上提示信息进行修改");
    }

    private Map<String, PlatformEnvironmentChecker> collectionCheckers() {
        List<PlatformEnvironmentChecker> checkers = BeanDefinitionUtils.getBeansOfTypeByOrdered(PlatformEnvironmentChecker.class);
        Map<String, PlatformEnvironmentChecker> checkerMap = new HashMap<>(checkers.size());
        for (PlatformEnvironmentChecker checker : checkers) {
            checkerMap.put(checker.type(), checker);
        }
        return checkerMap;
    }

    private Map<String, List<PlatformEnvironment>> collectionCurrentEnvironments(Collection<PlatformEnvironmentChecker> checkers) {
        Map<String, List<PlatformEnvironment>> environments = new HashMap<>(checkers.size());
        for (PlatformEnvironmentChecker checker : checkers) {
            environments.put(checker.type(), checker.collection());
        }
        return environments;
    }

    private Map<String, List<PlatformEnvironment>> collectionHistoryEnvironments(EnvironmentCheckContext context) {
        List<PlatformEnvironment> dbEnvironments = BeanDefinitionUtils.getBean(MetaSimulateService.class)
                .transientStaticExecute(MetaSimulator.simulate(), () -> {
                    String dsKey = DsApi.get().baseDsKey(PlatformEnvironment.MODEL_MODEL);
                    ModelDefinition model = PamirsSession.getContext().getModelConfig(PlatformEnvironment.MODEL_MODEL).getModelDefinition();
                    String tableName = DataPrefixManager.tablePrefix(ModuleConstants.MODULE_BASE, model.getModel(), model.getTable());
                    boolean isTableExist = Dialects.component(TableMetaDialectService.class, dsKey).existTable(dsKey, tableName);
                    if (!isTableExist) {
                        return new ArrayList<>();
                    }
                    return Models.origin().queryListByWrapper(Pops.<PlatformEnvironment>lambdaQuery()
                            .from(PlatformEnvironment.MODEL_MODEL)
                            .ge(PlatformEnvironment::getId, 0));
                });
        Map<String, List<PlatformEnvironment>> environments = new HashMap<>(context.getCheckers().size());
        for (PlatformEnvironment environment : dbEnvironments) {
            EnvironmentKey.Checker checker = context.getKey(environment).getChecker();
            if (checker != null) {
                environment = checker.convert(environment);
            }
            EnvironmentCheckResult deprecated = deprecated(context, environment);
            if (deprecated != null) {
                context.addDelete(deprecated);
                context.addDeprecated(deprecated);
            }
            environments.computeIfAbsent(environment.getType(), k -> new ArrayList<>()).add(environment);
        }
        return environments;
    }

    private EnvironmentCheckResult deprecated(EnvironmentCheckContext context, PlatformEnvironment environment) {
        String type = environment.getType();
        PlatformEnvironmentChecker checker = context.getChecker(type);
        EnvironmentCheckResult deprecated;
        if (checker == null) {
            deprecated = EnvironmentCheckResult.of(context.getKey(environment), environment);
        } else {
            deprecated = checker.deprecated(environment);
        }
        return deprecated;
    }

    private void saveEnvironments(EnvironmentCheckContext context, List<PlatformEnvironment> environments) {
        if (!context.isSaveEnvironments()) {
            return;
        }

        Models.origin().createOrUpdateBatch(environments);

        Set<String> deleteCodes = context.getDelete().values().stream()
                .flatMap(Collection::stream)
                .map(EnvironmentCheckResult::getEnvironment)
                .map(PlatformEnvironment::getCode)
                .collect(Collectors.toSet());
        if (!deleteCodes.isEmpty()) {
            DataShardingHelper.build().collectionSharding(deleteCodes, sublist -> {
                Models.origin().deleteByWrapper(Pops.<PlatformEnvironment>lambdaUpdate()
                        .from(PlatformEnvironment.MODEL_MODEL)
                        .in(PlatformEnvironment::getCode, sublist));
                return sublist;
            });
        }
    }

    private void saveEnvironmentsRecords(EnvironmentCheckContext context, List<PlatformEnvironment> environments, Map<String, List<PlatformEnvironment>> recordHistoryEnvironmentMap) {
        List<PlatformEnvironmentHistoryRecord> createRecords = new ArrayList<>(environmentHistoryRecordSaver.collectionUpdate(context, environments, recordHistoryEnvironmentMap));
        if (!createRecords.isEmpty()) {
            Models.origin().createBatch(createRecords);
        }
    }

    private void printCheckMessage(EnvironmentCheckContext context) {
        printCheckMessage("错误的环境信息", context.getError().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::error), StdPrinter.INSTANCE);
        printCheckMessage("过时的环境信息", context.getDeprecated().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::warn), StdPrinter.INSTANCE);
        printCheckMessage("警告的环境信息", context.getWarning().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::warn), StdPrinter.INSTANCE);

        printCheckMessage("创建的环境信息", context.getCreate().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::info), StdPrinter.INSTANCE);
        printCheckMessage("更新的环境信息", context.getUpdate().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::info), StdPrinter.INSTANCE);
        printCheckMessage("删除的环境信息", context.getDelete().values().stream().flatMap(Collection::stream).collect(Collectors.toList()), new Slf4jPrinter(log, Logger::info), StdPrinter.INSTANCE);
    }

    private void printCheckMessage(String title, List<EnvironmentCheckResult> results, Printer... printers) {
        if (results.isEmpty()) {
            return;
        }
        for (Printer printer : printers) {
            if (!printer.isEnabled()) {
                continue;
            }
            printer.println("**************************************************");
            printer.println("");
            printer.println(title);
            printer.println("");
            for (EnvironmentCheckResult result : results) {
                PlatformEnvironment environment = result.getEnvironment();
                String message = result.getMessage();
                if (StringUtils.isBlank(message)) {
                    printer.println("{} = {}", environment.getKey(), environment.getValue());
                } else {
                    printer.println("{} = {}; tip: {}", environment.getKey(), environment.getValue(), message);
                }
            }
            printer.println("");
            printer.println("**************************************************");
            break;
        }
    }

    private void clear() {
        if (clear != null) {
            clear.run();
        }
        saver = null;
        clear = null;
        EnvironmentKey.clear();
    }

    @Override
    public void process(AppLifecycleCommand command, List<ModuleDefinition> installModules, List<ModuleDefinition> upgradeModules, List<ModuleDefinition> reloadModules) {
        if (saver != null) {
            saver.run();
        }
        clear();
    }
}

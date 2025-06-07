package pro.shushi.pamirs.boot.standard.entity;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.boot.standard.checker.PlatformEnvironmentChecker;
import pro.shushi.pamirs.boot.standard.config.EnvironmentProtectedConfig;
import pro.shushi.pamirs.meta.api.session.cache.spi.SessionFillOwnSignApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.*;

/**
 * 环境检查上下文
 *
 * @author Adamancy Zhang at 11:06 on 2024-10-12
 */
public class EnvironmentCheckContext {

    /**
     * 环境检查器
     */
    private final Map<String, PlatformEnvironmentChecker> checkers;

    /**
     * 是否启用环境检查
     */
    private final boolean isCheckEnvironment;

    /**
     * 是否不可变
     */
    private final boolean isImmutableEnvironment;

    /**
     * 是否为协同开发环境
     */
    private final boolean isCollaborativeDevelopmentEnvironment;

    /**
     * 是否保存当前环境信息
     */
    private final boolean isSaveEnvironments;

    /**
     * 新创建的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> create = new HashMap<>();

    /**
     * 需要更新的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> update = new HashMap<>();

    /**
     * 需要删除的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> delete = new HashMap<>();

    /**
     * 配置错误的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> error = new HashMap<>();

    /**
     * 需要提示警告的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> warning = new HashMap<>();

    /**
     * 需要提示过期的环境信息
     */
    private final Map<String, Set<EnvironmentCheckResult>> deprecated = new HashMap<>();

    public EnvironmentCheckContext(Map<String, PlatformEnvironmentChecker> checkers) {
        this.checkers = checkers;
        this.isCheckEnvironment = EnvironmentProtectedConfig.isEnabled();
        this.isImmutableEnvironment = EnvironmentProtectedConfig.isEnabled();
        this.isCollaborativeDevelopmentEnvironment = Spider.getDefaultExtension(SessionFillOwnSignApi.class).handleOwnSign();
        this.isSaveEnvironments = EnvironmentProtectedConfig.isSaveEnvironments() && !this.isCollaborativeDevelopmentEnvironment;
    }

    public Map<String, PlatformEnvironmentChecker> getCheckers() {
        return checkers;
    }

    public PlatformEnvironmentChecker getChecker(String type) {
        return checkers.get(type);
    }

    public List<PlatformEnvironment> check(String type, List<PlatformEnvironment> currentEnvironments, List<PlatformEnvironment> historyEnvironments) {
        return getChecker(type).check(this, currentEnvironments, historyEnvironments);
    }

    public boolean checkBefore(Map<String, List<PlatformEnvironment>> currentEnvironmentMap, Map<String, List<PlatformEnvironment>> historyEnvironmentMap) {
        for (PlatformEnvironmentChecker checker : this.checkers.values()) {
            String type = checker.type();
            List<PlatformEnvironment> currentEnvironments = currentEnvironmentMap.get(type);
            if (currentEnvironments == null) {
                currentEnvironments = new ArrayList<>();
            }
            List<PlatformEnvironment> historyEnvironments = historyEnvironmentMap.get(type);
            if (historyEnvironments == null) {
                historyEnvironments = Collections.emptyList();
            }
            if (!checker.checkBefore(this, currentEnvironments, historyEnvironments)) {
                return false;
            }
            currentEnvironmentMap.putIfAbsent(type, currentEnvironments);
        }
        return true;
    }

    public List<PlatformEnvironment> checkAfter(List<PlatformEnvironment> allEnvironments, Map<String, List<PlatformEnvironment>> currentEnvironmentMap, Map<String, List<PlatformEnvironment>> historyEnvironmentMap) {
        List<PlatformEnvironment> finalEnvironments = new ArrayList<>();
        for (PlatformEnvironmentChecker checker : this.checkers.values()) {
            String type = checker.type();
            List<PlatformEnvironment> currentEnvironments = currentEnvironmentMap.get(type);
            if (currentEnvironments == null) {
                currentEnvironments = Collections.emptyList();
            }
            List<PlatformEnvironment> historyEnvironments = historyEnvironmentMap.get(type);
            if (historyEnvironments == null) {
                historyEnvironments = Collections.emptyList();
            }
            List<PlatformEnvironment> checkAfterEnvironments = checker.checkAfter(this, allEnvironments, currentEnvironments, historyEnvironments);
            if (CollectionUtils.isNotEmpty(checkAfterEnvironments)) {
                finalEnvironments.addAll(checkAfterEnvironments);
            }
        }
        return finalEnvironments;
    }

    public void save(List<PlatformEnvironment> allEnvironments, Map<String, List<PlatformEnvironment>> currentEnvironmentMap, Map<String, List<PlatformEnvironment>> historyEnvironmentMap) {
        for (PlatformEnvironmentChecker checker : this.checkers.values()) {
            List<PlatformEnvironment> currentEnvironments = currentEnvironmentMap.get(checker.type());
            if (currentEnvironments == null) {
                currentEnvironments = Collections.emptyList();
            }
            List<PlatformEnvironment> historyEnvironments = historyEnvironmentMap.get(checker.type());
            if (historyEnvironments == null) {
                historyEnvironments = Collections.emptyList();
            }
            checker.save(this, allEnvironments, currentEnvironments, historyEnvironments);
        }
    }

    public boolean isCheckEnvironment() {
        return isCheckEnvironment;
    }

    public boolean isImmutableEnvironment() {
        return isImmutableEnvironment;
    }

    public boolean isCollaborativeDevelopmentEnvironment() {
        return isCollaborativeDevelopmentEnvironment;
    }

    public boolean isSaveEnvironments() {
        return isSaveEnvironments;
    }

    public EnvironmentKey getKey(PlatformEnvironment environment) {
        EnvironmentKey environmentKey = EnvironmentKey.of(environment.getKey());
        if (EnvironmentKey.Level.NONE.equals(environmentKey.getLevel())) {
            PlatformEnvironmentChecker checker = this.checkers.get(environment.getType());
            if (checker == null) {
                return environmentKey;
            }
            environmentKey = Optional.ofNullable(checker.getKey(environment)).orElse(environmentKey);
        }
        return environmentKey;
    }

    public Map<String, Set<EnvironmentCheckResult>> getCreate() {
        return create;
    }

    public void addCreate(PlatformEnvironment environment) {
        addCreate(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addCreate(EnvironmentCheckResult result) {
        create.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public Map<String, Set<EnvironmentCheckResult>> getUpdate() {
        return update;
    }

    public void addUpdate(PlatformEnvironment environment) {
        addUpdate(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addUpdate(EnvironmentCheckResult result) {
        update.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public Map<String, Set<EnvironmentCheckResult>> getDelete() {
        return delete;
    }

    public void addDelete(PlatformEnvironment environment) {
        addDelete(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addDelete(EnvironmentCheckResult result) {
        delete.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public Map<String, Set<EnvironmentCheckResult>> getError() {
        return error;
    }

    public void addError(PlatformEnvironment environment) {
        addError(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addError(PlatformEnvironment environment, String message) {
        addError(EnvironmentCheckResult.of(getKey(environment), environment, message));
    }

    public void addError(EnvironmentCheckResult result) {
        error.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public Map<String, Set<EnvironmentCheckResult>> getWarning() {
        return warning;
    }

    public void addWarning(PlatformEnvironment environment) {
        addWarning(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addWarning(PlatformEnvironment environment, String message) {
        addWarning(EnvironmentCheckResult.of(getKey(environment), environment, message));
    }

    public void addWarning(EnvironmentCheckResult result) {
        warning.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public Map<String, Set<EnvironmentCheckResult>> getDeprecated() {
        return deprecated;
    }

    public void addDeprecated(PlatformEnvironment environment) {
        addDeprecated(EnvironmentCheckResult.of(getKey(environment), environment));
    }

    public void addDeprecated(PlatformEnvironment environment, String message) {
        addDeprecated(EnvironmentCheckResult.of(getKey(environment), environment, message));
    }

    public void addDeprecated(EnvironmentCheckResult result) {
        deprecated.computeIfAbsent(result.getEnvironment().getType(), k -> new LinkedHashSet<>()).add(result);
    }

    public void addContext(PlatformEnvironment environment) {
        EnvironmentKey environmentKey = getKey(environment);
        switch (environmentKey.getLevel()) {
            case ERROR:
                addError(environment);
                break;
            case WARNING:
                addWarning(environment);
                break;
            case DEPRECATED:
                addDeprecated(environment);
                break;
        }
    }

    public void clear() {
        checkers.clear();
        create.clear();
        update.clear();
        delete.clear();
        error.clear();
        warning.clear();
        deprecated.clear();
    }
}

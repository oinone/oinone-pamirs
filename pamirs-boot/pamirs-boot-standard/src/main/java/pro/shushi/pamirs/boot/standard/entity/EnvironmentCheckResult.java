package pro.shushi.pamirs.boot.standard.entity;

import pro.shushi.pamirs.meta.domain.PlatformEnvironment;

import java.util.Objects;

/**
 * 环境检查结果
 *
 * @author Adamancy Zhang at 11:11 on 2024-10-12
 */
public class EnvironmentCheckResult {

    private final EnvironmentKey key;

    private final PlatformEnvironment environment;

    private final String message;

    private EnvironmentCheckResult(EnvironmentKey key, PlatformEnvironment environment, String message) {
        assert environment != null : "Invalid environment.";
        this.key = key;
        this.environment = environment;
        this.message = message;
    }

    public static EnvironmentCheckResult of(EnvironmentKey key, PlatformEnvironment environment) {
        return of(key, environment, key.getMessage());
    }

    public static EnvironmentCheckResult of(EnvironmentKey key, PlatformEnvironment environment, String message) {
        return new EnvironmentCheckResult(key, environment, message);
    }

    public EnvironmentKey getKey() {
        return key;
    }

    public PlatformEnvironment getEnvironment() {
        return environment;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnvironmentCheckResult)) {
            return false;
        }
        EnvironmentCheckResult that = (EnvironmentCheckResult) o;
        return environment.getCode().equals(that.environment.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(environment.getCode());
    }
}

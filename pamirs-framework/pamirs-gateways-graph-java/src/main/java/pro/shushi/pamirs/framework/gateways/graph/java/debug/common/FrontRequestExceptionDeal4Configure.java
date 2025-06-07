package pro.shushi.pamirs.framework.gateways.graph.java.debug.common;

import com.alibaba.fastjson.serializer.SerializerFeature;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.DataFetcherExceptionHandlerResult;
import graphql.execution.ExecutionPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.common.entry.SimplePackageVersion;
import pro.shushi.pamirs.framework.common.utils.kryo.KryoUtils;
import pro.shushi.pamirs.framework.gateways.graph.error.ClientGraphQLError;
import pro.shushi.pamirs.framework.gateways.graph.java.constants.StackTraceConstants;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestExceptionDeal;
import pro.shushi.pamirs.framework.gateways.graph.java.debug.FrontRequestResultDeal;
import pro.shushi.pamirs.meta.annotation.fun.Data;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.K2;
import pro.shushi.pamirs.meta.util.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 异常时增加应用配置信息返回
 *
 * @author cpc@shushi.pro
 * @version 1.0.0
 * date 2024/4/3 2:41 下午
 */
@Component
@ConfigurationProperties(prefix = "pamirs.debug")
@Data
public class FrontRequestExceptionDeal4Configure implements FrontRequestExceptionDeal, FrontRequestResultDeal {

    private static final String[] DEFAULT_CONFIGURES = {
            // spring
            "spring.application.name",

            // server
            "server.address",
            "server.port",
            "server.sessionTimeout",
            "server.tomcat.max-threads",

            // redis
            "spring.redis.host",
            "spring.redis.port",
            "spring.redis.timeout",
            "spring.redis.database",

            // rocketmq
            "spring.rocketmq.name-server",

            // zookeeper
            "pamirs.zookeeper.zkConnectString",
            "pamirs.zookeeper.zkSessionTimeout",
            "pamirs.zookeeper.rootPath",

            // dubbo
            "dubbo.application.name",
            "dubbo.application.version",
            "dubbo.registry.address",
            "dubbo.registry.group",
            "dubbo.registry.timeout",
            "dubbo.protocol.name",
            "dubbo.protocol.host",
            "dubbo.protocol.port",
            "dubbo.protocol.serialization",
            "dubbo.protocol.payload",
            "dubbo.provider.group",
            "dubbo.provider.timeout",
            "dubbo.consumer.group",
            "dubbo.consumer.timeout",

            // nacos
            "spring.cloud.nacos.server-addr",
            "spring.cloud.nacos.discovery.enabled",
            "spring.cloud.nacos.discovery.namespace",
            "spring.cloud.nacos.discovery.prefix",
            "spring.cloud.nacos.discovery.file-extension",
            "spring.cloud.nacos.config.enabled",
            "spring.cloud.nacos.config.namespace",
            "spring.cloud.nacos.config.prefix",
            "spring.cloud.nacos.config.file-extension",

            // es
            "pamirs.elastic.url",

            // pamirs-framework
            "pamirs.framework.system.system-ds-key",
            "pamirs.framework.data.default-ds-key",
            "pamirs.framework.data.ds-map",
            "pamirs.framework.gateway.statistics",
            "pamirs.framework.gateway.show-doc",
            "pamirs.framework.gateway.async",
            "pamirs.framework.hook.excludes",
            "pamirs.framework.extpoint.excludes",
            "pamirs.meta.relation.pk-id",

            // distribution
            "pamirs.distribution.session.allMetaRefresh",
            "pamirs.distribution.session.ownSign",
            "pamirs.distribution.service.serviceScope",

            // pamirs-boot
            "pamirs.boot.init",
            "pamirs.boot.sync",
            "pamirs.boot.mode",
            "pamirs.boot.modules",

            // pamirs-event
            "pamirs.event.enabled",
            "pamirs.event.schedule.ownSign",
            "pamirs.event.schedule.enabled",

            // pamirs-auth
            "pamirs.auth.fun-filter",
            "pamirs.auth.fun-filter-only-login",

            // pamirs-channel
            "pamirs.channel.zkServers",

            // pamirs-eip
            "pamirs.eip.enabled",
            "pamirs.eip.open-api.enabled",
            "pamirs.eip.open-api.test",
            "pamirs.eip.open-api.route.host",
            "pamirs.eip.open-api.route.port",
            "pamirs.eip.open-api.route.aes-key",
            "pamirs.eip.open-api.route.expires",
            "pamirs.eip.open-api.route.delay-expires"
    };

    private String[] configures;

    @Autowired
    private Environment environment;

    @Override
    public void stackTrace(DataFetcherExceptionHandlerParameters handlerParameters, DataFetcherExceptionHandlerResult result, Throwable exception, ExecutionPath path) {
        Map<String, Object> msg = traceConfigure();
        if (msg.isEmpty()) {
            return;
        }
        result.getErrors().add(ClientGraphQLError.build(StackTraceConstants.STACKTRACE_CONFIGURE, JsonUtils.toJSONString(msg,
                SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.BrowserCompatible)));
    }

    @Override
    public void stackTrace(ExecutionResult executionResult, ExecutionInput executionInput) {
        Map<String, Object> msg = traceConfigure();
        if (msg.isEmpty()) {
            return;
        }
        addDebugInfo(executionResult, ClientGraphQLError.build(StackTraceConstants.STACKTRACE_CONFIGURE, JsonUtils.toJSONString(msg,
                SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.BrowserCompatible)));
    }

    private Map<String, Object> traceConfigure() {
        Map<String, Object> msg = new LinkedHashMap<>();

        // 非超级管理员无法获取环境信息
        if (!Boolean.TRUE.equals(PamirsSession.isAdmin())) {
            putPackageVersions(msg);
            return msg;
        }

        for (String configure : DEFAULT_CONFIGURES) {
            msg.put(configure, environment.getProperty(configure));
        }

        String[] configures = getConfigures();
        if (configures != null && configures.length > 0) {
            for (String configure : configures) {
                msg.put(configure, environment.getProperty(configure));
            }
        }

        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length > 0) {
            msg.put("activeProfiles", activeProfiles);
            for (String active : activeProfiles) {
                // 研发、测试环境才返回
                if ("dev".equals(active) || "test".equals(active)) {
                    Map<String, Object> systemProperties = KryoUtils.get().copy(((AbstractEnvironment) environment).getSystemProperties());
                    systemProperties.remove("java.ext.dirs");
                    systemProperties.remove("java.class.path");
                    systemProperties.remove("sun.boot.class.path");
                    systemProperties.remove("java.library.path");
                    systemProperties.remove("sun.boot.library.path");
                    msg.put("systemProperties", systemProperties);
                }
            }
        }

        putPackageVersions(msg);

        return msg;
    }

    @Override
    public int priority() {
        return 403;
    }

    private void putPackageVersions(Map<String, Object> msg) {
        putPackageVersion(msg, K2.class);
        putPackageVersion(msg, SimplePackageVersion.class);
        putPackageVersion(msg, FrontRequestExceptionDeal4Configure.class);
        putPackageVersion(msg, "pro.shushi.pamirs.boot.common.api.PamirsBootMainProcessApi");
        putPackageVersion(msg, "pro.shushi.pamirs.core.common.CommonModule");

        putPackageVersion(msg, "pro.shushi.pamirs.framework.faas.distribution.computer.RemoteComputer");
        putPackageVersion(msg, "pro.shushi.pamirs.distribution.gateway.spi.DistributionGraphQLSchemaApi");
        putPackageVersion(msg, "pro.shushi.pamirs.distribution.session.init.DistributionLifecycleCompletedAllInit");
        putPackageVersion(msg, "pro.shushi.pamirs.distribution.session.cd.spi.CdDistributionSessionFillOwnSignApi");
    }

    private void putPackageVersion(Map<String, Object> map, String className) {
        try {
            putPackageVersion(map, Class.forName(className));
        } catch (Throwable ignored) {
        }
    }

    private void putPackageVersion(Map<String, Object> map, Class<?> clazz) {
        SimplePackageVersion packageVersion = SimplePackageVersion.getPackageVersion(clazz);
        if (packageVersion == null) {
            return;
        }
        map.put(packageVersion.getImplementationTitle(), packageVersion.getImplementationVersion());
    }
}
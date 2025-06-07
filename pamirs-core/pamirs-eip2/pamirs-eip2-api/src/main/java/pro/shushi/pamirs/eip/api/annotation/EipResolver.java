package pro.shushi.pamirs.eip.api.annotation;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.model.EipIntegrationInterface;
import pro.shushi.pamirs.eip.api.model.EipOpenInterface;
import pro.shushi.pamirs.eip.api.service.EipService;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spring.BeanDefinitionUtils;
import pro.shushi.pamirs.meta.domain.fun.Argument;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EipResolver {

    public static void resolver(String module, IEipAnnotationSingletonConfig eipAnnotationSingletonConfig) {
        List<Function> functions = new ArrayList<>();
        for (Function f : PamirsSession.getContext().getStandaloneFunCache().getAll().values()) {
            if (f.getModule().equals(module)) {
                functions.add(f);
            }
        }
        List<EipOpenInterface> openInterfaces = new ArrayList<>();
        List<EipIntegrationInterface> integrationInterfaces = new ArrayList<>();
        for (Function function : functions) {
            if (function.getFunctionDefinition() == null || StringUtils.isAnyBlank(function.getFunctionDefinition().getClazz(), function.getFunctionDefinition().getMethod())) {
                continue;
            }
            Method method = null;
            try {
                Class<?>[] argClass = new Class[0];
                if (CollectionUtils.isNotEmpty(function.getFunctionDefinition().getArgumentList())) {
                    argClass = new Class[function.getFunctionDefinition().getArgumentList().size()];
                    int i = 0;
                    for (Argument argument : function.getFunctionDefinition().getArgumentList()) {
                        argClass[i] = Class.forName(argument.getLtype());
                        i++;
                    }
                }
                method = ReflectionUtils.findMethod(Class.forName(function.getFunctionDefinition().getClazz()), function.getFunctionDefinition().getMethod(), argClass);
            } catch (ClassNotFoundException e) {
                throw PamirsException.construct(EipExpEnumerate.SYSTEM_ERROR, e).errThrow();
            }
            EipOpenInterface eipOpenInterface = EipOpenConvert.convert(function, method, eipAnnotationSingletonConfig);
            if (eipOpenInterface != null) {
                openInterfaces.add(eipOpenInterface);
            }
            EipIntegrationInterface eipIntegrationInterface = EipIntegrateConvert.convert(function, method, eipAnnotationSingletonConfig);
            if (eipIntegrationInterface != null) {
                integrationInterfaces.add(eipIntegrationInterface);
            }

        }
        Models.data().createOrUpdateBatch(openInterfaces);
        EipService eipService = BeanDefinitionUtils.getBean(EipService.class);
        for (EipOpenInterface eipOpenInterface : openInterfaces) {
            eipService.registerOpenInterface(eipOpenInterface);
        }
        Models.data().createOrUpdateBatch(integrationInterfaces);
        for (EipIntegrationInterface eipIntegrationInterface : integrationInterfaces) {
            eipService.registerInterface(eipIntegrationInterface);
        }
    }
}

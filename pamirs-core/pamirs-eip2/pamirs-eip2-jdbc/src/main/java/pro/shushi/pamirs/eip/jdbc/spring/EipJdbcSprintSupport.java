package pro.shushi.pamirs.eip.jdbc.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.eip.jdbc.service.EipSQLChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EIP JDBC Spring Supported
 *
 * @author Adamancy Zhang at 17:40 on 2024-06-06
 */
@Component
public class EipJdbcSprintSupport implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        EipJdbcSprintSupport.applicationContext = applicationContext;
    }

    public static List<EipSQLChecker> getSQLCheckers() {
        if (applicationContext == null) {
            return null;
        }
        Map<String, EipSQLChecker> writerMap = applicationContext.getBeansOfType(EipSQLChecker.class);
        List<EipSQLChecker> writers = new ArrayList<>(writerMap.values());
        AnnotationAwareOrderComparator.sort(writers);
        return writers;
    }
}

package pro.shushi.pamirs.framework.connectors.data.tx.parser;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import pro.shushi.pamirs.framework.connectors.data.tx.transaction.PamirsTransactionTemplate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 事务配置解析
 * <p>
 * 2020/7/7 4:34 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class TxAttributeParser {

    public static void parseTransactionAnnotation(PamirsTransactionTemplate template, Annotation annotation) {
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(annotation, false, false);
        parseTransactionAnnotation(template, attributes);
    }

    public static void parseTransactionAnnotation(PamirsTransactionTemplate template, AnnotationAttributes attributes) {
        Object propagationValue = attributes.get("propagation");
        Propagation propagation;
        if (propagationValue instanceof Number) {
            propagation = propagation(attributes.getNumber("propagation"));
        } else {
            propagation = (Propagation) propagationValue;
        }
        template.setPropagationBehavior(propagation.value());
        Object isolationValue = attributes.get("isolation");
        Isolation isolation;
        if (isolationValue instanceof Number) {
            isolation = isolation(attributes.getNumber("isolation"));
        } else {
            isolation = (Isolation) isolationValue;
        }
        template.setIsolationLevel(isolation.value());
        template.setTimeout(attributes.getNumber("timeout").intValue());
        template.setReadOnly(attributes.getBoolean("readOnly"));
    }

    public static void parseTransactionAnnotation(PamirsTransactionTemplate template, Map<String, Object> attributeMap) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);
        parseTransactionAnnotation(template, attributes);
    }

    public static TransactionAttribute parseAnnotationRule(Annotation annotation) {
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(annotation, false, false);
        return parseAnnotationRule(attributes);
    }

    public static TransactionAttribute parseAnnotationRule(Map<String, Object> attributeMap) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);
        return parseAnnotationRule(attributes);
    }

    public static TransactionAttribute parseAnnotationRule(AnnotationAttributes attributes) {
        RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
        List<RollbackRuleAttribute> rollBackRules = new ArrayList<>();
        Class<?>[] rbf = attributes.getClassArray("rollbackFor");
        for (Class<?> rbRule : rbf) {
            RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
            rollBackRules.add(rule);
        }
        String[] rbfc = attributes.getStringArray("rollbackForClassName");
        for (String rbRule : rbfc) {
            RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
            rollBackRules.add(rule);
        }
        Class<?>[] nrbf = attributes.getClassArray("noRollbackFor");
        for (Class<?> rbRule : nrbf) {
            NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
            rollBackRules.add(rule);
        }
        String[] nrbfc = attributes.getStringArray("noRollbackForClassName");
        for (String rbRule : nrbfc) {
            NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
            rollBackRules.add(rule);
        }
        rbta.getRollbackRules().addAll(rollBackRules);
        return rbta;
    }

    public static Propagation propagation(Integer value) {
        for (Propagation propagation : Propagation.values()) {
            if (propagation.value() == value) {
                return propagation;
            }
        }
        return Propagation.REQUIRED;
    }

    public static Isolation isolation(Integer value) {
        for (Isolation isolation : Isolation.values()) {
            if (isolation.value() == value) {
                return isolation;
            }
        }
        return Isolation.DEFAULT;
    }

}

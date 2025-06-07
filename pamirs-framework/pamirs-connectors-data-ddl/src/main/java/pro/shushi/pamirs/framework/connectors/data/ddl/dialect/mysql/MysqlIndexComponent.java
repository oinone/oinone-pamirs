package pro.shushi.pamirs.framework.connectors.data.ddl.dialect.mysql;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.api.domain.model.logic.LogicIndex;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.FieldWrapper;
import pro.shushi.pamirs.framework.connectors.data.api.domain.wrapper.ModelWrapper;
import pro.shushi.pamirs.framework.connectors.data.ddl.dialect.api.IndexDialectComponent;
import pro.shushi.pamirs.framework.connectors.data.dialect.api.Dialect;
import pro.shushi.pamirs.framework.connectors.data.dialect.constants.DataProductVersion;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 索引操作组件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@Slf4j
@Dialect.component
@SPI.Service(DataProductVersion.PRODUCT_MYSQL)
@Component
public class MysqlIndexComponent implements IndexDialectComponent {

    @Override
    public String autoIncrement(ModelWrapper modelDefinition, FieldWrapper modelField, String columnDefinition, String keyGenerator) {
        String fieldKeyGenerator = Optional.ofNullable(modelField.getKeyGenerator())
                .filter(s -> !s.equals(KeyGeneratorEnum.NON.value())).orElse(null);
        if (modelField.getIsPrimaryKey()) {
            columnDefinition = fillNotNull(columnDefinition);
            if (StringUtils.isNotBlank(fieldKeyGenerator) && fieldKeyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value())) {
                columnDefinition = columnDefinition + " AUTO_INCREMENT";
            } else if (1 == modelDefinition.getPk().size()) {
                if (StringUtils.isNotBlank(keyGenerator) && keyGenerator.equals(KeyGeneratorEnum.AUTO_INCREMENT.value())) {
                    columnDefinition = columnDefinition + " AUTO_INCREMENT";
                }
            } else {
                if (null == modelDefinition.getIsRelationship() || !modelDefinition.getIsRelationship()) {
                    log.warn(MessageFormat.format(
                            "复合主键不会适用全局配置keyGenerator，请在主键字段上明确配置,model:{0},field:{1}",
                            modelDefinition.getModel(), modelField.getField())
                    );
                }
                return columnDefinition.replace(" AUTO_INCREMENT", CharacterConstants.SEPARATOR_EMPTY);
            }
        } else {
            if (columnDefinition.contains("AUTO_INCREMENT")) {
                log.error(MessageFormat.format(
                        "AUTO_INCREMENT must only configure for primary key,model:{0},field:{1}",
                        modelDefinition.getModel(), modelField.getField())
                );
                return columnDefinition.replace(" AUTO_INCREMENT", CharacterConstants.SEPARATOR_EMPTY);
            }
        }
        return columnDefinition;
    }

    @Override
    public String generatePrimaryColumn(List<String> pkColumns, boolean addQuote, LogicIndex logicIndex) {
        Set<String> origin = new HashSet<>(logicIndex.getColumn());
        Set<String> target = new HashSet<>(pkColumns);
        if (Sets.difference(origin, target).isEmpty() && Sets.difference(target, origin).isEmpty()) {
            return generatePrimaryColumn(logicIndex.getColumn(), addQuote);
        }
        return generatePrimaryColumn(pkColumns, addQuote);
    }

    private String fillNotNull(String columnDefinition) {
        if (!columnDefinition.contains("NOT NULL") && !columnDefinition.contains("not null")) {
            return columnDefinition + " NOT NULL";
        }
        return columnDefinition;
    }

}

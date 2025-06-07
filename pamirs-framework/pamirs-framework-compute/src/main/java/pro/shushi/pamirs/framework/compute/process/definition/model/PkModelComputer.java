package pro.shushi.pamirs.framework.compute.process.definition.model;

import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.compute.definition.ModelComputer;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.dto.meta.Meta;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.KeyGeneratorEnum;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pro.shushi.pamirs.framework.compute.emnu.ComputeExpEnumerate.BASE_ONLY_ONE_AUTO_INCREMENT_ALLOWED;
import static pro.shushi.pamirs.framework.compute.process.definition.model.PkModelComputer.SPI_NAME;

/**
 * 主键计算
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/2/19 2:16 上午
 */
@SPI.Service(SPI_NAME)
public class PkModelComputer implements ModelComputer<Meta, ModelDefinition> {

    public static final String SPI_NAME = "pk";

    @Override
    public Result<Void> compute(ComputeContext context, Meta meta, String model, ModelDefinition data, Map<String, Object> computeContext) {
        Result<Void> result = new Result<>();
        if (data.isMetaCompleted()) {
            return result;
        }
        // 计算pk
        boolean generateFromField = null != data.getSystemSource() && SystemSourceEnum.RELATION.equals(data.getSystemSource());
        if (generateFromField) {
            int autoIncrementPKNum = 0;
            for (String pk : data.getPk()) {
                ModelField pkField = data.fetchModelField(pk);
                if (null != pkField) {
                    autoIncrementPKNum += KeyGeneratorEnum.AUTO_INCREMENT.equals(pkField.getKeyGenerator()) ? 1 : 0;
                }
            }
            if (autoIncrementPKNum > 1) {
                throw PamirsException.construct(BASE_ONLY_ONE_AUTO_INCREMENT_ALLOWED).errThrow();
            }
        } else {
            computePk(data);
        }
        return result;
    }

    private void computePk(ModelDefinition data) {
        int autoIncrementPKNum = 0;
        if (ModelTypeEnum.TRANSIENT.equals(data.getType())) {
            data.unsetPk();
        } else {
            List<ModelField> pkFieldList = data.getModelFields().stream()
                    .filter(v -> !v.isMetaCompleted() && v.getPk())
                    .collect(Collectors.toList());
            if (pkFieldList.isEmpty()) {
                data.unsetPk();
            } else {
                pkFieldList.sort((a, b) -> {
                    int order = Comparator.comparing(ModelField::getPkIndex).compare(a, b);
                    if (order == 0) {
                        order = Comparator.comparing(ModelField::getField).compare(a, b);
                    }
                    return order;
                });
                for (ModelField pkField : pkFieldList) {
                    autoIncrementPKNum += KeyGeneratorEnum.AUTO_INCREMENT.equals(pkField.getKeyGenerator()) ? 1 : 0;
                }
                data.setPk(pkFieldList.stream().map(ModelField::getField).collect(Collectors.toList()));
            }
        }
        if (autoIncrementPKNum > 1) {
            throw PamirsException.construct(BASE_ONLY_ONE_AUTO_INCREMENT_ALLOWED).errThrow();
        }
    }
}

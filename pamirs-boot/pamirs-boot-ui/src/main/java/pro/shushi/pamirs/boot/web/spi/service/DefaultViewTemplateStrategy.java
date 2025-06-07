package pro.shushi.pamirs.boot.web.spi.service;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.boot.web.spi.api.ViewTemplateStrategyApi;
import pro.shushi.pamirs.boot.web.spi.domain.RegisterSearchWidget;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.domain.model.ModelDefinition;
import pro.shushi.pamirs.meta.domain.model.ModelField;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;

@Component
@Order
@SPI.Service
public class DefaultViewTemplateStrategy implements ViewTemplateStrategyApi {

    /**
     * 实现默认搜索栏字段策略. 返回null，则该字段不作为搜索字段
     *
     * @param modelDefinition
     * @param modelField
     * @return
     */
    @Override
    public RegisterSearchWidget computeSearchWidget(ModelDefinition modelDefinition, ModelField modelField) {
        TtypeEnum ttype = modelField.getTtype();
        // 是否能成为搜索字段
        boolean canSearch = isCanSearch(modelDefinition, modelField, ttype);
        if (!canSearch) {
            return null;
        }

        return searchWidgetByTtype(ttype.name());
    }

    /**
     * 可以对每种TtypeEnum返回对应的RegisterSearchWidget(自定搜索的Widget)
     *
     * @param ttype
     * @return
     */
    private RegisterSearchWidget searchWidgetByTtype(String ttype) {
        switch (ttype) {
            case "INTEGER":
            case "BOOLEAN":
            case "STRING":
            case "ENUM":
            case "DATETIME":
            case "YEAR":
            case "DATE":
            case "TIME":
            case "UID":
            case "PHONE":
            case "EMAIL":
                return RegisterSearchWidget.ofDefault();
            case "BINARY":
            case "FLOAT":
            case "TEXT":
            case "HTML":
            case "MONEY":
            case "MAP":
            case "OBJ":
            case "VOID":
                return null;
            default:
                return RegisterSearchWidget.ofDefault();
        }
    }

    //1、如果ttype为related，不填充。
    //2、如果ttype为relationType，store = false不填充，(relationStore = true && referenceFields.length)填充。
    //3、store = false不填充。
    private static Boolean isCanSearch(ModelDefinition modelDefinition, ModelField modelField, TtypeEnum ttype) {
        // 是否能成为搜索字段
        if (TtypeEnum.isRelatedType(ttype.value())) {
            return Boolean.FALSE;
        }
        if (!modelField.getStore()) {
            return Boolean.FALSE;
        }

        if (modelField.getInvisible() != null && modelField.getInvisible()) {
            return Boolean.FALSE;
        }

        Boolean canSearch = Boolean.FALSE;
        if (TtypeEnum.isRelationType(ttype)) {
            if (ModelTypeEnum.PROXY.equals(modelDefinition.getType())) {
                canSearch = CollectionUtils.isNotEmpty(modelField.getRelationFields());
            } else {
                canSearch = modelField.getRelationStore() && CollectionUtils.isNotEmpty(modelField.getRelationFields());
            }
        } else {
            if (TtypeEnum.STRING.equals(ttype) && modelField.getLtype().equals("java.util.List")) {
                canSearch = Boolean.FALSE;
            } else {
                canSearch = modelField.getStore();
            }
        }

        return canSearch;
    }

}

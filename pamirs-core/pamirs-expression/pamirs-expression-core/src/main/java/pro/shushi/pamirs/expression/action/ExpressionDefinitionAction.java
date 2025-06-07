package pro.shushi.pamirs.expression.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.expression.api.ExpressionDefinitionService;
import pro.shushi.pamirs.expression.model.ExpressionDefine;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.ViewTypeEnum;

/**
 * DesignerExpressionDefinition
 */
@Base
@Component
@Model.model(ExpressionDefine.MODEL_MODEL)
public class ExpressionDefinitionAction {

    @Autowired
    private ExpressionDefinitionService expressionDefinitionService;


    @Action(displayName = "确定", summary = "创建/更新", bindingType = ViewTypeEnum.FORM)
    public ExpressionDefine save(ExpressionDefine data) {
        expressionDefinitionService.createOrUpdate(data);
        return data;
    }

    /**
     * 模型标题存了id,而不是模型编码. 避免前端调用2次请求
     * 待模型标题表达式修改后删除
     *
     * @param data
     * @return
     */
    @Deprecated
    @Action(displayName = "根据模型id查询label表达式", bindingType = ViewTypeEnum.FORM)
    public ExpressionDefine queryModelLabelExpByModel(ExpressionDefine data) {
        return expressionDefinitionService.queryModelLabelExpByModel(data);
    }
}

package pro.shushi.pamirs.eip.api.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.IEipPagingPredict;
import pro.shushi.pamirs.eip.api.IEipPagingProcessor;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.pamirs.DefaultPagingPredictFunction;
import pro.shushi.pamirs.eip.api.pamirs.DefaultPagingProcessorFunction;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.domain.fun.FunctionDefinition;

/**
 * 分页器
 *
 * @author Adamancy Zhang at 19:21 on 2021-06-09
 */
@Base
@Model.model(EipPaging.MODEL_MODEL)
@Model(displayName = "分页器")
public class EipPaging extends TransientModel implements IEipPaging<SuperMap> {

    private static final long serialVersionUID = -633034633329901655L;

    public static final String MODEL_MODEL = "pamirs.eip.EipPaging";

    @Base
    @Field.Integer
    @Field(displayName = "每页数据行数")
    private Integer pageSize;

    @Base
    @Field.Integer
    @Field(displayName = "起始页数")
    private Integer startPage;

    @Base
    @Field.Integer
    @Field(displayName = "结束页数")
    private Integer endPage;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"processorNamespace", "processorFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "分页处理器函数")
    private FunctionDefinition processorFunction;

    @Base
    @Field.String
    @Field(displayName = "分页处理器函数命名空间")
    private String processorNamespace;

    @Base
    @Field.String
    @Field(displayName = "分页处理器函数名称")
    private String processorFun;

    @JSONField(serialize = false)
    @Base
    @Field.many2one
    @Field.Relation(relationFields = {"predictNamespace", "predictFun"}, referenceFields = {"namespace", "fun"})
    @Field(displayName = "分页判定函数")
    private FunctionDefinition predictFunction;

    @Base
    @Field.String
    @Field(displayName = "分页判定函数命名空间")
    private String predictNamespace;

    @Base
    @Field.String
    @Field(displayName = "分页判定函数名称")
    private String predictFun;

    @JSONField(serialize = false)
    @Override
    public IEipPagingProcessor<SuperMap> getProcessor() {
        String namespace = getProcessorNamespace();
        String fun = getProcessorFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return null;
        }
        return new DefaultPagingProcessorFunction<>(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipPagingPredict<SuperMap> getPredict() {
        String namespace = getPredictNamespace();
        String fun = getPredictFun();
        if (StringUtils.isAnyBlank(namespace, fun)) {
            return EipFunctionConstant.DEFAULT_PAGING_PREDICT;
        }
        return new DefaultPagingPredictFunction<>(namespace, fun);
    }

    @JSONField(serialize = false)
    @Override
    public IEipPaging<SuperMap> afterProperty() {
        return this;
    }
}

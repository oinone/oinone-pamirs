package pro.shushi.pamirs.eip.api.entity.impl;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.IEipPagingPredict;
import pro.shushi.pamirs.eip.api.constant.EipFunctionConstant;
import pro.shushi.pamirs.eip.api.entity.AbstractEipPaging;

/**
 * 默认使用SuperMap作为上下文承载对象
 */
public class DefaultEipPaging extends AbstractEipPaging<SuperMap> implements IEipPaging<SuperMap> {

    @Override
    protected IEipPagingPredict<SuperMap> getDefaultPredict() {
        return EipFunctionConstant.DEFAULT_PAGING_PREDICT;
    }
}

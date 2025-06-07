package pro.shushi.pamirs.eip.api.builder;

import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipIntegrationInterface;
import pro.shushi.pamirs.eip.api.IEipPaging;
import pro.shushi.pamirs.eip.api.entity.impl.DefaultEipPaging;

public class DefaultEipPagingBuilder extends AbstractEipPagingBuilder<SuperMap> {

    public DefaultEipPagingBuilder(AbstractEipInterfaceBuilder<SuperMap> interfaceBuilder) {
        super(interfaceBuilder);
    }

    @Override
    protected IEipPaging<SuperMap> build0(IEipIntegrationInterface<SuperMap> eipInterface) {
        return new DefaultEipPaging()
                .setPageSize(pageSize)
                .setStartPage(startPage)
                .setEndPage(endPage)
                .setProcessor(processor == null ? null : processor.apply(eipInterface))
                .setPredict(predict);
    }
}

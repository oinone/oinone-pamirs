package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.IEipApi;
import pro.shushi.pamirs.eip.api.context.EipCamelContext;
import pro.shushi.pamirs.eip.api.enmu.ExchangePatternEnum;

public abstract class AbstractEipApi implements IEipApi {

    private final EipCamelContext context;

    private final String interfaceName;

    private final String uri;

    private ExchangePatternEnum exchangePattern;

    private Boolean isEnabledLog;

    private final Boolean isDBManaged;

    private final Boolean isIgnoreLogFrequency;

    private final String category;

    public AbstractEipApi(EipCamelContext context, String interfaceName, String uri) {
        this(context, interfaceName, uri, null);
    }

    public AbstractEipApi(EipCamelContext context, String interfaceName, String uri, String category) {
        this.context = context;
        this.interfaceName = interfaceName;
        this.uri = uri;
        this.isEnabledLog = Boolean.TRUE;
        this.isDBManaged = Boolean.FALSE;
        this.isIgnoreLogFrequency = Boolean.FALSE;
        this.category = category;
    }

    @Override
    public EipCamelContext getContext() {
        return context;
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public ExchangePatternEnum getExchangePattern() {
        return exchangePattern;
    }

    @Override
    public Boolean getIsEnabledLog() {
        return isEnabledLog;
    }

    @Override
    public Boolean getIsIgnoreLogFrequency() {
        return isIgnoreLogFrequency;
    }

    @Override
    public Boolean getIsDBManaged() {
        return isDBManaged;
    }

    public AbstractEipApi setExchangePattern(ExchangePatternEnum exchangePattern) {
        this.exchangePattern = exchangePattern;
        return this;
    }

    public void disableLog() {
        this.isEnabledLog = Boolean.FALSE;
    }

    @Override
    public String catagory() {
        return category;
    }
}

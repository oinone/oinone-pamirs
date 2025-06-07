package pro.shushi.pamirs.eip.api.endpoint.function;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.support.DefaultEndpoint;

/**
 * @author drome
 * @date 2021/8/611:59 上午
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "function", title = "Function", syntax = "function:namespace:fun", producerOnly = true, category = {Category.CORE, Category.JAVA})
public class PamirsFunctionEndpoint extends DefaultEndpoint {

    private String namespace;

    private String fun;

    public PamirsFunctionEndpoint(String endpointUri, PamirsFunctionComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new PamirsFunctionProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("You cannot consume from a function endpoint");
    }

    @Override
    public PamirsFunctionComponent getComponent() {
        return (PamirsFunctionComponent) super.getComponent();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFun() {
        return fun;
    }

    public void setFun(String fun) {
        this.fun = fun;
    }
}

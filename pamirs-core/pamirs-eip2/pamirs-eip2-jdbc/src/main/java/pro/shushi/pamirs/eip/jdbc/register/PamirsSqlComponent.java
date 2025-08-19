package pro.shushi.pamirs.eip.jdbc.register;

import org.apache.camel.Endpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.component.sql.SqlEndpoint;
import org.apache.camel.support.CamelContextHelper;
import org.apache.camel.support.EndpointHelper;
import org.apache.camel.support.PropertyBindingSupport;
import org.apache.camel.util.PropertiesHelper;
import org.springframework.jdbc.core.JdbcTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * @author Adamancy Zhang at 14:59 on 2025-08-13
 */
@Slf4j
public class PamirsSqlComponent extends SqlComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        DataSource target = null;

        // endpoint options overrule component configured datasource
        String dsKey = getAndRemoveParameter(parameters, "dataSource", String.class);
        DataSource ds = null;
        if (dsKey != null) {
            ds = EndpointHelper.resolveReferenceParameter(getCamelContext(), dsKey, DataSource.class);
            if (ds != null) {
                target = ds;
            }
        }
        String dataSourceRef = getAndRemoveParameter(parameters, "dataSourceRef", String.class);
        if (target == null && dataSourceRef != null) {
            target = CamelContextHelper.mandatoryLookup(getCamelContext(), dataSourceRef, DataSource.class);
        }
        if (target == null) {
            // fallback and use component
            target = getDataSource();
        }
        if (target == null) {
            // check if the registry contains a single instance of DataSource
            Set<DataSource> dataSources = getCamelContext().getRegistry().findByType(DataSource.class);
            if (dataSources.size() > 1) {
                throw new IllegalArgumentException("Multiple DataSources found in the registry and no explicit configuration provided");
            } else if (dataSources.size() == 1) {
                target = dataSources.stream().findFirst().orElse(null);
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("DataSource must be configured");
        }
        log.debug("Using default DataSource discovered from registry: {}", target);

        String parameterPlaceholderSubstitute = getAndRemoveParameter(parameters, "placeholder", String.class, "#");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(target);
        Map<String, Object> templateOptions = PropertiesHelper.extractProperties(parameters, "template.");
        PropertyBindingSupport.bindProperties(getCamelContext(), jdbcTemplate, templateOptions);

        String query = remaining;
        boolean usePlaceholder = isUsePlaceholder();
        if (usePlaceholder) {
            query = query.replaceAll(parameterPlaceholderSubstitute, "?");
        }

        String onConsume = getAndRemoveParameter(parameters, "consumer.onConsume", String.class);
        if (onConsume == null) {
            onConsume = getAndRemoveParameter(parameters, "onConsume", String.class);
        }
        if (onConsume != null && usePlaceholder) {
            onConsume = onConsume.replaceAll(parameterPlaceholderSubstitute, "?");
        }
        String onConsumeFailed = getAndRemoveParameter(parameters, "consumer.onConsumeFailed", String.class);
        if (onConsumeFailed == null) {
            onConsumeFailed = getAndRemoveParameter(parameters, "onConsumeFailed", String.class);
        }
        if (onConsumeFailed != null && usePlaceholder) {
            onConsumeFailed = onConsumeFailed.replaceAll(parameterPlaceholderSubstitute, "?");
        }
        String onConsumeBatchComplete = getAndRemoveParameter(parameters, "consumer.onConsumeBatchComplete", String.class);
        if (onConsumeBatchComplete == null) {
            onConsumeBatchComplete = getAndRemoveParameter(parameters, "onConsumeBatchComplete", String.class);
        }
        if (onConsumeBatchComplete != null && usePlaceholder) {
            onConsumeBatchComplete = onConsumeBatchComplete.replaceAll(parameterPlaceholderSubstitute, "?");
        }

        SqlEndpoint endpoint = new SqlEndpoint(uri, this, jdbcTemplate, query);
        endpoint.setPlaceholder(parameterPlaceholderSubstitute);
        endpoint.setUsePlaceholder(isUsePlaceholder());
        endpoint.setOnConsume(onConsume);
        endpoint.setOnConsumeFailed(onConsumeFailed);
        endpoint.setOnConsumeBatchComplete(onConsumeBatchComplete);
        endpoint.setDataSource(ds);
        endpoint.setDataSourceRef(dataSourceRef);
        endpoint.setTemplateOptions(templateOptions);
        return endpoint;
    }
}

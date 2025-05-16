package pro.shushi.pamirs.eip.api.service.edit;

import pro.shushi.pamirs.eip.api.tmodel.EipIntegrationInterfaceEdit;


public interface EipInterfaceEditConvertService<T> {

    T convert(EipIntegrationInterfaceEdit edit);
}

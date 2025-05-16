package pro.shushi.pamirs.eip.api;

import org.apache.camel.Processor;

/**
 * @author Adamancy Zhang at 15:04 on 2021-02-24
 */
public interface IEipProcessor<T extends IEipApi> extends Processor {

    T getApi();
}

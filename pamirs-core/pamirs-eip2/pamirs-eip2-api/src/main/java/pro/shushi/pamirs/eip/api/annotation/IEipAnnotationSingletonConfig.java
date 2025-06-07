package pro.shushi.pamirs.eip.api.annotation;

import pro.shushi.pamirs.eip.api.IEipSingletonConfig;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;

public interface IEipAnnotationSingletonConfig<T extends BaseModel> extends IEipSingletonConfig<T> {

    String getHost();

    default void initSystem() {
        T t = this.singletonModel();
        EipResolver.resolver(PamirsSession.getContext().getModelConfig(Models.api().getModel(this)).getModule(), (IEipAnnotationSingletonConfig) t);
    }

}

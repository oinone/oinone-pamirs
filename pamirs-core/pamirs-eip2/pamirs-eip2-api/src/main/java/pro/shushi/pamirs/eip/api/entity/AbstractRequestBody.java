package pro.shushi.pamirs.eip.api.entity;

import pro.shushi.pamirs.eip.api.constant.EipEnvironmentEnum;

/**
 * EIP请求体
 *
 * @author Adamancy Zhang on 2021-02-06 16:54
 */
public abstract class AbstractRequestBody {

    private EipEnvironmentEnum environment;

    public EipEnvironmentEnum getEnvironment() {
        return environment;
    }

    public void setEnvironment(EipEnvironmentEnum environment) {
        this.environment = environment;
    }

    protected <NEW extends AbstractRequestBody> NEW transferToNewInstance(NEW newBody) {
        newBody.setEnvironment(this.getEnvironment());
        return newBody;
    }
}

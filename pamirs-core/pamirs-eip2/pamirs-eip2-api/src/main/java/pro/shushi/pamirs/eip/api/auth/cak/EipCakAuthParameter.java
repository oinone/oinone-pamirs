package pro.shushi.pamirs.eip.api.auth.cak;

import pro.shushi.pamirs.eip.api.auth.api.EipAuthParameterEnum;

/**
 * EipCakAuthParameter
 *
 * @author yakir on 2023/05/25 11:41.
 */
public enum EipCakAuthParameter implements EipAuthParameterEnum {

    ACCESSKEY("accessKey", "accessKey"),
    ACCESSKEY_ID("accessKeyId", "accessKeyId"),
    ACCESSSECRET("accessSecret", "accessSecret"),
    ACCESSSECRET_ID("accessSecretId", "accessSecretId"),


    ;

    private final String origin;
    private final String target;

    EipCakAuthParameter(String origin, String target) {
        this.origin = origin;
        this.target = target;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public String getTarget() {
        return target;
    }

}

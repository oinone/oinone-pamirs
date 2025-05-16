package pro.shushi.pamirs.meta.base.common;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.util.DigestUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.configure.annotation.ModelSigner;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.meta.base.api.ModelAttributesApi;
import pro.shushi.pamirs.meta.base.api.ModelBitOptionsApi;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.common.util.BitUtil;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.NullableBoolEnum;
import pro.shushi.pamirs.meta.enmu.SystemSourceEnum;
import pro.shushi.pamirs.meta.util.DiffUtils;

import java.util.HashMap;
import java.util.Map;

import static pro.shushi.pamirs.meta.base.common.MetaBaseModel.MODEL_MODEL;

/**
 * 元模型基类，id为主键且带code的模型，抽象基类
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/1 1:11 下午
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.ABSTRACT, priority = 32)
@Model(displayName = "元模型基类", summary = "元模型基类")
public abstract class MetaBaseModel extends IdModel
        implements ModelAttributesApi<MetaBaseModel>, ModelBitOptionsApi<MetaBaseModel> {

    private static final long serialVersionUID = -1231713302533009345L;

    public static final String MODEL_MODEL = "base.MetaModel";

    public static final String FIELD_SIGN = "sign";

    @Base
    @Field(displayName = "位", invisible = true, defaultValue = "0")
    private Long bitOptions;

    @Base
    @Field(displayName = "属性", invisible = true)
    private Map<String, Object> attributes;

    @Base
    @Field.Advanced(columnDefinition = "TINYINT(1) NOT NULL DEFAULT '1'")
    @Field(displayName = "系统元数据", defaultValue = "true", summary = "由系统产生的元数据")
    private Boolean sys;

    @Base
    @Field(displayName = "系统来源", index = true, defaultValue = "MANUAL", summary = "BASE是系统创建, MANUAL是人工创建")
    private SystemSourceEnum systemSource;

    @Base
    @JSONField(serialize = false)
    @Field.String
    @Field(displayName = "元数据签名", store = NullableBoolEnum.FALSE, invisible = true)
    private String sign;

    @Base
    @JSONField(serialize = false)
    @Field.String
    @Field(displayName = "校验码", store = NullableBoolEnum.FALSE, invisible = true)
    private String hash;

    private String stringify;

    public abstract String getSignModel();

    @Override
    public Object getAttribute(String name) {
        Map<String, Object> attributes = this.getAttributes();
        if (null == attributes) {
            return null;
        }
        return attributes.get(name);
    }

    @Override
    public MetaBaseModel addAttribute(String name, Object value) {
        Map<String, Object> attributes = this.getAttributes();
        if (null == attributes) {
            attributes = new HashMap<>();
            this.setAttributes(attributes);
        }
        attributes.put(name, value);
        return this;
    }

    @Override
    public MetaBaseModel removeAttribute(String name) {
        Map<String, Object> attributes = this.getAttributes();
        if (null == attributes) {
            return this;
        }
        attributes.remove(name);
        return this;
    }

    @Override
    public MetaBaseModel enableBitOption(Long bit) {
        Long bitOptions = this.getBitOptions();
        this.setBitOptions(BitUtil.enable(bitOptions, bit));
        return this;
    }

    @Override
    public MetaBaseModel disableBitOption(Long bit) {
        Long bitOptions = this.getBitOptions();
        this.setBitOptions(BitUtil.disable(bitOptions, bit));
        return this;
    }

    @Override
    public boolean hasBitOption(Long bit) {
        return BitUtil.has(this.getBitOptions(), bit);
    }

    @JSONField(serialize = false)
    @SuppressWarnings("unchecked")
    public String getSign() {
        String sign = (String) this.get_d().get(FIELD_SIGN);
        if (null == sign) {
            return Spider.getExtension(ModelSigner.class, getSignModel()).sign(this);
        }
        return sign;
    }

    @SuppressWarnings("unused")
    public String hashSum() {
        return hashSum(this.stringify());
    }

    public String hashSum(String stringify) {
        return DigestUtils.md5DigestAsHex(stringify.getBytes());
    }

    public String stringify() {
        return DiffUtils.stringify(this);
    }

}

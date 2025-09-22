package pro.shushi.pamirs.eip.api.tmodel;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.eip.api.excel.EipExcel;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * @author Gesi at 14:16 on 2025/7/29
 */
@Model.model(EipIntegrationFileHeader.MODEL_MODEL)
@Model(displayName = "集成文件头")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
public class EipIntegrationFileHeader extends TransientModel {

    public static final String MODEL_MODEL = "pamirs.eip.EipIntegrationFileHeader";

    @Field(displayName = "EipExcel")
    private String eipExcel;

    public void setEipExcel(String eipExcel) {
        get_d().put("eipExcel", eipExcel);
    }

    public void setEipExcel(EipExcel eipExcel) {
        if (eipExcel != null) {
            setEipExcel(JSON.toJSONString(eipExcel));
        } else {
            setEipExcel((String) null);
        }
    }

    public EipExcel fetchEipExcel() {
        if (StringUtils.isBlank(getEipExcel())) {
            return null;
        }
        return JSON.parseObject(getEipExcel(), EipExcel.class);
    }

}

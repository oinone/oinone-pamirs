package pro.shushi.pamirs.business;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * DomainChangeLog
 *
 * @author yakir on 2022/09/21 16:23.
 */
@Model(displayName = "公司修改域名日志")
@Model.model(DomainChangeLog.MODEL_MODEL)
@Model.Advanced(index = "companyCode,year")
public class DomainChangeLog extends IdModel {

    private static final long serialVersionUID = 5278563719551833272L;

    public static final String MODEL_MODEL = "business.DomainChangeLog";

    @Field(displayName = "公司编码")
    @Field.String
    private String companyCode;

    @Field(displayName = "年度")
    @Field.Integer
    private Integer year;

    @Field(displayName = "修改的域名")
    @Field.String
    private String domain;

    @Field(displayName = "次数")
    @Field.Integer
    private Integer times;
}

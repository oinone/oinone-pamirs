package pro.shushi.pamirs.boot.orm.test.mock.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.base.AbstractModel;
import pro.shushi.pamirs.meta.base.IdModel;

import java.util.List;

import static pro.shushi.pamirs.boot.orm.test.mock.model.TestInheritedReentryModel.MODEL_MODEL;
import static pro.shushi.pamirs.meta.constant.FunctionConstants.createOrUpdateBatch;

/**
 * 2020/8/26 4:36 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Model.model(MODEL_MODEL)
@Model(displayName = "应用分类", summary = "应用分类", labelFields = "name")
public class TestInheritedReentryModel extends IdModel {

    public static final String MODEL_MODEL = "test.TestInheritedReentryModel";
    private static final long serialVersionUID = 4538178164723426304L;

    @Field(unique = true)
    private String name;

    @Function.Advanced(managed = true)
    @Function.fun(createOrUpdateBatch)
    @Override
    public <T extends AbstractModel> Integer createOrUpdateBatch(List<T> dataList) {
        return super.createOrUpdateBatch(dataList);
    }

}

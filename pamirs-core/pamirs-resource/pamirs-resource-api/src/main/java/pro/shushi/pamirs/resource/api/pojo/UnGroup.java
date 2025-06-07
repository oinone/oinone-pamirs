package pro.shushi.pamirs.resource.api.pojo;

import pro.shushi.pamirs.resource.api.model.ResourceIconGroup;
import pro.shushi.pamirs.resource.api.util.UnGroupData;

public class UnGroup {

    private static final String NAME = UnGroupData.NAME;
    private static final Long ID = UnGroupData.ID;

    public static void getUnGroup() {
        new ResourceIconGroup().setName(NAME)
                .setSys(Boolean.FALSE)
                .setBatchCode(0L)
                .setId(ID).createOrUpdate();
    }
}

package pro.shushi.pamirs.framework.connectors.data.test.mock.service;

import org.springframework.stereotype.Component;
import org.springframework.test.util.AssertionErrors;
import pro.shushi.pamirs.framework.connectors.data.mapper.GenericMapper;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.framework.connectors.data.test.mock.model.ds.TestMultiDsModel;
import pro.shushi.pamirs.meta.annotation.sys.Ds;
import pro.shushi.pamirs.meta.api.dto.entity.DataMap;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 在类方法上指定数据源
 * <p>
 * 2020/7/4 11:02 上午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@Component
public class TestMultiDsService {

    @Resource
    private GenericMapper genericMapper;

    @Ds("ds0")
    public int insertDs0(DataMap one) {
        return genericMapper.insert(one);
    }

    @Ds("d${param.test}")
    public int insertDynamicDsKey(DataMap one) {
        return genericMapper.insert(one);
    }

    @Ds("d${param.test}")
    public void selectDynamicExpressionDs() {
        // 结果断言
        Map<String, Object> result = genericMapper.selectOne(Pops.<DataMap>query().from(TestMultiDsModel.modelModel).eq("`relation`", "test"));
        AssertionErrors.assertEquals("插入并查询单条记录失败", "yihui", result.get("module"));
    }

}

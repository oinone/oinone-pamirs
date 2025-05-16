package pro.shushi.pamirs.framework.connectors.data.elastic.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pro.shushi.pamirs.framework.connectors.data.elastic.common.domain.ElasticIndex;

import java.util.Date;

/**
 * TestElasticRestManage
 *
 * @author yakir on 2020/04/16 20:04.
 */
public class TestElasticRestManage extends AbstractBaseTest {

    private static final Logger log = LoggerFactory.getLogger(TestElasticRestManage.class);

    @Autowired
    private ElasticIndicesImpl restManage;

    @Test
    @DisplayName("测试初始化索引")
    public void testInitIndex() {

        String index = "test_init_index";

        ElasticIndex elasticIndex = new ElasticIndex()
                .setIndex(index)
                .setAlias(index)
                .setCreateDate(new Date());

        restManage.init(elasticIndex);

    }

    @Test
    @DisplayName("测试创建索引")
    public void testCreateIndex() {

        String index = "test_init_index";

        restManage.create(index);

    }

}

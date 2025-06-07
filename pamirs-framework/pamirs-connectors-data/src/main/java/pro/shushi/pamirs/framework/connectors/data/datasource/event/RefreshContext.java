package pro.shushi.pamirs.framework.connectors.data.datasource.event;

import java.util.HashSet;
import java.util.Set;

/**
 * 刷新处理流程上下文
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/1/11 4:58 上午
 */
public class RefreshContext {

    private Set<String> refreshConfSet = new HashSet();

    private Set<String> refreshDsSet = new HashSet();

    private Set<String> refreshAllSet = new HashSet();

    public Set<String> getRefreshConfSet() {
        return refreshConfSet;
    }

    public void setRefreshConfSet(Set<String> refreshConfSet) {
        this.refreshConfSet = refreshConfSet;
    }

    public Set<String> getRefreshDsSet() {
        return refreshDsSet;
    }

    public void setRefreshDsSet(Set<String> refreshDsSet) {
        this.refreshDsSet = refreshDsSet;
    }

    public Set<String> getRefreshAllSet() {
        return refreshAllSet;
    }

    public void setRefreshAllSet(Set<String> refreshAllSet) {
        this.refreshAllSet = refreshAllSet;
    }
}

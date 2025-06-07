package pro.shushi.pamirs.core.common.hook;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.core.common.behavior.ITreeCodeModel;
import pro.shushi.pamirs.meta.annotation.Hook;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.core.faas.HookAfter;
import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.constant.FunctionConstants;
import pro.shushi.pamirs.meta.util.FieldUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Base
@Component
public class QueryPageHook4TreeAfter implements HookAfter {

    @SuppressWarnings("unchecked")
    @Hook(priority = Integer.MAX_VALUE, displayName = "树形Parent查询优化")
    @Override
    public Object run(Function function, Object ret) {

        if (!FunctionConstants.queryPage.equals(function.getFun())) {
            return ret;
        }
        Pagination<CodeModel> pagination = (Pagination<CodeModel>) ((Object[]) ret)[0];
        if (pagination == null || CollectionUtils.isEmpty(pagination.getContent())) {
            return ret;
        } else {
            Object o = pagination.getContent().get(0);
            if (!(o instanceof ITreeCodeModel)) {
                return ret;
            }
        }

        List<CodeModel> content = pagination.getContent();

        Map<String, CodeModel> map = new HashMap<>();
        for (CodeModel o : content) {
            String code = ((ITreeCodeModel) o).getCode();
            map.put(code, o);
        }

        for (CodeModel o : content) {
            CodeModel parent = map.get(((ITreeCodeModel) o).getParentCode());
            if (parent != null) {
                FieldUtils.setFieldValue(o, "parent", parent);
            }
        }
        return ret;
    }
}

package pro.shushi.pamirs.eip.api.serializable;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.core.common.SuperMap;
import pro.shushi.pamirs.eip.api.IEipDeserialization;
import pro.shushi.pamirs.eip.api.IEipSerializable;
import pro.shushi.pamirs.eip.api.constant.EipContextConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * @author Adamancy Zhang at 20:47 on 2021-02-27
 */
public abstract class AbstractSerializable implements IEipSerializable<SuperMap>, IEipDeserialization<SuperMap> {

    protected abstract List<?> listSerializableList(List<?> list);

    protected abstract SuperMap stringToSuperMap(String inObjectString);

    @SuppressWarnings("unchecked")
    @Override
    public SuperMap serializable(Object inObject) {
        if (inObject == null) {
            return new SuperMap();
        }
        SuperMap result;
        if (inObject instanceof SuperMap) {
            result = (SuperMap) inObject;
        } else if (inObject instanceof Map) {
            result = new SuperMap((Map<String, Object>) inObject);
        } else if (inObject instanceof InputStream) {
            result = inputStreamToString((InputStream) inObject);
        } else if (inObject instanceof List) {
            result = new SuperMap();
            List<?> list = (List<?>) inObject;
            if (CollectionUtils.isNotEmpty(list)) {
                result.put(EipContextConstant.LIST_KEY, listSerializableList((List<?>) inObject));
            }
        } else if (inObject instanceof String) {
            String inObjectString = (String) inObject;
            if (StringUtils.isNotBlank(inObjectString)) {
                result = stringToSuperMap(inObjectString);
            } else {
                result = new SuperMap();
            }
        } else {
            result = stringToSuperMap(JSON.toJSONString(inObject));
        }
        return result;
    }

    @Override
    public Object deserialization(SuperMap outObject) {
        return outObject;
    }

    protected SuperMap inputStreamToString(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return serializable(sb.toString());
        } catch (IOException e) {
            return new SuperMap();
        }
    }
}

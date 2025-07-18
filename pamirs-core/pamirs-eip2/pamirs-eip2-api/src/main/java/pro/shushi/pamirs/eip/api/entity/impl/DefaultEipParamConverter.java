package pro.shushi.pamirs.eip.api.entity.impl;

import org.apache.commons.collections4.CollectionUtils;
import pro.shushi.pamirs.core.common.TreeHelper;
import pro.shushi.pamirs.eip.api.IEipContext;
import pro.shushi.pamirs.eip.api.IEipConvertParam;
import pro.shushi.pamirs.eip.api.IEipParamConverter;
import pro.shushi.pamirs.eip.api.IEipParamConverterCallback;
import pro.shushi.pamirs.eip.api.enmu.EipExpEnumerate;
import pro.shushi.pamirs.eip.api.enmu.ParamTypeEnum;
import pro.shushi.pamirs.eip.api.util.EipParamConverterHelper;
import pro.shushi.pamirs.framework.common.entry.TreeNode;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultEipParamConverter<T> implements IEipParamConverter<T> {

    @Override
    public void convert(IEipContext<T> context, List<IEipConvertParam<T>> convertParamList, IEipParamConverterCallback<T> callback) {
        Set<String> convertInParamKeySet = new HashSet<>();
        List<IEipConvertParam<T>> listConvertInParamList = new ArrayList<>();
        for (IEipConvertParam<T> convertParam : convertParamList) {
            //判断入参定义是否存在数组标记
            if (convertParam.getInParam().contains(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
                //留存根参数
                listConvertInParamList.add(convertParam);
                //根据数组标记进行分割，形成树结构所需的中间节点
                String paramKey = convertParam.getInParam();
                int flagIndex = paramKey.lastIndexOf(IEipContext.DEFAULT_LIST_FLAG_KEY);
                while (flagIndex != -1) {
                    int newFlagIndex = flagIndex + IEipContext.DEFAULT_LIST_FLAG_KEY.length();
                    if (paramKey.length() == newFlagIndex) {
                        break;
                    }
                    paramKey = paramKey.substring(0, newFlagIndex);
                    if (convertInParamKeySet.contains(paramKey)) {
                        break;
                    } else {
                        listConvertInParamList.add(convertParam.clone(paramKey, convertParam.getOutParam()));
                        convertInParamKeySet.add(paramKey);
                    }
                    flagIndex = paramKey.lastIndexOf(IEipContext.DEFAULT_LIST_FLAG_KEY, flagIndex - 1);
                }
                continue;
            }
            getAndPutValue(context, convertParam, null, convertParam.getInParam(), convertParam.getOutParam(), callback);
        }
        if (CollectionUtils.isNotEmpty(listConvertInParamList)) {
            listParamReadyConverter(context, listConvertInParamList, callback);
        }
    }

    private void listParamReadyConverter(IEipContext<T> context, List<IEipConvertParam<T>> listConvertInParamList, IEipParamConverterCallback<T> callback) {
        List<TreeNode<IEipConvertParam<T>>> inParamRootList = TreeHelper.convert(listConvertInParamList, IEipConvertParam::getInParam,
                v -> {
                    String paramKey = v.getInParam();
                    if (paramKey.endsWith(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
                        int flagIndex = paramKey.lastIndexOf(IEipContext.DEFAULT_LIST_FLAG_KEY, paramKey.length() - IEipContext.DEFAULT_LIST_FLAG_KEY.length() - 1);
                        if (flagIndex == -1) {
                            return null;
                        } else {
                            return paramKey.substring(0, flagIndex + IEipContext.DEFAULT_LIST_FLAG_KEY.length());
                        }
                    } else {
                        return paramKey.substring(0, paramKey.lastIndexOf(IEipContext.DEFAULT_LIST_FLAG_KEY) + IEipContext.DEFAULT_LIST_FLAG_KEY.length());
                    }
                });
        for (TreeNode<IEipConvertParam<T>> inParamRoot : inParamRootList) {
            listParamConverter(context, inParamRoot, new ArrayList<>(), inParamRoot.getKey(), callback);
        }
    }

    private void listParamConverter(IEipContext<T> context, TreeNode<IEipConvertParam<T>> inParamRoot, List<AtomicInteger> inParamCounterList,
                                    String currentKey, IEipParamConverterCallback<T> callback) {
        int currentInParamCounterIndex = inParamRoot.getLevel() - 1;
        //当进入新的一层时保证计数器数量充足，最大计数器数量不会超过【最大层级-1】
        while (currentInParamCounterIndex >= inParamCounterList.size()) {
            inParamCounterList.add(new AtomicInteger(0));
        }
        AtomicInteger currentInParamCounter = inParamCounterList.get(currentInParamCounterIndex);
        IEipConvertParam<T> convertParam = inParamRoot.getValue();
        String nextKey, listKey;
        Object value;
        if (inParamRoot.isLeaf()) {
            TreeNode<IEipConvertParam<T>> parentInParamNode = inParamRoot.getParent();
            if (parentInParamNode != null) {
                nextKey = currentKey.concat(inParamRoot.getKey().substring(parentInParamNode.getKey().length()));
            } else {
                nextKey = currentKey;
            }
            boolean isListLeafNode = nextKey.endsWith(IEipContext.DEFAULT_LIST_FLAG_KEY),
                    isForeach = false;
            String outParam = convertParam.getOutParam();
            if (isListLeafNode) {
                nextKey = nextKey.substring(0, nextKey.length() - IEipContext.DEFAULT_LIST_FLAG_KEY.length());
                if (outParam.endsWith(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
                    if (ParamTypeEnum.ENUMERATION.equals(convertParam.getInParamType()) && ParamTypeEnum.ENUMERATION.equals(convertParam.getOutParamType())) {
                        isForeach = true;
                    } else {
                        outParam = outParam.substring(0, outParam.length() - IEipContext.DEFAULT_LIST_FLAG_KEY.length());
                    }
                } else {
                    isForeach = true;
                }
            }
            if (isForeach) {
                value = EipParamConverterHelper.getContextValue(convertParam.getOriginContextType(), context, nextKey);
                if (value instanceof Collection) {
                    Collection<?> values = (Collection<?>) value;
                    int index = 0;
                    for (Object item : values) {
                        currentInParamCounter.set(index);
                        getAndPutValue(context, convertParam, inParamCounterList, nextKey, outParam, callback, item);
                        index++;
                    }
                    currentInParamCounter.set(0);
                }
            } else {
                getAndPutValue(context, convertParam, inParamCounterList, nextKey, outParam, callback);
            }
        } else {
            TreeNode<IEipConvertParam<T>> parentInParamNode = inParamRoot.getParent();
            if (parentInParamNode != null) {
                nextKey = currentKey.concat(inParamRoot.getKey().substring(parentInParamNode.getKey().length()));
            } else {
                nextKey = currentKey;
            }
            if (nextKey.endsWith(IEipContext.DEFAULT_LIST_FLAG_KEY)) {
                value = EipParamConverterHelper.getContextValue(convertParam.getOriginContextType(), context, nextKey.substring(0, nextKey.length() - IEipContext.DEFAULT_LIST_FLAG_KEY.length()));
                if (value instanceof Collection) {
                    int maxCount = ((Collection<?>) value).size();
                    for (int index = 0; index < maxCount; index++) {
                        currentInParamCounter.set(index);
                        listKey = EipParamConverterHelper.LIST_FLAG_PATTERN.matcher(nextKey).replaceFirst("[" + index + "]");
                        for (TreeNode<IEipConvertParam<T>> inParamChildNode : inParamRoot.getChildren()) {
                            listParamConverter(context, inParamChildNode, inParamCounterList, listKey, callback);
                        }
                    }
                    currentInParamCounter.set(0);
                } else {
                    if (value == null || !inParamRoot.hasChildren()) {
                        return;//异常解析
                    }
                    for (TreeNode<IEipConvertParam<T>> inParamChildNode : inParamRoot.getChildren()) {
                        listParamConverter(context, inParamChildNode, inParamCounterList, nextKey, callback);
                    }
                }
            }
        }
    }

    private void getAndPutValue(IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList,
                                String inParam, String outParam,
                                IEipParamConverterCallback<T> callback) {
        getAndPutValue(context, convertParam, inParamCounterList, inParam, outParam, callback, EipParamConverterHelper.getContextValue(convertParam.getOriginContextType(), context, inParam));
    }

    private void getAndPutValue(IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList,
                                String inParam, String outParam,
                                IEipParamConverterCallback<T> callback, Object inValue) {

        Object value = convertValue(context, convertParam, inParamCounterList, callback, inValue);
        if (value == null) {
            if (Boolean.TRUE.equals(convertParam.getRequired())) {
                throw PamirsException.construct(EipExpEnumerate.PARAM_REQUIRED).appendMsg(inParam).errThrow();
            }
            return;
        }
        if (ParamTypeEnum.String.equals(convertParam.getOutParamType())) {
            if (convertParam.getSize() != null && convertParam.getSize() > 0 && value.toString().length() > convertParam.getSize()) {
                throw PamirsException.construct(EipExpEnumerate.PARAM_OVER_SIZE).appendMsg(inParam + " > " + convertParam.getSize()).errThrow();
            }
        }
        EipParamConverterHelper.putContextValue(convertParam.getTargetContextType(), context, EipParamConverterHelper.getFinalParameter(outParam, inParamCounterList), value);
    }

    private Object convertValue(IEipContext<T> context, IEipConvertParam<T> convertParam, List<AtomicInteger> inParamCounterList,
                                IEipParamConverterCallback<T> callback, Object inValue) {
        Object value = EipParamConverterHelper.convertValue(convertParam, inValue);
        if (value == null) {
            return null;
        }
        return EipParamConverterHelper.callback(callback, context, convertParam, inParamCounterList, value);
    }
}

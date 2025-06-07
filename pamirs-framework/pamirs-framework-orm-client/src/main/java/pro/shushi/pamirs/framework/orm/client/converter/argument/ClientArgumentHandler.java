package pro.shushi.pamirs.framework.orm.client.converter.argument;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.framework.common.emnu.FwExpEnumerate;
import pro.shushi.pamirs.framework.compute.system.check.spi.api.CheckFunctionServiceApi;
import pro.shushi.pamirs.framework.orm.client.constant.IWrapperFieldConstants;
import pro.shushi.pamirs.meta.api.CommonApiFactory;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.compute.PamirsDataComputer;
import pro.shushi.pamirs.meta.api.core.compute.context.ComputeContext;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientArgumentHandlerApi;
import pro.shushi.pamirs.meta.api.core.orm.convert.ClientDataConverter;
import pro.shushi.pamirs.meta.api.core.orm.template.context.ModelComputeContext;
import pro.shushi.pamirs.meta.api.dto.config.ModelConfig;
import pro.shushi.pamirs.meta.api.dto.fun.Arg;
import pro.shushi.pamirs.meta.api.dto.fun.Function;
import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.base.BaseModel;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.meta.common.spi.SPI;
import pro.shushi.pamirs.meta.common.spi.Spider;
import pro.shushi.pamirs.meta.enmu.FunctionBitOptions;
import pro.shushi.pamirs.meta.enmu.TtypeEnum;
import pro.shushi.pamirs.meta.util.FieldUtils;
import pro.shushi.pamirs.meta.util.JsonUtils;
import pro.shushi.pamirs.meta.util.TypeUtils;

import java.util.List;
import java.util.Map;

/**
 * 请求参数处理
 * <p>
 * 2021/3/12 6:04 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
@SPI.Service
public class ClientArgumentHandler implements ClientArgumentHandlerApi {

    @Override
    public void in(ModelConfig modelConfig, Function function, boolean isQuery,
                   Map<String, Object> requestArgs, Object[] args) {
        ModelComputeContext context = new ModelComputeContext().setFun(function.getFun());
        context.initPath();

        if (!function.getFunctionDefinition().hasBitOption(FunctionBitOptions.ENABLE_CHECK.getOption())) {
            PamirsSession.directive().disableCheck();
        }

        // 参数转换
        int i = 0;
        for (Arg arg : function.getArguments()) {
            context.segment(arg.getName());
            Object argument = requestArgs.get(arg.getName());
            if (null == arg.getModel() && !TtypeEnum.isRelationType(arg.getTtype())) {
                dealNonModelArg(args, i, arg, argument);
            } else {
                args[i] = dealArg(context, arg.getModel(), modelConfig.getModel(), argument, isQuery);
            }
            context.dropSegment();
            i++;
        }

        // 函数接口级别的数据校验
        boolean returnWhenError = PamirsSession.getRequestVariables().returnWhenError();
        Boolean success = Spider.getDefaultExtension(CheckFunctionServiceApi.class)
                .check(returnWhenError, function.getFunctionDefinition(), requestArgs, args);

        // 错误处理
        if (!success || !PamirsSession.getMessageHub().isSuccess()) {
            throw PamirsException.construct(FwExpEnumerate.BASE_CHECK_DATA_ERROR).errThrow();
        }
    }

    private void dealNonModelArg(Object[] args, int i, Arg arg, Object argument) {
        String ltype = arg.getLtype();
        String ltypeT = arg.getLtypeT();
        if (argument instanceof String) {
            if (TypeUtils.isMap(ltype)) {
                args[i] = JsonUtils.parseMap((String) argument);
            } else if (TypeUtils.isCollection(ltype)) {
                if (TypeUtils.isMap(ltypeT)) {
                    args[i] = JsonUtils.parseMapList((String) argument);
                } else {
                    args[i] = JsonUtils.parseObjectList((String) argument);
                }
            } else {
                args[i] = argument;
            }
        } else {
            args[i] = argument;
        }
    }

    @SuppressWarnings({"unchecked"})
    private Object dealArg(ModelComputeContext context, String argModel, String model, Object obj, boolean isQuery) {
        if (StringUtils.isBlank(argModel) && StringUtils.isBlank(model) || null == obj) {
            return obj;
        }
        if (null == argModel || BaseModel.MODEL_MODEL.equals(argModel)) {
            argModel = model;
        }
        if (StringUtils.isNotBlank(argModel)) {
            // 前后端字段适配
            if (IWrapper.MODEL_MODEL.equals(argModel)) {
                String rsql = (String) FieldUtils.getFieldValue(obj, IWrapperFieldConstants.rsql);
                List<String> selects = (List<String>) FieldUtils.getFieldValue(obj, IWrapperFieldConstants.selects);
                Map<String, Object> data = (Map<String, Object>) FieldUtils.getFieldValue(obj, IWrapperFieldConstants.queryData);
                obj = Models.pops().construct(model).setSelects(selects).setRsql(rsql).setQueryData(data);
            } else {
                Models.api().setDataModel(argModel, obj);
                obj = ClientDataConverter.get().in(context, argModel, obj);
            }
            if (!isQuery) {
                // 数据计算
                Object arg = obj;
                if (!(arg instanceof List)) {
                    arg = Lists.newArrayList(obj);
                }
                boolean isDoCheck = PamirsSession.directive().isDoCheck();
                try {
                    PamirsSession.directive().disableCheck();
                    ComputeContext computeContext = ComputeContext.requestInit();
                    CommonApiFactory.getApi(PamirsDataComputer.class).computeRelationField(computeContext, argModel, arg);
                } finally {
                    if (isDoCheck) {
                        PamirsSession.directive().enableCheck();
                    }
                }
            }
        }
        return obj;
    }

    @Override
    public Object out(String returnModel, String model, Object obj) {
        if (StringUtils.isBlank(returnModel) || StringUtils.isBlank(model) || null == obj) {
            return obj;
        }
        if (BaseModel.MODEL_MODEL.equals(returnModel)) {
            returnModel = model;
        }
        Models.api().setDataModel(model, obj);
        if (StringUtils.isNotBlank(returnModel)) {
            // 前后端字段适配
            obj = ClientDataConverter.get().out(returnModel, obj);
        }
        return obj;
    }

}

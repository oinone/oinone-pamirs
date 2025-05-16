//package pro.shushi.pamirs.trigger.tmodel;
//
//import pro.shushi.pamirs.meta.annotation.Field;
//import pro.shushi.pamirs.meta.annotation.Function;
//import pro.shushi.pamirs.meta.annotation.Model;
//import pro.shushi.pamirs.meta.annotation.sys.Base;
//import pro.shushi.pamirs.meta.api.dto.condition.Pagination;
//import pro.shushi.pamirs.meta.api.dto.wrapper.IWrapper;
//import pro.shushi.pamirs.meta.base.AbstractModel;
//import pro.shushi.pamirs.meta.constant.FunctionConstants;
//import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
//import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;
//import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
//import pro.shushi.pamirs.trigger.model.ScheduleTaskAction;
//
///**
// * @author Adamancy Zhang
// * @date 2020-11-02 22:34
// */
//@Model.Advanced(type = ModelTypeEnum.ABSTRACT)
//@Model.model(ScheduleTaskActionDetail.MODEL_MODEL)
//@Model(displayName = "计划任务执行明细", labelFields = "name")
//public class ScheduleTaskActionDetail extends AbstractTaskActionDetail {
//
//    private static final long serialVersionUID = 7287707050253562010L;
//
//    public static final String MODEL_MODEL = "trigger.ScheduleTaskActionDetail";
//
//    @Base
//    @Field.many2one
//    @Field(displayName = "计划任务")
//    private ScheduleTaskAction task;
//
//    @Function.Advanced(type = FunctionTypeEnum.QUERY, managed = true)
//    @Function.fun(FunctionConstants.queryPage)
//    @Function(openLevel = {FunctionOpenEnum.API})
//    @Override
//    public <T extends AbstractModel> Pagination<T> queryPage(Pagination<T> page, IWrapper<T> queryWrapper) {
//        return page;
//    }
//}

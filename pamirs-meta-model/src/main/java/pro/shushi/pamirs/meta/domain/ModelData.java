//package pro.shushi.pamirs.meta.model;
//
//import pro.shushi.pamirs.meta.base.IdModel;
//import pro.shushi.pamirs.meta.enmu.ModelTtypeEnum;
//import pro.shushi.pamirs.meta.enmu.StateEnum;
//
//import java.util.Date;
//
//@pro.shushi.pamirs.meta.annotation.Model(state= StateEnum.BASE)
//@pro.shushi.pamirs.meta.annotation.ModelConstraint(unique = {"module,name"},check = {"checkModelModel(model)","checkModuleName(module)"})
//public class ModelData extends IdModel {
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.BOOLEAN, help = "是否来自lowcode", compute = "0",computeEventType = "CREATE")
//    private Boolean isLowcode;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.BOOLEAN, help = "是否可以修改")
//    private Boolean noupdate;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.STRING, csize=512, help = "初始化数据的xml里的Id，编辑器自动生成")
//    private String name;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.STRING, help = "module的java包名")
//    private String module;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.STRING, help = "model")
//    private String model;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.ID, help = "目标model所在表的数据id")
//    private Long resId;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.DATE, help = "data初始化时间")
//    private Date dataInit;
//
//    @pro.shushi.pamirs.meta.annotation.ModelField(ttype = ModelTtypeEnum.DATE, help = "data更新时间")
//    private Date dataUpdate;
//
//}

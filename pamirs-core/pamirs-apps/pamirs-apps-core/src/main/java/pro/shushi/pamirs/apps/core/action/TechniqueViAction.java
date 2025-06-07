package pro.shushi.pamirs.apps.core.action;

import pro.shushi.pamirs.apps.api.enmu.TechniqueViConstantEnum;
import pro.shushi.pamirs.apps.api.tmodel.TechniqueVi;
import pro.shushi.pamirs.apps.api.tmodel.TechniqueViValue;
import pro.shushi.pamirs.meta.annotation.Function;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.enmu.FunctionOpenEnum;
import pro.shushi.pamirs.meta.enmu.FunctionTypeEnum;

import java.util.ArrayList;

/**
 * @author drome
 * @date 2022/4/20下午2:26
 */
@Base
@Model.model(TechniqueVi.MODEL_MODEL)
public class TechniqueViAction {

    @Function(openLevel = FunctionOpenEnum.API, summary = " 查询模块数据")
    @Function.Advanced(type = FunctionTypeEnum.QUERY)
    public TechniqueVi fetchDetail(TechniqueVi data) {
        return new TechniqueVi()
                .setIPaaSList(
                        new ArrayList<TechniqueViValue>() {{
                            add(new TechniqueViValue().setCode("WORKFLOW").setName(TechniqueViConstantEnum.WORKFLOW.getTranslate()).setValue("94").setPosition("{\"left\":\"47px\",\"top\":\"46px\"}"));
                            add(new TechniqueViValue().setCode("MESSAGE_QUEUE").setName(TechniqueViConstantEnum.MESSAGE_QUEUE.getTranslate()).setValue("23").setPosition("{\"left\":\"160px\",\"top\":\"83px\"}"));
                            add(new TechniqueViValue().setCode("TRIGGER").setName(TechniqueViConstantEnum.TRIGGER.getTranslate()).setValue("72").setPosition("{\"left\":\"52px\",\"top\":\"176px\"}"));
                            add(new TechniqueViValue().setCode("EXTEND_POINT").setName(TechniqueViConstantEnum.EXTEND_POINT.getTranslate()).setValue("66").setPosition("{\"left\":\"52px\",\"bottom\":\"96px\"}"));
                            add(new TechniqueViValue().setCode("INTERCEPTOR").setName(TechniqueViConstantEnum.INTERCEPTOR.getTranslate()).setValue("25").setPosition("{\"left\":\"156px\",\"top\":\"182px\"}"));
                            add(new TechniqueViValue().setCode("DATA_CHANGE").setName(TechniqueViConstantEnum.DATA_CHANGE.getTranslate()).setValue("155").setPosition("{\"left\":\"162px\",\"bottom\":\"73px\"}"));
                            add(new TechniqueViValue().setCode("DATA_SYNC").setName(TechniqueViConstantEnum.DATA_SYNC.getTranslate()).setValue("67").setPosition("{\"right\":\"52px\",\"bottom\":\"95px\"}"));
                            add(new TechniqueViValue().setCode("EIP_INTERFACE").setName(TechniqueViConstantEnum.EIP_INTERFACE.getTranslate()).setValue("285").setPosition("{\"right\":\"12px\",\"top\":\"185px\"}"));
                        }})
                .setUiPaaSList(
                        new ArrayList<TechniqueViValue>() {{
                            add(new TechniqueViValue().setCode("PRODUCT").setName(TechniqueViConstantEnum.PRODUCT.getTranslate()).setValue("8").setPosition("{\"top\":\"3px\",\"left\":\"142px\",\"background\":\"#FFFFFF\",\"border\":\"1px solid var(--oio-primary-color)\",\"borderRadius\":\"4px\",\"fontSize\":\"14px\",\"color\":\"var(--oio-primary-color)\"}"));
                            add(new TechniqueViValue().setCode("LOGIC").setName(TechniqueViConstantEnum.LOGIC.getTranslate()).setValue("23").setPosition("{\"top\":\"-6px\",\"left\":\"206px\",\"color\":\"#FFFFFF\",\"background\":\"var(--oio-primary-color)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("BUSINESS_COMPONENT").setName(TechniqueViConstantEnum.BUSINESS_COMPONENT.getTranslate()).setValue("76").setPosition("{\"top\":\"55px\",\"left\":\"135px\",\"color\":\"#FFFFFF\",\"background\":\"var(--oio-primary-color)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("ROUTER_RULE").setName(TechniqueViConstantEnum.ROUTER_RULE.getTranslate()).setValue("28").setPosition("{\"top\":\"77px\",\"left\":\"0\",\"color\":\"#FFFFFF\",\"background\":\"var(--oio-primary-color)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("VALIDATION_RULE").setName(TechniqueViConstantEnum.VALIDATION_RULE.getTranslate()).setValue("123").setPosition("{\"top\":\"110px\",\"left\":\"70px\",\"color\":\"#FFFFFF\",\"background\":\"var(--oio-primary-color)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("DATA_FILTER_RULE").setName(TechniqueViConstantEnum.DATA_FILTER_RULE.getTranslate()).setValue("6326").setPosition("{\"top\":\"93px\",\"right\":\"107px\",\"color\":\"#FFFFFF\",\"background\":\"var(--oio-primary-color)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("MODEL").setName(TechniqueViConstantEnum.MODEL.getTranslate()).setValue("315").setPosition("{\"top\":\"0px\",\"right\":\"52px\",\"background\":\"rgba(255,255,255,0.80)\",\"border\":\"1px solid #1066DA\",\"borderRadius\":\"4px\",\"fontSize\":\"14px\",\"color\":\"#1066DA\"}"));
                            add(new TechniqueViValue().setCode("PROFESSION_MODEL").setName(TechniqueViConstantEnum.PROFESSION_MODEL.getTranslate()).setValue("68").setPosition("{\"top\":\"5px\",\"right\":\"138px\",\"background\":\"rgba(16,102,218,0.80)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("BUSINESS_MODEL").setName(TechniqueViConstantEnum.BUSINESS_MODEL.getTranslate()).setValue("159").setPosition("{\"top\":\"35px\",\"right\":\"86px\",\"background\":\"rgba(16,102,218,0.80)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"}"));
                            add(new TechniqueViValue().setCode("BASE_MODEL").setName(TechniqueViConstantEnum.BASE_MODEL.getTranslate()).setValue("88").setPosition("{\"top\":\"69px\",\"right\":\"35px\",\"background\":\"rgba(16,102,218,0.80)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\"},"));
                            add(new TechniqueViValue().setCode("USER_EXPERIENCE").setName(TechniqueViConstantEnum.USER_EXPERIENCE.getTranslate()).setValue("287").setPosition("{\"top\":\"227px\",\"left\":\"53px\",\"background\":\"rgba(255,255,255,0.90)\",\"border\":\"1px solid #04105D\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\",\"borderRadius\":\"4px\",\"fontSize\":\"14px\",\"color\":\"#04115E\"}"));
                            add(new TechniqueViValue().setCode("FORM").setName(TechniqueViConstantEnum.FORM.getTranslate()).setValue("35").setPosition("{\"top\":\"169px\",\"left\":\"-13px\"}"));
                            add(new TechniqueViValue().setCode("LAYOUT").setName(TechniqueViConstantEnum.LAYOUT.getTranslate()).setValue("64").setPosition("{\"top\":\"184px\",\"left\":\"72px\"}"));
                            add(new TechniqueViValue().setCode("THEME").setName(TechniqueViConstantEnum.THEME.getTranslate()).setValue("2").setPosition("{\"top\":\"200px\",\"left\":\"6px\"}"));
                            add(new TechniqueViValue().setCode("INTERACTIVE_COMPONENT").setName(TechniqueViConstantEnum.INTERACTIVE_COMPONENT.getTranslate()).setValue("26").setPosition("{\"top\":\"198px\",\"left\":\"152px\"}"));
                            add(new TechniqueViValue().setCode("TEMPLATE").setName(TechniqueViConstantEnum.TEMPLATE.getTranslate()).setValue("76").setPosition("{\"top\":\"228px\",\"left\":\"154px\"}"));
                            add(new TechniqueViValue().setCode("LINK").setName(TechniqueViConstantEnum.LINK.getTranslate()).setValue("11").setPosition("{\"top\":\"259px\",\"left\":\"132px\"}"));
                            add(new TechniqueViValue().setCode("VIEW").setName(TechniqueViConstantEnum.VIEW.getTranslate()).setValue("68").setPosition("{\"top\":\"261px\",\"left\":\"213px\"}"));
                            add(new TechniqueViValue().setCode("DATA").setName(TechniqueViConstantEnum.DATA.getTranslate()).setValue("338").setPosition("{\"top\":\"212px\",\"right\":\"87px\",\"background\":\"rgba(255,255,255,0.90)\",\"border\":\"1px solid #00B9FF\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\",\"borderRadius\":\"4px\",\"fontSize\":\"14px\",\"color\":\"#00B9FF\"}"));
                            add(new TechniqueViValue().setCode("CHART").setName(TechniqueViConstantEnum.CHART.getTranslate()).setValue("17").setPosition("{\"top\":\"150px\",\"right\":\"92px\",\"background\":\"rgba(0,185,255,0.70)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\",\"color\":\"#FFFFFF\"}"));
                            add(new TechniqueViValue().setCode("REPORT").setName(TechniqueViConstantEnum.REPORT.getTranslate()).setValue("57").setPosition("{\"top\":\"179px\",\"right\":\"87px\",\"background\":\"rgba(0,185,255,0.70)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\",\"color\":\"#FFFFFF\"}"));
                            add(new TechniqueViValue().setCode("SCREEN").setName(TechniqueViConstantEnum.SCREEN.getTranslate()).setValue("265").setPosition("{\"top\":\"212px\",\"right\":\"171px\",\"background\":\"rgba(0,185,255,0.70)\",\"boxShadow\":\"0px 2px 4px 0px rgba(30,62,124,0.15)\",\"color\":\"#FFFFFF\"}"));
                        }})
                .setHpaPaaSList(
                        new ArrayList<TechniqueViValue>() {{
                            add(new TechniqueViValue().setCode("STORAGE").setName(TechniqueViConstantEnum.STORAGE.getTranslate()).setValue("67").setPosition("{\"top\":\"65px\",\"left\":\"101px\"}"));
                            add(new TechniqueViValue().setCode("CACHE").setName(TechniqueViConstantEnum.CACHE.getTranslate()).setValue("322").setPosition("{\"top\":\"100px\",\"right\":\"62px\"}"));
                            add(new TechniqueViValue().setCode("DATA_SOURCE").setName(TechniqueViConstantEnum.DATA_SOURCE.getTranslate()).setValue("87").setPosition("{\"top\":\"136px\",\"left\":\"29px\"}"));
                            add(new TechniqueViValue().setCode("SEARCH").setName(TechniqueViConstantEnum.SEARCH.getTranslate()).setValue("1742").setPosition("{\"top\":\"191px\",\"left\":\"183px\"}"));
                            add(new TechniqueViValue().setCode("DISTRIBUTE_CONFIG").setName(TechniqueViConstantEnum.DISTRIBUTE_CONFIG.getTranslate()).setValue("23").setPosition("{\"top\":\"52px\",\"right\":\"149px\"}"));
                            add(new TechniqueViValue().setCode("MICRO_SERVICE").setName(TechniqueViConstantEnum.MICRO_SERVICE.getTranslate()).setValue("65").setPosition("{\"top\":\"116px\",\"right\":\"188px\"}"));
                        }})
                .setAPaaSList(
                        new ArrayList<TechniqueViValue>() {{
                            add(new TechniqueViValue().setCode("BUSINESS").setName(TechniqueViConstantEnum.BUSINESS.getTranslate()).setValue("85").setPosition("{\"left\":\"78px\",\"top\":\"57px\"}"));
                            add(new TechniqueViValue().setCode("TERMINAL").setName(TechniqueViConstantEnum.TERMINAL.getTranslate()).setValue("54").setPosition("{\"right\":\"34px\",\"top\":\"47px\"}"));
                            add(new TechniqueViValue().setCode("LINE").setName(TechniqueViConstantEnum.LINE.getTranslate()).setValue("14").setPosition("{\"left\":\"118px\",\"top\":\"145px\"}"));
                            add(new TechniqueViValue().setCode("APPLICATION").setName(TechniqueViConstantEnum.APPLICATION.getTranslate()).setValue("3").setPosition("{\"right\":\"14px\",\"top\":\"145px\"}"));
                        }})
                ;
    }
}

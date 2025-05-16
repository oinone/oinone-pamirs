package pro.shushi.pamirs.business.api.enumeration;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

/**
 * EntType
 *
 * @author yakir on 2022/09/13 16:49.
 */
@Dict(dictionary = EntType.dict, displayName = "企业-行业类型")
public enum EntType implements IEnum<String> {

    ENT_HU_LIAN_WANG_XIN_XI_JI_SHU("ENT_HU_LIAN_WANG_XIN_XI_JI_SHU", "互联网/信息技术", "互联网/信息技术", 0, null),
    ENT_JI_SUAN_JI_RUAN_JIAN("ENT_JI_SUAN_JI_RUAN_JIAN", "计算机软件", "计算机软件", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_YING_JIAN_SHE_SHI_FU_WU("ENT_YING_JIAN_SHE_SHI_FU_WU", "硬件设施服务", "硬件设施服务", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_DIAN_ZI_SHANG_WU("ENT_DIAN_ZI_SHANG_WU", "电子商务", "电子商务", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_YOU_XI("ENT_YOU_XI", "游戏", "游戏", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_QI_YE_YING_YONG("ENT_QI_YE_YING_YONG", "企业应用", "企业应用", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_JI_SHI_TONG_XUN("ENT_JI_SHI_TONG_XUN", "即时通讯", "即时通讯", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_YING_YIN_SHI_PIN("ENT_YING_YIN_SHI_PIN", "影音视频", "影音视频", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_SHE_JIAO_WANG_LUO("ENT_SHE_JIAO_WANG_LUO", "社交网络", "社交网络", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_WANG_LUO_AN_QUAN("ENT_WANG_LUO_AN_QUAN", "网络安全", "网络安全", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_RUAN_JIAN_KAI_FA("ENT_RUAN_JIAN_KAI_FA", "软件开发", "软件开发", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_DIAN_XIN_YUN_YING_FU_WU("ENT_DIAN_XIN_YUN_YING_FU_WU", "电信运营服务", "电信运营服务", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_XI_TONG_JI_CHENG("ENT_XI_TONG_JI_CHENG", "系统集成", "系统集成", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_GUANG_BO_DIAN_SHI_CHUAN_SHU("ENT_GUANG_BO_DIAN_SHI_CHUAN_SHU", "广播电视传输", "广播电视传输", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_WEI_XING_CHUAN_SHU_FU_WU("ENT_WEI_XING_CHUAN_SHU_FU_WU", "卫星传输服务", "卫星传输服务", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_QI_TA_RUAN_JIAN_FU_WU("ENT_QI_TA_RUAN_JIAN_FU_WU", "其他软件服务", "其他软件服务", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_XIN_XI_JI_SHU_ZI_XUN("ENT_XIN_XI_JI_SHU_ZI_XUN", "信息技术咨询", "信息技术咨询", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),
    ENT_QI_TA_HU_LIAN_WANG("ENT_QI_TA_HU_LIAN_WANG", "其他互联网", "其他互联网", 1, ENT_HU_LIAN_WANG_XIN_XI_JI_SHU),

    ENT_PEI_XUN_FU_WU("ENT_PEI_XUN_FU_WU", "培训服务", "培训服务", 0, null),
    ENT_PEI_XUN_FU_WU_JI_GOU("ENT_PEI_XUN_FU_WU_JI_GOU", "培训服务机构", "培训服务机构", 1, ENT_PEI_XUN_FU_WU),
    ENT_ZAI_XIAN_PEI_XUN("ENT_ZAI_XIAN_PEI_XUN", "在线培训", "在线培训", 1, ENT_PEI_XUN_FU_WU),
    ENT_ZHI_YE_PEI_XUN("ENT_ZHI_YE_PEI_XUN", "职业培训", "职业培训", 1, ENT_PEI_XUN_FU_WU),
    ENT_QI_TA_PEI_XUN_FU_WU("ENT_QI_TA_PEI_XUN_FU_WU", "其他培训服务", "其他培训服务", 1, ENT_PEI_XUN_FU_WU),

    ENT_JIAO_YU_HANG_YE("ENT_JIAO_YU_HANG_YE", "教育行业", "教育行业", 0, null),
    ENT_XUE_QIAN_JIAO_YU("ENT_XUE_QIAN_JIAO_YU", "学前教育", "学前教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_CHU_ZHONG_DENG_JIAO_YU("ENT_CHU_ZHONG_DENG_JIAO_YU", "初中等教育", "初中等教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_ZHONG_DENG_ZHI_YE_JIAO_YU("ENT_ZHONG_DENG_ZHI_YE_JIAO_YU", "中等职业教育", "中等职业教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_GAO_DENG_ZHI_YE_JIAO_YU("ENT_GAO_DENG_ZHI_YE_JIAO_YU", "高等职业教育", "高等职业教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_GAO_DENG_JIAO_YU("ENT_GAO_DENG_JIAO_YU", "高等教育", "高等教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_JIAO_YU_PEI_XUN_JI_GOU("ENT_JIAO_YU_PEI_XUN_JI_GOU", "教育培训机构", "教育培训机构", 1, ENT_JIAO_YU_HANG_YE),
    ENT_JI_NENG_PEI_XUN("ENT_JI_NENG_PEI_XUN", "技能培训", "技能培训", 1, ENT_JIAO_YU_HANG_YE),
    ENT_ZAI_XIAN_JIAO_YU("ENT_ZAI_XIAN_JIAO_YU", "在线教育", "在线教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_TE_SHU_JIAO_YU("ENT_TE_SHU_JIAO_YU", "特殊教育", "特殊教育", 1, ENT_JIAO_YU_HANG_YE),
    ENT_JIAO_YU_FU_WU_JI_GOU("ENT_JIAO_YU_FU_WU_JI_GOU", "教育服务机构", "教育服务机构", 1, ENT_JIAO_YU_HANG_YE),
    ENT_JIAO_YU_HANG_ZHENG_JI_GOU("ENT_JIAO_YU_HANG_ZHENG_JI_GOU", "教育行政机构", "教育行政机构", 1, ENT_JIAO_YU_HANG_YE),
    ENT_JIAO_YU_YAN_JIU_JI_GOU("ENT_JIAO_YU_YAN_JIU_JI_GOU", "教育研究机构", "教育研究机构", 1, ENT_JIAO_YU_HANG_YE),
    ENT_KE_HOU_FU_WU("ENT_KE_HOU_FU_WU", "课后服务", "课后服务", 1, ENT_JIAO_YU_HANG_YE),
    ENT_QI_TA_JIAO_YU("ENT_QI_TA_JIAO_YU", "其他教育", "其他教育", 1, ENT_JIAO_YU_HANG_YE),

    ENT_ZHI_ZAO_YE("ENT_ZHI_ZAO_YE", "制造业", "制造业", 0, null),
    ENT_FANG_ZHI_YE("ENT_FANG_ZHI_YE", "纺织业", "纺织业", 1, ENT_ZHI_ZAO_YE),
    ENT_PI_GE_MAO_YU_XIE("ENT_PI_GE_MAO_YU_XIE", "皮/革/毛/羽/鞋", "皮/革/毛/羽/鞋羽/鞋", 1, ENT_ZHI_ZAO_YE),
    ENT_MU_CAI_CAO_BIAN_ZHI_ZAO("ENT_MU_CAI_CAO_BIAN_ZHI_ZAO", "木材草编制造", "木材草编制造", 1, ENT_ZHI_ZAO_YE),
    ENT_ZAO_ZHI_HE_ZHI_ZHI_PIN("ENT_ZAO_ZHI_HE_ZHI_ZHI_PIN", "造纸和纸制品", "造纸和纸制品", 1, ENT_ZHI_ZAO_YE),
    ENT_SHI_HUA_LIAN_JIAO_RAN_LIAO("ENT_SHI_HUA_LIAN_JIAO_RAN_LIAO", "石化/炼焦/燃料", "石化/炼焦/燃料", 1, ENT_ZHI_ZAO_YE),
    ENT_HUA_GONG_YUAN_LIAO_ZHI_PIN("ENT_HUA_GONG_YUAN_LIAO_ZHI_PIN", "化工原料制品", "化工原料制品", 1, ENT_ZHI_ZAO_YE),
    ENT_XIANG_JIAO_HE_SU_LIAO("ENT_XIANG_JIAO_HE_SU_LIAO", "橡胶和塑料", "橡胶和塑料", 1, ENT_ZHI_ZAO_YE),
    ENT_FEI_JIN_SHU_KUANG_WU("ENT_FEI_JIN_SHU_KUANG_WU", "非金属矿物", "非金属矿物", 1, ENT_ZHI_ZAO_YE),
    ENT_HEI_SE_JIN_SHU_YE_LIAN("ENT_HEI_SE_JIN_SHU_YE_LIAN", "黑色金属冶炼", "黑色金属冶炼", 1, ENT_ZHI_ZAO_YE),
    ENT_YOU_SE_JIN_SHU_YE_LIAN("ENT_YOU_SE_JIN_SHU_YE_LIAN", "有色金属冶炼", "有色金属冶炼", 1, ENT_ZHI_ZAO_YE),
    ENT_TONG_YONG_SHE_BEI_ZHI_ZAO("ENT_TONG_YONG_SHE_BEI_ZHI_ZAO", "通用设备制造", "通用设备制造", 1, ENT_ZHI_ZAO_YE),
    ENT_ZHUAN_YONG_SHE_BEI_ZHI_ZAO("ENT_ZHUAN_YONG_SHE_BEI_ZHI_ZAO", "专用设备制造", "专用设备制造", 1, ENT_ZHI_ZAO_YE),
    ENT_QI_TA_YUN_SHU_SHE_BEI("ENT_QI_TA_YUN_SHU_SHE_BEI", "其他运输设备", "其他运输设备", 1, ENT_ZHI_ZAO_YE),
    ENT_DIAN_QI_JI_XIE_QI_CAI("ENT_DIAN_QI_JI_XIE_QI_CAI", "电气机械/器材", "电气机械/器材电气机械/器材", 1, ENT_ZHI_ZAO_YE),
    ENT_YI_QI_YI_BIAO_ZHI_ZAO("ENT_YI_QI_YI_BIAO_ZHI_ZAO", "仪器仪表制造", "仪器仪表制造", 1, ENT_ZHI_ZAO_YE),
    ENT_FEI_QI_ZI_YUAN_LI_YONG("ENT_FEI_QI_ZI_YUAN_LI_YONG", "废弃资源利用", "废弃资源利用", 1, ENT_ZHI_ZAO_YE),
    ENT_JIN_SHU_JI_XIE_XIU_LI("ENT_JIN_SHU_JI_XIE_XIU_LI", "金属/机械修理", "金属/机械修理金属/机械修理", 1, ENT_ZHI_ZAO_YE),
    ENT_YIN_SHUA_HE_JI_LU_MEI_JIE_FU_ZHI_YE("ENT_YIN_SHUA_HE_JI_LU_MEI_JIE_FU_ZHI_YE", "印刷和记录媒介复制业", "印刷和记录媒介复制业", 1, ENT_ZHI_ZAO_YE),
    ENT_YI_YAO_ZHI_ZAO_YE("ENT_YI_YAO_ZHI_ZAO_YE", "医药制造业", "医药制造业", 1, ENT_ZHI_ZAO_YE),
    ENT_NONG_FU_SHI_PIN_JIA_GONG_YE("ENT_NONG_FU_SHI_PIN_JIA_GONG_YE", "农副食品加工业", "农副食品加工业", 1, ENT_ZHI_ZAO_YE),
    ENT_QI_TA_ZHI_ZAO_YE("ENT_QI_TA_ZHI_ZAO_YE", "其他制造业", "其他制造业", 1, ENT_ZHI_ZAO_YE),

    ENT_MAO_YI_PI_FA_LING_SHOU("ENT_MAO_YI_PI_FA_LING_SHOU", "贸易/批发/零售", "贸易/批发/零售", 0, null),
    ENT_LIN_SHI_CU_XIAO("ENT_LIN_SHI_CU_XIAO", "临时促销", "临时促销", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_JIN_CHU_KOU("ENT_JIN_CHU_KOU", "进出口", "进出口", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_PI_FA("ENT_PI_FA", "批发", "批发", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_SHANG_DIAN_CHAO_SHI("ENT_SHANG_DIAN_CHAO_SHI", "商店/超市", "商店/超市商店/超市", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_DIAN_SHI_DIAN_HUA_XIAO_SHOU("ENT_DIAN_SHI_DIAN_HUA_XIAO_SHOU", "电视/电话销售", "电视/电话销售电视/电话销售", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_LING_SHOU("ENT_LING_SHOU", "零售", "零售", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_LING_SHOU_FU_WU("ENT_LING_SHOU_FU_WU", "零售服务", "零售服务", 1, ENT_MAO_YI_PI_FA_LING_SHOU),
    ENT_QI_TA_MAO_YI("ENT_QI_TA_MAO_YI", "其他贸易", "其他贸易", 1, ENT_MAO_YI_PI_FA_LING_SHOU),

    ENT_FANG_DI_CHAN_YE("ENT_FANG_DI_CHAN_YE", "房地产业", "房地产业", 0, null),
    ENT_FANG_DI_CHAN_KAI_FA("ENT_FANG_DI_CHAN_KAI_FA", "房地产开发", "房地产开发", 1, ENT_FANG_DI_CHAN_YE),
    ENT_FANG_DI_CHAN_ZHONG_JIE("ENT_FANG_DI_CHAN_ZHONG_JIE", "房地产中介", "房地产中介", 1, ENT_FANG_DI_CHAN_YE),
    ENT_FANG_DI_CHAN_DAI_LI("ENT_FANG_DI_CHAN_DAI_LI", "房地产代理", "房地产代理", 1, ENT_FANG_DI_CHAN_YE),
    ENT_FANG_DI_CHAN_CE_HUA("ENT_FANG_DI_CHAN_CE_HUA", "房地产策划", "房地产策划", 1, ENT_FANG_DI_CHAN_YE),
    ENT_FANG_DI_CHAN_ZU_LIN("ENT_FANG_DI_CHAN_ZU_LIN", "房地产租赁", "房地产租赁", 1, ENT_FANG_DI_CHAN_YE),
    ENT_WU_YE_GUAN_LI("ENT_WU_YE_GUAN_LI", "物业管理", "物业管理", 1, ENT_FANG_DI_CHAN_YE),
    ENT_SHANG_YE_DI_CHAN("ENT_SHANG_YE_DI_CHAN", "商业地产", "商业地产", 1, ENT_FANG_DI_CHAN_YE),
    ENT_ZHU_ZHAI_DI_CHAN("ENT_ZHU_ZHAI_DI_CHAN", "住宅地产", "住宅地产", 1, ENT_FANG_DI_CHAN_YE),
    ENT_GONG_YE_DI_CHAN("ENT_GONG_YE_DI_CHAN", "工业地产", "工业地产", 1, ENT_FANG_DI_CHAN_YE),
    ENT_YANG_LAO_DI_CHAN("ENT_YANG_LAO_DI_CHAN", "养老地产", "养老地产", 1, ENT_FANG_DI_CHAN_YE),
    ENT_CHAN_YE_YUAN_QU("ENT_CHAN_YE_YUAN_QU", "产业园区", "产业园区", 1, ENT_FANG_DI_CHAN_YE),
    ENT_DI_CHAN_HANG_ZHENG_JI_GOU("ENT_DI_CHAN_HANG_ZHENG_JI_GOU", "地产行政机构", "地产行政机构", 1, ENT_FANG_DI_CHAN_YE),
    ENT_DI_CHAN_FU_WU_JI_GOU("ENT_DI_CHAN_FU_WU_JI_GOU", "地产服务机构", "地产服务机构", 1, ENT_FANG_DI_CHAN_YE),
    ENT_QI_TA_DI_CHAN("ENT_QI_TA_DI_CHAN", "其他地产", "其他地产", 1, ENT_FANG_DI_CHAN_YE),

    ENT_JIAN_ZHU_YE("ENT_JIAN_ZHU_YE", "建筑业", "建筑业", 0, null),
    ENT_JIAN_ZHU_SHE_JI("ENT_JIAN_ZHU_SHE_JI", "建筑设计", "建筑设计", 1, ENT_JIAN_ZHU_YE),
    ENT_JIAN_ZHU_JIAN_CAI("ENT_JIAN_ZHU_JIAN_CAI", "建筑建材", "建筑建材", 1, ENT_JIAN_ZHU_YE),
    ENT_JIA_JU_JIAN_CAI("ENT_JIA_JU_JIAN_CAI", "家居建材", "家居建材", 1, ENT_JIAN_ZHU_YE),
    ENT_JIAN_ZHU_HANG_ZHENG_JI_GOU("ENT_JIAN_ZHU_HANG_ZHENG_JI_GOU", "建筑行政机构", "建筑行政机构", 1, ENT_JIAN_ZHU_YE),
    ENT_JIAN_ZHU_FU_WU_JI_GOU("ENT_JIAN_ZHU_FU_WU_JI_GOU", "建筑服务机构", "建筑服务机构", 1, ENT_JIAN_ZHU_YE),
    ENT_FANG_WU_JIAN_ZHU("ENT_FANG_WU_JIAN_ZHU", "房屋建筑", "房屋建筑", 1, ENT_JIAN_ZHU_YE),
    ENT_TU_MU_GONG_CHENG("ENT_TU_MU_GONG_CHENG", "土木工程", "土木工程", 1, ENT_JIAN_ZHU_YE),
    ENT_JIAN_ZHU_AN_ZHUANG("ENT_JIAN_ZHU_AN_ZHUANG", "建筑安装", "建筑安装", 1, ENT_JIAN_ZHU_YE),
    ENT_ZHUANG_XIU_ZHUANG_HUANG("ENT_ZHUANG_XIU_ZHUANG_HUANG", "装修装潢", "装修装潢", 1, ENT_JIAN_ZHU_YE),
    ENT_QI_TA_JIAN_ZHU_YE("ENT_QI_TA_JIAN_ZHU_YE", "其他建筑业", "其他建筑业", 1, ENT_JIAN_ZHU_YE),

    ENT_JIN_RONG_YE("ENT_JIN_RONG_YE", "金融业", "金融业", 0, null),
    ENT_YIN_HANG("ENT_YIN_HANG", "银行", "银行", 1, ENT_JIN_RONG_YE),
    ENT_BAO_XIAN("ENT_BAO_XIAN", "保险", "保险", 1, ENT_JIN_RONG_YE),
    ENT_ZHENG_QUAN("ENT_ZHENG_QUAN", "证券", "证券", 1, ENT_JIN_RONG_YE),
    ENT_TOU_ZI("ENT_TOU_ZI", "投资", "投资", 1, ENT_JIN_RONG_YE),
    ENT_JI_JIN("ENT_JI_JIN", "基金", "基金", 1, ENT_JIN_RONG_YE),
    ENT_HU_LIAN_WANG_JIN_RONG("ENT_HU_LIAN_WANG_JIN_RONG", "互联网金融", "互联网金融", 1, ENT_JIN_RONG_YE),
    ENT_QI_TA_JIN_RONG_YE("ENT_QI_TA_JIN_RONG_YE", "其他金融业", "其他金融业", 1, ENT_JIN_RONG_YE),

    ENT_FU_WU_YE("ENT_FU_WU_YE", "服务业", "服务业", 0, null),
    ENT_LIAN_SUO_JIU_DIAN("ENT_LIAN_SUO_JIU_DIAN", "连锁酒店", "连锁酒店", 1, ENT_FU_WU_YE),
    ENT_PU_TONG_JIU_DIAN("ENT_PU_TONG_JIU_DIAN", "普通酒店", "普通酒店", 1, ENT_FU_WU_YE),
    ENT_QI_TA_JIU_DIAN_ZHU_XIU("ENT_QI_TA_JIU_DIAN_ZHU_XIU", "其他酒店住宿", "其他酒店住宿", 1, ENT_FU_WU_YE),
    ENT_PU_TONG_CAN_YIN("ENT_PU_TONG_CAN_YIN", "普通餐饮", "普通餐饮", 1, ENT_FU_WU_YE),
    ENT_LIAN_SUO_CAN_YIN("ENT_LIAN_SUO_CAN_YIN", "连锁餐饮", "连锁餐饮", 1, ENT_FU_WU_YE),
    ENT_WAI_MAI_CAN_YIN("ENT_WAI_MAI_CAN_YIN", "外卖餐饮", "外卖餐饮", 1, ENT_FU_WU_YE),
    ENT_QI_TA_CAN_YIN("ENT_QI_TA_CAN_YIN", "其他餐饮", "其他餐饮", 1, ENT_FU_WU_YE),
    ENT_LU_YOU_JING_DIAN_CHANG_SUO("ENT_LU_YOU_JING_DIAN_CHANG_SUO", "旅游景点/场所", "旅游景点/场所旅游景点/场所", 1, ENT_FU_WU_YE),
    ENT_YU_LE_JIAN_SHEN("ENT_YU_LE_JIAN_SHEN", "娱乐健身", "娱乐健身", 1, ENT_FU_WU_YE),
    ENT_JIA_ZHENG_FU_WU("ENT_JIA_ZHENG_FU_WU", "家政服务", "家政服务", 1, ENT_FU_WU_YE),
    ENT_ZHONG_JIE_DAI_LI("ENT_ZHONG_JIE_DAI_LI", "中介代理", "中介代理", 1, ENT_FU_WU_YE),
    ENT_WANG_BA("ENT_WANG_BA", "网吧", "网吧", 1, ENT_FU_WU_YE),
    ENT_CHU_HANG_JIAO_TONG_FU_WU("ENT_CHU_HANG_JIAO_TONG_FU_WU", "出行交通服务", "出行交通服务", 1, ENT_FU_WU_YE),
    ENT_LU_HANG_SHE("ENT_LU_HANG_SHE", "旅行社", "旅行社", 1, ENT_FU_WU_YE),
    ENT_QI_TA_LU_YOU_FU_WU("ENT_QI_TA_LU_YOU_FU_WU", "其他旅游服务", "其他旅游服务", 1, ENT_FU_WU_YE),
    ENT_KE_PIAO_FU_WU("ENT_KE_PIAO_FU_WU", "客票服务", "客票服务", 1, ENT_FU_WU_YE),
    ENT_QI_TA_FU_WU_YE("ENT_QI_TA_FU_WU_YE", "其他服务业", "其他服务业", 1, ENT_FU_WU_YE),

    ENT_YUN_SHU_WU_LIU_CANG_CHU("ENT_YUN_SHU_WU_LIU_CANG_CHU", "运输/物流/仓储", "运输/物流/仓储", 0, null),
    ENT_CANG_CHU_YE("ENT_CANG_CHU_YE", "仓储业", "仓储业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_DAO_LU_YUN_SHU_YE("ENT_DAO_LU_YUN_SHU_YE", "道路运输业", "道路运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_TIE_LU_YUN_SHU_YE("ENT_TIE_LU_YUN_SHU_YE", "铁路运输业", "铁路运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_SHUI_SHANG_YUN_SHU_YE("ENT_SHUI_SHANG_YUN_SHU_YE", "水上运输业", "水上运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_HANG_KONG_YUN_SHU_YE("ENT_HANG_KONG_YUN_SHU_YE", "航空运输业", "航空运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_GUAN_DAO_YUN_SHU_YE("ENT_GUAN_DAO_YUN_SHU_YE", "管道运输业", "管道运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_QI_TA_YUN_SHU_YE("ENT_QI_TA_YUN_SHU_YE", "其他运输业", "其他运输业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_ZHUANG_XIE_BAN_YUN_YE("ENT_ZHUANG_XIE_BAN_YUN_YE", "装卸搬运业", "装卸搬运业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_YUN_SHU_DAI_LI_YE("ENT_YUN_SHU_DAI_LI_YE", "运输代理业", "运输代理业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_KE_PIAO_DAI_LI_YE("ENT_KE_PIAO_DAI_LI_YE", "客票代理业", "客票代理业", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_YOU_ZHENG_FU_WU("ENT_YOU_ZHENG_FU_WU", "邮政服务", "邮政服务", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_KUAI_DI_FU_WU("ENT_KUAI_DI_FU_WU", "快递服务", "快递服务", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_PEI_SONG_LAN_SHOU_FU_WU("ENT_PEI_SONG_LAN_SHOU_FU_WU", "配送揽收服务", "配送揽收服务", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),
    ENT_QI_TA_KUAI_DI_FU_WU("ENT_QI_TA_KUAI_DI_FU_WU", "其他快递服务", "其他快递服务", 1, ENT_YUN_SHU_WU_LIU_CANG_CHU),

    ENT_WEN_TI_YU_LE_CHUAN_MEI("ENT_WEN_TI_YU_LE_CHUAN_MEI", "文体/娱乐/传媒", "文体/娱乐/传媒", 0, null),
    ENT_XIN_WEN("ENT_XIN_WEN", "新闻", "新闻", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_GUANG_GAO_GONG_GUAN("ENT_GUANG_GAO_GONG_GUAN", "广告/公关", "广告/公关", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_BAO_ZHI_ZA_ZHI("ENT_BAO_ZHI_ZA_ZHI", "报纸/杂志", "报纸/杂志", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_GUANG_BO("ENT_GUANG_BO", "广播", "广播", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_YING_SHI("ENT_YING_SHI", "影视", "影视", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_CHU_BAN("ENT_CHU_BAN", "出版", "出版", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_WEN_HUA_YI_SHU_YE("ENT_WEN_HUA_YI_SHU_YE", "文化艺术业", "文化艺术业", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_TI_YU("ENT_TI_YU", "体育", "体育", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_DONG_MAN("ENT_DONG_MAN", "动漫", "动漫", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_MEI_TI("ENT_MEI_TI", "媒体", "媒体", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_QI_TA_WEN_TI("ENT_QI_TA_WEN_TI", "其他文体", "其他文体", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_QI_TA_YU_LE("ENT_QI_TA_YU_LE", "其他娱乐", "其他娱乐", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),
    ENT_QI_TA_CHUAN_MEI("ENT_QI_TA_CHUAN_MEI", "其他传媒", "其他传媒", 1, ENT_WEN_TI_YU_LE_CHUAN_MEI),

    ENT_SHANG_YE_FU_WU_ZU_LIN("ENT_SHANG_YE_FU_WU_ZU_LIN", "商业服务/租赁", "商业服务/租赁", 0, null),
    ENT_HUI_JI_SHEN_JI_FU_WU("ENT_HUI_JI_SHEN_JI_FU_WU", "会计审计服务", "会计审计服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_REN_LI_ZI_YUAN_FU_WU("ENT_REN_LI_ZI_YUAN_FU_WU", "人力资源服务", "人力资源服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_GUAN_LI_ZI_XUN_FU_WU("ENT_GUAN_LI_ZI_XUN_FU_WU", "管理咨询服务", "管理咨询服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_FA_LU_FU_WU("ENT_FA_LU_FU_WU", "法律服务", "法律服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_JIAN_CE_REN_ZHENG_FU_WU("ENT_JIAN_CE_REN_ZHENG_FU_WU", "检测认证服务", "检测认证服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_FAN_YI_FU_WU("ENT_FAN_YI_FU_WU", "翻译服务", "翻译服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_ZI_XUN_DIAO_CHA_FU_WU("ENT_ZI_XUN_DIAO_CHA_FU_WU", "咨询调查服务", "咨询调查服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_ZHI_SHI_CHAN_QUAN_FU_WU("ENT_ZHI_SHI_CHAN_QUAN_FU_WU", "知识产权服务", "知识产权服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_AN_QUAN_BAO_HU_FU_WU("ENT_AN_QUAN_BAO_HU_FU_WU", "安全保护服务", "安全保护服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_QI_TA_SHANG_WU_FU_WU("ENT_QI_TA_SHANG_WU_FU_WU", "其他商务服务", "其他商务服务", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_JI_XIE_SHE_BEI_ZU_LIN("ENT_JI_XIE_SHE_BEI_ZU_LIN", "机械设备租赁", "机械设备租赁", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_WEN_HUA_YONG_PIN_ZU_LIN("ENT_WEN_HUA_YONG_PIN_ZU_LIN", "文化用品租赁", "文化用品租赁", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),
    ENT_QI_TA_ZU_LIN("ENT_QI_TA_ZU_LIN", "其他租赁", "其他租赁", 1, ENT_SHANG_YE_FU_WU_ZU_LIN),

    ENT_YI_LIAO_YI_YAO("ENT_YI_LIAO_YI_YAO", "医疗医药", "医疗医药", 0, null),
    ENT_JI_CENG_YI_LIAO_WEI_SHENG_JI_GOU("ENT_JI_CENG_YI_LIAO_WEI_SHENG_JI_GOU", "基层医疗卫生机构", "基层医疗卫生机构", 1, ENT_YI_LIAO_YI_YAO),
    ENT_MIN_YING_YI_YUAN("ENT_MIN_YING_YI_YUAN", "民营医院", "民营医院", 1, ENT_YI_LIAO_YI_YAO),
    ENT_GONG_LI_YI_YUAN("ENT_GONG_LI_YI_YUAN", "公立医院", "公立医院", 1, ENT_YI_LIAO_YI_YAO),
    ENT_SHE_QU_YI_YUAN("ENT_SHE_QU_YI_YUAN", "社区医院", "社区医院", 1, ENT_YI_LIAO_YI_YAO),
    ENT_YI_LIAO_RUAN_JIAN("ENT_YI_LIAO_RUAN_JIAN", "医疗软件", "医疗软件", 1, ENT_YI_LIAO_YI_YAO),
    ENT_MEI_RONG_ZHENG_XING("ENT_MEI_RONG_ZHENG_XING", "美容整形", "美容整形", 1, ENT_YI_LIAO_YI_YAO),
    ENT_YAO_PIN_QI_XIE("ENT_YAO_PIN_QI_XIE", "药品器械", "药品器械", 1, ENT_YI_LIAO_YI_YAO),
    ENT_JIAN_KANG_GUAN_LI("ENT_JIAN_KANG_GUAN_LI", "健康管理", "健康管理", 1, ENT_YI_LIAO_YI_YAO),
    ENT_YAO_DIAN_LIAN_SUO("ENT_YAO_DIAN_LIAN_SUO", "药店连锁", "药店连锁", 1, ENT_YI_LIAO_YI_YAO),
    ENT_YI_LIAO_CHUANG_XIN("ENT_YI_LIAO_CHUANG_XIN", "医疗创新", "医疗创新", 1, ENT_YI_LIAO_YI_YAO),
    ENT_JI_BING_FANG_KONG_JI_GOU("ENT_JI_BING_FANG_KONG_JI_GOU", "疾病防控机构", "疾病防控机构", 1, ENT_YI_LIAO_YI_YAO),
    ENT_HU_LI_XIU_YANG_JI_GOU("ENT_HU_LI_XIU_YANG_JI_GOU", "护理休养机构", "护理休养机构", 1, ENT_YI_LIAO_YI_YAO),
    ENT_QI_TA_YI_LIAO_JI_GOU("ENT_QI_TA_YI_LIAO_JI_GOU", "其他医疗机构", "其他医疗机构", 1, ENT_YI_LIAO_YI_YAO),

    ENT_KE_YAN_FU_WU("ENT_KE_YAN_FU_WU", "科研服务", "科研服务", 0, null),
    ENT_SHI_YAN_YAN_JIU_SUO("ENT_SHI_YAN_YAN_JIU_SUO", "试验/研究所", "试验/研究所", 1, ENT_KE_YAN_FU_WU),
    ENT_ZHUAN_YE_JI_SHU_FU_WU("ENT_ZHUAN_YE_JI_SHU_FU_WU", "专业技术服务", "专业技术服务", 1, ENT_KE_YAN_FU_WU),
    ENT_KE_JI_TUI_GUANG_YING_YONG("ENT_KE_JI_TUI_GUANG_YING_YONG", "科技推广应用", "科技推广应用", 1, ENT_KE_YAN_FU_WU),

    ENT_GONG_GONG_HUAN_JING("ENT_GONG_GONG_HUAN_JING", "公共/环境", "公共/环境", 0, null),
    ENT_SHENG_TAI_HUAN_JING_ZHI_LI("ENT_SHENG_TAI_HUAN_JING_ZHI_LI", "生态环境治理", "生态环境治理", 1, ENT_GONG_GONG_HUAN_JING),
    ENT_SHUI_LI_GUAN_LI("ENT_SHUI_LI_GUAN_LI", "水利管理", "水利管理", 1, ENT_GONG_GONG_HUAN_JING),

    ENT_JU_MIN_FU_WU("ENT_JU_MIN_FU_WU", "居民服务", "居民服务", 0, null),
    ENT_JU_MIN_FU_WU_YE("ENT_JU_MIN_FU_WU_YE", "居民服务业", "居民服务业", 1, ENT_JU_MIN_FU_WU),
    ENT_CHAN_PIN_XIU_LI_YE("ENT_CHAN_PIN_XIU_LI_YE", "产品修理业", "产品修理业", 1, ENT_JU_MIN_FU_WU),
    ENT_JU_MING_QI_TA_FU_WU_YE("ENT_QI_TA_FU_WU_YE", "其他服务业", "其他服务业", 1, ENT_JU_MIN_FU_WU),
    ENT_SHU_ZI_SHE_QU("ENT_SHU_ZI_SHE_QU", "数字社区", "数字社区", 1, ENT_JU_MIN_FU_WU),
    ENT_SHU_ZI_XIAO_QU("ENT_SHU_ZI_XIAO_QU", "数字小区", "数字小区", 1, ENT_JU_MIN_FU_WU),

    ENT_KAI_CAI_YE("ENT_KAI_CAI_YE", "开采业", "开采业", 0, null),
    ENT_MEI_TAN("ENT_MEI_TAN", "煤炭", "煤炭", 1, ENT_KAI_CAI_YE),
    ENT_SHI_YOU_TIAN_RAN_QI_LIAN_HUA_SHI_HUA("ENT_SHI_YOU_TIAN_RAN_QI_LIAN_HUA_SHI_HUA", "石油/天然气/炼化/石化", "石油/天然气/炼化/石化", 1, ENT_KAI_CAI_YE),
    ENT_HEI_SE_JIN_SHU("ENT_HEI_SE_JIN_SHU", "黑色金属", "黑色金属", 1, ENT_KAI_CAI_YE),
    ENT_YOU_SE_JIN_SHU("ENT_YOU_SE_JIN_SHU", "有色金属", "有色金属", 1, ENT_KAI_CAI_YE),
    ENT_FEI_JIN_SHU("ENT_FEI_JIN_SHU", "非金属", "非金属", 1, ENT_KAI_CAI_YE),
    ENT_KAI_CAI_FU_ZHU_HUO_DONG("ENT_KAI_CAI_FU_ZHU_HUO_DONG", "开采辅助活动", "开采辅助活动", 1, ENT_KAI_CAI_YE),
    ENT_QI_TA_CAI_KUANG_YE("ENT_QI_TA_CAI_KUANG_YE", "其他采矿业", "其他采矿业", 1, ENT_KAI_CAI_YE),

    ENT_NONG_LIN_MU_YU("ENT_NONG_LIN_MU_YU", "农/林/牧/渔", "农/林/牧/渔", 0, null),
    ENT_NONG_YE("ENT_NONG_YE", "农业", "农业", 1, ENT_NONG_LIN_MU_YU),
    ENT_LIN_YE("ENT_LIN_YE", "林业", "林业", 1, ENT_NONG_LIN_MU_YU),
    ENT_CHU_MU_YE("ENT_CHU_MU_YE", "畜牧业", "畜牧业", 1, ENT_NONG_LIN_MU_YU),
    ENT_YU_YE("ENT_YU_YE", "渔业", "渔业", 1, ENT_NONG_LIN_MU_YU),
    ENT_NONG_LIN_MU_YU_FU_WU("ENT_NONG_LIN_MU_YU_FU_WU", "农林牧渔服务", "农林牧渔服务", 1, ENT_NONG_LIN_MU_YU),

    ENT_DIAN_RE_RAN_QI_SHUI_GONG_YING("ENT_DIAN_RE_RAN_QI_SHUI_GONG_YING", "电/热/燃气/水供应", "电/热/燃气/水供应", 0, null),
    ENT_DIAN_RE_SHENG_CHAN_GONG_YING("ENT_DIAN_RE_SHENG_CHAN_GONG_YING", "电/热生产供应", "电/热生产供应", 1, ENT_DIAN_RE_RAN_QI_SHUI_GONG_YING),
    ENT_RAN_QI_SHENG_CHAN_GONG_YING("ENT_RAN_QI_SHENG_CHAN_GONG_YING", "燃气生产供应", "燃气生产供应", 1, ENT_DIAN_RE_RAN_QI_SHUI_GONG_YING),
    ENT_SHUI_SHENG_CHAN_GONG_YING("ENT_SHUI_SHENG_CHAN_GONG_YING", "水生产供应", "水生产供应", 1, ENT_DIAN_RE_RAN_QI_SHUI_GONG_YING),

    ;


    public static final String dict = "business.EntType";

    private final String  value;
    private final String  displayName;
    private final String  help;
    private final Integer depth;
    private final EntType parent;

    EntType(String value, String displayName, String help, Integer depth, EntType parent) {
        this.value       = value;
        this.displayName = displayName;
        this.help        = help;
        this.depth       = depth;
        this.parent      = parent;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHelp() {
        return help;
    }

    public Integer getDepth() {
        return depth;
    }

    public EntType getParent() {
        return parent;
    }
}

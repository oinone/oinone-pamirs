package pro.shushi.pamirs.meta.api;

/**
 * 通用接口
 * <p>
 * Generator    结尾的接口，代表生成基本类型数据之意
 * Checker      结尾的接口，代表简单的校验规则之意
 * Validator    结尾的接口，代表较复杂的校验之意
 * Processor    结尾的接口，代表较复杂的处理逻辑之意
 * Iterator     结尾的接口，代表遍历处理数据之意，返回可遍历容器或迭代器
 * Converter    结尾的接口，代表转换数据之意
 * <p>
 * generate     开头的方法，代表生成之意
 * default      开头的方法，代表默认取值之意，使用该方法取值后需要其他方法逻辑来补充该取值规则
 * fetch        开头的方法，代表取值之意，比较完善的逻辑
 * validate     开头的方法，代表校验之意
 * convert      开头的方法，代表转换之意
 * priority     开头的方法，代表优先级之意
 * unique       开头的方法，代表唯一键之意
 * list         开头的方法，代表获取列表之意
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/4 2:11 上午
 */
public interface CommonApi {

}

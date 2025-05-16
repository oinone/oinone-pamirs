package pro.shushi.pamirs.message.engine;

import pro.shushi.pamirs.message.model.MessageSource;

/**
 * 1.已有选择服务器
 * 2.未选择服务器，按照sequence装载
 */
public interface IMessageEngine<T> {

    T get(MessageSource messageSource);

}

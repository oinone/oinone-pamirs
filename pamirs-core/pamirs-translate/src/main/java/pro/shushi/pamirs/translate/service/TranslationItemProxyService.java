package pro.shushi.pamirs.translate.service;

import pro.shushi.pamirs.resource.api.model.ResourceTranslation;
import pro.shushi.pamirs.translate.proxy.TranslationItemProxy;

import java.util.List;

/**
 * @author: xuxin
 * @createTime: 2024/05/12 10:47
 */
public interface TranslationItemProxyService {

    /**
     * 创建翻译资源 包含翻译资源项
     *
     * @param data 翻译资源
     */
    void create(ResourceTranslation data);

    /**
     * 确定并刷新 批量
     *
     * @param data        翻译资源
     * @param resLangCode 源语言
     */
    void createOrUpdateAndRefreshBatch(ResourceTranslation data, String resLangCode);

    /**
     * 刷新远程资源
     */
    void refreshRemoteResource();

    /**
     * 工具箱添加翻译打开显示
     *
     * @param data         翻译资源项
     * @param langCode     目标语言
     * @param resLangCode  源语言
     * @param searchOrigin 匹配源术语
     */
    List<TranslationItemProxy> queryByInsertPage(TranslationItemProxy data, String langCode, String resLangCode, String searchOrigin);

    /**
     * 工具箱更改翻译打开显示
     *
     * @param data         翻译资源项
     * @param langCode     目标语言
     * @param resLangCode  源语言
     * @param searchTarget 匹配翻译值
     * @return
     */
    List<TranslationItemProxy> queryByChangePage(TranslationItemProxy data, String langCode, String resLangCode, String searchTarget);

    /**
     * 创建翻译资源项
     *
     * @param data        翻译资源项
     * @param resLangCode 源语言
     */
    void createTranslationResourceItem(TranslationItemProxy data, String resLangCode);

    /**
     * 修改翻译资源项
     *
     * @param data        翻译资源项
     * @param resLangCode 源语言
     */
    void updateTranslationResourceItem(TranslationItemProxy data, String resLangCode);
}

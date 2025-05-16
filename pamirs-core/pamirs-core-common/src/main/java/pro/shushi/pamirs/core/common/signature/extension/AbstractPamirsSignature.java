package pro.shushi.pamirs.core.common.signature.extension;

import pro.shushi.pamirs.core.common.signature.PamirsSignature;

/**
 * 抽象签名对象，内置不可修改的签名属性
 *
 * @author Adamancy Zhang on 2021-05-09 14:29
 */
public abstract class AbstractPamirsSignature implements PamirsSignature {

    private final String signature;

    public AbstractPamirsSignature() {
        this(null);
    }

    public AbstractPamirsSignature(String signature) {
        if (signature == null) {
            this.signature = this.getClass().getName();
        } else {
            this.signature = signature;
        }
    }

    @Override
    public String signature() {
        return signature;
    }
}

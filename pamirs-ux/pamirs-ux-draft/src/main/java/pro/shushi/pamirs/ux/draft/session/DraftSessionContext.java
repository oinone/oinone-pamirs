package pro.shushi.pamirs.ux.draft.session;

import pro.shushi.pamirs.ux.draft.model.Draft;
import pro.shushi.pamirs.meta.api.dto.fun.Function;

/**
 * 草稿会话上下文
 *
 * @author Adamancy Zhang at 11:53 on 2025-10-21
 */
public class DraftSessionContext {

    private final Draft draft;

    private final Function delettFunction;

    public DraftSessionContext(Draft draft, Function delettFunction) {
        this.draft = draft;
        this.delettFunction = delettFunction;
    }

    public Draft getDraft() {
        return draft;
    }

    public Function getDelettFunction() {
        return delettFunction;
    }
}
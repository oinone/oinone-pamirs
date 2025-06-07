package pro.shushi.pamirs.user.api.login;

import pro.shushi.pamirs.meta.api.dto.model.PamirsUserDTO;
import pro.shushi.pamirs.meta.base.IdModel;

/**
 * @author shier
 * date 2020/4/11
 */
public interface IUserLogin<T extends IdModel> {

    Object login(T t);

    PamirsUserDTO fetchUserIdByReq();

    String type();
}

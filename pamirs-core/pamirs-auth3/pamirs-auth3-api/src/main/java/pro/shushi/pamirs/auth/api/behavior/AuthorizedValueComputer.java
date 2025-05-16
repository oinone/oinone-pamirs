package pro.shushi.pamirs.auth.api.behavior;

/**
 * 授权值计算
 *
 * @author Adamancy Zhang at 18:21 on 2024-02-04
 */
@FunctionalInterface
public interface AuthorizedValueComputer {

    AuthorizedValueComputer AUTHORIZE = (c, t) -> c | t;

    AuthorizedValueComputer REVOKE = (c, t) -> c & ~t;

    AuthorizedValueComputer UPDATE = (c, t) -> t;

    long compute(long currentAuthorizedValue, long targetAuthorizedValue);
}
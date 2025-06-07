package pro.shushi.pamirs.core.common.behavior;

/**
 * create uid and write uid convert create username and write username behavior model
 * <p>
 * Model Class using {@code implements IUserNameModel} enable behavior operation.
 * <p>
 * Model Class add createUserName and writeUserName using behavior operation result.
 * <p>
 * {@code @Field.String}<br>
 * {@code @Field(displayName = "Create User", store = NullableBoolEnum.FALSE, translate = true)}<br>
 * {@code private String createUserName;}
 * <p>
 * {@code @Field.String}<br>
 * {@code @Field(displayName = "Lasted Update User", store = NullableBoolEnum.FALSE, translate = true)}<br>
 * {@code private String writeUserName;}
 *
 * @author Adamancy Zhang at 09:52 on 2024-03-21
 */
public interface IUserNameModel {

    Long getCreateUid();

    Long getWriteUid();

    String getCreateUserName();

    <T> T setCreateUserName(String userName);

    String getWriteUserName();

    <T> T setWriteUserName(String userName);
}

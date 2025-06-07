package pro.shushi.pamirs.framework.compute.process.common;

import pro.shushi.pamirs.meta.api.dto.common.Message;
import pro.shushi.pamirs.meta.api.dto.common.Result;
import pro.shushi.pamirs.meta.api.session.PamirsSession;
import pro.shushi.pamirs.meta.enmu.InformationLevelEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 计算帮助类
 * <p>
 * 2021/3/12 12:45 下午
 *
 * @author d@shushi.pro
 * @version 1.0.0
 */
public class ComputeHelper {

    public static String limitString(String content) {
        if (null == content) {
            return null;
        }
        if (content.length() > 1024) {
            return content.substring(0, 1024) + "...";
        }
        return content;
    }

    public static Result<Void> generateCheckResult(Boolean isSuccess, Supplier<String> infoSupplier) {
        Result<Void> result = new Result<>();
        result.setSuccess(isSuccess);
        List<Message> messageList = new ArrayList<>();
        addMessageList(messageList, PamirsSession.getMessageHub().getDataMessages());
        if (!isSuccess) {
            messageList.add(Message.init().setLevel(InformationLevelEnum.ERROR).setMessage(infoSupplier.get()));
            addMessageList(messageList, PamirsSession.getMessageHub().getErrorMessages());
            messageList.add(Message.init().setLevel(InformationLevelEnum.ERROR)
                    .setMessage("以上错误请参考错误数据并对其进行修正。"));
        }
        result.addMessages(messageList);
        return result;
    }

    private static void addMessageList(List<Message> messageList, List<Message> newMessageList) {
        if (null == newMessageList) {
            return;
        }
        messageList.addAll(newMessageList);
    }

}

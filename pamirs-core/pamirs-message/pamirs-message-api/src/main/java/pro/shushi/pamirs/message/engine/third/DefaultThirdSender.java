package pro.shushi.pamirs.message.engine.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.taobao.api.ApiException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.message.enmu.MessageExpEnumerate;
import pro.shushi.pamirs.message.model.third.ThirdMailMessageTransientModel;
import pro.shushi.pamirs.message.utils.WxWorkUtil;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.meta.api.Fun;
import pro.shushi.pamirs.meta.common.exception.PamirsException;
import pro.shushi.pamirs.resource.api.model.ResourceConfig;

import java.util.HashMap;
import java.util.List;

import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.MAIL_NO_WECHATWORK_CONFIG_ERROR;

@Slf4j
public class DefaultThirdSender implements ThirdSender {
    @Override
    public boolean send(ThirdMailMessageTransientModel third) {
        if (null == third) {
            return false;
        }
        List<Long> receivePartnerIds = third.getReceivePartnerIds();
//        if (CollectionUtils.isNotEmpty(receivePartnerIds)){
//            PageCondition pageCondition = new PageCondition(PamirsEmployee.class);
//            pageCondition.setWrapper(new QueryWrapper().in("id", receivePartnerIds));
//            List<PamirsEmployee> allById = new PamirsEmployee().queryList(pageCondition);
//            if (CollectionUtils.isNotEmpty(allById)) {
//                List<String> wxWorkUser = new ArrayList<>();
//                List<String> dingUserIdList = new ArrayList<>();
//                for (PamirsEmployee resourceEmployee : allById) {
//                    if (StringUtils.isNotBlank(resourceEmployee.getWxUserId())) {
//                        wxWorkUser.add(resourceEmployee.getWxUserId());
//                    }
//                    if (StringUtils.isNotBlank(resourceEmployee.getDingtalkUserid())){
//                        dingUserIdList.add(resourceEmployee.getDingtalkUserid());
//                    }
//
//                }
//                try {
//                    sendWxWorkTextMessage(wxWorkUser,third.getInputBody(),getWxToken(false));
//                } catch (Exception e) {
//                    log.error("{}",SYSTEM_ERROR.msg());
//                }
//                try {
//                    sendTextMailByDing(dingUserIdList,fetchDingToken(),third.getInputBody());
//                }catch (Exception e) {
//                    log.error("{}",SYSTEM_ERROR.msg());
//                }
//            }
//        } else {
//            return false;
//        }
        return true;
    }

    private String getWxToken(boolean isForce) {
        return Fun.run("pro.shushi.pamirs.user.WxWorkFun", "getAccessToken", isForce,"weChatWorkTokenConfig");
    }


    public boolean sendWxWorkTextMessage(List<String> userId, String msg, String token) throws Exception {
        if (CollectionUtils.isEmpty(userId) || StringUtils.isBlank(msg)){
            return false;
        }
        String connectString = ResourceConfig.fetchConfigValue("weChatWorkSendMessageConfig").getValue();
        if (StringUtils.isBlank(token) || StringUtils.isBlank(connectString)) {
            log.error("{}", MAIL_NO_WECHATWORK_CONFIG_ERROR.msg());
            return false;
        }
        JSONObject jsonObject     = JSON.parseObject(connectString);
        String     connectString1 = jsonObject.getString("connectString");
        String     agentid        = jsonObject.getString("agentid");

        String url = WxWorkUtil.appendParam(connectString1, new HashMap<String, String>() {{
            put("access_token", token);
        }});

        String s = WxWorkUtil.executePostRequest(url, new HashMap<String, Object>() {{
            put("touser", StringUtils.join(userId, "|"));
            put("agentid", agentid);
            put("text", new JSONObject(new HashMap<String, Object>(){{put("content",msg);}}));
            put("msgtype", "text");
        }});
        JSONObject jsonObject1 = JSON.parseObject(s);
        String     errcode     = jsonObject1.getString("errcode");
        if ("0".equals(errcode)) {
            // 发送成功
        } else if ("42001".equalsIgnoreCase(errcode)){
            return sendWxWorkTextMessage(userId, msg, getWxToken(true));
        } else {
            log.warn("message send failed  errorMsg = [{}] ", jsonObject1.getString("errmsg"));
        }
        String invaliduser = jsonObject1.getString("invaliduser");
        if (StringUtils.isNotBlank(invaliduser)) {
            String[] split = invaliduser.split("\\|");
            log.warn("message send failed  user = [{}] ", split);
        }
        return true;
    }

    /**
     * 发送钉钉文本消息
     */
    public void sendTextMailByDing(List<String> userIdList, String token, String content) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");

        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (CollectionUtils.isEmpty(userIdList)){
            //必须要有一个接收对象
            throw PamirsException.construct(MessageExpEnumerate.MAIL_DDING_NO_PARTNER).errThrow();
        }
        String userIds = StringUtils.join(userIdList, ",");
        request.setUseridList(userIds);
        String agentId = ResourceConfig.fetchConfigValue("dingLogin_agentId").getValue();
//        String agentId = "309235101";
        request.setAgentId(Long.valueOf(agentId));
        request.setToAllUser(false);//是否发送给所有用户

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent(content);
        request.setMsg(msg);

        try {
            OapiMessageCorpconversationAsyncsendV2Response response = client.execute(request,token);
            if (! ("0".equalsIgnoreCase(response.getErrorCode()))){
                //发送失败
                log.error(response.getErrmsg());
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取钉钉access_token
     * @return
     */
    private String fetchDingToken() throws Exception {
        DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        String appkey = ResourceConfig.fetchConfigValue("dingLogin_appkey").getValue();
        String appsecret = ResourceConfig.fetchConfigValue("dingtalkMail_appsecret").getValue();
//        String appkey = "dingdeuguh0kc9lkdq51";
//        String appsecret = "E3VdkoZ5DB4AzpVnuhUbGcpXEa-i4atgQ09B88H2C4E_YUfTyCBf66UOMb6mV2y0";
        request.setAppkey(appkey);
        request.setAppsecret(appsecret);
        request.setHttpMethod("GET");
        try {
            OapiGettokenResponse response = client.execute(request);
            return response.getAccessToken();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}

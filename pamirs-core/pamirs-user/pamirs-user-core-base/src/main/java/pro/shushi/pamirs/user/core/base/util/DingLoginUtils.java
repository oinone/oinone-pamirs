package pro.shushi.pamirs.user.core.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;

public class DingLoginUtils {

    private static final String DINGDING_URL = "https://oapi.dingtalk.com";
    private static final String METHOD_GET = "GET";

    /**
     * 根据appId和appSecret获取accessToken
     *
     * @param appId
     * @param appSecret
     * @return
     * @throws ApiException
     */
    public static String fetchAccessToken(String appId, String appSecret) throws ApiException {
        OapiSnsGettokenResponse response = null;
        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/sns/gettoken");
        OapiSnsGettokenRequest request = new OapiSnsGettokenRequest();
        request.setAppid(appId);
        request.setAppsecret(appSecret);
        request.setHttpMethod(METHOD_GET);
        response = client.execute(request);
        String body = response.getBody();
        JSONObject jo = JSON.parseObject(body);
        String errcode = jo.getString("errcode");
        String access_token = null;
        if ("0".equals(errcode)) {
            access_token = (String) jo.get("access_token");
        }
        return access_token;
    }

    public static void main(String[] args) throws ApiException {
        String eee = fetchAccessToken("222", "eee");
    }

    /**
     * @param accessToken
     * @param code
     * @return
     * @throws ApiException
     */
    public static String fetchPersistentCode(String accessToken, String code) throws ApiException {
        OapiSnsGetPersistentCodeResponse response = null;

        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/sns/get_persistent_code");
        OapiSnsGetPersistentCodeRequest request = new OapiSnsGetPersistentCodeRequest();
        request.setTmpAuthCode(code);
        response = client.execute(request, accessToken);

        return response.getBody();
    }

    public static String fetchSnsToken(String accessToken, String openId, String persistentCode) throws ApiException {
        OapiSnsGetSnsTokenResponse response = null;
        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/sns/get_sns_token");
        OapiSnsGetSnsTokenRequest request = new OapiSnsGetSnsTokenRequest();
        request.setPersistentCode(persistentCode);
        request.setOpenid(openId);
        response = client.execute(request, accessToken);
        return response.getSnsToken();
    }


    public static String fetchUserInfo(String snsToken) throws ApiException {
        OapiSnsGetuserinfoResponse response = null;
        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/sns/getuserinfo");
        OapiSnsGetuserinfoRequest request = new OapiSnsGetuserinfoRequest();
        request.setSnsToken(snsToken);
        request.setHttpMethod(METHOD_GET);
        response = client.execute(request);
        return response.getBody();
    }

    /**
     * response
     * {
     * "errcode": 0, 返回码
     * "errmsg": "ok", 返回码文本描述
     * "contactType": 0, 联系类型，0表示企业内部员工，1表示企业外部联系人
     * "userid": "userid1"，员工id
     * }
     * 根据unionId获取userId
     */
    public static String fetchUserIdByUnionId(String unionId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/user/getUseridByUnionid");
        OapiUserGetUseridByUnionidRequest request = new OapiUserGetUseridByUnionidRequest();
        request.setUnionid(unionId);
        request.setHttpMethod(METHOD_GET);
        OapiUserGetUseridByUnionidResponse response = client.execute(request, accessToken);
        String userId = response.getUserid();
        return userId;
    }

    /**
     * 根据userId获取用户的在钉钉所有信息，包括部门、角色、工号等等,很多很多
     */
    public static String fetchUserInfoAllInDing(String userId, String accessToken) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(DINGDING_URL + "/user/get");
        OapiUserGetRequest request = new OapiUserGetRequest();
        request.setUserid(userId);
        request.setHttpMethod(METHOD_GET);
        OapiUserGetResponse response = client.execute(request, accessToken);

        return response.getBody();
    }
}

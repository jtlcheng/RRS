package com.cheng.rrs.user.api;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.JwtHelper;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.user.service.UserInfoService;
import com.cheng.rrs.user.utils.ConstantWxPropertiesUtils;
import com.cheng.rrs.user.utils.HttpClientUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @package: com.cheng.rrs.user.api
 * @Author: cheng
 * @Date: 2022-09-16 22:44
 **/
@Controller
@RequestMapping("/api/ucenter/wx")
@Api(tags = "微信登录")
@Slf4j
public class WeixinApiController {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取微信登录参数
     */
    //生成微信扫描二维码
    @GetMapping("getLoginParam")
    @ResponseBody
    @ApiOperation("生成微信扫描二维码")
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantWxPropertiesUtils.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantWxPropertiesUtils.WX_OPEN_APP_ID);
        map.put("redirect_uri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis() + "");//System.currentTimeMillis()+""
        return Result.ok(map);
    }

    /**
     * @Description: 微信登录回调
     * @Param:
     * @return:
     * @Author: cheng
     * @Date: 2022/9/17 14:21
     */
    @ApiOperation("微信登录回调")
    @GetMapping("callback")

    public String callBack(String code, String state) {
        //获取授权临时票据
        System.out.println("微信授权服务回调");
        System.out.println("code:" + code);
        System.out.println("state:" + state);

        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(code)) {
            log.error("非法回调请求");
            throw new YyghException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //拿着code和微信id和密钥，请求微信固定地址 得到两个值
        //使用code和appid以及appSecret获取access_token

        //%s 占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");

        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantWxPropertiesUtils.WX_OPEN_APP_ID,
                ConstantWxPropertiesUtils.WX_OPEN_APP_SECRET, code);
        String accessTokenInfo = null;
        //使用HttpClientUtils请求这个地址
        try {
            accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

        } catch (Exception e) {
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        System.out.println("使用code换取的access_token结果=" + accessTokenInfo);

        //把accessTokenInfo用JSONObject转换
        JSONObject resultJson = JSONObject.parseObject(accessTokenInfo);
        if (resultJson.getString("errcode") != null) {
            log.error("获取access_token失败：" + resultJson.getString("errcode") + resultJson.getString("errmsg"));
            throw new YyghException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        //从返回字符串获取两个值 openid 和 access_token
        String access_token = resultJson.getString("access_token");
        String openid = resultJson.getString("openid");
        System.out.println("dada"+openid);

        //判断数据库中是否存在微信的扫描人信息
        //根据 openid判断
        UserInfo userInfo= userInfoService.selectWxInfoOpenId(openid);

        if (userInfo == null) {//数据不存在微信信息
            // 拿着openid 和 access_token请求微信地址 得到扫描人信息
            // 先根据openid进行数据库查询
            // UserInfo userInfo = userInfoService.getByOpenid(openId);
            // 如果没有查到用户信息,那么调用微信个人信息获取的接口
            // if(null == userInfo){
            // 如果查询到个人信息，那么直接进行登录
            // 使用access_token换取受保护的资源：微信的个人信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
            String resultInfo = null;
            try {
                resultInfo = HttpClientUtils.get(userInfoUrl);
            } catch (Exception e) {
                throw new YyghException(ResultCodeEnum.FETCH_USERINFO_ERROR);
            }
            System.out.println("使用access_token, openid获取用户信息的结果 = " + resultInfo);
            //把resultInfo用JSONObject转换
            JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
            //解析用户信息
            //用户昵称
            String nickname = resultUserInfoJson.getString("nickname");
            System.out.println("djahdkjah："+nickname);
            //用户头像
            String headimgurl = resultUserInfoJson.getString("headimgurl");

            //获取扫码人信息添加到数据库
            UserInfo userInfo1=new UserInfo();
            userInfo1.setNickName(nickname);
            userInfo1.setOpenid(openid);
            userInfo1.setStatus(1);
            userInfoService.save(userInfo1);
            userInfo=userInfo1;
        }

        //返回name和token字符串
        Map<String, String> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //判断userInfo是否有手机号 如果手机为空 返回openid
        //如果手机号不为空 返回openid值是空字符串
        //前端判断 如果openid不为空 绑定手机号 如果openid为空 不需要绑定手机号
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            map.put("openid", userInfo.getOpenid());
        } else {
            map.put("openid", "");
        }
        //使用jwt生成字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        //跳转到前端页面
        return "redirect:"
                + ConstantWxPropertiesUtils.YYGH_BASE_URL
                + "/weixin/callback?token=" + map.get("token")
                + "&openid=" + map.get("openid")
                + "&name=" + URLEncoder.encode((String) map.get("name"));
    }
}

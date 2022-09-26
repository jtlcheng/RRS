package com.cheng.rrs.user.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.utils.AuthContextHolder;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.user.service.UserInfoService;
import com.cheng.rrs.vo.user.LoginVo;
import com.cheng.rrs.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @package: com.cheng.rrs.user.controller
 * @Author: cheng
 * @Date: 2022-09-14 09:33
 **/
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户模块")
public class UserInfoApiController {
    @Autowired
    private UserInfoService userInfoService;

    /**
    * @Description: 用户手机号登录
    * @Param:  LoginVo
    * @return: info
    * @Author: cheng
    * @Date: 2022/9/14 9:59
    */
    @ApiOperation("用户手机号登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, Object> info = userInfoService.loginUser(loginVo);
        return Result.ok(info);
    }
    //用户认证接口
    /**
    * @Description:  用户认证接口
    * @Param:  UserAuthVo
    * @return:
    * @Author: cheng
    * @Date: 2022/9/18 16:09
    */
    @ApiOperation("用户认证接口")
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        //传递两个参数，第一个参数用户id，第二参数认证数据对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息接口
    /**
    * @Description:  获取用户id信息接口
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/9/18 16:24
    */
    @ApiOperation("获取用户id信息接口")
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}

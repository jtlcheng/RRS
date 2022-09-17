package com.cheng.rrs.user.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.user.service.UserInfoService;
import com.cheng.rrs.vo.user.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

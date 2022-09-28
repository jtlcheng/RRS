package com.cheng.rrs.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.user.service.UserInfoService;
import com.cheng.rrs.vo.user.UserInfoQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @package: com.cheng.rrs.user.controller
 * @Author: cheng
 * @Date: 2022-09-20 08:45
 **/
@Api(tags = "平台用户管理")
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Autowired
    private UserInfoService userInfoService;

    //用户列表(条件查询带分页)
    /**
    * @Description: 用户列表(条件查询带分页)
    * @Param: page limit
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 9:05
    */
    @ApiOperation("用户列表(条件查询带分页)")
    @GetMapping("userInfo/{page}/{limit}")
    public Result getListPageByCondition(@PathVariable long page,
                                         @PathVariable long limit,
                                         UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> pageParam=new Page<>(page,limit);
        IPage<UserInfo> pageModel = userInfoService.selectPage(pageParam,userInfoQueryVo);
        return Result.ok(pageModel);
    }
    /**
    * @Description: 锁定
    * @Param: userId status
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 10:35
    */
    @ApiOperation("锁定")
    @GetMapping("userInfo/lockStatus/{userId}/{status}")
    public Result lockStatus(@PathVariable Long userId,
                             @PathVariable Integer status){
        userInfoService.lock(userId,status);
        return Result.ok();
    }
    //用户详情
    /**
    * @Description: 用户详情
    * @Param:  userId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 11:07
    */
    @ApiOperation("用户详情")
    @GetMapping("userInfo/show/{userId}")
    public Result userShow(@PathVariable Long userId){
        Map<String,Object> userMap=userInfoService.show(userId);
        return Result.ok(userMap);
    }
    //认证审批
    /**
    * @Description: 认证审批
    * @Param: userId authStatus -1 不通过 2 通过
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 12:42
    */
    @ApiOperation("认证审批")
    @GetMapping("userInfo/approvalUser/{userId}/{authStatus}")
    public Result approvalUser(@PathVariable Long userId,
                               @PathVariable Integer authStatus){
        userInfoService.approval(userId,authStatus);
        return Result.ok();
    }
}

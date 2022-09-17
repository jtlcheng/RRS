package com.cheng.rrs.msm.controller;

import com.alibaba.excel.util.StringUtils;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.msm.service.MsmService;
import com.cheng.rrs.msm.utils.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @package: com.cheng.rrs.msm.controller
 * @Author: cheng
 * @Date: 2022-09-14 16:41
 **/
@RestController
@RequestMapping("/api/msm")
@Api(tags = "短信验证")
public class MsmApiController {
    @Autowired
    private MsmService msmService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
    * @Description: 发送短信验证码
    * @Param:  phone
    * @return:
    * @Author: cheng
    * @Date: 2022/9/14 16:44
    */
    @ApiOperation("发送短信验证码")
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis获取验证码，如果获取获取到，返回ok
        // key 手机号  value 验证码
        String code = redisTemplate.opsForValue().get(phone);
        if(!StringUtils.isEmpty(code)) {
            return Result.ok();
        }
        //如果从redis获取不到，
        // 生成验证码，
        code = RandomUtil.getSixBitRandom();
        //调用service方法，通过整合短信服务进行发送
        boolean isSend = msmService.send(phone,code);
        //生成验证码放到redis里面，设置有效时间
        if(isSend) {
            redisTemplate.opsForValue().set(phone,code,2, TimeUnit.MINUTES);
            return Result.ok();
        } else {
            return Result.fail().message("发送短信失败");
        }
    }
}

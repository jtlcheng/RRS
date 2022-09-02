package com.cheng.rrs.hosp.controller;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.utils.MD5;
import com.cheng.rrs.hosp.service.HospitalSetService;
import com.cheng.rrs.model.hosp.HospitalSet;
import com.cheng.rrs.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSerController {
    //注入service
    @Autowired
    HospitalSetService hospitalService;

    /**
     * 查询素有医院信息
     * @return
     */
    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("findAll")
    public Result findAllHospitalSet(){
        List<HospitalSet> message = hospitalService.list();
        return Result.ok(message);
    }

    /**
     * 根据id删除医院信息
     * @param id
     * @return
     */
    @ApiOperation("逻辑删除医院设置")
    @DeleteMapping("{id}")
    public Result removeHospSet(@PathVariable Long id){
        boolean flag = hospitalService.removeById(id);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }
    /**
     *分页条件查询
     * @param current 当前记录数
     * @param limit 每页多少条数据
     * @param hospitalSetQueryVo
     * @return
     */
    @ApiOperation("分页条件查询")
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result findPageHospSet(@PathVariable long current, @PathVariable long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo ){
        //创建Page对象，传递当前页，每页记录数
        Page<HospitalSet> page=new Page<>(current,limit);
        //构建条件
        QueryWrapper<HospitalSet> wrapper=new QueryWrapper<>();
        String hosname = hospitalSetQueryVo.getHosname();
        String hoscode = hospitalSetQueryVo.getHoscode();
        if (!StringUtils.isEmpty(hosname)){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hoscode)){
            wrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        //调用方法实现分页查询
        Page<HospitalSet> hospitalSetPage = hospitalService.page(page, wrapper);
        //返回结果
        return Result.ok(hospitalSetPage);
    }

    /**
     * 添加医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation("添加医院设置")
    @PostMapping("saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名密钥
        Random random=new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        boolean flag = hospitalService.save(hospitalSet);

        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 根据id获取医院设置
     * @param id
     * @return
     */
    @ApiOperation("根据id获取医院设置")
    @GetMapping("getHospSetById/{id}")
    public Result getHospSetById(@PathVariable Long id){

        HospitalSet hospitalSets = hospitalService.getById(id);
            return Result.ok(hospitalSets);

    }

    /**
     * 修改医院设置
     * @param hospitalSet
     * @return
     */
    @ApiOperation("修改医院设置")
    @PostMapping("updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean flag = hospitalService.updateBatchById(Collections.singleton(hospitalSet));
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 批量删除医院设置
     * @param idList 删除医院的参数
     * @return
     */
    @ApiOperation("批量删除医院设置")
    @DeleteMapping("removeHospSet")
    public Result removeHospSet(@RequestBody List<Long> idList){
        boolean flag = hospitalService.removeByIds(idList);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 医院设置锁定和解锁
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("医院设置锁定和解锁")
    @PutMapping("lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id, @PathVariable Integer status){
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        boolean flag = hospitalService.updateById(hospitalSet);
        if (flag){
            return Result.ok();
        }else {
            return Result.fail();
        }
    }

    /**
     * 发送签名密钥
     * @param id
     * @return
     */
    @ApiOperation("发送签名密钥")
    @PutMapping("senStringKey/{id}")
    public Result senStringKey(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        //TOD0发送短信
        return Result.ok();
    }
}

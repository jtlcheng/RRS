package com.cheng.rrs.user.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.utils.AuthContextHolder;
import com.cheng.rrs.model.user.Patient;
import com.cheng.rrs.user.service.PatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @package: com.cheng.rrs.user.controller
 * @Author: cheng
 * @Date: 2022-09-19 09:25
 **/
@RestController
@Api(tags = "就诊人管理")
@RequestMapping("/api/patient")
public class PatientApiController {
    @Autowired
    private PatientService patientService;

    //获取就诊人列表
    /**
    * @Description: 获取就诊人列表
    * @Param:
    * @return: list
    * @Author: cheng
    * @Date: 2022/9/19 9:30
    */
    @ApiOperation("获取就诊人列表")
    @GetMapping("auth/findAllList")
    public Result findAll(HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        System.out.println(userId);
        List<Patient> list=patientService.findAllUserId(userId);
        return Result.ok(list);
    }
    //添加就诊人
    /**
    * @Description: 添加就诊人
    * @Param: patient
    * @return:
    * @Author: cheng
    * @Date: 2022/9/19 10:00
    */
    @ApiOperation("添加就诊人")
    @PostMapping("auth/savePatient")
    public Result savePatient(@RequestBody Patient patient,
                              HttpServletRequest request){
        //获取当前登录用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return Result.ok();
    }
    //根据id获取就诊人信息
    /**
    * @Description: 根据id获取就诊人信息
    * @Param: id
    * @return: patient
    * @Author: cheng
    * @Date: 2022/9/19 10:31
    */
    @GetMapping("auth/get/{id}")
    @ApiOperation("根据id获取就诊人信息")
    public Result getPatientId(@PathVariable Long id){
        Patient patient=patientService.getPatientId(id);
        return Result.ok(patient);
    }
    //修改就诊人
    /**
    * @Description: 修改就诊人
    * @Param: patient
    * @return:
    * @Author: cheng
    * @Date: 2022/9/19 10:37
    */
    @PostMapping("auth/updatePatient")
    @ApiOperation("修改就诊人")
    public Result updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }
    //删除就诊人
    /**
    * @Description: 删除就诊人
    * @Param: id
    * @return:
    * @Author: cheng
    * @Date: 2022/9/19 10:39
    */
    @ApiOperation("删除就诊人")
    @GetMapping("auth/removePatient/{id}")
    public Result removePatient(@PathVariable Long id){
        patientService.removeById(id);
        return Result.ok();
    }
    //获取就诊人
    /**
    * @Description:  根据就诊人id获取就诊人信息
    * @Param: id
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 11:05
    */
    @GetMapping("auth/inner/get/{id}")
    @ApiOperation("根据就诊人id获取就诊人信息")
    public Patient getPatientOrder(@PathVariable Long id){
        Patient patient = patientService.getById(id);
        return patient;
    }
}

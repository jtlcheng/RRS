package com.cheng.rrs.hosp.controller.api;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.hosp.service.ScheduleService;
import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.model.hosp.Hospital;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.DepartmentVo;
import com.cheng.rrs.vo.hosp.HospitalQueryVo;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.order.SignInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @package: com.cheng.rrs.hosp.controller.api
 * @Author: cheng
 * @Date: 2022-09-10 20:22
 **/
@RestController
@RequestMapping("/api/hosp/hospital")
@Api(tags = "前台医院列表")
public class HospApiController {
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;

    /**
    * @Description:  查询医院列表功能
    * @Param:  page,limit
    * @return: hospitals
    * @Author: cheng
    * @Date: 2022/9/10 20:25
    */
    @ApiOperation("查询医院列表功能")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable Integer page,
                               @PathVariable Integer limit,
                               HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectHospPage(page, limit, hospitalQueryVo);
        List<Hospital> content = hospitals.getContent();
        int totalPages = hospitals.getTotalPages();
        return Result.ok(hospitals);
    }

    /**
    * @Description:  根据医院名称进行查询
    * @Param:  hosname
    * @return: list
    * @Author: cheng
    * @Date: 2022/9/10 20:30
    */
    @ApiOperation("根据医院名称进行查询")
    @GetMapping("findHospName/{hosname}")
    public Result findHospName(@PathVariable String hosname){
        List<Hospital>list = hospitalService.findByHosname(hosname);
        return Result.ok(list);
    }

    /**
    * @Description:  根据医院编号获取科室
    * @Param:  hoscode
    * @return:  list
    * @Author: cheng
    * @Date: 2022/9/12 8:28
    */
    @ApiOperation("根据医院编号获取科室")
    @GetMapping("department/{hoscode}")
    public Result index(@PathVariable String hoscode){
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
    /**
    * @Description:  根据医院编号获取医院挂号预约详情
    * @Param:  hoscode
    * @return:  map
    * @Author: cheng
    * @Date: 2022/9/12 8:33
    */
    @ApiOperation("根据医院编号获取医院挂号预约详情")
    @GetMapping("findHospDetail/{hoscode}")
    public Result item(@PathVariable String hoscode){
        Map<String,Object> map=hospitalService.item(hoscode);
        return Result.ok(map);
    }
    //获取可预约排班数据
    /**
    * @Description: 获取可预约排班数据
    * @Param: page limit hoscode depcode
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 14:23
    */
    @ApiOperation("获取可预约排班数据")
    @GetMapping("auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getBookingSchedule(@PathVariable Integer page,
                                     @PathVariable Integer limit,
                                     @PathVariable String hoscode,
                                     @PathVariable String depcode){
        Map<String, Object> ruleSchedule =
                scheduleService.getBookingRuleSchedule(page, limit, hoscode, depcode);
        return Result.ok(ruleSchedule);
    }
    //获取排班数据
    /**
    * @Description: 获取排班数据
    * @Param: hoscode  depcode workDate
    * @return:
    * @Author: cheng
    * @Date: 2022/9/20 14:30
    */
    @ApiOperation("获取排班数据")
    @GetMapping("auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleList(@PathVariable String hoscode,
                                   @PathVariable String depcode,
                                   @PathVariable String workDate){
        List<Schedule> detailSchedule = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return Result.ok(detailSchedule);
    }
    //根据排班id获取排版数据
    /**
    * @Description: 根据排班id获取排班数据
    * @Param: scheduleId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 8:57
    */
    @ApiOperation("根据排班id获取排班数据")
    @GetMapping("auth/getSchedule/{scheduleId}")
    public Result getSchedule(@PathVariable String scheduleId ){
        return Result.ok(scheduleService.getById(scheduleId));

    }
    //根据排班id获取预约下单数据
    /**
    * @Description: 根据排班id获取预约下单数据
    * @Param: scheduleId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 11:21
    */
    @ApiOperation("根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(@PathVariable String scheduleId){
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

    //获取医院签名信息
    /**
    * @Description: 获取医院签名信息
    * @Param: hoscode
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 12:48
    */
    @ApiOperation("获取医院签名信息")
    @GetMapping("inner/getSignInfoVo/{hoscode}")
    public SignInfoVo getSignInfoVo(@PathVariable String hoscode){
        return hospitalService.getSignInfoVo(hoscode);
    }
}

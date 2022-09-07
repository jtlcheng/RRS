package com.cheng.rrs.hosp.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.hosp.service.ScheduleService;
import com.cheng.rrs.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @package: com.cheng.rrs.hosp.controller
 * @Author: cheng
 * @Date: 2022-09-06 09:27
 **/
@RestController
@RequestMapping("admin/hosp/schedule")
@Api(tags = "排班接口")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医院编号和科室编号，查询排班规则数据
     * @param page
     * @param limit
     * @param hoscode
     * @param depcode
     * @return
     */
    @ApiOperation("排班数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getScheduleRule(@PathVariable long page,
                                  @PathVariable long limit,
                                  @PathVariable String hoscode,
                                  @PathVariable String depcode){
        Map<String,Object> map=
                scheduleService.getRuleSchedule(page,limit,hoscode,depcode);
        return Result.ok(map);
    }

    /**
     * 根据医院编号、科室编号、工作日期查询排班详细信息
     * @param hoscode
     * @param depcode
     * @param workDate
     * @return
     */
    @GetMapping("getDetailSchedule/{hoscode}/{depcode}/{workDate}")
    @ApiOperation("根据医院编号、科室编号、工作日期查询排班详细信息")
    public Result getDetailSchedule(@PathVariable String hoscode,
                                    @PathVariable String depcode,
                                    @PathVariable String workDate){
        List<Schedule> list =
                scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return Result.ok(list);
    }
}

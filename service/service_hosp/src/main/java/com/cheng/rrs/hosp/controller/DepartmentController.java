package com.cheng.rrs.hosp.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @package: com.cheng.rrs.hosp.controller
 * @Author: cheng
 * @Date: 2022-09-05 16:59
 **/
@RestController
@RequestMapping("admin/hosp/department")
@Api(tags = "科室信息")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;


    /**
    * @Description:  根据hoscode获取科室列表信息
    * @Param:  hoscode
    * @return:
    * @Author: cheng
    * @Date: 2022/9/5 17:03
    */
    @ApiOperation("根据hoscode获取科室列表信息")
    @GetMapping("getDeptList/{hoscode}")
    public Result getDeptList(@PathVariable String hoscode){
        List<DepartmentVo> list=departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}

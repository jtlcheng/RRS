package com.cheng.rrs.hosp.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.hosp.service.HospitalSetService;
import com.cheng.rrs.model.hosp.Hospital;
import com.cheng.rrs.vo.hosp.HospitalQueryVo;
import com.cheng.rrs.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 医院列表接口
 * @author liucheng
 * @version 1.0
 */
@RestController
@RequestMapping("/admin/hosp/hospital")
@Api(tags = "医院列表接口")
public class HospitalController {
    @Autowired
    private HospitalService hospitalService;

    /**
     * 医院列表(条件查询分页)
     * @param page
     * @param limit
     * @param hospitalQueryVo
     * @return
     */
    //医院列表(条件查询分页)
    @ApiOperation("医院列表(条件查询分页)")
    @PostMapping("list/{page}/{limit}")
    public Result listHosp(@PathVariable Integer page,
                           @PathVariable Integer limit,
                           @RequestBody HospitalQueryVo hospitalQueryVo){
        Page<Hospital> pageModel=hospitalService.selectHospPage(page,limit,hospitalQueryVo);
        return Result.ok(pageModel);
    }
    //更新医院上线状态
    /**
     * 更新医院上线状态
     * @param id
     * @param status
     * @return
     */
    @ApiOperation("更新医院上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateHospStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    //医院详情信息
    /**
     * 医院详情信息
     * @param id
     * @return
     */
    @ApiOperation("医院详情信息")
    @GetMapping("showHospDetail/{id}")
    public Result showHospDetail(@PathVariable String id){
        Map<String,Object> map=hospitalService.getHospById(id);
        return Result.ok(map);
    }

}

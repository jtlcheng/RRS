package com.cheng.rrs.hosp.controller;

import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/hosp/hospitalSet")
public class HospitalSerController {
    //注入service
    @Autowired
    HospitalService hospitalService;

    /**
     * 查询素有医院信息
     * @return
     */
    @GetMapping("findAll")
    public List<HospitalSet> findAllHospitalSet(){
        List<HospitalSet> message = hospitalService.list();
        return message;
    }

    /**
     * 根据id删除医院信息
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public boolean removeHospSet(@PathVariable Long id){
        boolean flag = hospitalService.removeById(id);
        return flag;
    }

}

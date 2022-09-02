package com.cheng.rrs.hosp.service;

import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.vo.hosp.DepartmentQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    //删除科室接口
    void remove(String hoscode, String depcode);


}

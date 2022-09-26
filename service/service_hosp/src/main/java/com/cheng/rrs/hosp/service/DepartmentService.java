package com.cheng.rrs.hosp.service;

import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.vo.hosp.DepartmentQueryVo;
import com.cheng.rrs.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    //上传科室接口
    void save(Map<String, Object> paramMap);

    //查询科室接口
    Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo);

    //删除科室接口
    void remove(String hoscode, String depcode);

    //查询所有科室信息
    List<DepartmentVo> findDeptTree(String hoscode);

    //根据医院编号、科室编号、工作日期查询排班信息
    String getDepartmentName(String hoscode, String depcode);

    //获取部门
    Department getDepartment(String hoscdoe, String depcode);
}

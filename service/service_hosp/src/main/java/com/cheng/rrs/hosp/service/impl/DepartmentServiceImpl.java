package com.cheng.rrs.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cheng.rrs.hosp.repository.DepartmentRepository;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.vo.hosp.DepartmentQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    DepartmentRepository departmentRepository;

    /**
     * 上传科室接口
     * @param paramMap
     */
    @Override
    public void save(Map<String, Object> paramMap) {
        //把paramMap转换成department对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Department department = JSONObject.parseObject(paramMapString,Department.class);
        //根据医院编号，和科室编号查询
        Department departmentExist=departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());

        if (departmentExist==null){
            //如果不存在 上传
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
           //如果存在更新
            department.setId(departmentExist.getId());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public Page<Department> findPageDepartment(Integer page, Integer limit, DepartmentQueryVo departmentQueryVo) {


        //0是第一页
        //创建Pageable对象,设置当前页和每页记录数
        Pageable pageable= PageRequest.of(page-1,limit);
        //DepartmentQueryVo转换为department对象
        Department department=new Department();
        BeanUtils.copyProperties(departmentQueryVo,department);
        department.setIsDeleted(0);
        //创建Example对象
        //MongoDb查询
        ExampleMatcher matcher=ExampleMatcher.matching()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                .withIgnoreCase(true);
        Example<Department> example=Example.of(department,matcher);

        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Department departmentByHoscodeAndDepcode = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (departmentByHoscodeAndDepcode!=null){
            //调用方法删除
            departmentRepository.deleteById(departmentByHoscodeAndDepcode.getId());
        }

    }
}

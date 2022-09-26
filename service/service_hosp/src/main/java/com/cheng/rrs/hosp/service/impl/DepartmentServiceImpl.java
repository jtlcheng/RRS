package com.cheng.rrs.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cheng.rrs.hosp.repository.DepartmentRepository;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.vo.hosp.DepartmentQueryVo;
import com.cheng.rrs.vo.hosp.DepartmentVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 科室接口实现方法
 * @author cheng
 * @version 1.0
 */
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

    //查询科室接口
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

    //查询所有科室信息
    @Override
    public List<DepartmentVo> findDeptTree(String hoscode) {
        //创建list集合，用于封装所有数据
        List<DepartmentVo> result=new ArrayList<>();
        //根据医院编号，查询医院所有科室信息
        Department departmentQuery=new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号 bigCode分组 获取每个大科室里面的子级科室
        Map<String, List<Department>> departmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 departmentMap
        for (Map.Entry<String, List<Department >> entry : departmentMap.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> department1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1=new DepartmentVo();
            departmentVo1.setDepcode(bigCode);
            departmentVo1.setDepname(department1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children=new ArrayList<>();
            for (Department department : department1List) {
                DepartmentVo departmentVo2=new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        return result;
    }

    //根据医院编号、科室编号、工作日期查询排班信息
    @Override
    public String getDepartmentName(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            return department.getDepname();
        }
        return null;
    }

    //获取部门
    @Override
    public Department getDepartment(String hoscdoe, String depcode) {
        return departmentRepository.getDepartmentByHoscodeAndDepcode(hoscdoe, depcode);

    }
}

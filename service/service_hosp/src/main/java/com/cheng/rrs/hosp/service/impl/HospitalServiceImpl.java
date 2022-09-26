package com.cheng.rrs.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.cmn.client.DictFeignClient;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.hosp.mapper.HospitalSetMapper;
import com.cheng.rrs.hosp.repository.HospitalRepository;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.model.hosp.Hospital;
import com.cheng.rrs.model.hosp.HospitalSet;
import com.cheng.rrs.vo.hosp.HospitalQueryVo;
import com.cheng.rrs.vo.order.SignInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 医院接口实现方法
 * @author cheng
 * @version 1.0
 */
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalSetMapper,HospitalSet> implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private DictFeignClient dictFeignClient;
    @Override
    public void save(Map<String, Object> map) {
        //把参数map集合转换对象 Hospital
        String s = JSONObject.toJSONString(map);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);

        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist=hospitalRepository.getHospitalByHoscode(hoscode);

        //如果存在进行修改
        if (hospitalExist!=null){
            hospitalExist.setId(hospital.getId());
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(hospitalExist.getUpdateTime());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospitalExist);
        }else { //如果不存在进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    //根据医院编号查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    //医院列表(条件查询分页)
    @Override
    public Page<Hospital> selectHospPage(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {
        //创建Pageable对象
        Pageable pageable= PageRequest.of(page-1,limit);
        //创建条件匹配器
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        //hospitalQueryVo转换成Hospital对象
        Hospital hospital=new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        //创建对象
        Example<Hospital> example=Example.of(hospital,matcher);
        //调用方法实现查询
        Page<Hospital> pages = hospitalRepository.findAll(example, pageable);
        //获取查询list集合，遍历进行医院等级封装
        pages.getContent().stream().forEach(item->{
            this.setHospitalHosType(item);
        });
        return pages;
    }

    //更新医院上线状态
    @Override
    public void updateStatus(String id, Integer status) {
        //根据id查询医院信息
        Hospital hospital = hospitalRepository.findById(id).get();
        //设置修改的值
        hospital.setStatus(status);
        hospital.setUpdateTime(new Date());
        hospitalRepository.save(hospital);
    }

    //医院详情信息
    @Override
    public Map<String,Object> getHospById(String id) {
        Map<String,Object>result=new HashMap<>();
        Hospital hospital = this.setHospitalHosType(hospitalRepository.findById(id).get());
        //医院基本信息(包含医院等级)
        result.put("hospital",hospital);
        //单独处理更直观
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重新返回
        hospital.setBookingRule(null);
        return result;
    }

    //获取医院名称
    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if (hospital!=null){
            return hospital.getHosname();
        }
        return "";
    }

    //根据医院名称进行查询
    @Override
    public List<Hospital> findByHosname(String hosname) {
        return hospitalRepository.getHospitalByHosnameLike(hosname);
    }

    //根据医院编号获取医院挂号预约详情
    @Override
    public Map<String, Object> item(String hoscode) {
        Map<String,Object> result=new HashMap<>();
        //医院详情
        Hospital hospital = this.setHospitalHosType(this.getByHoscode(hoscode));
        result.put("hospital",hospital);
        //预约规则
        result.put("bookingRule",hospital.getBookingRule());
        //不需要重新返回
        hospital.setBookingRule(null);

        return result;
    }

    //获取医院签名信息
    @Override
    public SignInfoVo getSignInfoVo(String hoscode) {
        QueryWrapper<HospitalSet> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(queryWrapper);
        if (hospitalSet==null){
            throw new YyghException(ResultCodeEnum.HOSPITAL_OPEN);
        }
        SignInfoVo signInfoVo=new SignInfoVo();
        signInfoVo.setApiUrl(hospitalSet.getApiUrl());
        signInfoVo.setSignKey(hospitalSet.getSignKey());
        return signInfoVo;
    }

    //获取查询list集合，遍历进行医院等级封装
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public Hospital setHospitalHosType(Hospital hospital) {
        //根据dictCode和valu获取医院等级名称
        String hostypeString = dictFeignClient.getName("Hostype", hospital.getHostype());
        //查询省 市 区
        String provinceNameString = dictFeignClient.getName(hospital.getProvinceCode());
        String cityNameString = dictFeignClient.getName(hospital.getCityCode());
        String districtNameString = dictFeignClient.getName(hospital.getDistrictCode());

        hospital.getParam().put("fullAddress",provinceNameString+cityNameString+districtNameString);

        hospital.getParam().put("hostypeString",hostypeString);
        return hospital;
    }
}

package com.cheng.rrs.hosp.controller.api;

import com.alibaba.excel.util.StringUtils;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.HttpRequestHelper;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.common.utils.MD5;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.hosp.service.HospitalSetService;
import com.cheng.rrs.hosp.service.ScheduleService;
import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.model.hosp.Hospital;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.DepartmentQueryVo;
import com.cheng.rrs.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * mongodb实现接口
 * @author liucheng
 * @version 1.0
 */
@RestController
@RequestMapping("api/hosp")
@Api(tags = "医院管理接口")
public class ApiController {
    @Autowired
    HospitalService hospitalService;
    @Autowired
    HospitalSetService hospitalSetService;
    @Autowired
    DepartmentService departmentService;
    @Autowired
    ScheduleService scheduleService;

    //删除排班接口
    /**
     * 删除排班接口
     * @param request
     * @return
     */
    @ApiOperation("删除排班接口")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String hosScheduleId = (String) paramMap.get("hosScheduleId");
        //签名校验
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
    //查询排班接口
    /**
     * 查询排班接口
     * @param request
     * @return
     */
    @PostMapping("schedule/list")
    @ApiOperation("查询排班接口")
    public Result findSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号和科室信息
        String hoscode = (String) paramMap.get("hoscode");
        //签名校验
        //获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //根据医院传递过来的签名查询数据库签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //从数据查询过来的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //当前页和每页记录数
        Integer page=StringUtils.isEmpty(paramMap.get("page"))?1: Integer.parseInt((String) paramMap.get("page"));
        Integer limit = StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        ScheduleQueryVo scheduleQueryVo=new ScheduleQueryVo();

        scheduleQueryVo.setHoscode(hoscode);

        Page<Schedule> pageModel=scheduleService.findPageSchedule(page,limit,scheduleQueryVo);
        return Result.ok(pageModel);
    }
    //上传排班接口
    /**
     * 上传排班接口
     * @param request
     * @return
     */
    @PostMapping("saveSchedule")
    @ApiOperation("上传排班接口")
    public Result saveSchedule(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        //签名校验
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(paramMap);
        return Result.ok();
    }
    /**
     * 删除科室接口
     * @param request
     * @return
     */
    //删除科室接口
    @PostMapping("department/remove")
    @ApiOperation("删除科室接口")
    public Result removeDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //医院编号和科室编号
        String hoscode = (String) paramMap.get("hoscode");
        String depcode = (String) paramMap.get("depcode");
        //签名校验
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.remove(hoscode,depcode);
        return Result.ok();

    }
    //查询科室接口
    /**
     * 查询科室接口
     * @param request
     * @return
     */
    @PostMapping("department/list")
    @ApiOperation("查询科室接口")
    public Result findDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //签名校验
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //当前页和每页记录数
        Integer page=StringUtils.isEmpty(paramMap.get("page"))?1: Integer.parseInt((String) paramMap.get("page"));
        Integer limit = StringUtils.isEmpty(paramMap.get("limit"))?1:Integer.parseInt((String) paramMap.get("limit"));

        DepartmentQueryVo departmentQueryVo=new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);

        //调用Service方法
        Page<Department> pageModel= departmentService.findPageDepartment(page,limit,departmentQueryVo);

        return Result.ok(pageModel);
    }
    /**
     * 上传科室接口
     * @param request
     * @return
     */
    //上传科室接口
    @PostMapping("saveDepartment")
    @ApiOperation("上传科室接口")
    public Result saveDepartment(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);

        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.save(paramMap);
        return Result.ok();
    }

    /**
     * 查询医院接口
     * @param request
     * @return
     */
    //查询医院接口
    @PostMapping("hospital/show")
    @ApiOperation("查询医院接口")
    public Result getHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //获取医院编号
        String hoscode = (String) paramMap.get("hoscode");
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String signKeys = hospitalSetService.getSignKeys(hoscode);
        //3.把数据库查询的签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKeys);
        //4.判读签名是否一致
        if (!hospSign.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //调用Service方法实现根据医院编号查询
        Hospital hospital=hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }


    /**
     * 上传医院接口
     * @param request
     * @return
     */
    //上传医院接口
    @PostMapping("saveHospital")
    @ApiOperation("传递医院接口")
    public Result saveHospital(HttpServletRequest request){
        //获取传递过来的医院信息
        Map<String, String[]> requestMap = request.getParameterMap();
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(requestMap);
        //1.获取医院系统传递过来的签名，签名进行MD5加密
        String hospSign = (String) paramMap.get("sign");
        //2.根据传递过来的医院编码，查询数据库，查询签名
        String hoscode = (String) paramMap.get("hoscode");
        String signKey = hospitalSetService.getSignKeys(hoscode);
        //3.把数据查询签名进行MD5加密
        String signKeyMd5 = MD5.encrypt(signKey);
        //4.判断签名是否一致
        if (!hospSign.equals(signKeyMd5)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中"+"转换为了 “ ”,因此我们要转换回来
        String  logoData = (String) paramMap.get("logoData");
        logoData = logoData.replaceAll(" ", "+");
        paramMap.put("logoData",logoData);
        hospitalService.save(paramMap);
        return Result.ok();

    }

}

package com.cheng.rrs.hosp.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.hosp.mapper.ScheduleMapper;
import com.cheng.rrs.hosp.repository.ScheduleRepository;
import com.cheng.rrs.hosp.service.DepartmentService;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.hosp.service.ScheduleService;
import com.cheng.rrs.model.hosp.BookingRule;
import com.cheng.rrs.model.hosp.Department;
import com.cheng.rrs.model.hosp.Hospital;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.BookingScheduleRuleVo;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 医院排班接口实现方法
 *
 * @author cheng
 * @version 1.0
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper,Schedule> implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    //上传排班接口
    public void save(Map<String, Object> paramMap) {
        //paramMap转换为schedule对象
        String paramMapString = JSONObject.toJSONString(paramMap);
        Schedule schedule = JSONObject.parseObject(paramMapString, Schedule.class);

        //根据医院编号和排班编号查询
        Schedule scheduleExist =
                scheduleRepository.getScheduleByHoscodeAndHosScheduleId(schedule.getHoscode(), schedule.getHosScheduleId());

        //判断
        if (scheduleExist == null) {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        } else {
            scheduleExist.setId(schedule.getId());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            scheduleRepository.save(schedule);
        }
    }

    //查询排班接口分页
    @Override
    public Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo) {
        //0是第一页
        //创建Pageable对象,设置当前页和每页记录数
        Pageable pageable = PageRequest.of(page - 1, limit);
        //scheduleQueryVo转换为schedule对象
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(scheduleQueryVo, schedule);
        schedule.setIsDeleted(0);
        //创建Example对象
        //MongoDb查询
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);
        Example<Schedule> example = Example.of(schedule, matcher);
        Page<Schedule> all = scheduleRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(String hoscode, String hosScheduleId) {
        Schedule scheduleByHoscodeAndHosScheduleId =
                scheduleRepository.getScheduleByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        if (scheduleByHoscodeAndHosScheduleId != null) {
            scheduleRepository.deleteById(scheduleByHoscodeAndHosScheduleId.getId());
        }
    }

    //排班详情
    @Override
    public Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode) {
        //根据医院编号和科室编号查询
        Criteria criteria =
                Criteria.where("hoscode").
                is(hoscode).
                and("depcode").
                is(depcode);
        //根据工作日workDate期进行分组
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(criteria),//匹配条件
                Aggregation.group("workDate")//分组字段
                        .first("workDate").as("workDate")
                        //统计号源数量
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                //排序
                Aggregation.sort(Sort.Direction.DESC, "workDate"),
                //实现分页
                Aggregation.skip((page - 1) * limit),
                Aggregation.limit(limit)
        );
        //调用方法,最终执行
        AggregationResults<BookingScheduleRuleVo> aggResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        System.out.println("aggResults:"+aggResults);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList =
                aggResults.getMappedResults();
        System.out.println("bookingScheduleRuleVoList:"+bookingScheduleRuleVoList);
        //分组查询的总记录数
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalAggregation =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalAggregation.getMappedResults().size();
        System.out.println("totalAggregation:"+totalAggregation);
        //把日期对应星期获取
        for (BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleVoList) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }
        //设置最终数据，进行返回
        Map<String, Object> result = new HashMap<>();
        result.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        result.put("total", total);
        //获取医院名称
        String hosName = hospitalService.getHospName(hoscode);
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname", hosName);
        result.put("baseMap", baseMap);
        return result;
    }

    //根据医院编号、科室编号、工作日期查询排班信息
    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
        //根据参数查询mongodb
        List<Schedule> scheduleList =
                scheduleRepository.findScheduleByHoscodeAndDepcodeAndWorkDate(hoscode, depcode,
                        new DateTime(workDate).toDate());
        //把得到list集合遍历，设置其他值：医院名称，科室名称，日期对应星期
        scheduleList.stream().forEach(item -> {
            this.packageSchedule(item);
        });
        return scheduleList;

    }

    //获取可预约排班数据
    @Override
    public Map<String, Object> getBookingRuleSchedule(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String, Object> map = new HashMap<>();
        //获取预约规则
        //根据医院编号获取医院规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        if (hospital == null) {
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        //获取可预约日期分页数据
        IPage iPage = this.getListDate(page, limit, bookingRule);
        //当前页可预约日期
        List<Date> dateList = iPage.getRecords();
        System.out.println("dateList:"+dateList);
        //获取可预约日期科室剩余预约数
        Criteria criteria =
                Criteria.
                        where("hoscode").is(hoscode).
                        and("depcode").is(depcode).
                        and("workDate").in(dateList);
        Aggregation agg = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("workDate")//分组字段
                        .first("workDate")
                        .as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")//可预约数
                        .sum("reservedNumber").as("reservedNumber"));//剩余预约数
        /**
         * Criteria criteria =
         *                 Criteria.where("hoscode").
         *                 is(hoscode).
         *                 and("depcode").
         *                 is(depcode);
         *         //根据工作日workDate期进行分组
         *         Aggregation agg = Aggregation.newAggregation(
         *                 Aggregation.match(criteria),//匹配条件
         *                 Aggregation.group("workDate")//分组字段
         *                         .first("workDate").as("workDate")
         *                         //统计号源数量
         *                         .count().as("docCount")
         *                         .sum("reservedNumber").as("reservedNumber")
         *                         .sum("availableNumber").as("availableNumber"),
         *                 //排序
         *                 Aggregation.sort(Sort.Direction.DESC, "workDate"),
         *                 //实现分页
         *                 Aggregation.skip((page - 1) * limit),
         *                 Aggregation.limit(limit)
         *         );
         */
        AggregationResults <BookingScheduleRuleVo> aggregateResults =
                mongoTemplate.aggregate(agg, Schedule.class, BookingScheduleRuleVo.class);
        System.out.println("aggregateResults:"+aggregateResults);
        List<BookingScheduleRuleVo> scheduleRuleVoList =
                aggregateResults.getMappedResults();
        //获取科室剩余预约数
        System.out.println("scheduleRuleVoList:"+scheduleRuleVoList);

        //合并数据 将统计数据ScheduleVo 根据 "安排日期" 合并到BookingRuleVo
        //map集合 key 日期 value预约规则和剩余数量
        Map<Date, BookingScheduleRuleVo> scheduleVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(scheduleRuleVoList)) {
            scheduleVoMap = scheduleRuleVoList.
                    stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate,
                            BookingScheduleRuleVo -> BookingScheduleRuleVo));
        }
        //获取可预约排班规则
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (Integer i = 0, len = dateList.size(); i < len; i++) {
            Date date = dateList.get(i);
            //从map集合根据key日期获取value值
            BookingScheduleRuleVo bookingScheduleRuleVo = scheduleVoMap.get(date);
            System.out.println("bookingScheduleRuleVo:"+bookingScheduleRuleVo);
            //如果当天没有排班医生
            if (bookingScheduleRuleVo == null) {//说明当天没有排班医生
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                //就诊医生人数
                bookingScheduleRuleVo.setDocCount(0);
                //科室剩余预约数 -1表示无号
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
            bookingScheduleRuleVo.setWorkDate(date);
            bookingScheduleRuleVo.setWorkDateMd(date);
            //计算当天预约日期为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(date));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);

            //最后一页最后一条为即将预约 状态 0 正常 1 即将放号 -1 当天已停止挂号
            if (i == len - 1 && page == iPage.getPages()) {
                bookingScheduleRuleVo.setStatus(1);
            } else {
                bookingScheduleRuleVo.setStatus(0);
            }
            //当天预约如果过了停号时间 不能预约
            if (i == 0 && page == 1) {
                DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (stopTime.isBeforeNow()) {
                    //停止预约
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }
        //可预约日期规则数据
        map.put("bookingScheduleRuleList", bookingScheduleRuleVoList);
        map.put("total", iPage.getTotal());
        //其他基础数据
        Map<String, Object> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospName(hoscode));
        //科室
        Department department = departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("binDepartmentName", department.getBigname());
        //科室名称
        baseMap.put("departmentName", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap", baseMap);
        return map;
    }
    //根据排班id获取排班数据
    @Override
    public Schedule getById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        return this.packageSchedule(schedule);
    }
    //根据排班id获取预约下单数据
    @Override
    public ScheduleOrderVo getScheduleOrderVo(String scheduleId) {
        ScheduleOrderVo scheduleOrderVo=new ScheduleOrderVo();
        //排班信息
        Schedule schedule = this.getById(scheduleId);
        if (schedule==null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //获取预约规则信息
        Hospital hospital = hospitalService.getByHoscode(schedule.getHoscode());
        if (hospital==null){
            throw new YyghException(ResultCodeEnum.DATA_ERROR);
        }
        BookingRule bookingRule = hospital.getBookingRule();
        if (bookingRule==null){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //把获取数据设置到scheduleOrderVo中
        scheduleOrderVo.setHoscode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospitalService.getHospName(schedule.getHoscode()));
        scheduleOrderVo.setDepcode(schedule.getDepcode());
//        scheduleOrderVo.setDepcode(departmentService.getDepartmentName(schedule.getHoscode(),schedule.getDepcode()));
        scheduleOrderVo.setDepname(departmentService.getDepartmentName(schedule.getHoscode(),schedule.getDepcode()));
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        System.out.println("scheduleOrderVo:"+scheduleOrderVo);

        //退号截至日期(如 就诊前一天为 -1 当天为0)
        Integer quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(), bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());
        //预约开始时间
        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        //预约截止日期
        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(), bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        //当天停止挂号日期
        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());
        return scheduleOrderVo;
    }

    //更新排班数据 用于mq
    @Override
    public void update(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    /**
     * 获取可预约日期分页数据
     */
    private IPage <Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        //当天放号时间  年 月 日 小时 分钟
        DateTime releaseTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        //获取预约周期
        Integer cycle = bookingRule.getCycle();
        //如果当天放号时间已过 则预约周期后一天为即将放号时间 周期加1
        if (releaseTime.isBeforeNow())
            cycle += 1;
        //可预约所有日期，最后一天显示即将放号倒计时
        List<Date> dateList = new ArrayList<>();
        for (Integer i = 0; i < cycle; i++) {
            //计算当前预约日期
            DateTime curDateTime = new DateTime().plusDays(i);
            String dateString = curDateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }
        //日期分页，由于预约周期不一样 页面一排最多显示七天数据 多了就要分页显示
        List<Date> pageDateList = new ArrayList<>();
        Integer start = (page - 1) * limit;
        Integer end = (page - 1) * limit + limit;

        //如果显示的数据小于7 直接显示
        if (end > dateList.size())
            end = dateList.size();
        for (Integer i = start; i < end; i++) {
            pageDateList.add(dateList.get(i));
        }
        //如果显示数据大于7 进行分页
        IPage<Date> iPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, dateList.size());
        iPage.setRecords(pageDateList);
        return iPage;
    }

    //将Date日期 (yyyy-MM-dd:mm)转换为 DateTime
    private DateTime getDateTime(Date date, String releaseTime) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " " + releaseTime;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }


    //封装排班详情其他值 医院名称 科室名称 日期对应星期
    private Schedule packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname", hospitalService.getHospName(schedule.getHoscode()));
        //设置科室名称
        schedule.getParam().put("depname", departmentService.getDepartmentName(schedule.getHoscode(), schedule.getDepcode()));
        //设置日期
        schedule.getParam().put("dayOfWeek", this.getDayOfWeek(new DateTime(schedule.getWorkDate())));

        return schedule;
    }

    /**
     * 根据获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants
                    .SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants
                    .MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants
                    .TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants
                    .WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants
                    .THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants
                    .FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants
                    .SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}

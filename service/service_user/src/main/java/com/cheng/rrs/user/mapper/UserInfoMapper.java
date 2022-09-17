package com.cheng.rrs.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @package: com.cheng.rrs.user.mapper
 * @Author: cheng
 * @Date: 2022-09-14 09:34
 **/
@Mapper
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}

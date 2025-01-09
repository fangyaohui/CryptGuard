package com.example.crptguardusetest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.crptguardusetest.Entity.UserInfoPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @FileName UserInfoMapper
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfoPO> {
}

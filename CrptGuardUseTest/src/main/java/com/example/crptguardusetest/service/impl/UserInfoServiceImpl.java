package com.example.crptguardusetest.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.crptguardusetest.Entity.UserInfoPO;
import com.example.crptguardusetest.mapper.UserInfoMapper;
import com.example.crptguardusetest.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
 * @FileName UserInfoServiceImpl
 * @Description
 * @Author yaoHui
 * @date 2025-01-09
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoPO> implements UserInfoService {
}

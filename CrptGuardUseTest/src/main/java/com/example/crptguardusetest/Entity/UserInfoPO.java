package com.example.crptguardusetest.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.crypt.cryptguard.annotation.DecryptTransient;
import lombok.Data;
import org.apache.ibatis.plugin.Intercepts;

import java.util.List;

/**
 * @FileName UserInfoPO
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Data
@TableName("user_info")
public class UserInfoPO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

}

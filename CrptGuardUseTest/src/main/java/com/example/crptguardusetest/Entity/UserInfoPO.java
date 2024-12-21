package com.example.crptguardusetest.Entity;

import com.crypt.cryptguard.annotation.DecryptTransient;
import lombok.Data;

import java.util.List;

/**
 * @FileName UserInfoPO
 * @Description
 * @Author yaoHui
 * @date 2024-12-14
 **/
@Data
@DecryptTransient
public class UserInfoPO {

    private String username;

    private String password;

    private List<String> stringList;

    private Long paramLongObject;

    private Integer paramIntegerObject;

    private long paramLong;

    private int paramInt;

    private Double paramDoubleObject;

    private double paramDouble;

    private float paramFloat;

    private Float paramFloatObject;

    private RolePO rolePO;

}

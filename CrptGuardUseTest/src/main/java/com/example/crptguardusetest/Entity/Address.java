package com.example.crptguardusetest.Entity;

import lombok.Data;

/**
 * @FileName Address
 * @Description
 * @Author yaoHui
 * @date 2024-12-22
 **/
@Data
public class Address {
    private String street;
    private String city;
    private Details details;
}

package com.mitanshu.spring_ecomm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductResponse {
    private int id;
    private String name;
    private double price;
    private String description;
}

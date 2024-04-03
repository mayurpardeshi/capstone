package com.capstone.productservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FakeStoreProductListDto {
    List<FakeStoreProductDto> products;

    public FakeStoreProductListDto(){
        products = new ArrayList<>();
    }
}

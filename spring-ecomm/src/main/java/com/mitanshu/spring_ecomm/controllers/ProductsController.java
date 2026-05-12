package com.mitanshu.spring_ecomm.controllers;

import com.mitanshu.spring_ecomm.dtos.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/")
public class ProductsController {

    static List<ProductResponse> products = new ArrayList<>();

    static {
        products.add(new ProductResponse(1, "Chips", 12, "Tasty Chips"));
        products.add(new ProductResponse(2, "Soda", 8, "Cool Drink"));
        products.add(new ProductResponse(3, "Chocolate", 20, "Sweet Chocolate"));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() throws Exception {
//        throw new Exception("abc error");
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody String product){

        System.out.println(product);

        return new ResponseEntity<>(products.get(0), HttpStatus.CREATED);
    }
}

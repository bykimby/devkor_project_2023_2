package com.example.devkorproject.customer.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Data
@Getter
@Setter
public class ChangeCustomerNameReq {
    private String customerName;
    @ConstructorProperties({"customerName"})
    public ChangeCustomerNameReq(
            String customerName
    ) {
        this.customerName=customerName;
    }
}

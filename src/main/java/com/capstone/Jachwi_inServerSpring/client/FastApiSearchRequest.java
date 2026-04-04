package com.capstone.Jachwi_inServerSpring.client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FastApiSearchRequest {
    private String query;
    private int top_k;
}

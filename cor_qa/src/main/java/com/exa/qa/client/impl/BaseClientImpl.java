package com.exa.qa.client.impl;

import com.exa.qa.client.BaseClient;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

@Component
public class BaseClientImpl implements BaseClient {
    @Override
    public Result findById(String labelId) {
        return new Result(false, StatusCode.OPERATION_FAIL.getCode(),"熔断器开启");
    }
}

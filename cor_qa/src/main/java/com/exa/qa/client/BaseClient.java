package com.exa.qa.client;

import com.exa.qa.client.impl.BaseClientImpl;
import entity.Result;
import entity.StatusCode;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(value = "cor-database",fallback = BaseClientImpl.class)
public interface BaseClient {

    /**
     * 注意在@PathVariable里必须写valu
     * @param labelId
     * @return
     */
    @RequestMapping(value = "/label/{labelId}",method = RequestMethod.GET)
    public Result findById(@PathVariable("labelId") String labelId);
}

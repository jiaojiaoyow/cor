package com.exa.order.controller;


import com.alipay.api.AlipayApiException;
import com.exa.order.service.PayService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/zfb")
public class PayController {

    @Autowired
    private PayService payService;




    /*
     * 支付宝扫码支付,调用后自动跳转支付宝页面
     * qr_pay_mode 有两种模式
     * PC扫码支付的方式，支持前置模式和跳转模式。
     * 前置模式是将二维码前置到商户的订单确认页的模式。需要商户在自己的页面中以 iframe 方式请求
     * */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public void pay(HttpServletResponse response,@RequestBody Map<String,String> map) throws IOException, AlipayApiException {
        payService.pay(response,map);
    }


    /*
     * 支付宝扫码支付成功返回，一般只做展示
     * 业务逻辑处理请勿在该执行
     * 后面去掉，直接跳转支付成功页面
     * */
    @RequestMapping(value = "/return", method = RequestMethod.GET)
    public void payreturn(HttpServletResponse response, HttpServletRequest request) throws IOException {
        payService.payreturn(response,request);
    }


    /*
     * 异步通知地址 notify_url，通过 POST 请求的形式将支付结果作为参数通知到商户系统
     * 程序执行完后必须通过 PrintWriter 类打印输出"success"（不包含引号）。
     * 如果商户反馈给支付宝的字符不是 success 这7个字符，支付宝服务器会不断重发通知，直到超过 24 小时 22 分钟。一般情况下，25 小时以内完成 8 次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）；
     * 该页面不能在本机电脑测试，请到服务器上做测试。请确保外部可以访问该页面。
     * 建议该页面只做支付成功的业务逻辑处理，退款的处理请以调用退款查询接口的结果为准。
     * */
    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public void notify(HttpServletResponse response, HttpServletRequest request) throws IOException, AlipayApiException {
        payService.notify(response,request);
    }


    /*
     * 支付宝交易查询
     * 传入订单号
     * */
    @RequestMapping(value = "/query/{out_trade_no}", method = RequestMethod.GET)
    public Result query(@PathVariable String out_trade_no) throws AlipayApiException {
        return new Result(true, StatusCode.SUCCESS.getCode(), "查询成功", payService.query(out_trade_no));

    }

    /*
     * 支付宝退款请求
     * out_trade_no 订单号 必填
     * out_request_no 退款请求号 部分退款专用（每次请求号需不同） 选填（查询用到）
     * refund_amount 退款的金额 必填
     * refund_reason 退款的原因说明 选填
     * */
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public Result refund(@RequestBody Map<String,String> map) throws AlipayApiException {
        return new Result(true, StatusCode.SUCCESS.getCode(), "退款成功", payService.refund(map));

    }

    /*
     * 支付宝退款请求查询
     * out_trade_no 订单号 必填
     * out_request_no 必填
     * 退款请求号，部分退款的请求号不同，全额退款，请求和为创建交易时的外部交易号
     * */
    @RequestMapping(value = "/refund.query/{out_trade_no}/{out_request_no}", method = RequestMethod.GET)
    public Result refund_query(@PathVariable String out_trade_no, @PathVariable String out_request_no) throws AlipayApiException {
        String refund_amount = payService.refund_query(out_trade_no,out_request_no);
        if (refund_amount == null)
            return new Result(false, StatusCode.OPERATION_FAIL.getCode(), "退款失败，请重试");
        else
            return new Result(true, StatusCode.SUCCESS.getCode(), "退款成功");

    }


}

package com.exa.order.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.exa.order.config.AlipayConfig;
import com.exa.order.dao.OrderDao;
import com.exa.order.entity.OrderStatus;
import com.exa.order.pojo.Order;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class PayService {


    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderDao orderDao;

    /*
     * 支付宝扫码支付,调用后自动跳转支付宝页面
     * qr_pay_mode 有两种模式
     * PC扫码支付的方式，支持前置模式和跳转模式。
     * 前置模式是将二维码前置到商户的订单确认页的模式。需要商户在自己的页面中以 iframe 方式请求
     * */
    public void pay(HttpServletResponse response, Map<String, String> map) throws IOException, AlipayApiException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        AlipayTradePagePayRequest aliPayRequest = new AlipayTradePagePayRequest();
        aliPayRequest.setReturnUrl(alipayConfig.return_url);
        aliPayRequest.setNotifyUrl(alipayConfig.notify_url);
        /*
        模拟信息
        Order order = new Order();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String code= RandomStringUtils.randomNumeric(6);
        order.setOrderid(sdf.format(new Date())+code);
        order.setPaymoney(111.11);
        String subject = "Test";
        System.out.println("商户订单号:"+order.getOrderid());*/
        /*
         * out_trade_no 商户订单号
         * total_amount 订单总金额（要保留小数后两位）
         * subject 订单标题
         * product_code 销售产品码
         * 必填，其他可选
         * 如
         * time_expire 绝对超时时间，格式为yyyy-MM-dd HH:mm:ss   （加上这个不知道为什么传不过去，就不加了）
         * */
//        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time_expire = sdf.format(new Date(new Date().getTime()+ 24*60*60*1000));
//        System.out.println(time_expire);
        //电脑网站支付FAST_INSTANT_TRADE_PAY，不同支付不同码
        aliPayRequest.setBizContent("{"
                + "\"out_trade_no\":\""+ map.get("out_trade_no") +"\","
//                + "\"time_expire\":\""+ time_expire +"\","
                + "\"total_amount\":\""+ map.get("total_amount") +"\","
                + "\"subject\":\""+ map.get("subject") +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                + "}");
        String result;
        AlipayClient apAlipayClient
                = new DefaultAlipayClient(alipayConfig.gatewayUrl,
                alipayConfig.app_id,alipayConfig.merchant_private_key,"json",
                alipayConfig.charset,alipayConfig.alipay_public_key,alipayConfig.sign_type);
        AlipayTradePagePayResponse aliPayResponse= apAlipayClient.pageExecute(aliPayRequest);
        result = aliPayResponse.getBody();
        out.println(result);
    }


    /*
     * 支付宝扫码支付成功返回，一般只做展示
     * 业务逻辑处理请勿在该执行
     * 后面去掉，直接跳转支付成功页面
     * */
    public void payreturn(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            try {
                valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            params.put(name, valueStr);
        }
        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.alipay_public_key, alipayConfig.charset, alipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //——请在这里编写您的程序（以下代码仅作参考）——
        if (signVerified) {
            String out_trade_no = null;
            String trade_no = null;
            String total_amount = null;
            try {
                //商户订单号
                out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //支付宝交易号
                trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                //付款金额
                total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            out.println("支付宝交易号:" + trade_no +
                    "<br/>商户订单号:" + out_trade_no +
                    "<br/>交易金额:" + total_amount);
        } else {
            out.println("验签失败");
        }
    }

    // 如果商户反馈给支付宝的字符不是 success 这7个字符，PrintWriter输出
    // 支付宝服务器会不断重发通知，直到超过 24 小时 22 分钟。
    // 一般情况下，25 小时以内完成 8 次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）
    public void notify(HttpServletResponse response, HttpServletRequest request) throws IOException, AlipayApiException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            System.out.println(name + ":" + valueStr);
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.alipay_public_key, alipayConfig.charset, alipayConfig.sign_type); //调用SDK验证签名
        if (signVerified) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            try{
                // 用来判定是退款还是付款
                String buyer_pay_amount = new String(request.getParameter("buyer_pay_amount").getBytes("ISO-8859-1"), "UTF-8");
                System.out.println(buyer_pay_amount);
            }catch (Exception e){
                System.out.println("退款进入");
                //必须输出"success"给支付宝，
                out.println("success");
                return;
            }
            // 用户在交易中支付的金额
            String buyer_pay_amount = new String(request.getParameter("buyer_pay_amount").getBytes("ISO-8859-1"), "UTF-8");
            // 交易付款时间
            String gmt_payment = new String(request.getParameter("gmt_payment").getBytes("ISO-8859-1"), "UTF-8");


            /*
             * 交易完成状态，不能再退款
             * 如果商品不允许退款，交易成功状态变为交易完成状态
             * 退款日期超过可退款期限后（如三个月可退款），支付宝系统订单转为交易完成
             * */
            if (trade_status.equals("TRADE_FINISHED")) {
                // 交易结束时间
                String gmt_close = new String(request.getParameter("gmt_close").getBytes("ISO-8859-1"), "UTF-8");
                orderDao.updateFinished(Double.valueOf(buyer_pay_amount), OrderStatus.PAID.getCode(),
                        gmt_payment,gmt_close,out_trade_no);
            }
            /*
             * 交易成功状态，三个月内可退款
             * 扫码成功trade_status是TRADE_SUCCESS
             * 部分退款时也会调用 ,trade_status 也是TRADE_SUCCESS
             * */
            else if (trade_status.equals("TRADE_SUCCESS")) {
                orderDao.updateSuccess(Double.valueOf(buyer_pay_amount), OrderStatus.PAID.getCode(),
                        gmt_payment,out_trade_no);
            }
            //必须输出"success"给支付宝，
            out.println("success");

        } else {//验证失败
            System.out.println("fail");
            out.println("fail");
            //调试用，写文本函数记录程序运行情况是否正常
            String sWord = AlipaySignature.getSignCheckContentV1(params);
            alipayConfig.logResult(sWord);
        }
    }

    /*
     * 支付宝交易查询
     * 传入订单号
     * */
    public Map<String,String> query(String out_trade_no) throws AlipayApiException {
        // 初始化的AlipayClient
        AlipayClient apAlipayClient
                = new DefaultAlipayClient(alipayConfig.gatewayUrl,
                alipayConfig.app_id, alipayConfig.merchant_private_key, "json",
                alipayConfig.charset, alipayConfig.alipay_public_key, alipayConfig.sign_type);
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\"}");
        AlipayTradeQueryResponse alipayResponse = apAlipayClient.execute(alipayRequest);
        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", alipayResponse.getOutTradeNo());
        map.put("trade_no", alipayResponse.getTradeNo());
        map.put("trade_status", alipayResponse.getTradeStatus());
        map.put("total_amount", alipayResponse.getTotalAmount());
        if (alipayResponse.getSendPayDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("send_pay_date", sdf.format(alipayResponse.getSendPayDate()));
        } else
            map.put("send_pay_date", null);
        System.out.println("支付宝交易号:" + map.get("trade_no"));
        System.out.println("商家订单号:" + map.get("out_trade_no"));
        System.out.println("交易状态码:" + map.get("trade_status"));
        System.out.println("交易的订单金额:" + map.get("total_amount"));
        System.out.println("本次交易打款给卖家的时间:" + map.get("send_pay_date"));
        return map;
    }

    /*
     * 支付宝退款请求
     * out_trade_no 订单号 必填
     * out_request_no 退款请求号 部分退款专用（每次请求号需不同） 选填（查询用到）
     * refund_amount 退款的金额 必填
     * refund_reason 退款的原因说明 选填
     * */
    public Map<String,String> refund(Map<String, String> map) throws AlipayApiException {
        // 初始化的AlipayClient
        AlipayClient apAlipayClient
                = new DefaultAlipayClient(alipayConfig.gatewayUrl,
                alipayConfig.app_id, alipayConfig.merchant_private_key, "json",
                alipayConfig.charset, alipayConfig.alipay_public_key, alipayConfig.sign_type);
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + map.get("out_trade_no") + "\","
                + "\"refund_amount\":\"" + map.get("refund_amount") + "\","
                + "\"refund_reason\":\"" + map.get("refund_reason") + "\","
                + "\"out_request_no\":\"" + map.get("out_request_no") + "\"}");
        AlipayTradeRefundResponse aliPayResponse = apAlipayClient.execute(alipayRequest);
        Map<String, String> map1 = new HashMap<>();
        map1.put("out_trade_no", aliPayResponse.getOutTradeNo());
        map1.put("trade_no", aliPayResponse.getTradeNo());
        map1.put("fund_change", aliPayResponse.getFundChange());
        map1.put("refund_fee", aliPayResponse.getRefundFee());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map1.put("gmt_refund_pay", sdf.format(aliPayResponse.getGmtRefundPay()));
        map1.put("buyer_user_id", aliPayResponse.getBuyerUserId());

        // 资金发生变化
        // 操作数据库，修改订单已支付金额
        if("Y".equals(map1.get("fund_change"))){
            Double refund_amount =  Double.valueOf(map.get("refund_amount"));
            String out_trade_no = map.get("out_trade_no");
            System.out.println("退款:"+refund_amount+",商家订单号："+ out_trade_no);
            orderDao.updatePaymoney(0-refund_amount,out_trade_no);
        }

        System.out.println("支付宝交易号:" + map1.get("trade_no"));
        System.out.println("商家订单号:" + map1.get("out_trade_no"));
        System.out.println("本次退款是否发生了资金变化:" + map1.get("fund_change"));
        System.out.println("退款总金额:" + map1.get("refund_fee"));
        System.out.println("退款支付时间:" + map1.get("gmt_refund_pay"));
        System.out.println("买家在支付宝的用户id:" + map1.get("buyer_user_id"));
        return map1;
    }



    /*
     * 支付宝退款请求查询
     * out_trade_no 订单号
     * out_request_no 退款请求号，部分退款的请求号不同，全额退款，请求和为创建交易时的外部交易号
     * */
    public String refund_query(String out_trade_no, String out_request_no) throws AlipayApiException {
        // 初始化的AlipayClient
        AlipayClient apAlipayClient
                = new DefaultAlipayClient(alipayConfig.gatewayUrl,
                alipayConfig.app_id, alipayConfig.merchant_private_key, "json",
                alipayConfig.charset, alipayConfig.alipay_public_key, alipayConfig.sign_type);
        AlipayTradeFastpayRefundQueryRequest alipayRequest = new AlipayTradeFastpayRefundQueryRequest();
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"out_request_no\":\"" + out_request_no + "\"}");
        AlipayTradeFastpayRefundQueryResponse alipayResponse = apAlipayClient.execute(alipayRequest);
        return alipayResponse.getRefundAmount();
    }


}

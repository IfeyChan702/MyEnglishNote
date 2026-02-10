package com.ruoyi.system.sign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ruoyi.common.utils.ifey.SendMsgToIfeyBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SignToolsPublic {
    private static final Logger log = LoggerFactory.getLogger(SignToolsPublic.class);
    public static String amazoneKey = "jaklfj232kljlsad238932jkkjkjsakj";
    /**
     * 只保留最新的300个
     */
    private Map<String, Long> signAndTime = new LinkedHashMap<String, Long>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > 300;
        }
    };


    /**
     * 只适合没参数,传入随机数和时间戳的秘钥
     * @param stringBody
     * @return
     */
    public boolean ifSign(String stringBody,String key) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 获取请求头信息
            SignBean signBean = objectMapper.readValue(stringBody, SignBean.class);
            boolean sign = ifSign(signBean,key);
            if(!sign){
                log.info("ifSign:不符合签名要求"+stringBody);
                SendMsgToIfeyBot.sentTokakaserviceerrorMsg("ifSign:不符合签名要求stringBody=" + stringBody);
            }
            return sign;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }
    public boolean ifSign(SignBean signBean,String key) {
        if(signBean == null) {
            log.info("ifSign: signBean 为空" + signBean);
            return false;
        }
        String sign = signBean.getSign();
        if(sign == null || sign.equals("")) {
            log.info("ifSign: 签名为空" + signBean);
            return false;
        }
//        if(!isTimestampValid(Long.parseLong(signBean.getDataTime()))) {
//            log.info("ifSign: 时间戳过期" + signBean);
//            return false;
//        }
        try {
            String mySign = MD5Util.md5Encode(key + signBean.getRequestNo() + signBean.getDataTime());
            //签名正确
            if(sign.equals(mySign)) {
                Long lastTime = signAndTime.get(mySign);
//                lastTime = null;  //先不校验签名,正式环境需要加上
                if (lastTime != null) {
                    // 键存在并且对应的值不为空
                    // 进行相关操作
                    log.info("ifSign: 不通过,旧的请求签名" + signBean);
                    SendMsgToIfeyBot.sentTokakaserviceerrorMsg("旧的请求签名" + signBean);
                    return false;
                } else {
                    // 键不存在或对应的值为空
                    // 执行其他逻辑
//                    log.info("ifSign: 签名通过" + signBean);
                    signAndTime.put(mySign, Long.valueOf(signBean.getDataTime()));
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static final long EXPIRATION_TIME = 10 * 1000; // 10秒

    public static boolean isTimestampValid(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long difference = currentTime - timestamp;
        return difference >= 0 && difference <= EXPIRATION_TIME;
    }

}

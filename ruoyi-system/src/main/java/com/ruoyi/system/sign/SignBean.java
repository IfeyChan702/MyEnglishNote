package com.ruoyi.system.sign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;
/**
 * @author huang
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignBean implements Serializable {
    private String requestNo;
    private String dataTime;
    private String sign;
    public void generateSign(String key) throws Exception {
        String requestNo = UUID.randomUUID().toString().replaceAll("-", "");
        Long timestamp = System.currentTimeMillis();
        setRequestNo(requestNo);
        setDataTime(String.valueOf(timestamp));
        setSign(MD5Util.md5Encode(key + requestNo + timestamp));
    }
}

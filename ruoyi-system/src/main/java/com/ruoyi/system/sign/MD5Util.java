package com.ruoyi.system.sign;

import java.security.MessageDigest;

/**
 * 采用MD5加密
 * @author zhangjincai
 */
public class MD5Util {
    /***
     * MD5加密 生成32位md5码
     * @param inStr
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("MD5");
        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /***
     * MD5加密 生成32位md5码
     * @param inStr
     * @return 返回32位md5码
     */
    public static String md5EncodeTry(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] byteArray = inStr.getBytes("UTF-8");
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
//            Log.e("MD5", "MD5加密失败："+e.toString());
            return "";
        }

    }

//    /**
//     * 测试主函数
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String args[]) throws Exception {
//        String str = new String("1.0|test03|20230829000076553181|91738ecdb3f02865e988f39263587ad5");
//        System.out.println("原始：" + str);
//        System.out.println("MD5后：" + md5Encode(str));
//    }
}

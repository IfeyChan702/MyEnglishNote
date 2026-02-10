package com.ruoyi.common.utils.ifey;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendMsgToIfeyBot {
    public static String sentToKakaYunYingQun = "kakaerror";
    public static String sentTokakaserviceerror = "kakaserviceerror";
    public static void sentTokakaserviceerrorMsg(String msg){
        SendMsgMsgType( msg,sentTokakaserviceerror);
    }
    public static void SendMsg(String msg){
        System.out.println("SendMsgToIfeyBot: " + msg);
        try {
//            String encodedMsg = Base64.encodeBase64String(msg.getBytes("UTF-8"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("botMsg", msg);
            parameters.put("msgType", "ifeybot");
            OkHttpUtils.sendPostRequest("https://bot.spyn.top/api/pubSendXiaoFenDuiMsgApi", parameters);
        } catch (IOException e) {

        }
    }

    public static void SendMsgUSDTError(String msg){
        System.out.println("SendMsgToIfeyBot: " + msg);
        try {
//            String encodedMsg = Base64.encodeBase64String(msg.getBytes("UTF-8"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("botMsg", msg);
            parameters.put("msgType", "usdterror");
            OkHttpUtils.sendPostRequest("https://bot.spyn.top/api/pubSendXiaoFenDuiMsgApi", parameters);
        } catch (IOException e) {

        }
    }

    public static void SendMsgAutoChange(String msg){
        System.out.println("SendMsgAutoChange: " + msg);
        try {
//            String encodedMsg = Base64.encodeBase64String(msg.getBytes("UTF-8"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("botMsg", "\uD83D\uDCE3\uD83D\uDCE3 自动切换通知 \uD83D\uDCE3\uD83D\uDCE3 \n"+msg);
            parameters.put("msgType", "ifeyautochange");
            OkHttpUtils.sendPostRequest("https://bot.spyn.top/api/pubSendXiaoFenDuiMsgApi", parameters);
        } catch (IOException e) {

        }
    }

    /**
     * 代付积累了太多的单子
     * @param msg
     */
    public static void SendMsgPayOutTimes(String msg){
        System.out.println("SendMsgToIfeyBot: " + msg);
        try {
//            String encodedMsg = Base64.encodeBase64String(msg.getBytes("UTF-8"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("botMsg", msg);
            parameters.put("msgType", "ifeybot");
            OkHttpUtils.sendPostRequest("https://bot.spyn.top/api/pubSendXiaoFenDuiMsgApi", parameters);
        } catch (IOException e) {

        }
    }


    public static void SendMsgMsgType(String msg,String msgType){
        System.out.println("SendMsgMsgType: " + msg);
        try {
//            String encodedMsg = Base64.encodeBase64String(msg.getBytes("UTF-8"));
            Map<String, String> parameters = new HashMap<>();
            parameters.put("botMsg", msg);
            parameters.put("msgType", msgType);
            OkHttpUtils.sendPostRequest("https://bot.spyn.top/api/pubSendXiaoFenDuiMsgApi", parameters);
        } catch (IOException e) {
            System.out.println("SendMsgMsgType:"+e);
        }
    }
}

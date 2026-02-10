//package com.ruoyi.system.quartz.giftcard.mailevt;
//
//import cn.kyber.payplat.module.plt.service.email.mailparse.EmailMessageDTO;
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import java.util.concurrent.TimeUnit;
//
//public class MailMessageDTOStorage {
//    private static Cache<Long, EmailMessageDTO> cache(){
//        return MessageDTOStorageHolder.cache;
//    }
//    private static class MessageDTOStorageHolder{
//        private static final Cache<Long, EmailMessageDTO> cache = Caffeine.newBuilder()
//                // 设置最后一次写入或访问后经过固定时间过期
//                .expireAfterWrite(2, TimeUnit.HOURS)
//                // 初始的缓存空间大小
//                .initialCapacity(5000)
//                // 缓存的最大条数
//                .maximumSize(500000)
//                .build();
//    }
//
//    //get
//    public static EmailMessageDTO get(Long preOrderId){
//        return cache().getIfPresent(preOrderId);
//    }
//
//
//    public static void put(Long preOrderId,EmailMessageDTO messageDTO){
//        if (preOrderId==null||messageDTO==null)return;
//        cache().put(preOrderId,messageDTO);
//    }
//
//    public static void remove(Long preOrderId){
//       cache().invalidate(preOrderId);
//    }
//
//
//    private MailMessageDTOStorage(){}
//}

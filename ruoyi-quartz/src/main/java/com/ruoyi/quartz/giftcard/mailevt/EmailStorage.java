//package com.ruoyi.system.quartz.giftcard.mailevt;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.CacheLoader;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
////预单id 和 分单数  分单数 -1 则说明没有等到邮件
//@Slf4j
//public class EmailStorage {
//    private static Cache<Long, Integer> cache(){
//        return LocalCacheHolder.cache;
//    }
//    private static class LocalCacheHolder{
//        private static final Cache<Long, Integer> cache = Caffeine.newBuilder()
//                // 设置最后一次写入或访问后经过固定时间过期
//                .expireAfterWrite(72, TimeUnit.HOURS)
//                // 初始的缓存空间大小
//                .initialCapacity(50000)
//                // 缓存的最大条数
//                .maximumSize(5000000)
//                .removalListener((key, value, cause) -> {
//                    log.info("预单号 {} was removed from EmailStorage cause of {}", key, cause);
//                })
//                .build();
//    }
//
//    //get
//    public static Integer get(Long id){
//        Integer count=cache().getIfPresent(id);
//        return count==null?0:count;
//    }
//
//
//    public static void put(Long id,Integer count){
//        //如果id长度超过integer.MAX_VALUE报错
//        if(id>Integer.MAX_VALUE){
//            throw new RuntimeException("id长度超过integer.MAX_VALUE,邮件解析器需要升级");
//        }
//        cache().put(id,count);
//    }
//
//    public static void remove(Long id){
//        cache().invalidate(id);
//    }
//
//
//
//    private EmailStorage(){}
//}

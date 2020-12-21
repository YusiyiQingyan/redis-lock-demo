package com.weiwen.boot_redis01.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author weiwen
 * @email yusiyiqingyan@163.com
 * @date 2020/12/21 11:50
 * @Description 商品库存扣减Controller
 */

@RestController
public class GoodsBuyController {

    private static final String REDIS_LOCK_KEY = "redisLockKey";

    @Value("${server.port}")
    private Integer serverPort;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Redisson redisson;

    @GetMapping("/goods-buy")
    public String goodsBuy() throws Exception {
        // 设置分布式锁的value值必须唯一,可以使用UUID或者其他算法
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        RLock redissonLock = redisson.getLock(REDIS_LOCK_KEY);
        redissonLock.lock();
        try {
            /*// 调用setIfAbsent()方法,如果redis中 key不存在，则新建,同时设置时，添加上key的过期时间
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDIS_LOCK_KEY, value, 10L, TimeUnit.SECONDS);*/
/*            // 加锁 和为锁设置过期时间不是原子操作,在并发环境下是不允许的
            redisTemplate.expire(REDIS_LOCK_KEY, 10L, TimeUnit.SECONDS);*/
/*            if (!flag) {
                return "抢锁失败,呜呜呜~~";
            }*/

            String goodKey = "goods:weiwen01";
            String result = redisTemplate.opsForValue().get(goodKey);
            int goodRemain = result == null ? 0 : Integer.parseInt(result);
            if (goodRemain > 0) {
                // 如果库存数大于0，表示商品还未售完，可以继续进行销售
                int realNum = goodRemain - 1;
                redisTemplate.opsForValue().set(goodKey, String.valueOf(realNum));
                System.out.println("您已成功秒杀商品，商品还剩：" + realNum + "件，\t 服务开启在端口：" + serverPort);
                return "您已成功秒杀商品，商品还剩：" + realNum + "件，\t 服务开启在端口：" + serverPort;
            } else {
                // 如果剩余库存数小于等于0，表示商品已经售空，返回对应提示信息
                System.out.println("商品已售空/活动已经结束/调用超时,欢迎下次光临！");
            }
            return "商品已售空/活动已经结束/调用超时,欢迎下次光临！";
        } finally {
            // 释放锁之前需要判断当前的锁是不是自己加的
/*            if (value.equalsIgnoreCase(redisTemplate.opsForValue().get(REDIS_LOCK_KEY))) {
            // 【判断是否是自己的锁】和执行【释放锁】操作不是原子的,有可能正是在你判断过后,锁刚好过期，变成别人的了
                redisTemplate.delete(REDIS_LOCK_KEY);
            }*/
/*            // 使用Redis的事务机制,来完成删除key、释放锁操作
            while (true) {
                // 为redis 的key REDIS_LOCK_KEY添加一个监视器，监视在redis事务提交之前,REDIS_LOCK_KEY是否被修改
                redisTemplate.watch(REDIS_LOCK_KEY);
                if (value.equalsIgnoreCase(redisTemplate.opsForValue().get(REDIS_LOCK_KEY))) {
                    redisTemplate.setEnableTransactionSupport(true);
                    // 开启事务
                    redisTemplate.multi();
                    redisTemplate.delete(REDIS_LOCK_KEY);
                    List<Object> res = redisTemplate.exec();
                    if ( res==null) {
                        // 如果删除失败,continue跳出本次循环，继续尝试
                        continue;
                    }
                    // 删除key ,释放锁成功,取消掉监视器,并退出while死循环
                    redisTemplate.unwatch();
                    break;
                }
            }*/
/*            // 使用LUA脚本执行 删除key,释放锁 操作
            Jedis jedis = RedisUtil.getJedis();
            // 定义LUA脚本
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            try {
                Object result = jedis.eval(script, Collections.singletonList(REDIS_LOCK_KEY), Collections.singletonList(value));
                if ("1".equals(result.toString())) {
                    System.out.println("del REDIS_LOCK_KEY success!");
                } else {
                    System.out.println("del REDIS_LOCK_KEY fail!");
                }
            } finally {
                if (null != jedis) {
                    jedis.close();
                }
            }*/
            if (redissonLock.isLocked() && redissonLock.isHeldByCurrentThread()) {
                redissonLock.unlock();
            }
        }
    }

}
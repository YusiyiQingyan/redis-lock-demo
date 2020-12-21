# redis-lock-demo

使用Redis 的 setnx 命令实现最简单的Redis分布式锁,解决商品超卖问题。

> boot_redis01 和boot_redis02 为代码完全一样的两个SpringBoot项目,只是两者端口号不同,<br>
> 用来模拟同一套业务系统分布式部署的场景。为模拟出秒杀的业务场景,可以使用 JMeter压测工具请求服务。
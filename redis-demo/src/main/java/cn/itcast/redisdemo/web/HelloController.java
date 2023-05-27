package cn.itcast.redisdemo.web;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/get/{key}")
    public String hi(@PathVariable String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/set/{key}/{value}")
    public String hi(@PathVariable String key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value);
        return "success";
    }
//    public Item getItemById(Integer id){
//        //从缓存中查询
//        Object jsonObj = redisTemplate.opsForValue().get("item:" + id);
//        //缓存中有
//        if (jsonObj != null) {
//            System.out.println("=============从缓存中查询=============");
//            //缓存中有直接返回数据
//            String jsonString = jsonObj.toString();
//            if ("null".equals(jsonString)) {
//                return null;
//            }
//            Item item = JSON.parseObject(jsonString, Item.class);
//            return item;
//        }else {
//            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("itemLock:" + id);
//            RLock readLock = readWriteLock.readLock();
//            readLock.lock();
//            try {
//                System.out.println("开启readLock");
//                //再次查询一下缓存
//                //因为readLock只阻塞写操作所以再次读缓存避免多次访问数据库
//                //从缓存中查询
//                jsonObj = redisTemplate.opsForValue().get("item:" + id);
//                //缓存中有
//                if (jsonObj != null) {
//                    //缓存中有直接返回数据
//                    String jsonString = jsonObj.toString();
//                    if ("null".equals(jsonString)) {
//                        return null;
//                    }
//                    Item item = JSON.parseObject(jsonString, Item.class);
//                    return item;
//                }
//                System.out.println("==查询数据库==");
//                //从数据库查询
//                Item item = ItemService.getItemById(id);
//                //查询完成再存储到redis
//                redisTemplate.opsForValue().set("item:" + id, JSON.toJSONString(item), 300, TimeUnit.SECONDS);
//                return item;
//            }finally {
//                readLock.unlock();
//            }
//        }
//
//    }
//    public void updateItem(Item item){
//        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("itemLock:" + item.getId());
//        RLock writeLock = readWriteLock.writeLock();
//        writeLock.lock();
//        writeLock.lock();
//        try {
//            System.out.println("writeLock开启");
//            redisTemplate.opsForValue().set("item:" + item.getId(), JSON.toJSONString(item), 300, TimeUnit.SECONDS);
//        }finally {
//            writeLock.unlock();
//        }
//    }
}

package com.pzj.common.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

@Service
public class RedisUtils<V> {
    @Autowired
    protected RedisTemplate<String, V> redisTemplate;
    public final static String         REDIS_KEY         = "appapi";
    public final static int            REDIS_EXPIRE_TIME = 120;

    /**
    * 设置redisTemplate 
    * 
    * @param redisTemplate
    */
    public void setRedisTemplate(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 序列化
     * 
     * @param keyId
     * @return
     */
    protected byte[] serialize(String keyId) {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        return stringSerializer.serialize(keyId);
    }

    /**
     * 返序列化
     * 
     * @param keyId
     * @return
     */
    protected String deserialize(byte[] keyId) {
        RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        return stringSerializer.deserialize(keyId);
    }

    public boolean add(final String keyId, final String value, final Integer times) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = serialize(REDIS_KEY + keyId);
                byte[] name = serialize(value);
                Boolean setNX = connection.setNX(key, name);
                if (times > 0) {
                    connection.expire(key, times);
                }
                return setNX;
            }
        });
        return result;
    }

    /**  
     * 删除 
     * <br>------------------------------<br> 
     * @param key 
     */
    public void delete(String key) {
        redisTemplate.delete(REDIS_KEY + key);
    }

    /** 
     * 删除多个 
     * <br>------------------------------<br> 
     * @param keys 
     */
    public void delete(List<String> keys) {
        redisTemplate.delete(keys);
    }

    /** 
     * 修改  
     * <br>------------------------------<br> 
     * @param user 
     * @return  
     */
    public boolean update(final String keyId, final String value) {
        if (get(keyId) == null) {
            throw new NullPointerException("数据行不存在, key = " + REDIS_KEY + keyId);
        }
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = serialize(REDIS_KEY + keyId);
                byte[] name = serialize(value);
                connection.set(key, name);
                return true;
            }
        });
        return result;
    }

    /**  
     * 通过keyId获取 
     * <br>------------------------------<br> 
     * @param keyId 
     * @return 
     */
    public String get(final String keyId) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] key = serialize(REDIS_KEY + keyId);
                byte[] value = connection.get(key);
                if (value == null) {
                    return null;
                }
                String name = deserialize(value);
                return name;
            }
        });
        return result;
    }
}

package com.jt.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

//是操作redis工具API
@Service
public class RedisService {
	
	@Autowired(required=false)  //程序启动时暂时不注入,但是如果调用时自动注入
	//private ShardedJedisPool jedisPool;
	private JedisSentinelPool sentinelPool;
	
	public void set(String key,String value){
		Jedis jedis = sentinelPool.getResource();
		jedis.set(key, value);
		sentinelPool.returnResource(jedis);
	}
	
	public String get(String key){
		Jedis jedis = sentinelPool.getResource();
		String result = jedis.get(key);
		sentinelPool.returnResource(jedis);
		return result;
	}
	
	
	
	
	
	/*public void set(String key,String value){
		ShardedJedis shardedJedis = jedisPool.getResource();
		shardedJedis.set(key, value);
		jedisPool.returnResource(shardedJedis);//将链接还回池中
	}
	
	public String get(String key){
		ShardedJedis shardedJedis = jedisPool.getResource();
		String result = shardedJedis.get(key);
		jedisPool.returnResource(shardedJedis);
		return result;
	}
	
	//为key添加超时时间
	public void set(String key,String value,int seconds){
		ShardedJedis shardedJedis = jedisPool.getResource();
		shardedJedis.setex(key, seconds, value);
		jedisPool.returnResource(shardedJedis);//将链接还回池中
	}*/
}

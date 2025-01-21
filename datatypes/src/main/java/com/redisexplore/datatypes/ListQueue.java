package com.redisexplore.datatypes;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ListQueue {

	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	private static final long EXPIRE_TIME =60; //1 minute
	
    public static void main( String[] args )
    {
    	JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
    	String queueName="queue:1";
    	ListQueue app = new ListQueue();
    	
    	app.push(jedisPool, queueName);
    	app.pop(jedisPool, queueName);
    	jedisPool.close();
    	
    }
    
    /**
     *  pushes the item into the end of queue
     * @param jedisPool
     * @param queueName
     */
    private void push(JedisPool jedisPool, String queueName) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i=1;i <=5 ;i++) {
				String val = "value:"+i;
				System.out.println("Pushing value: "+val);		
				conn.rpush(queueName, val);
			}
			// also set TTL
			conn.expire(queueName, EXPIRE_TIME);
		}
		
	}

    /**
     *  pops the item from the queue
     * @param jedisPool
     * @param queueName
     */
    private void pop(JedisPool jedisPool, String queueName) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i=1;i <=5 ;i++) {
				System.out.println("Popping :"+ conn.lpop(queueName));
			}		
		}
		
	}

}

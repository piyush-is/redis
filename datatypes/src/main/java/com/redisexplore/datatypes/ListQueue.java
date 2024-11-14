package com.redisexplore.datatypes;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class ListQueue {

	private static final String HOST = "localhost";
	private static final int PORT = 6379;

    public static void main( String[] args )
    {
    	JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
    	String queueName="queue:1";
    	ListQueue app = new ListQueue();

    	jedisPool.close();
    	
    }
    
    /**
     *  pushes the item into the end of queue
     * @param jedisPool
     * @param queueName
     */
    private void push(JedisPool jedisPool, String queueName) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i=0;i <5 ;i++) {
				// conn.
			}		
		}
		
	}

}

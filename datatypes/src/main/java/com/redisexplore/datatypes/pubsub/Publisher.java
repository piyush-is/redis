package com.redisexplore.datatypes.pubsub;

import redis.clients.jedis.Jedis;

/**
 * Class to demonstrate the pub-sub capabilities of the Redis.
 */
public class Publisher {
	
	private static final String HOST = "localhost";
	private static final int PORT = 6379;

	public static void main(String[] args) {
	        // Connect to Redis server
	        try (Jedis jedis = new Jedis(HOST, PORT)) {
	            // Publish a message to the "news" channel
	            jedis.publish("news-channel", "Hello, Redis @" + System.currentTimeMillis());
	            System.out.println("Message published!");
	        }
	    }
}

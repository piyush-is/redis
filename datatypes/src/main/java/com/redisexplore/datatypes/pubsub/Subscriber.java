package com.redisexplore.datatypes.pubsub;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Subscriber {
	
	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	
    public static void main(String[] args) {
        // Connect to Redis server
        try (Jedis jedis = new Jedis(HOST, PORT)) {
            // Define a new JedisPubSub instance to handle messages
            JedisPubSub pubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Received message from channel " + channel + ": " + message);
                }
            };
            // Subscribe to the "my-channel" channel
            jedis.subscribe(pubSub, "news-channel");
        }
    }
}

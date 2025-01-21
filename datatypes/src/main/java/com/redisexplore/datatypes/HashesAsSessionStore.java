package com.redisexplore.datatypes;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * This sample program demonstrates Redis capability to efficiently store and manipulate session data.
 */
public class HashesAsSessionStore {

	private static final String POINTS = "points";
	private static final String ROLES = "roles";
	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	private static final long EXPIRE_TIME = 60; // 1 minute

	public static void main(String[] args) {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
		HashesAsSessionStore app = new HashesAsSessionStore();
		app.addPlayerData(jedisPool);
		jedisPool.close();

	}

	private void addPlayerData(JedisPool jedisPool) {
		try (Jedis conn = jedisPool.getResource()) {
			
			Map<String, String> player1 = constructPlayerData("PL0001","Player1", new String[] {"admin","moderator","authenticated"}, 50, 15000);
			Map<String, String> player2 = constructPlayerData("PL0002","Player2", new String[] {"player","authenticated"}, 72, 2000);
			Map<String, String> player3 = constructPlayerData("PL0003","Player3", new String[] {"admin"}, 43, 1200);
			conn.hset("session:player:1", player1);
			conn.hset("session:player:2", player2);
			conn.hset("session:player:3", player3);
			System.out.println("Player 1 Roles: "+getRoles(jedisPool, "session:player:1"));
			System.out.println("Player 2 Roles: "+getRoles(jedisPool, "session:player:2"));
			// increase the points of the player, initial value was 2000 so increment by 500 should return 2500
			System.out.println("Updated score of player 2 is : " +increasePoints(jedisPool, "session:player:2", 500));
			
			
			// also set TTL
			conn.expire("session:player:1", EXPIRE_TIME);
			conn.expire("session:player:2", EXPIRE_TIME);
		}
				
		

	}

	private Map<String, String> constructPlayerData(String id, String name, String[] roles, int level, long points) {
		Map<String, String> playerData = new HashMap<>();
		playerData.put("id", id);
		playerData.put("name", name);
		playerData.put(ROLES, String.join(",", roles));
		playerData.put("level", String.valueOf(level));
		playerData.put(POINTS, String.valueOf(points));
		System.out.println("Player Data :" + playerData);
		return playerData;
	}

	
	private String getRoles(JedisPool jedisPool, String playerId) {
		try (Jedis conn = jedisPool.getResource()) {
			// efficiently return specific value from hash with single call
		return	conn.hget(playerId, ROLES);
		}
		
	}
	

	private long increasePoints(JedisPool jedisPool, String playerId, long points) {
		try (Jedis conn = jedisPool.getResource()) {
			// efficiently update specific points value 
		return	conn.hincrBy(playerId, POINTS, points);
		}
		
	}
	
	
}

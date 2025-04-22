package com.redisexplore.datatypes;

import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.util.KeyValue;

public class SortedSetsAsLeaderboard {

	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	private static final long EXPIRE_TIME =60; //1 minute
	
	public static void main(String [] args) {
    	JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
    	String sortedSet="leaderboard:1";
    	SortedSetsAsLeaderboard app = new SortedSetsAsLeaderboard();
    	
    	app.addData(jedisPool, sortedSet);
    	app.printDetails(jedisPool, sortedSet);
    	app.printRank(jedisPool, sortedSet);
    	jedisPool.close();
    	
	}
	
	private void printRank(JedisPool jedisPool, String sortedSet) {
		try (Jedis conn = jedisPool.getResource()) {
			
			KeyValue<Long, Double> kvp3 = conn.zrevrankWithScore(sortedSet, "player:3");
			// the index starts from 0 in Sorted Set hence add 1 for correct rank
			System.out.println("Printing player:3 rank:" + (kvp3.getKey()+1) + " with score:" + kvp3.getValue());
		}
		
	}

	private void addData(JedisPool jedisPool, String sortedSet) {
		try (Jedis conn = jedisPool.getResource()) {
			Random r = new Random();
			int i = 1;
			for(i=1;i <=5 ;i++) {
				conn.zadd(sortedSet, r.nextDouble(10, 25), String.valueOf("player:"+i));
			}
			conn.zadd(sortedSet, 20.5, String.valueOf("player:"+i++));
			conn.zadd(sortedSet, 15.5, String.valueOf("player:"+i++));
			// also set TTL
			conn.expire(sortedSet, EXPIRE_TIME);
		}
		
	}

	private void printDetails(JedisPool jedisPool, String sortedSet) {
		try (Jedis conn = jedisPool.getResource()) {
			System.out.println("Printing players leaderboard:");
			// for leaderboards, highest score comes first so apply reverse range
			conn.zrevrangeByScoreWithScores(sortedSet, 25, 10).stream().forEach(x -> 
				System.out.println(x.getElement() +":" + x.getScore()));
			
			System.out.println("Printing players leaderboard between 15.5 and 20.5 :");
			conn.zrevrangeByScoreWithScores(sortedSet, 20.5, 15.5).stream().forEach(x -> 
					System.out.println(x.getElement() +":" + x.getScore()));
			
		}
		
		
	}

}

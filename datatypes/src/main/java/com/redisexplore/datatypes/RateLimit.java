package com.redisexplore.datatypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RateLimit {
	
	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	// we will use number of request per minute approach
	private static final long EXPIRE_TIME =60; //1 minute
	private static final long EXPIRE_TIME_IN_MS = EXPIRE_TIME * 1000; //expire time in ms

	private static final int NUM_OF_REQUEST_PER_WINDOW =100; //
	private static final String NUM_OF_REQUEST_PER_WINDOW_STR = String.valueOf(NUM_OF_REQUEST_PER_WINDOW); 
	//private static final String SESSION_ID = "user:001"; // it can be anything session-id, ip, user-id that reliably helps determine user session 
    
	public static void main( String[] args ) throws Exception
    {
    	JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);

    	RateLimit app = new RateLimit();

    	app.test(jedisPool, 100000);
    	// wait for a minute and re-attempt
    	System.out.println("Waiting for a minute to re-attempt");
    	Thread.currentThread().sleep(1000*60);
    	app.test(jedisPool, 100000);
    	
    	jedisPool.close();
    	
    }
	
	private void test(JedisPool jedisPool, int numOfRequests) {
    	Map<String, Integer> totalRequestMap = new HashMap<>();
    	Map<String, Integer> failedRequestMap = new HashMap<>();
    	Random rand = new Random();
    	long start = System.currentTimeMillis();
    	for(int i=0;i <100000;i++) {
    		String userId= "user:"+ String.valueOf(rand.nextInt(100, 200));
    		int totalReq = totalRequestMap.getOrDefault(userId, Integer.valueOf(0));
    		totalRequestMap.put(userId, ++totalReq);
    		boolean tokensLeft = canProcessRequest(jedisPool,userId);
    		if(!tokensLeft) {
    			int failedReq = failedRequestMap.getOrDefault(userId, Integer.valueOf(0));
    			failedRequestMap.put(userId, ++failedReq);
        			
    		}
    	}
    	System.out.println("Total time taken for " + numOfRequests + " req:" + ((System.currentTimeMillis() - start)/1000));

    	System.out.println("Total :" + totalRequestMap);
    	System.out.println("Failed :" + failedRequestMap);
	}
	
	// token bucket implementation
	// as described here: https://www.geeksforgeeks.org/token-bucket-algorithm/
	// and here :  https://redis.io/learn/howtos/ratelimiting
	
	private boolean canProcessRequest(JedisPool jedisPool, String sessionId) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int ctr=0;ctr <10;ctr++) {
				String lastRefreshed = conn.get(sessionId+"_last_refreshed");
				// if the tokens are not found initialize them
				if(lastRefreshed == null) {
					lastRefreshed = String.valueOf(System.currentTimeMillis());
					conn.set(sessionId+"_last_refreshed", lastRefreshed);
					conn.set(sessionId+"_ctr", NUM_OF_REQUEST_PER_WINDOW_STR);
				}
				long lastRefreshedNum = Long.valueOf(lastRefreshed);
				if(System.currentTimeMillis() - lastRefreshedNum > EXPIRE_TIME_IN_MS) {
					
					// last request was before the expire time, so reset the counter(s)
					conn.set(sessionId+"_last_refreshed", String.valueOf(System.currentTimeMillis()));
					conn.set(sessionId+"_ctr", NUM_OF_REQUEST_PER_WINDOW_STR);
				} else {
					// get the counter
					int requestLeft = Integer.valueOf(conn.get(sessionId+"_ctr"));
					if(requestLeft <= 0) {
						// no more attempts left
						return false;
					}
				} // end else
					
				// finally decerement the counter by 1 as request can be handled
				conn.decr(sessionId+"_ctr");
				return true;
			} // ends for
		
		} // ends try
		//return false in case any error occurs
		return false;
	}
}

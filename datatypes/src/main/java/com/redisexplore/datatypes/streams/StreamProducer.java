package com.redisexplore.datatypes.streams;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.StreamEntryID;

/**
 * A class to test the Redis Streams (producer). This class uses Jedis library
 * for Redis connectivity.
 */

public class StreamProducer {

	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	private static final int NUM_OF_TEST_MSG=10;
	private static final String STREAM_NAME="pg_gateway:txns";

	public static void main(String[] args) throws Exception {

		// create the Redis Client instance		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
		
		// create a randomizer for generating random values
		Random rand = new Random();
		try(Jedis conn = jedisPool.getResource()) {
		
		// create loop to publish multiple messsages.
		for (int ctr = 0; ctr < NUM_OF_TEST_MSG; ctr++) {
			// create message body
			Map<String, String> messageBody = new HashMap<>();
			messageBody.put("txnId", ("0x88483487sd%TdhgsDR-"+ctr)+ rand.nextInt());
			messageBody.put("acctId", ("27067437634-"+ctr)+rand.nextInt());
			messageBody.put("amt", String.valueOf(rand.nextInt(1000, 9999)));
			messageBody.put("txn_ts", String.valueOf(System.currentTimeMillis()));

			// send the message to redis stream
			//String messageId = syncCommands.xadd(STREAM_NAME, messageBody);
			StreamEntryID entryId = conn.xadd(STREAM_NAME, StreamEntryID.NEW_ENTRY, messageBody);
			System.out.print("Entry Id :" + entryId.toString() +"  ");
			//print the send message
			System.out.println("Message Body:" + messageBody);
		}
		} finally {
		// close the connection
		jedisPool.close();
		}

	}

}

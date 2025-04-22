package com.redisexplore.datatypes.streams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamGroupInfo;

/**
 * A class to test the Redis Streams (consumer). This class uses Jedis library
 * for Redis connectivity.
 */

public class StreamConsumerTwo {

	private static final String HOST = "localhost";
	private static final int PORT = 6379;
	private static final String STREAM_NAME = "pg_gateway:txns";
	private static final String CONSUMER_GRP = "cg-1";
	private static final String APP_NAME = "app-2";

	public static void main(String[] args) throws Exception {

		// create the Redis Client instance
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);

		try (Jedis conn = jedisPool.getResource()) {

			List<StreamGroupInfo> groupInfo = null;
			try {
				groupInfo = conn.xinfoGroups(STREAM_NAME);
			}catch(Exception e) {
				// if the stream is not found jedis throws the exception. 
				// could not find a better way to handle stream existence.
				// hence as of now using try-catch.
				groupInfo = null;
			}
			
			System.out.println(groupInfo);
			// create a consumer group
			if(groupInfo == null || groupInfo.isEmpty() || !containsCG(groupInfo)) {
				String val = conn.xgroupCreate(STREAM_NAME, CONSUMER_GRP, new StreamEntryID("0-0"), true);
				System.out.println("Group Created :" + val);

			}
			
			boolean created = conn.xgroupCreateConsumer(STREAM_NAME, CONSUMER_GRP, APP_NAME);
			System.out.println("Consumer Created :" + created);
			// create loop to read multiple messages.
			XReadGroupParams params = new XReadGroupParams();
			params.count(1);
			params.block(60*1000);
			Map<String, StreamEntryID> streams = new HashMap<>();
			streams.put(STREAM_NAME, StreamEntryID.UNRECEIVED_ENTRY);

			while (true) {

				// pull the message from redis stream
				// String messageId = syncCommands.xadd(STREAM_NAME, messageBody);
				List<Map.Entry<String, List<StreamEntry>>> dataSet = conn.xreadGroup(CONSUMER_GRP, APP_NAME, params,
						streams);
				System.out.println("After readGroup");
				//System.out.println("******* Received Dataset : ******" + dataSet.size());
				if (dataSet != null && !dataSet.isEmpty()) {
					Map.Entry<String, List<StreamEntry>> data = dataSet.getFirst();
					System.out.println(data.getKey() + "   " + data.getValue());
				}

//			StreamEntryID entryId = conn.xadd(STREAM_NAME, StreamEntryID.NEW_ENTRY, messageBody);
//			System.out.print("Entry Id :" + entryId.toString() +"  ");
//			//print the send message
//			System.out.println("Message Body:" + messageBody);
			}
		} finally {
			// close the connection
			jedisPool.close();
		}

//		// create the Redis Client instance
//		RedisClient redisClient = RedisClient.create("redis://" + HOST + ":" + PORT);
//		StatefulRedisConnection<String, String> connection = redisClient.connect();
//		RedisCommands<String, String> syncCommands = connection.sync();
//		try {
//			syncCommands.xgroupCreate(XReadArgs.StreamOffset.from(STREAM_NAME, "0-0"), CONSUMER_GRP);
//		} catch (RedisBusyException redisBusyException) {
//			System.out.println(String.format("Group already exists : %s", CONSUMER_GRP));
//		}
//
//		try {
//
//			System.out.println("Waiting for new messages");
//
//			while (true) {
//
//				List<StreamMessage<String, String>> messages = syncCommands.xreadgroup(
//						Consumer.from(CONSUMER_GRP, "consumer_1"), XReadArgs.StreamOffset.lastConsumed(STREAM_NAME));
//
//				if (!messages.isEmpty()) {
//					for (StreamMessage<String, String> message : messages) {
//						System.out.println(message);
//						// Confirm that the message has been processed using XACK
//						syncCommands.xack(STREAM_NAME, CONSUMER_GRP, message.getId());
//					}
//				}
//
//			}
//		} finally {
//			// close the connection
//			connection.close();
//			redisClient.shutdown();
//		}

	}

	private static boolean containsCG(List<StreamGroupInfo> groupInfo) {
		return groupInfo.stream().filter(x -> x.getName().equals(CONSUMER_GRP)).count() > 0;
		//return false;
	}
}

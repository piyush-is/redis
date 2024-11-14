package com.redisexplore.datatypes;
import redis.clients.jedis.*;


/**
 * Program to use String as Counter.
 *
 */
public class StringCtr 
{
	private static final String HOST = "localhost";
	private static final int PORT = 6379;

    public static void main( String[] args )
    {
    	JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), HOST, PORT);
    	String ctrName="ctr:1";
    	StringCtr app = new StringCtr();
    	app.initCtr(jedisPool, ctrName,1);
    	app.incrCtr(jedisPool, ctrName, 5);
    	app.decrCtr(jedisPool, ctrName, 2);
    	jedisPool.close();
    	
    	
    }
    
    /**
     *  Increase the given ctr by given value
     * @param jedisPool
     * @param ctrName
     * @param incrValue-
     */
    private void incrCtr(JedisPool jedisPool, String ctrName, int incrValue) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i=0;i <5 ;i++) {
				 System.out.println( "New Increased Value :" + conn.incrBy(ctrName, incrValue));
			}		
		}
		
	}

    /**
     *  Decrease the given ctr by given value
     * @param jedisPool
     * @param ctrName
     * @param incrValue-
     */
    private void decrCtr(JedisPool jedisPool, String ctrName, int incrValue) {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i=0;i <5 ;i++) {
				 System.out.println( "New Decreased Value :" + conn.decrBy(ctrName, incrValue));
			}		
		}
		
	}

    /**
     * Method to initialize String counter
     * 
     * @param jedisPool- The Jedis Pool Object 
     * @param ctrName 
     * @param initValue
     * 
     */
	private void initCtr(JedisPool jedisPool, String ctrName, int initValue) {
		try (Jedis conn = jedisPool.getResource()) {
			
			  conn.set(ctrName, String.valueOf(initValue));
			  String value = conn.get(ctrName);
			  System.out.println( "Init Value :" + value );
		}
		
	}
	
	
}

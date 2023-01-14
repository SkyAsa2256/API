package com.envyful.api.database.impl;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.config.type.RedisDatabaseDetails;
import com.envyful.api.database.Database;
import com.envyful.api.database.impl.redis.Subscribe;
import com.google.common.collect.Lists;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SimpleLettuceDatabase implements Database {

    private final RedisClient pool;
    private final List<ReflectivePubSub> pubSubs = Lists.newArrayList();

    public SimpleLettuceDatabase(RedisDatabaseDetails details) {
        this(details.getIp(), details.getPort(), details.getPassword());
    }

    public SimpleLettuceDatabase(String host, int port, String password) {
        this.pool = this.initJedis(host, port, password);
    }

    private RedisClient initJedis(String ip, int port, String password) {
        return RedisClient.create("redis://" + password + "@" + ip + ":" + port);
    }

    @Override
    public StatefulRedisConnection<String, String> getRedis() throws UnsupportedOperationException {
        return this.pool.connect();
    }

    @Override
    public RedisClient getClient() throws UnsupportedOperationException {
        return this.pool;
    }

    @Override
    public void close() {
        this.pool.close();
    }

    @Override
    public Connection getConnection() throws SQLException, UnsupportedOperationException {
        throw new UnsupportedOperationException("CANNOT CREATE SQL CONNECTION FROM JEDIS");
    }

    @Override
    public void subscribe(Object o) throws UnsupportedOperationException {
        for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
            Subscribe subscribe = declaredMethod.getAnnotation(Subscribe.class);

            if (subscribe == null) {
                continue;
            }

            StatefulRedisPubSubConnection<String, String> connection = this.pool.connectPubSub();
            connection.addListener(new ReflectivePubSub(o, declaredMethod));
        }
    }

    private static final class ReflectivePubSub extends RedisPubSubAdapter<String, String> {

        private final Object o;
        private final Method method;

        private ReflectivePubSub(Object o, Method method) {
            this.o = o;
            this.method = method;
        }

        @Override
        public void message(String channel, String message) {
            try {
                this.method.invoke(o, channel, message);
            } catch (InvocationTargetException | IllegalAccessException e) {
                UtilLogger.getLogger().error("Jedis error in '{}' for '{}'", channel, message);
                e.printStackTrace();
            }
        }
    }
}

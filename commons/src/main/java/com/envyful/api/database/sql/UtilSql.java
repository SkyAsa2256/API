package com.envyful.api.database.sql;

import com.envyful.api.concurrency.UtilLogger;
import com.envyful.api.database.Database;
import com.google.common.collect.Lists;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 *
 * An SQL utility class for running queries and updates
 *
 */
public class UtilSql {

    private UtilSql() {
        throw new UnsupportedOperationException("Static utility class");
    }

    /**
     *
     * Executes the query provided as an update {@link PreparedStatement#executeUpdate()}
     * <br>
     * Any errors will log to {@link UtilLogger} if set
     *
     * @param database The database to query
     * @param query The query
     * @param data The data to add
     * @return The int after running
     */
    public static int executeUpdate(Database database, String query, SqlType... data) {
        try (var connection = database.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < data.length; i++) {
                data[i].add(i + 1, preparedStatement);
            }

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error executing SQL (" + query + ")", e));
        }

        return -1;
    }

    /**
     *
     * Executes a batch query provided as an update {@link PreparedStatement#executeUpdate()}
     * <br>
     * Any errors will log to {@link UtilLogger} if set
     *
     * @param database The database to query
     * @param query The query
     * @param data The data to add
     * @return The int after running
     */
    public static <T> int[] executeBatchUpdate(Database database, String query, List<T> data, Function<T, List<SqlType>> parsing) {
        try (var connection = database.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {

            for (var datum : data) {
                var sqlTypes = parsing.apply(datum);

                for (int i = 0; i < sqlTypes.size(); i++) {
                    sqlTypes.get(i).add(i + 1, preparedStatement);
                }

                preparedStatement.addBatch();
            }

            return preparedStatement.executeBatch();
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error executing SQL (" + query + ")", e));
        }

        return new int[0];
    }

    /**
     *
     * Executes the query provided as an update {@link PreparedStatement#executeQuery()}
     * <br>
     * Any errors will log to {@link UtilLogger} if set
     *
     * @param database The database to query
     * @param query The query
     * @param data The data to add
     * @return The int after running
     */
    public static ResultSet executeQuery(Database database, String query, SqlType... data) {
        try (var connection = database.getConnection();
             var preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < data.length; i++) {
                data[i].add(i + 1, preparedStatement);
            }

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error executing SQL (" + query + ")", e));
        }

        return null;
    }

    /**
     *
     * Executes the query provided as an update {@link PreparedStatement#executeQuery()}
     * then converts it to the data type
     * <br>
     * Any errors will log to {@link UtilLogger} if set
     *
     * @param database The database to query
     * @param query The query
     * @param data The data to add
     * @return The int after running
     */
    public static <T> List<T> executeQuery(Database database, String query, Function<ResultSet, T> converter, SqlType... data) {
        try (var resultSet = executeQuery(database, query, data)) {
            List<T> convertedData = Lists.newArrayList();
            
            while (resultSet.next()) {
                convertedData.add(converter.apply(resultSet));
            }
            
            return convertedData;
        } catch (SQLException e) {
            UtilLogger.logger().ifPresent(logger -> logger.error("Error executing SQL (" + query + ")", e));
        }
        
        return Collections.emptyList();
    }

    /**
     *
     * Creates a query builder
     *
     * @param database The database to query
     * @return The builder
     * @param <T> The type
     */
    public static <T> QueryBuilder<T> query(Database database) {
        return new QueryBuilder<T>().database(database);
    }


    /**
     *
     * Creates an update builder
     *
     * @param database The database to update
     * @return The builder
     */
    public static UpdateBuilder update(Database database) {
        return new UpdateBuilder().database(database);
    }

    /**
     *
     * Creates a batch update builder
     *
     * @param data The data to send
     * @return The builder
     * @param <T> The type
     */
    public static <T> BatchUpdateBuilder<T> batchUpdate(List<T> data) {
        return new BatchUpdateBuilder<T>().data(data);
    }

    /**
     *
     * Creates a batch update builder
     *
     * @param data The data to send
     * @return The builder
     * @param <T> The type
     */
    public static <T> BatchUpdateBuilder<T> batchUpdate(T... data) {
        return new BatchUpdateBuilder<T>().data(data);
    }

    public static class QueryBuilder<T> {

        private Database database;
        private String query;
        private List<SqlType> data = Lists.newArrayList();
        private Function<ResultSet, T> converter = null;

        private QueryBuilder() {}

        public QueryBuilder<T> database(Database database) {
            this.database = database;
            return this;
        }

        public QueryBuilder<T> query(String query) {
            this.query = query;
            return this;
        }

        public QueryBuilder<T> data(SqlType... data) {
            this.data = Lists.newArrayList(data);
            return this;
        }

        public QueryBuilder<T> converter(Function<ResultSet, T> converter) {
            this.converter = converter;
            return this;
        }

        public ResultSet execute() {
            if (this.database == null) {
                throw new IllegalArgumentException("Database cannot be null");
            }

            return executeQuery(this.database, this.query, this.data.toArray(new SqlType[0]));
        }

        public List<T> executeWithConverter() {
            if (this.database == null) {
                throw new IllegalArgumentException("Database cannot be null");
            }

            if (this.converter == null) {
                throw new IllegalArgumentException("Converter cannot be null");
            }

            return executeQuery(this.database, this.query, this.converter, this.data.toArray(new SqlType[0]));
        }
    }

    public static class UpdateBuilder {

        private Database database;
        private String query;
        private List<SqlType> data = Lists.newArrayList();

        private UpdateBuilder() {}

        public UpdateBuilder database(Database database) {
            this.database = database;
            return this;
        }

        public UpdateBuilder query(String query) {
            this.query = query;
            return this;
        }

        public UpdateBuilder data(SqlType... data) {
            this.data = Lists.newArrayList(data);
            return this;
        }

        public int execute() {
            if (this.database == null) {
                throw new IllegalArgumentException("Database cannot be null");
            }

            return executeUpdate(this.database, this.query, this.data.toArray(new SqlType[0]));
        }
    }

    public static class BatchUpdateBuilder<T> {

        private Database database;
        private String query;
        private List<T> data = Lists.newArrayList();
        private Function<T, List<SqlType>> converter;

        private BatchUpdateBuilder() {}

        public BatchUpdateBuilder<T> database(Database database) {
            this.database = database;
            return this;
        }

        public BatchUpdateBuilder<T> query(String query) {
            this.query = query;
            return this;
        }

        public BatchUpdateBuilder<T> data(List<T> data) {
            this.data.addAll(data);
            return this;
        }

        public BatchUpdateBuilder<T> data(T... data) {
            this.data.addAll(Lists.newArrayList(data));
            return this;
        }

        public BatchUpdateBuilder<T> converter(Function<T, List<SqlType>> converter) {
            this.converter = converter;
            return this;
        }

        public int[] execute() {
            if (this.database == null) {
                throw new IllegalArgumentException("Database cannot be null");
            }

            if (this.converter == null) {
                throw new IllegalArgumentException("Converter cannot be null");
            }

            return executeBatchUpdate(this.database, this.query, this.data, this.converter);
        }
    }

}

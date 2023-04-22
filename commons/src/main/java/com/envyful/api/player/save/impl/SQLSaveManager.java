package com.envyful.api.player.save.impl;

import com.envyful.api.database.Database;
import com.envyful.api.player.PlayerManager;
import com.envyful.api.player.attribute.Attribute;
import com.envyful.api.player.save.AbstractSaveManager;
import com.envyful.api.player.save.SaveHandlerFactory;
import com.envyful.api.player.save.VariableSaveHandler;
import com.envyful.api.player.save.attribute.ColumnData;
import com.envyful.api.player.save.attribute.Queries;
import com.envyful.api.player.save.attribute.SaveHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SQLSaveManager<T> extends AbstractSaveManager<T> {

    private final Database database;
    protected final Map<Class<? extends Attribute<?, ?>>, SQLAttributeData> registeredSqlAttributeData = Maps.newConcurrentMap();

    public SQLSaveManager(PlayerManager<?, ?> playerManager, Database database) {
        super(playerManager);
        this.database = database;
    }

    @Override
    public List<Attribute<?, ?>> loadData(UUID uuid) {
        if (this.registeredSqlAttributeData.isEmpty()) {
            return Collections.emptyList();
        }

        List<Attribute<?, ?>> attributes = Lists.newArrayList();

        for (Map.Entry<Class<? extends Attribute<?, ?>>, AttributeData> entry : this.registeredAttributes.entrySet()) {
            AttributeData value = entry.getValue();
            SQLAttributeData sqlAttributeData = this.registeredSqlAttributeData.get(entry.getKey());
            Attribute<?, ?> attribute = value.getConstructor().apply(value.getManager());

            try (Connection connection = this.database.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sqlAttributeData.getQueries().loadQuery())) {
                Field[] fields = sqlAttributeData.getFieldsPositions().get(sqlAttributeData.getQueries().loadQuery());

                for (int i = 0; i < fields.length; i++) {
                    preparedStatement.setObject(i, fields[i].get(attribute));
                }

                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    attributes.add(attribute);
                    continue;
                }

                for (Map.Entry<Field, FieldData> fieldData : sqlAttributeData.getFieldData().entrySet()) {
                    FieldData data = fieldData.getValue();

                    if (data.getSaveHandler() != null) {
                        fieldData.getKey().set(attribute, data.getSaveHandler().invert(resultSet.getString(fieldData.getValue().getName())));
                    } else {
                        fieldData.getKey().set(attribute, resultSet.getObject(fieldData.getValue().getName()));
                    }
                }
            } catch (SQLException | IllegalAccessException e) {
                e.printStackTrace();
            }

            attributes.add(attribute);
        }

        return attributes;
    }

    @Override
    public void saveData(UUID player, Attribute<?, ?> attribute) {
        SQLAttributeData sqlAttributeData = this.registeredSqlAttributeData.get(attribute.getClass());

        try (Connection connection = this.database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlAttributeData.getQueries().updateQuery())) {
            Field[] fieldPositions = sqlAttributeData.getFieldsPositions().get(sqlAttributeData.getQueries().updateQuery());

            for (int i = 0; i < fieldPositions.length; i++) {
                Field fieldPosition = fieldPositions[i];

                FieldData fieldData = sqlAttributeData.getFieldData().get(fieldPosition);

                if (fieldData.getSaveHandler() != null) {
                    preparedStatement.setString(i, fieldData.getSaveHandler().convert(fieldPosition.get(attribute)));
                } else {
                    preparedStatement.setObject(i, fieldPosition.get(attribute));
                }
            }

            preparedStatement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerAttribute(Object manager, Class<? extends Attribute<?, ?>> attribute) {
        Map<Field, FieldData> fieldData = this.getFieldData(attribute);
        Queries queries = attribute.getAnnotation(Queries.class);

        if (queries == null) {
            return;
        }

        Map<String, Field[]> fieldsPositions = ImmutableMap.of(
                queries.loadQuery(), this.getFieldPositions(queries.loadQuery(), fieldData),
                queries.updateQuery(), this.getFieldPositions(queries.updateQuery(), fieldData)
        );

        super.registerAttribute(manager, attribute);
        this.registeredSqlAttributeData.put(attribute, new SQLAttributeData(queries, fieldData, fieldsPositions));
    }

    private Map<Field, FieldData> getFieldData(Class<? extends Attribute<?, ?>> attribute) {
        Map<Field, FieldData> fieldData = Maps.newHashMap();

        for (Field declaredField : attribute.getDeclaredFields()) {
            if (Modifier.isTransient(declaredField.getModifiers())) {
                continue;
            }

            declaredField.setAccessible(true);
            ColumnData columnData = declaredField.getAnnotation(ColumnData.class);
            SaveHandler saveHandler = declaredField.getAnnotation(SaveHandler.class);
            String name;
            VariableSaveHandler<?> variableSaveHandler = null;

            if (columnData == null) {
                name = this.calculateColumnName(declaredField);
            } else {
                name = columnData.value();
            }

            if (saveHandler != null) {
                variableSaveHandler = SaveHandlerFactory.getSaveHandler(saveHandler.value());
            }

            fieldData.put(declaredField, new FieldData(declaredField, name, variableSaveHandler));
        }

        return fieldData;
    }

    private Field[] getFieldPositions(String query, Map<Field, FieldData> fieldData) {
        List<Field> indexes = Lists.newArrayList();

        for (String s : query.split(" ")) {
            for (Map.Entry<Field, FieldData> fieldStringEntry : fieldData.entrySet()) {
                FieldData parameter = fieldStringEntry.getValue();

                if (s.equals(parameter.getName()) || s.startsWith(parameter.getName()) || s.endsWith(parameter.getName()) || s.contains(parameter.getName())) {
                    indexes.add(fieldStringEntry.getKey());
                }
            }
        }

        return indexes.toArray(new Field[0]);
    }

    private String calculateColumnName(Field field) {
        String name = field.getName();
        StringBuilder newName = new StringBuilder();

        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                newName.append("_");
            } else if(Character.isUpperCase(c)) {
                newName.append("_").append(Character.toLowerCase(c));
            } else {
                newName.append(c);
            }
        }

        return newName.toString();
    }

    public static class SQLAttributeData {

        private final Queries queries;
        private final Map<Field, FieldData> fieldData;
        private final Map<String, Field[]> fieldsPositions;

        public SQLAttributeData(Queries queries, Map<Field, FieldData> fieldData, Map<String, Field[]> fieldsPositions) {
            this.queries = queries;
            this.fieldData = fieldData;
            this.fieldsPositions = fieldsPositions;
        }

        public Queries getQueries() {
            return this.queries;
        }

        public Map<Field, FieldData> getFieldData() {
            return this.fieldData;
        }

        public Map<String, Field[]> getFieldsPositions() {
            return this.fieldsPositions;
        }
    }

    public static class FieldData {

        private final Field field;
        private final String name;
        private final VariableSaveHandler<?> saveHandler;

        public FieldData(Field field, String name, VariableSaveHandler<?> saveHandler) {
            this.field = field;
            this.name = name;
            this.saveHandler = saveHandler;
        }

        public Field getField() {
            return this.field;
        }

        public String getName() {
            return this.name;
        }

        public VariableSaveHandler<?> getSaveHandler() {
            return this.saveHandler;
        }
    }
}

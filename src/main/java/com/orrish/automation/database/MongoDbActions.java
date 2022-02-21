package com.orrish.automation.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.orrish.automation.entrypoint.SetUp;
import com.orrish.automation.utility.report.ReportUtility;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.json.Converter;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MongoDbActions {

    private static MongoDbActions mongoDbActions;
    private static MongoDatabase mongoDatabase;
    private static Exception exception;

    static {
        try {
            ConnectionString connectionString = new ConnectionString(SetUp.mongoDbConnectionString);
            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .retryWrites(true)
                    .build();
            MongoClient mongoClient = MongoClients.create(mongoClientSettings);
            mongoDatabase = mongoClient.getDatabase(SetUp.mongoDatabaseName);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    private MongoDbActions() {
    }

    public static MongoDbActions getInstance() {
        if (mongoDbActions == null) {
            synchronized (MongoDbActions.class) {
                if (mongoDbActions == null) {
                    mongoDbActions = new MongoDbActions();
                    if (exception != null) {
                        ReportUtility.reportInfo("Could not connect to mongodb.");
                        ReportUtility.reportExceptionDebug(exception);
                    }
                }
            }
        }
        return mongoDbActions;
    }

    public List<String> getAllDocumentsFromMongoDBWithCriteria(String collectionName, String criteria) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        MongoIterable<Document> documents = (criteria == null) ? collection.find() : collection.find(BsonDocument.parse(criteria));
        Iterator<Document> iterator = documents.iterator();
        List<String> listOfDocuments = new ArrayList<>();
        while (iterator.hasNext()) {
            JsonWriterSettings writerSettings = JsonWriterSettings
                    .builder()
                    .doubleConverter(new JsonDoubleConverter())
                    .int32Converter(new JsonIntegerConverter())
                    .int64Converter(new JsonLongConverter())
                    .outputMode(JsonMode.EXTENDED)
                    .indent(true)
                    .build();
            listOfDocuments.add(iterator.next().toJson(writerSettings));
        }
        return listOfDocuments;
    }

    private Object getValueOfObjectToSet(String value) {
        try {
            if (value.trim().equalsIgnoreCase("true") || value.trim().equalsIgnoreCase("false"))
                return Boolean.parseBoolean(value);
            return Integer.parseInt(value);
        } catch (Exception integerException) {
            try {
                return Long.parseLong(value);
            } catch (Exception longException) {
            }
        }
        //Remove quotes if present in the string
        return value.startsWith("\"") ? value.substring(1, value.length() - 1) : value;
    }

    public int updateMongoDBForCollectionSetForCriteria(String collectionName, String setValue, String criteria) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        String valueToSetString = setValue.split("=")[1].trim();
        Object valueToSet = getValueOfObjectToSet(valueToSetString);
        UpdateResult value = collection.updateOne(BsonDocument.parse(criteria), Updates.set(setValue.split("=")[0].trim(), valueToSet));
        return (int) value.getModifiedCount();
    }

    public int deleteInMongoDBForCollectionWithCriteria(String collectionName, String criteria) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        DeleteResult deleteResult = collection.deleteMany(BsonDocument.parse(criteria));
        return (int) deleteResult.getDeletedCount();
    }

    public static class JsonDateTimeConverter implements Converter<Long> {

        final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

        @Override
        public void convert(Long value, StrictJsonWriter strictJsonWriter) {
            try {
                Instant instant = new Date(value).toInstant();
                strictJsonWriter.writeString(DATE_TIME_FORMATTER.format(instant));
            } catch (Exception e) {
            }
        }
    }

    public static class JsonDoubleConverter implements Converter<Double> {
        @Override
        public void convert(Double value, StrictJsonWriter strictJsonWriter) {
            try {
                strictJsonWriter.writeRaw((value.toString()));
            } catch (Exception e) {
            }
        }
    }

    public static class JsonIntegerConverter implements Converter<Integer> {
        @Override
        public void convert(Integer value, StrictJsonWriter strictJsonWriter) {
            try {
                strictJsonWriter.writeRaw((value.toString()));
            } catch (Exception e) {
            }
        }
    }

    public static class JsonLongConverter implements Converter<Long> {
        @Override
        public void convert(Long value, StrictJsonWriter strictJsonWriter) {
            try {
                strictJsonWriter.writeRaw(String.valueOf(value));
            } catch (Exception e) {
            }
        }
    }
}

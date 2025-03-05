package vttp.batch5.paf.movies.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;

import jakarta.json.JsonObject;
import vttp.batch5.paf.movies.models.Director;
import vttp.batch5.paf.movies.util.JSON.JSONDefaultValues;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_DIRECTOR;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_GENRES;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_IMDB_ID;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_IMDB_RATING;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_IMDB_VOTES;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_OVERVIEW;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_TAGLINE;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_TITLE;
import static vttp.batch5.paf.movies.util.Mongo.MongoErrorFields.F_ERROR;
import static vttp.batch5.paf.movies.util.Mongo.MongoErrorFields.F_IMDB_ID;
import static vttp.batch5.paf.movies.util.Mongo.MongoErrorFields.F_TIMESTAMP;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.C_ERRORS;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.C_IMDB;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_DIRECTORS;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_GENRES;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_ID;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_IMDB_ID_FIELD;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_IMDB_RATING;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_IMDB_VOTES;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_MOVIES;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_OVERVIEW;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_TAGLINE;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_TITLE;

@Repository
public class MongoMovieRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

 // TODO: Task 2.3
 // You can add any number of parameters and return any type from the method
 // You can throw any checked exceptions from the method
 // Write the native Mongo query you implement in the method in the comments
 //
 // db.imdb.bulkWrite([
//  {insertOne : {<field> : <document>}} 
// ])
 //
    public void batchInsertMovies(List<JsonObject> itemsToInsert) {
        List<String> imdbIds = new ArrayList<>();
        itemsToInsert.forEach((item) -> {
            imdbIds.add(item.getString(F_JSON_IMDB_ID));
        });
        try {
            System.out.println("Inserting into Mongo");
            List<Document> documentsToInsert = new LinkedList<>();
            itemsToInsert.forEach((object) -> {
            documentsToInsert.add(getParams(object));
            });
            mongoTemplate.insert(documentsToInsert,C_IMDB);
        } catch (MongoException e) {
            logError(imdbIds, e);
        }
        
        // List<String> imdbIds = new ArrayList<>();
        // Map<String, Integer> imdbIdsMap = new HashMap<>();
        // List<InsertOneModel<Document>> params = new ArrayList<>(); 
        // System.out.println(array.size());
        // try {
        //     MongoCollection<Document> collection = mongoTemplate.getCollection(C_IMDB);
        //     for(int i = 0; i < array.size(); i++){
        //         JsonObject object = array.getJsonObject(i);
        //         String id = object.getString(F_JSON_IMDB_ID);
        //         if (!imdbIdsMap.containsKey(id)){
        //             imdbIdsMap.put(id, i);
        //             imdbIds.add(id);
        //             params.add(new InsertOneModel<>(getParams(object)));
        //             if(params.size() == 25){

        //                 collection.bulkWrite(params);
        //                 params = new ArrayList<>();
        //             }
        //         }
        //         if(i == array.size() - 1){
        //             collection.bulkWrite(params);
        //         }
        //     }
        // } catch (MongoException me){
        //     logError(imdbIds, me);
        // }

    }

    private Document getParams(JsonObject object){
        Document documentToInsert = new Document();
        documentToInsert.append(F_JSON_IMDB_ID,JSONDefaultValues.getStringInput(F_JSON_IMDB_ID, object))
        .append(F_TITLE, JSONDefaultValues.getStringInput(F_JSON_TITLE, object))
        .append(F_DIRECTORS, JSONDefaultValues.getStringInput(F_JSON_DIRECTOR, object))
        .append(F_OVERVIEW, JSONDefaultValues.getStringInput(F_JSON_OVERVIEW, object))
        .append(F_TAGLINE,JSONDefaultValues.getStringInput(F_JSON_TAGLINE, object))
        .append(F_GENRES,JSONDefaultValues.getStringInput(F_JSON_GENRES, object))
        .append(F_IMDB_RATING,JSONDefaultValues.getIntegerInput(F_JSON_IMDB_RATING, object))
        .append(F_IMDB_VOTES,JSONDefaultValues.getIntegerInput(F_JSON_IMDB_VOTES, object));
        return documentToInsert;
    
    }

 // TODO: Task 2.4
 // You can add any number of parameters and return any type from the method
 // You can throw any checked exceptions from the method
 // Write the native Mongo query you implement in the method in the comments
 //
 //    native MongoDB query here
 //
    public void logError(List<String> imdbIds, MongoException me) {
        MongoCollection<Document> errorCollection = mongoTemplate.getCollection(C_ERRORS);
        Document errorDocument = new Document().append(F_IMDB_ID,imdbIds).append(F_ERROR, me.getMessage())
        .append(F_TIMESTAMP,LocalDate.now());
        errorCollection.insertOne(errorDocument);
    }

 // TODO: Task 3
 // Write the native Mongo query you implement in the method in the comments
 //db.imdb.aggregate([
    //     {
    //         $group : {
    //             _id:'$directors',
    //             movies : {
    //                 $push : {
    //                     movies : '$imdb_id'
    //                 }
    //             }
    //         }
    //     }
    // ]);
 //    native MongoDB query here
 //
    public Map<String, List<String>> getTopDirectors(){
        GroupOperation groupByDirector = Aggregation.group(F_DIRECTORS).push(F_IMDB_ID_FIELD).as(F_MOVIES);
        Aggregation aggregation = Aggregation.newAggregation(groupByDirector);
        List<Document> results = mongoTemplate.aggregate(aggregation, C_IMDB,Document.class).getMappedResults();
        Map<String, List<String>> directorsMovieList = new HashMap<>();

        results.forEach(r -> {
            List<String> movies = r.getList("movies", String.class);
            directorsMovieList.put(r.getString(F_ID), movies);
        });
        return directorsMovieList;
    }

    public List<Director> sortDirectors(Map<String, List<String>> directorsList){
       List<Director> temp = new ArrayList<>();
       for(Entry<String, List<String>> entry : directorsList.entrySet()){
            temp.add(new Director(entry.getKey(), entry.getValue().size()));
       }
       Collections.sort(temp, Collections.reverseOrder());
       return temp;
    }

    

}

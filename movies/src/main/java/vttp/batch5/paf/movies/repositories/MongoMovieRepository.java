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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
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
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_IMDB_RATING;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_IMDB_VOTES;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_MOVIES;
import static vttp.batch5.paf.movies.util.Mongo.MongoFields.F_MOVIES_COUNT;
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

    }

    private Document getParams(JsonObject object){
        Document documentToInsert = new Document();
        documentToInsert.append(F_ID,JSONDefaultValues.getStringInput(F_JSON_IMDB_ID, object))
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
//  db.getCollection("imdb").aggregate([
//     {
//         $match : {
//             directors : {
//                 $ne : ""
//             }
//         }
//     },
//     {
//         $group : {
//             _id : "$directors",
//             movies : {
//                 $push : "$_id"
//             }
//             }
//         }, {
//             $addFields : {
//                 movieCount : {
//                     $size : "$movies"
//                 }
//             }
//         }, {
//             $sort : {
//                 movieCount : -1
//             }
//     }
// ])
//    native MongoDB query here
//

    public List<Director> getDirectors(){
        List<Director> directors = new ArrayList<>();
        Map<String, List<String>> directorsMap = getDirectorsAndMovieIds();

        for(Entry<String, List<String>> entry : directorsMap.entrySet()){
            directors.add(new Director(entry.getKey(),entry.getValue()));
        }
        Collections.sort(directors, Collections.reverseOrder());
        return directors;
    }
    
    private List<Document> getDirectorsInfo(){
        Criteria notEmpty = Criteria.where(F_DIRECTORS).ne("");
        MatchOperation removeEmptyValues = Aggregation.match(notEmpty);
        GroupOperation groupByDirectors = Aggregation.group(F_DIRECTORS).push(F_ID).as(F_MOVIES);
        AddFieldsOperation addMoviesCount = AddFieldsOperation.addField(F_MOVIES_COUNT).withValueOfExpression("{$size : '$movies'}").build();
        SortOperation sortByMovieCount = Aggregation.sort(Sort.by(Direction.DESC,F_MOVIES_COUNT));

        Aggregation aggregation = Aggregation.newAggregation(removeEmptyValues,groupByDirectors,addMoviesCount,sortByMovieCount);
        return mongoTemplate.aggregate(aggregation, C_IMDB,Document.class).getMappedResults();
    }

    private Map<String, List<String>> getDirectorsAndMovieIds(){
        Map<String, List<String>> directorsInfo = new HashMap<>();
        List<Document> directors = getDirectorsInfo();

        directors.forEach(d -> {
            List<String> movieIds = d.getList(F_MOVIES, String.class);
            directorsInfo.put(d.getString(F_ID),movieIds);
        });

        return directorsInfo;
    }
        

}

package vttp.batch5.paf.movies.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.batch5.paf.movies.models.Director;
import vttp.batch5.paf.movies.util.JSON.JSONDefaultValues;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_BUDGET;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_IMDB_ID;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_RELEASE_DATE;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_REVENUE;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_RUNTIME;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_VOTE_AVERAGE;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_VOTE_COUNT;
import static vttp.batch5.paf.movies.util.SQL.MySQLQueries.FIND_PROFIT;
import static vttp.batch5.paf.movies.util.SQL.MySQLQueries.INSERT_INTO_TABLE_IMDB;
import static vttp.batch5.paf.movies.util.SQL.MySQLQueries.MYSQL_COUNT;
import static vttp.batch5.paf.movies.util.SQL.MySQLQueries.SELECT_ALL_FIELDS;

@Repository
public class MySQLMovieRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  // TODO: Task 2.3
  // You can add any number of parameters and return any type from the method
  public void batchInsertMovies(List<JsonObject> itemsToInsert){
    List<Object[]> params = new ArrayList<>();
    itemsToInsert.forEach((object) -> {
      params.add(getParams(object));
    });
    System.out.println("Inserting into MySQL");
    jdbcTemplate.batchUpdate(INSERT_INTO_TABLE_IMDB,params);
  }

  // public void batchInsertMovies(JsonArray array) {
  //   List<Object[]> params = new ArrayList<>();
  //   Map<String, Integer> imdbIds = new HashMap<>();
  //   System.out.println(array.size());
  //   for(int i = 0; i < array.size(); i++){
  //     JsonObject object = array.getJsonObject(i);
  //     String id = object.getString(F_JSON_IMDB_ID);
  //     if(!imdbIds.containsKey(id)){
  //       imdbIds.put(id, i);
  //       params.add(getParams(object));
  //       if(params.size() == 25){
  //         jdbcTemplate.batchUpdate(INSERT_INTO_TABLE_IMDB, params);
  //         params = new ArrayList<>();
  //       }
  //     }

  //     if(i == array.size() - 1){
  //       jdbcTemplate.batchUpdate(INSERT_INTO_TABLE_IMDB, params);
  //     }
  //   }
  // }

  private Object[] getParams(JsonObject object){
    Object[] objectParams = new Object[]{JSONDefaultValues.getStringInput(F_JSON_IMDB_ID, object),JSONDefaultValues.getIntegerInput(F_JSON_VOTE_AVERAGE, object)
    ,JSONDefaultValues.getIntegerInput(F_JSON_VOTE_COUNT, object),JSONDefaultValues.getStringInput(F_JSON_RELEASE_DATE, object),JSONDefaultValues.getIntegerInput(F_JSON_REVENUE, object),
    JSONDefaultValues.getIntegerInput(F_JSON_BUDGET, object), JSONDefaultValues.getIntegerInput(F_JSON_RUNTIME, object)};

    return objectParams;
  }


  public boolean doesRowExists(){
    SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SELECT_ALL_FIELDS);
    while(rowSet.next()){
      System.out.println(rowSet.getInt(MYSQL_COUNT));
      return rowSet.getInt(MYSQL_COUNT) > 0;
    }
    return false;
    
  }
  
  // TODO: Task 3
  public JsonArray getDirectorsInfo(int count, List<Director> directors, Map<String, List<String>> directorsList){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(int i = 1; i < count + 1; i++){
          arrayBuilder.add(getDirector(directorsList, directors.get(i)));
        }
        return arrayBuilder.build();
  }

    public JsonObject getDirector(Map<String, List<String>> directorsList, Director director){
        List<String> movieIds = directorsList.get(director.getName());
        JsonObjectBuilder object = Json.createObjectBuilder();
        double revenue = 0.00;
        double budget = 0.00;
        for(String movieId : movieIds){
          SqlRowSet rowSet = jdbcTemplate.queryForRowSet(FIND_PROFIT, movieId);
          while(rowSet.next()){
            revenue += rowSet.getDouble("revenue_sum");
            budget += rowSet.getDouble("budget_sum");
          }
        }

        object.add("director_name",director.getName()).add("movies_count",director.getMovies())
        .add("total_revenue",revenue).add("total_budget",budget);

        return object.build();
    }

}

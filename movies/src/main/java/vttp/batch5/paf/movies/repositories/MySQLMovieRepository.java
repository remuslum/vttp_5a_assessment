package vttp.batch5.paf.movies.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonObject;
import vttp.batch5.paf.movies.models.Director;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_DIRECTOR_NAME;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_MOVIE_COUNT;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_TOTAL_BUDGET;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_TOTAL_REVENUE;
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
  public List<Document> getDirectorsInfo(int count, List<Director> directors){
    List<Document> directorsDocument = new ArrayList<>();
    for(int i = 0; i < count; i++){
      directorsDocument.add(buildDirectorObject(directors.get(i)));
    }
    return directorsDocument;
  }

  private Document buildDirectorObject(Director director){
    setRevenueAndBudget(director);
    Document documentToAdd = new Document();
    
    documentToAdd.append(TASK_3_DIRECTOR_NAME,director.getName()).append(TASK_3_MOVIE_COUNT,director.getMovies())
    .append(TASK_3_TOTAL_REVENUE,director.getRevenue()).append(TASK_3_TOTAL_BUDGET,director.getBudget());

    return documentToAdd;
  }

  private void setRevenueAndBudget(Director director){
    List<String> movieIds = director.getMovieIds();
    double revenue = 0.00;
    double budget = 0.00;
    for(String id:movieIds){
      SqlRowSet rowSet = jdbcTemplate.queryForRowSet(FIND_PROFIT, id);
      while(rowSet.next()){
        revenue += rowSet.getDouble("revenue_sum");
        budget += rowSet.getDouble("budget_sum");
      }
    }
    director.setRevenue(revenue);
    director.setBudget(budget); 
  }

}

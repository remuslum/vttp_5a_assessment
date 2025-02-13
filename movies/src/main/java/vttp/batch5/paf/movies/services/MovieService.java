package vttp.batch5.paf.movies.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.JsonArray;
import vttp.batch5.paf.movies.components.JSONComponent;
import vttp.batch5.paf.movies.models.Director;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

@Service
public class MovieService {
  @Autowired
  private JSONComponent jsonComponent;

  @Autowired
  private MongoMovieRepository mongoRepository;

  @Autowired
  private MySQLMovieRepository mySQLMovieRepository;

  @Transactional
  // TODO: Task 2
  public void loadData(String fileName){
    JsonArray array = jsonComponent.readJsonFile(fileName);
    System.out.println("Loading into Mongo");
    mongoRepository.batchInsertMovies(array);
    mySQLMovieRepository.batchInsertMovies(array);

  }

  // TODO: Task 3
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public JsonArray getProlificDirectors(int count) {
    Map<String, List<String>> directorsAndMovies = mongoRepository.getTopDirectors();
    List<Director> sortedDirectors = mongoRepository.sortDirectors(directorsAndMovies);
    return mySQLMovieRepository.getDirectorsInfo(count, sortedDirectors, directorsAndMovies);
  }


  // TODO: Task 4
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public void generatePDFReport() {

  }

}

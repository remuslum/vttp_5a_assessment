package vttp.batch5.paf.movies.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;
import vttp.batch5.paf.movies.services.MovieService;

@Component
public class Dataloader implements CommandLineRunner{

  //TODO: Task 2
  @Autowired
  MovieService movieService;

  @Autowired
  MySQLMovieRepository mySQLMovieRepository;

  @Override
  public void run(String... args) throws Exception {
    if(!mySQLMovieRepository.doesRowExists()){
      System.out.println(args[0]);
      movieService.loadData(args[0]);
      System.out.println("Data loaded");
    }
  }


}

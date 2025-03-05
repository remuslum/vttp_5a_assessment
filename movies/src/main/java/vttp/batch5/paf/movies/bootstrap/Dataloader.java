package vttp.batch5.paf.movies.bootstrap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;
import vttp.batch5.paf.movies.services.MovieService;

@Component
public class Dataloader implements CommandLineRunner{
  private final String ARG_FILE="file";

  //TODO: Task 2
  @Autowired
  MovieService movieService;

  @Autowired
  MySQLMovieRepository mySQLMovieRepository;

  @Override
  public void run(String... args) throws Exception {
    Options option = new Options().addOption(
      Option.builder().longOpt(ARG_FILE).hasArg().required().build()
    );
    CommandLineParser parser = new DefaultParser();

    try {
      CommandLine command = parser.parse(option, args);
      String filePath = "";
      if(command.hasOption(ARG_FILE)){
        filePath = command.getOptionValue(ARG_FILE);
      }
      else {
        filePath = "data/movies_post_2010.zip";
      }
      System.out.println(mySQLMovieRepository.doesRowExists());
      // Load data into database
      if(!mySQLMovieRepository.doesRowExists()){
        System.out.println("Loading Data");
        movieService.loadData(filePath);
      }
    } catch (ParseException e) {
      System.err.println("ParseException occured, unable to parse arguments");
    }

    
    // if(!mySQLMovieRepository.doesRowExists()){
    //   movieService.loadData();
    //   System.out.println("Data loaded");
    // }
  }


}

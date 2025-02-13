package vttp.batch5.paf.movies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp.batch5.paf.movies.services.MovieService;

@Controller
public class MainController {
  @Autowired
  private MovieService movieService;

  // TODO: Task 3
  @GetMapping(path="/api/summary",produces="application/json")
  public ResponseEntity<String> getDirectorsInfo(@RequestParam MultiValueMap<String, String> params){
    int count = Integer.parseInt(params.getFirst("count"));
    System.out.println(movieService.getProlificDirectors(count));
    return new ResponseEntity<>(movieService.getProlificDirectors(count).toString(), HttpStatusCode.valueOf(200));
  }

  
  // TODO: Task 4
  // @GetMapping(path="api/summary/pdf",produces="application/json")
  // public 

}

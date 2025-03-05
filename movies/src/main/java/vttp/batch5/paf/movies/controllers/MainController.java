package vttp.batch5.paf.movies.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import vttp.batch5.paf.movies.services.MovieService;

@Controller
public class MainController {
  @Autowired
  private MovieService movieService;

  // TODO: Task 3
  @GetMapping(path="/api/summary",produces="application/json")
  public ResponseEntity<String> getDirectorsInfo(@RequestParam MultiValueMap<String, String> params,HttpSession httpSession){
    int count = Integer.parseInt(params.getFirst("count"));
    JsonArray output = movieService.getProlificDirectors(count);
    httpSession.setAttribute("arrayInfo", output);
    return new ResponseEntity<>(output.toString(), HttpStatusCode.valueOf(200));
  }

  
  // TODO: Task 4
  @GetMapping(path="api/summary/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> generateJasperReport(@RequestParam int count, HttpSession httpSession){
    JsonArray output = Optional.ofNullable((JsonArray) httpSession.getAttribute("arrayInfo")).orElse(Json.createArrayBuilder().build());
    try {
        byte[] response = movieService.generatePDFReport(count);
        return ResponseEntity.ok(response);
    } catch (JRException e) {
      e.printStackTrace();
      String errorMessage = "Error creating report";
      return new ResponseEntity<>(errorMessage.getBytes(),HttpStatusCode.valueOf(500));
    }
  }

}

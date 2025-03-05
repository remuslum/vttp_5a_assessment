package vttp.batch5.paf.movies.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.pdf.SimplePdfExporterConfiguration;
import net.sf.jasperreports.pdf.SimplePdfReportConfiguration;
import vttp.batch5.paf.movies.components.JSONComponent;
import vttp.batch5.paf.movies.models.Director;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_DIRECTOR_NAME;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_MOVIE_COUNT;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_TOTAL_BUDGET;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_3_TOTAL_REVENUE;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_4_BUDGET;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_4_DIRECTOR;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_4_MOVIE;
import static vttp.batch5.paf.movies.util.JSON.JSONArrayFields.TASK_4_REVENUE;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_IMDB_ID;

@Service
public class MovieService {
  @Autowired
  private JSONComponent jsonComponent;

  @Autowired
  private MongoMovieRepository mongoRepository;

  @Autowired
  private MySQLMovieRepository mySQLMovieRepository;

  @Value("${batch}")
  private String batch;

  @Value("${name}")
  private String name;

  @Transactional
  // TODO: Task 2
  public void loadData(String fileName){
    JsonArray array = jsonComponent.readJsonArray(fileName);
    Map<String, Integer> imdbIdsMap = new HashMap<>();
    List<JsonObject> itemsToInsert = new ArrayList<>();

    for(int i  = 0; i < array.size(); i++){
      JsonObject object = array.getJsonObject(i);
      String id = object.getString(F_JSON_IMDB_ID);

      if(!imdbIdsMap.containsKey(id)){
        imdbIdsMap.put(id,1);
        itemsToInsert.add(object);
      }

      if(itemsToInsert.size() == 25 || (i == array.size() - 1)){
        mySQLMovieRepository.batchInsertMovies(itemsToInsert);
        mongoRepository.batchInsertMovies(itemsToInsert);
        itemsToInsert = new ArrayList<>();
      }

    }
  }

  // TODO: Task 3
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public JsonArray getProlificDirectors(int count) {
    List<Director> directors = mongoRepository.getDirectors();
    List<Document> directorsDoc = mySQLMovieRepository.getDirectorsInfo(count,directors);
    
    JsonArrayBuilder builder = Json.createArrayBuilder();
    directorsDoc.forEach(d -> {
      builder.add(Json.createReader(new StringReader(d.toJson())).readObject());
    });

    return builder.build();
  }


  // TODO: Task 4
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public byte[] generatePDFReport(int count) throws JRException {
    List<Director> directorList = mongoRepository.getDirectors();
    List<Document> directorDoc = mySQLMovieRepository.getDirectorsInfo(count, directorList);
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    
    directorDoc.forEach(d -> {
      JsonObjectBuilder builder = Json.createObjectBuilder();
      builder.add(TASK_4_DIRECTOR,d.getString(TASK_3_DIRECTOR_NAME)).add(TASK_4_MOVIE,d.getInteger(TASK_3_MOVIE_COUNT))
      .add(TASK_4_REVENUE,d.getDouble(TASK_3_TOTAL_REVENUE)).add(TASK_4_BUDGET,d.getDouble(TASK_3_TOTAL_BUDGET));
      
      arrayBuilder.add(builder.build());
    });

    JsonArray directorArray = arrayBuilder.build();

    JsonObject firstObject = Json.createObjectBuilder().add("name",name).add("batch",batch).build();
    JsonDataSource reportDS = new JsonDataSource(
      new ByteArrayInputStream(firstObject.toString().getBytes())
    );
    JsonDataSource directorsDS = new JsonDataSource(
      new ByteArrayInputStream(directorArray.toString().getBytes())
    );

    Map<String, Object> params = new HashMap<>();
    params.put("DIRECTOR_TABLE_DATASET",directorsDS);

    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("data/director_movies_report.jasper"));

    JasperPrint print = JasperFillManager.fillReport(jasperReport, params, reportDS);

    ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

    JRPdfExporter exporter = new JRPdfExporter();
    exporter.setExporterInput(new SimpleExporterInput(print));
    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfOutputStream));

    SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
    SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
    exporter.setConfiguration(reportConfig);
    exporter.setConfiguration(exportConfig);
    exporter.exportReport();
    
    return pdfOutputStream.toByteArray();
  }  

}

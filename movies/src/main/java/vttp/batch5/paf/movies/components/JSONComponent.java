package vttp.batch5.paf.movies.components;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.stereotype.Component;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.F_JSON_RELEASE_DATE;

@Component
public class JSONComponent {

    public JsonArray readJsonArray(String filePath){
        String line = "";
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        try {
            System.out.println("Am I reading file");
            BufferedReader br = readZipFile(filePath);
            while((line = br.readLine()) != null){
                JsonObject object = Json.createReader(new StringReader(line)).readObject();
                LocalDate releasedDate = LocalDate.parse(object.getString(F_JSON_RELEASE_DATE));
                if(releasedDate.getYear() >= 2018){
                    jsonArrayBuilder.add(Json.createReader(new StringReader(line)).readObject());
                }
            }
            return jsonArrayBuilder.build();
        } catch (IOException ie) {
            System.out.println("IO Exception occured");
        }
        return jsonArrayBuilder.build();
    }

    public BufferedReader readZipFile(String filePath){
        try {
            // Read zip file
            ZipFile zipFile = new ZipFile((filePath));
            ZipEntry entry = zipFile.entries().nextElement();
            BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));

            return br;
        } catch (FileNotFoundException fe){
            System.out.println("File Not Found Exception occured");
        } catch (IOException ie){
            System.out.println("IO Exception occured");
        } 
        return null;
    }

    // public JsonArray readJsonFile(String filePath){
    //     String line = "";
    //     JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    //     try {
    //         // FileInputStream inputStream = new FileInputStream(filePath);
    //         BufferedReader br = new BufferedReader(new FileReader(filePath));
    //         // JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

    //         while((line = br.readLine()) != null){
    //             JsonObject object = Json.createReader(new StringReader(line)).readObject();
    //             LocalDate releasedDate = LocalDate.parse(object.getString(F_JSON_RELEASE_DATE));
    //             if(releasedDate.getYear() >= 2018){
    //                 jsonArrayBuilder.add(Json.createReader(new StringReader(line)).readObject());
    //             }
    //         }
            
    //         br.close();
            
    //     } catch (FileNotFoundException e){
    //         System.out.println("File not Found Exception occured");
    //     } catch (IOException e){
    //         System.out.println("IOException Occured");
    //     }
    //     return jsonArrayBuilder.build();
    // }

    // {"title":"Sin City: A Dame to Kill For","vote_average":6,"vote_count":3812,"status":"Released",
    // "release_date":"2014-08-20","revenue":39400000,"runtime":102,"budget":65000000,"imdb_id":"tt0458481",
    // "original_language":"en","overview":"Some of Sin City's most hard-boiled citizens cross paths with a few of its more reviled inhabitants.",
    // "popularity":58,"tagline":"There is no justice without sin.","genres":"Crime, Action, Thriller","spoken_languages":"English",
    // "casts":"John Wirt, Callie Hernandez, Mike Davis, Dennis Haysbert, Rob Franco, Jaime King, Billy Blair, Lawrence Varnado, Emmy Robbin, Alcides Dias, Bob Schreck, Bart Fletcher, Juno Temple, Josh Brolin, Christopher Lloyd, Christopher Meloni, Patricia Vonne, Powers Boothe, Jude Ciccolella, Bruce Willis, Mickey Rourke, Patrick Sane, Dimitrius Pulido, Tommy Townsend, Gregory Kelly, Jamie Chung, Kea Ho, Stacy Keach, Kimberly Cox, Marton Csokas, Johnny Reno, Julia Garner, Jeremy Piven, Eloise DeJoria, Samuel Davis, Joseph Gordon-Levitt, Alexa PenaVega, Eva Green, Ray Liotta, Vincent Fuentes, Will Beinbrink, Luis Albert Acevedo Jr., Jessica Alba, Alejandro Rose-Garcia, Robert Rodriguez, Jimmy Gonzales, Lady Gaga, Rosario Dawson, Daylon Walton, Robert Lott, Christian Bowman, Greg Ingram","director":"Robert Rodriguez, Frank Miller","imdb_rating":7,"imdb_votes":173162,"poster_path":"/50kALxDX4mmzIRljbNbPY0u4cie.jpg"}
}

package vttp.batch5.paf.movies.util.JSON;

import java.util.Optional;

import jakarta.json.JsonObject;
import static vttp.batch5.paf.movies.util.JSON.JSONFields.STRING_DEFAULT_VALUE;

public class JSONDefaultValues {

    public static String getStringInput(String field, JsonObject jsonObject){
        return Optional.ofNullable(jsonObject.getString(field)).orElse(STRING_DEFAULT_VALUE);
  }

    public static int getIntegerInput(String field, JsonObject jsonObject){
        try {
          return jsonObject.getInt(field);
        } catch (ClassCastException e){
          return 0;
        }
  }
}

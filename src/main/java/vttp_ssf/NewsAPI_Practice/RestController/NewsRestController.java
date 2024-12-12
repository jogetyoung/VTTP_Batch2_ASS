package vttp_ssf.NewsAPI_Practice.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vttp_ssf.NewsAPI_Practice.Service.NewsService;

@RestController
@RequestMapping("/news")
public class NewsRestController {

    @Autowired
    private NewsService newsService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNewsById(@PathVariable int id) {

        try{
           JsonObject jsonNews = newsService.getArticlesById(id);

           if (jsonNews == null) {
               JsonObject errorResponse = Json.createObjectBuilder()
                       .add("Error: ", "Cannot find news article " + id)
                       .build();

               return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(errorResponse.toString());
           } else {
               return ResponseEntity.status(200).body(jsonNews.toString());
           }
        } catch (Exception e) {
            //return generic error message
            return ResponseEntity.status(500).body("Error retrieving article with ID " + id);
        }
    }
}

package vttp_ssf.NewsAPI_Practice.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vttp_ssf.NewsAPI_Practice.Model.News;
import vttp_ssf.NewsAPI_Practice.Repository.NewsRepo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service  // Indicates that this class is a service layer component
public class NewsService {

    @Autowired
    private NewsRepo newsRepo;  // Injects the Redis repository for data storage

    @Value("${cryptonews.api.key}")
    private String apiKey;  // API key for authentication

    @Value("${cryptonews.api.url}")
    private String apiUrl;  // Base URL of the CryptoCompare API

    /**
     * Fetches a list of articles from the external CryptoCompare API.
     *
     * @param lang  The language of the articles (e.g., "EN").
     * @param limit The maximum number of articles to fetch.
     * @return A list of News articles parsed from the API response.
     */
    public List<News> getArticles(String lang, int limit) {

        // Step 1: Build the API request URL with query parameters
        String url = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("lang", lang)   // Sets the language of articles (e.g., "EN" for English)
                .queryParam("limit", limit) // Sets the maximum number of articles to fetch
                .build()
                .toUriString();
        System.out.println("URL: " + url); // Print the constructed API URL for debugging

        // Step 2: Create a GET request with headers
        RequestEntity<Void> requestEntity = RequestEntity
                .get(url)                           // Specify the API endpoint to call
                .header("x-api-key", apiKey)        // Add the API key for authentication
                .accept(MediaType.APPLICATION_JSON) // Expect JSON format in the response
                .build();

        // Step 3: Create RestTemplate for sending the API request
        RestTemplate restTemplate = new RestTemplate();

        // Initialize a list to store the parsed articles
        List<News> news = new ArrayList<>();

        try {
            // Step 4: Send the GET request and store the response
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            String payload = response.getBody();  // Extract the response body as a string

            // Step 5: Parse the JSON response
            JsonReader jsonReader = Json.createReader(new StringReader(payload));
            JsonObject jsonObject = jsonReader.readObject();          // Convert the response into a JsonObject
            JsonArray jsonArray = jsonObject.getJsonArray("Data");   // Extract the "Data" array containing articles

            System.out.println("Processing JsonArray");

            // Step 6: Loop through each article in the JSON array
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject newsObject = jsonArray.getJsonObject(i);

                // Create a new News object to store the article's data
                News articles = new News();

                // Step 7: Extract core article information and set it in the News object
                articles.setId(newsObject.getJsonNumber("ID").intValue()); // Unique article ID
                articles.setPublishedOn(new Date(newsObject.getJsonNumber("PUBLISHED_ON").longValue())); // Publish date
                articles.setTitle(newsObject.getString("TITLE"));          // Article title
                articles.setUrl(newsObject.getString("URL"));              // Article link
                articles.setImageUrl(newsObject.getString("IMAGE_URL"));   // Image URL
                articles.setBody(newsObject.getString("BODY"));            // Main article content


                // Step 8: Extract and set article keywords (tags)
                // Create an empty list to store extracted keywords
                List<String> tags = new ArrayList<>();

                // Extract the "KEYWORDS" string from the API response
                // Example: "Crypto|Bitcoin|Blockchain"
                String[] keywords = newsObject.getString("KEYWORDS").split("\\|");

                /* Why Use "\\|":
                 * - "|" is a special character in regular expressions (regex), meaning "OR".
                 * - To treat it as a literal character, we need to escape it with "\\".
                 * - If the string is empty, "split()" returns an empty array, preventing null errors.
                 */

                // Add all extracted keywords to the 'tags' list
                Collections.addAll(tags, keywords);
                /* Why Use 'Collections.addAll':
                 * - It efficiently adds all elements from the 'keywords' array to the list.
                 * - Equivalent code using a loop would be:
                 *   for (String keyword : keywords) {
                 *       tags.add(keyword);
                 *   }
                 */

                // Assign the populated 'tags' list to the article's 'keywords' property
                articles.setKeywords(tags);


                // Step 9: Extract and set article categories
                JsonArray categories = newsObject.getJsonArray("CATEGORY_DATA");
                List<String> categoryList = new ArrayList<>();
                for (int j = 0; j < categories.size(); j++) {
                    JsonObject categoryObject = categories.getJsonObject(j);
                    String categoryData = categoryObject.getString("CATEGORY");  // Extract category name
                    categoryList.add(categoryData);                             // Add to the list
                }
                articles.setCategory(categoryList);  // Save categories to the article

                // Step 10: Add the fully populated article to the list
                news.add(articles);
            }

        } catch (Exception e) {
            // Handle any errors that occur during the request or parsing
            e.printStackTrace();
        }

        // Final Step: Return the list of articles
        return news;
    }



    /**
     * Saves a list of articles to the Redis database using the repository.
     *
     * @param articles The list of articles to be saved.
     */
    public void saveArticles(List<News> articles) {
        if (articles == null || articles.isEmpty()) {
            System.out.println("No articles to save.");
            return;
        }

        // Save each article using the repository
        for (News article : articles) {
            newsRepo.saveNewsArticles(article);
            System.out.println("Saved article: " + article.getTitle());
        }
    }

    /**
     * Retrieves a specific article from Redis and converts it to a JSON object.
     *
     * @param id The ID of the article to retrieve.
     * @return A JsonObject representing the article or null if not found.
     */
    public JsonObject getArticlesById(int id) {
        JsonObject jsonObject = null;

        try {
            // Retrieve the article from Redis using its ID
            News article = newsRepo.getNewsArticleById(id);

            // Convert the article to a JSON object
            jsonObject = Json.createObjectBuilder()
                    .add("id", article.getId())
                    .add("title", article.getTitle())
                    .add("body", article.getBody())
                    .add("published_on", article.getPublishedOn().getTime())
                    .add("url", article.getUrl())
                    .add("imageurl", article.getImageUrl())
                    .add("tags", String.valueOf(article.getKeywords()))
                    .add("categories", String.valueOf(article.getCategory()))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();  // Print the error if something goes wrong
        }

        return jsonObject;  // Return the JSON representation of the article
    }
}
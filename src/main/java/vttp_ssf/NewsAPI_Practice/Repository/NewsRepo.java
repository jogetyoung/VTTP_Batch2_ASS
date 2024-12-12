package vttp_ssf.NewsAPI_Practice.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import vttp_ssf.NewsAPI_Practice.Model.News;

@Repository
public class NewsRepo {

    // Inject RedisTemplate for Redis database operations
    @Autowired
    @Qualifier("redis-object")  // Reference the correct Redis template bean
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Saves a news article into Redis.
     *
     * @param article The article to be saved.
     *
     * Redis storage structure:
     * - "Articles" is the hash key (like a table).
     * - article.getId() is the unique key for each article (like a row ID).
     * - article is the actual content saved.
     */
    public void saveNewsArticles(News article) {
        redisTemplate.opsForHash().put("Articles", String.valueOf(article.getId()), article);
    }

    /**
     * Retrieves a news article from Redis by its ID.
     *
     * @param newsId The unique ID of the requested article.
     * @return The found News article object.
     * @throws IllegalArgumentException if the article is not found in Redis.
     */
    public News getNewsArticleById(int newsId) {
        // Retrieve the article from the "Articles" hash using its ID
        News article = (News) redisTemplate.opsForHash().get("Articles", String.valueOf(newsId));

        // If no article is found, throw an error
        if (article == null) {
            throw new IllegalArgumentException("Article with ID " + newsId + " not found in Redis.");
        }

        return article;  // Return the found article
    }
}
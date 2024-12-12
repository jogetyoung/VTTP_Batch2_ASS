package vttp_ssf.NewsAPI_Practice.Model;

import java.util.Date;
import java.util.List;

public class News {

    private int id;

    private Date publishedOn;

    private String title;

    private String url;

    private String imageUrl;

    private String body;

    private List<String> keywords;

    private List<String> category;

    private boolean saveArticle;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(Date publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public boolean isSaveArticle() {
        return saveArticle;
    }

    public void setSaveArticle(boolean saveArticle) {
        this.saveArticle = saveArticle;
    }

    public News(int id, Date publishedOn, String title, String url, String imageUrl, String body, List<String> keywords, List<String> category) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.body = body;
        this.keywords = keywords;
        this.category = category;
    }

    public News(int id, Date publishedOn, String title, String url, String imageUrl, String body, List<String> keywords, List<String> category, boolean saveArticle) {
        this.id = id;
        this.publishedOn = publishedOn;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.body = body;
        this.keywords = keywords;
        this.category = category;
        this.saveArticle = saveArticle;
    }




    public News() {

    }
}

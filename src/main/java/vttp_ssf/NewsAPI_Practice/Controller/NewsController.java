package vttp_ssf.NewsAPI_Practice.Controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vttp_ssf.NewsAPI_Practice.Model.News;
import vttp_ssf.NewsAPI_Practice.Service.NewsService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/")
    public String getNews(Model model, HttpSession session) {

        String lang = "EN";
        int limit = 10;

        List<News> newsList = newsService.getArticles(lang, limit);

        //store articles in a session
        session.setAttribute("savedArticles", newsList);

        model.addAttribute("newsList", newsList);

        return "news";
    }

    @PostMapping("/articles")
    public String saveArticles(@RequestParam(value = "selectedArticles", required = false) List<Integer> selectedArticles,
                               HttpSession session, Model model) {

        // Handle case where no articles are selected
        if (selectedArticles == null || selectedArticles.isEmpty()) {
            model.addAttribute("message", "No articles selected.");

            return "redirect:/";
        }

        // Retrieve saved articles from the session or create a new list
        List<News> savedArticles = (List<News>) session.getAttribute("savedArticles");
        if (savedArticles == null || savedArticles.isEmpty()) {
           model.addAttribute("message", "No articles selected");
           return "redirect:/";
        }

//        // Fetch articles and save them to the session
//        List<News> latestArticles = newsService.getArticles("EN", 10);
//        for (Integer id : selectedArticles) {
//            latestArticles.stream()
//                    .filter(news -> news.getId() == id)
//                    .findFirst()
//                    .ifPresent(savedArticles::add);
//        }
//
//        // Update the session
//        session.setAttribute("savedArticles", savedArticles);
//
//        // Redisplay the latest articles
//        model.addAttribute("newsList", latestArticles);
//        return "news";

        List <News> articlesToSave = savedArticles.stream()
                .filter(article -> selectedArticles.contains(article.getId()))
                .collect(Collectors.toList());

        //save selected articles to Redis
        newsService.saveArticles(articlesToSave);

        //message to indicate saving is successful
        model.addAttribute("message", "Articles saved successfully");

        return "redirect:/";
    }



}

package com.example.demo.Controller;

import com.example.demo.Collection.Quiz;
import com.example.demo.Collection.extras.Questions;
import com.example.demo.Exceptions.GeneralException;
import com.example.demo.Model.QuizShortModel;
import com.example.demo.Services.QuizService;
import com.example.demo.Exceptions.QuizNotFoundException;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    //Saves a Quiz into Quiz Database
    @PostMapping
    public String saveQuiz(@RequestBody Quiz quiz) {
        String timestamp = String.valueOf(new java.util.Date());
        String id = "user" + quiz.getAuthorID() + quiz.getTitle() + timestamp + UUID.randomUUID();
        String hashed = Hashing.sha256()
                .hashString(id, StandardCharsets.UTF_8)
                .toString();
        quiz.setQuizID(hashed);
        quiz.setTimestamp(Calendar.getInstance().getTime());
        return quizService.saveQuiz(quiz);
    }

    //Fetch a particular Quiz
    @GetMapping
    public Quiz fetchQuizById(@RequestParam("id") String quizID) throws QuizNotFoundException {
        return quizService.findQuizById(quizID);
    }

    @GetMapping("/questions")
    public Questions fetchQuestionById(@RequestParam("id") String quizID) throws QuizNotFoundException{
        return quizService.findQuestionById(quizID);
    }

    //Delete a particular Quiz
    @DeleteMapping("/{id}")
    public void deleteQuizById(@PathVariable("id") String quizID) {
        quizService.deleteQuizById(quizID);
    }

    //Updates a particular Quiz
    @PutMapping("/{id}")
    public Quiz updateQuiz(@PathVariable("id") String quizID, @RequestBody Quiz quiz) {
        return quizService.updateQuiz(quizID, quiz);
    }

    //Fetch quiz by rating
    @GetMapping("/rating")
    public List<QuizShortModel> fetchByRating(@RequestParam Double minRating, @RequestParam Double maxRating) {
        return quizService.fetchByRating(minRating, maxRating);
    }


    //Generic search
    @GetMapping("/search")
    public List<QuizShortModel> searchMain(@RequestParam String searchKey,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "25") Integer size) throws GeneralException {
        Pageable pageable = PageRequest.of(page, size);
        return quizService.search(searchKey, pageable);
    }


    //Custom Search
    @GetMapping("/customSearch")
    public Page<QuizShortModel> searchQuiz(@RequestParam(required = false) String title,
                                           @RequestParam(required = false) String desc,
                                           @RequestParam(required = false) String author,
                                           @RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return quizService.searchQuiz(title, desc, author, pageable);
    }


    //Fetch List of uploads
    @GetMapping("/uploads")
    List<QuizShortModel> fetchUserUploads(@RequestParam String authorID) {
        return quizService.fetchUserUploads(authorID);
    }

    //load For you page
    @GetMapping("/load")
    List<QuizShortModel> loadQuiz(@RequestParam(name = "page",defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "50") Integer size) {

        Pageable pageable = PageRequest.of(page, size);
        return quizService.loadQuiz(pageable);
    }

}

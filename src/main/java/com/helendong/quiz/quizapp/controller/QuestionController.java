package com.helendong.quiz.quizapp.controller;
import com.helendong.quiz.quizapp.dto.QuestionDTO;
import com.helendong.quiz.quizapp.model.Question;
import com.helendong.quiz.quizapp.model.Quiz;
import com.helendong.quiz.quizapp.service.QuestionService;
import com.helendong.quiz.quizapp.service.QuizService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private QuizService quizService;


    @Autowired
    public QuestionController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        Question newQuestion = convertToEntity(questionDTO);
        Question createdQuestion = questionService.createQuestion(newQuestion);
        QuestionDTO createdQuestionDTO = convertToDto(createdQuestion);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuestionDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        Optional<Question> question = questionService.getQuestionById(id);
        return question.map(value -> ResponseEntity.ok(convertToDto(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        List<Question> allQuestions = questionService.getAllQuestions();
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : allQuestions) {
            questionDTOs.add(convertToDto(question));
        }
        return ResponseEntity.ok(questionDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionDTO questionDTO) {
        Question updatedQuestion = convertToEntity(questionDTO);
        Question savedQuestion = questionService.updateQuestion(id, updatedQuestion);
        QuestionDTO updatedQuestionDTO = convertToDto(savedQuestion);
        return ResponseEntity.ok(updatedQuestionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok().build();
    }

    @Transactional
    private Question convertToEntity(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setText(questionDTO.getText());
        question.setAnswer(questionDTO.getAnswer());

        if (questionDTO.getQuizId() != null) {
            Optional<Quiz> quizOptional = quizService.getQuizById(questionDTO.getQuizId());
            if (!quizOptional.isPresent()) {
                throw new EntityNotFoundException("Quiz not found for id: " + questionDTO.getQuizId());
            }
            Quiz quiz = quizOptional.get();
            question.setQuiz(quiz);
        }

        return question;
    }


    private QuestionDTO convertToDto(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setText(question.getText());
        questionDTO.setAnswer(question.getAnswer());

        if (question.getQuiz() != null) {
            questionDTO.setQuizId(question.getQuiz().getId());
        }
        return questionDTO;
    }

}

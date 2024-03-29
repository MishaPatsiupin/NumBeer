package com.example.demo.controller;

import com.example.demo.entity.CategoryEntity;
import com.example.demo.entity.FactCategoryEntity;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.FactCategoryRepository;
import com.example.demo.repository.NumberRepository;
import com.example.demo.service.FactCategoryService;
import com.example.demo.service.FactService;
import com.example.demo.service.NumberService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@AllArgsConstructor
public class FactInfoController {

    NumberService numberService;
    NumberRepository numberRepository;
    CategoryRepository categoryRepository;
    FactCategoryService factCategoryService;
    FactService factService;
    private final FactCategoryRepository factCategoryRepository;
    private final SecureRandom random = new SecureRandom ();


    @GetMapping(value = "/test")
    public ResponseEntity<List<String>> getTest(@RequestParam(value = "number", defaultValue = "random")
                                                @Pattern(regexp = "\\d+|^(random)") String numberS,
                                                @RequestParam(value = "type", defaultValue = "trivia")
                                                @Pattern(regexp = "^(year|math|trivia)$") String type) {
        long number = 0;
        if (numberS.equals("random")) {
            number = 500 - random.nextLong(1001);
        } else {
            number = Long.parseLong(numberS);
        }

        long numberId = numberRepository.findByNumberData(number).getId();//+

        List<FactCategoryEntity> test;
        test = factCategoryRepository.findFactCategoriesByFactId(numberId);//+


        List<String> testS = new ArrayList<>();//а зачем оно надо, категорию давай
        for (int i = 0; i < test.size(); i++) {
            if (test.get(i).getCategory().getId() == categoryRepository.findIdByName(type)) {
                testS.add("Fact id:" + test.get(i).getFact().getId() + ", " + number + " " + test.get(i).getFact().getDescription());
            }

        }

        return ResponseEntity.ok(testS);
    }

    @GetMapping(value = "/info")
    public ResponseEntity<String> getInfoOne(@RequestParam(value = "number", defaultValue = "random")
                                                 @Pattern(regexp = "\\d+|^(random)") String numberS,
                                             @RequestParam(value = "type", defaultValue = "trivia")
                                                 @Pattern(regexp = "^(year|math|trivia)$") String type){

        return factCategoryService.getFactByFactAndCategory(numberS, type);
    }

    @GetMapping(value = "/info/all", produces = "application/json")
    public ResponseEntity<List<String>> getInfoAll(@RequestParam(value = "number", defaultValue = "random")
                                                   @Pattern(regexp = "\\d+|^(random)") String numberS,
                                                   @RequestParam(value = "type", defaultValue = "trivia")
                                                   @Pattern(regexp = "^(year|math|trivia)$") String type) {

        return factCategoryService.getFactsByFactAndCategory(numberS, type);
    }


    @GetMapping(value = "/**")
    public ResponseEntity<String> defaultMethod() {
        return new ResponseEntity<>("Please specify a valid path. For example, http://localhost:8080/info?number=5&type=math", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException() {
        return new ResponseEntity<>("STATUS: 500. Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
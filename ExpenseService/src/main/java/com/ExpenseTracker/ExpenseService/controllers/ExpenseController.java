package com.ExpenseTracker.ExpenseService.controllers;

import com.ExpenseTracker.ExpenseService.DTO.ExpenseDTO;
import com.ExpenseTracker.ExpenseService.Services.ExpenseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expense/v1")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping(path = "/getExpense")
    public ResponseEntity<List<ExpenseDTO>> getExpense(@RequestParam(value = "user_id") @NonNull String userId){
        try{
            List<ExpenseDTO> expenseDtoList = expenseService.getExpenses(userId);
            return new ResponseEntity<>(expenseDtoList, HttpStatus.OK);
        }catch(Exception ex){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path="/addExpense")
    public ResponseEntity<Boolean> addExpenses(@RequestHeader(value = "X-User-Id") @NonNull String userId,@RequestBody ExpenseDTO expenseDto){
        try{
            expenseDto.setUserId(userId);
            if(expenseDto.getExternalId() == null){
                expenseDto.setExternalId(UUID.randomUUID().toString());
            }
            return new ResponseEntity<>(expenseService.createExpense(expenseDto), HttpStatus.OK);
        }catch (Exception ex){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
}

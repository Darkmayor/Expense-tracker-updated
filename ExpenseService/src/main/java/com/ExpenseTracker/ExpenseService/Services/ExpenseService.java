package com.ExpenseTracker.ExpenseService.Services;

import com.ExpenseTracker.ExpenseService.DTO.ExpenseDTO;
import com.ExpenseTracker.ExpenseService.Entities.Expense;
import com.ExpenseTracker.ExpenseService.Repository.ExpenseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public boolean createExpense(ExpenseDTO expenseDto){
        setCurrency(expenseDto);
        try{

            expenseRepository.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;
        }catch(DataIntegrityViolationException ex){
            log.error("Failed to process expense event , externalId={}", expenseDto.getExternalId() , ex);
            return false;
        }
    }

    public boolean updateExpense(ExpenseDTO expenseDto){
        setCurrency(expenseDto);
        Optional<Expense> expenseFoundOpt = expenseRepository.findByUserIdAndExternalId(expenseDto.getUserId(), expenseDto.getExternalId());
        if(expenseFoundOpt.isEmpty()){
            return false;
        }
        Expense expense = expenseFoundOpt.get();
        expense.setAmount(expenseDto.getAmount());
        expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant())?expenseDto.getMerchant():expense.getMerchant());
        expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency())?expenseDto.getCurrency():expense.getCurrency());
        expenseRepository.save(expense);
        return true;
    }

    public List<ExpenseDTO> getExpenses(String userId){
        List<Expense> expenseOpt = expenseRepository.findByUserId(userId);
        return objectMapper.convertValue(expenseOpt, new TypeReference<List<ExpenseDTO>>() {});
    }

    private void setCurrency(ExpenseDTO expenseDto){
        if(Objects.isNull(expenseDto.getCurrency())){
            expenseDto.setCurrency("inr");
        }
    }
}

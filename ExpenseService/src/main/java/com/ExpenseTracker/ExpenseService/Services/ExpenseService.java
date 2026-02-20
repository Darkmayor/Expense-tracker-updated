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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "expenses", key = "#expenseDto.userId")
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

    @Cacheable(value = "expenses", key = "#userId")
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

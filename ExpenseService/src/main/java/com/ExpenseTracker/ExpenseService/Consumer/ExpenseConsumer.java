package com.ExpenseTracker.ExpenseService.Consumer;

import com.ExpenseTracker.ExpenseService.DTO.ExpenseDTO;
import com.ExpenseTracker.ExpenseService.Services.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseConsumer {


    private final ExpenseService expenseService;

    @KafkaListener(topics = "${spring.kafka.topic-json.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ExpenseDTO eventData) {
        try{
            // Todo: Make it transactional, and check if duplicate event (Handle idempotency)
            expenseService.createExpense(eventData);
        }catch(Exception ex){
            ex.printStackTrace();
            System.out.println("AuthServiceConsumer: Exception is thrown while consuming kafka event");
        }
    }
}

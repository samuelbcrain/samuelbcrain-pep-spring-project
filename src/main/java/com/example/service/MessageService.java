package com.example.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;
import com.example.entity.Message;

@Service
@Transactional
public class MessageService {

    MessageRepository messageRepository;
    AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Message> addMessage (Message message) {
        if (!message.getMessageText().isBlank() &&
            message.getMessageText().length() <= 255 &&
            accountRepository.findAccountByAccountId(message.getPostedBy()) != null) {
                // if message text is valid and account id exists, save and return saved message with ok status
                return new ResponseEntity<Message>(messageRepository.save(message), HttpStatus.OK);
        }
        // if text invalid or account does NOT exist, return bad request status
        return new ResponseEntity<Message>(message, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Message>> getAllMessages () {
        return new ResponseEntity<List<Message>>(messageRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Message> getMessageById (int messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            // if message exists, return message with ok status
            Message message = optionalMessage.get();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        // if message does NOT exist, respond only with ok status
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Integer> deleteMessageById (int messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            // if message exists, delete and return rows affected and ok status
            messageRepository.deleteById(messageId);
            return ResponseEntity.ok(1);
        }
        // if message does NOT exist, respond only with ok status
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Integer> updateMessageById (int messageId, String messageText) {
        if (messageText.length() > 255 || messageText.isBlank()) {
            // if text invalid, respond with bad request status
            return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
        }
        Message messageToUpdate;
        try {
            messageToUpdate = messageRepository.getById(messageId);
            // if message exists update message text and respond with rows affected and ok status
            messageToUpdate.setMessageText(messageText);
            messageRepository.save(messageToUpdate);
            return ResponseEntity.ok(1);
        }
        catch (EntityNotFoundException e) {
            // if message does not exist, respond with bad request status
            return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<List<Message>> getAllMessagesByAccountId (int accountId) {
        return ResponseEntity.status(HttpStatus.OK).body(messageRepository.findMessagesByAccountId(accountId));
    }
}

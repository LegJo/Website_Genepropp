package com.acfjj.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.acfjj.app.repository.ConversationRepository;
import com.acfjj.app.repository.MessageRepository;
import com.acfjj.app.repository.UserRepository;
import com.acfjj.app.utils.ValidationType;
import com.acfjj.app.model.Conversation;
import com.acfjj.app.model.Message;
import com.acfjj.app.model.User;

@Service
@Scope("singleton")
public class ConversationService extends AbstractService {
    public Conversation getConversation(long id) {
		return conversationRepository.findById(id).orElse(null);
	}
    
    public List<Conversation> getUserConversations(User user) {
    	List<Conversation> conversationsResult = conversationRepository.findByUser1(user);
    	conversationsResult.addAll(conversationRepository.findByUser2(user));
    	return conversationsResult;    	
    }
    
    public Conversation getConversationOfUsers(User user1, User user2) {
    	Conversation conversationResult = conversationRepository.findByUser1AndUser2(user1, user2);
    	if(Objects.isNull(conversationResult)) {
    		conversationResult = conversationRepository.findByUser1AndUser2(user2, user1);
    	} 
    	return conversationResult;
    }
    
    public String addConversation(User user1, User user2) {    	
    	if(!Objects.isNull(user1.getConversationWith(user2))) {
    		return "Conversation already exist";
    	}
    	Conversation conversation = new Conversation(user1, user2);
    	conversationRepository.save(conversation);
    	user1.addConversation1(conversation);
		userRepository.save(user1);
		user2.addConversation2(conversation);
		userRepository.save(user2);
		return "Success";
    }
    
    public void updateConversation(long id, Conversation conversation) {
    	if(getConversation(id) != null && conversation.getId() == id) {
			conversationRepository.save(conversation);
		}
    }
    
    public void updateMessage(long id, Message message) {
    	if(getMessage(id) != null && message.getId() == id) {
			messageRepository.save(message);
		}
    }
    
    public void addMessageToConversation(Message message, Conversation conversation) {
    	messageRepository.save(message);
    	conversation.addMessage(message);
    	messageRepository.save(message);
    	updateConversation(conversation.getId(), conversation);
    }
    
    public List<Message> getUserValidationsOfConcernedUser(User concernedUser, ValidationType validationType) {
    	return messageRepository.findByConcernedUserIdAndValidationType(concernedUser.getId(), validationType);
    }
    
    public Message getMessage(Long msgId) {
    	return messageRepository.findById(msgId).orElse(null);
    }
    
    public void disableValidation(Message msg) {
    	msg.disableValidation();
    	updateMessage(msg.getId(), msg);
    }
 
}

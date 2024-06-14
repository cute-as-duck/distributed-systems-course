package chat.service;

import chat.config.BeanFactory;
import chat.dto.SendingFile;
import chat.dto.ChatMessage;
import chat.frame.ChatFrame;
import chat.dto.Person;
import chat.web.model.Message;

public class MessageRouterService {
    private static final String MESSAGE_KEY = "Message";
    private static final String REMOVE_PERSON = "Remove Person";
    private static final String ADD_PERSON = "Add Person";
    private static final String CHAT_MESSAGE = "Chat Message";
    private static final String FILE = "File";

    private ChatFrame chatFrame;

    public void process(Message message) {
        chatFrame = BeanFactory.chatFrame();

        String messageKey = message.headers().get(MESSAGE_KEY);
        switch (messageKey) {
            case ADD_PERSON -> chatFrame.onAddPersonAction(Person.parseFromJson(message.body()));
            case REMOVE_PERSON -> chatFrame.onRemovePersonAction(message.body());
            case CHAT_MESSAGE -> chatFrame.onReceiveMessageAction(ChatMessage.fromJson(message.body()));
            case FILE -> chatFrame.onReceiveFileAction(SendingFile.fromJson(message.body()));
        }
    }
}

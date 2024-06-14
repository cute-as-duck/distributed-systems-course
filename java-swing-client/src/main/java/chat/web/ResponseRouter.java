package chat.web;

import chat.config.BeanFactory;
import chat.service.CallbackService;
import chat.service.EncryptionService;
import chat.filter.DecryptingFilter;
import chat.service.MessageRouterService;
import chat.web.model.Message;

public class ResponseRouter {

    private static final String REQUEST_ID_KEY = "Request_id";
    private static final String MESSAGE_KEY = "Message";

    private final CallbackService callbackService = BeanFactory.callbackService();
    private final MessageRouterService messageRouterService = BeanFactory.messageRouterService();

    public void process(String data) {

         Message message = Message.parseMessage(new DecryptingFilter().process(data));

        if (message.headers().containsKey(REQUEST_ID_KEY)) {
            callbackService.handleResponse(message);
        } else if (message.headers().containsKey(MESSAGE_KEY)) {
            messageRouterService.process(message);
        }
    }
}

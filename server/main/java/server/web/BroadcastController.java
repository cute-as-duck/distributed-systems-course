package server.web;

import server.dto.Person;
import server.model.Message;
import server.service.PersonService;
import server.service.PersonServiceImpl;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class BroadcastController {

    private static final String MESSAGE_KEY = "Message";
    private static final String REMOVE_PERSON = "Remove Person";
    private static final String ADD_PERSON = "Add Person";

    private final PersonService personService = new PersonServiceImpl();

    private ChatServer chatServer;

    public void chatServer(ChatServer chatServer) {
        this.chatServer = chatServer;
    }

    public void onAddPerson(Person person) {
        Message message = new Message().addHeader(MESSAGE_KEY, ADD_PERSON).addBody(person.toJson());
        personService.getOnlinePersons().stream()
                .filter(p -> !p.username().equals(person.username()))
                .forEach(p -> chatServer.sendMessage(
                        getInetAddressByName(p.host()), p.port(), message.toString())
                );
    }

    public void onRemovePerson(String username) {
        Message message = new Message().addHeader(MESSAGE_KEY, REMOVE_PERSON).addBody(username);
        personService.getOnlinePersons().stream()
                .forEach(p -> chatServer.sendMessage(
                        getInetAddressByName(p.host()), p.port(), message.toString())
                );
    }

    private InetAddress getInetAddressByName(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}

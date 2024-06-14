package server.handler;

import server.config.AppProps;
import server.model.PersonDetails;
import server.dto.AuthRequest;
import server.dto.Person;
import server.dto.PersonListResponse;
import server.exception.NotAuthenticatedException;
import server.exception.PersonAlreadyExistsException;
import server.exception.PersonNotFoundException;
import server.service.PersonServiceImpl;
import server.web.BroadcastController;
import server.model.Message;
import server.service.PersonService;

import java.util.List;

public class MessageHandler {

    private static final String ENCRYPTION_KEY = "Encryption Key";
    private static final String ENCRYPTION_KEY_PROP = "encryption.key";
    private static final String RESPONSE_KEY = "Response";
    private static final String OK = "Ok";
    private static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    private static final String NOT_AUTHENTICATED = "Not authenticated";
    private static final String NOT_FOUND = "Not found";

    private final PersonService personService = new PersonServiceImpl();
    private final BroadcastController broadcastController = new BroadcastController();

    public Message encryptionKey() {
        String key = AppProps.getProp(ENCRYPTION_KEY_PROP);
        return new Message().addHeader(ENCRYPTION_KEY, key);
    }

    public Message addPerson(AuthRequest authRequest) {
        Message message = new Message();
        PersonDetails personDetails;
        try{
            personDetails = personService.addPerson(PersonDetails.fromAuthRequest(authRequest));
        } catch (PersonAlreadyExistsException e) {
            return message.addHeader(RESPONSE_KEY, USERNAME_ALREADY_EXISTS);
        }
        broadcastController.onAddPerson(Person.fromPersonDetails(personDetails));
        return message.addHeader(RESPONSE_KEY, OK);
    }

    public Message getOnlinePersons() {
        List<Person> persons = personService.getOnlinePersons();
        return new Message()
                .addHeader(RESPONSE_KEY, OK)
                .addBody(new PersonListResponse(persons).toJson());
    }

    public Message authenticate(AuthRequest authRequest) {
        Message message = new Message();
        PersonDetails personDetails;
        try {
            personDetails = personService.authenticate(PersonDetails.fromAuthRequest(authRequest));
        } catch (PersonNotFoundException e) {
            return message.addHeader(RESPONSE_KEY, NOT_FOUND);
        } catch (NotAuthenticatedException e) {
            return message.addHeader(RESPONSE_KEY, NOT_AUTHENTICATED);
        }
        broadcastController.onAddPerson(Person.fromPersonDetails(personDetails));
        return message.addHeader(RESPONSE_KEY, OK);
    }

    public Message exitChat(String username) {
        personService.exitChat(username);
        broadcastController.onRemovePerson(username);
        return new Message().addHeader(RESPONSE_KEY, OK);
    }
}

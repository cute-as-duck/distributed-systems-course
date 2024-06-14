package server.service;

import server.model.PersonDetails;
import server.dto.Person;

import java.util.List;

public interface PersonService {
    PersonDetails addPerson(PersonDetails personDetails);
    PersonDetails authenticate(PersonDetails personDetails);
    List<Person> getOnlinePersons();
    void exitChat(String name);
}

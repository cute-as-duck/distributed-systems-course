package server.repository;

import server.dto.Person;
import server.model.PersonDetails;

import java.util.List;
import java.util.Optional;

public interface PersonRepository {
    PersonDetails addPerson(PersonDetails personDetails);
    List<Person> getOnlinePersons();
    Optional<PersonDetails> findByName(String username);
    PersonDetails updatePerson(PersonDetails personDetails);
    void setOfflineStatus(String username);
}

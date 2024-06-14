package server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.dto.Person;
import server.exception.NotAuthenticatedException;
import server.exception.PersonAlreadyExistsException;
import server.exception.PersonNotFoundException;
import server.model.PersonDetails;
import server.repository.PersonRepository;
import server.repository.impl.PersonRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository = new PersonRepositoryImpl();

    @Override
    public PersonDetails addPerson(PersonDetails personDetails) {
        Optional<PersonDetails> optionalPerson = personRepository.findByName(personDetails.username());
        if (optionalPerson.isPresent()) {
            throw new PersonAlreadyExistsException();
        }
        String encryptedPassword = PasswordEncoder.generateSecurePassword(personDetails.password());
        personDetails.setPassword(encryptedPassword);
        return personRepository.addPerson(personDetails);
    }

    @Override
    public PersonDetails authenticate(PersonDetails personDetails) {
        Optional<PersonDetails> optionalPerson = personRepository.findByName(personDetails.username());
        if (optionalPerson.isEmpty()) {
            throw new PersonNotFoundException();
        } else if (!PasswordEncoder.verifyUserPassword(
                personDetails.password(), optionalPerson.get().password())) {
            throw new NotAuthenticatedException();
        }
        return personRepository.updatePerson(personDetails);
    }

    @Override
    public List<Person> getOnlinePersons() {
        return personRepository.getOnlinePersons();
    }

    @Override
    public void exitChat(String name) {
        personRepository.setOfflineStatus(name);
    }
}

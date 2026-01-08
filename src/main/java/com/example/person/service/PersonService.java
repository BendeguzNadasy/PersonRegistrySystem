package com.example.person.service;

import com.example.person.entity.Person;
import com.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    /**
     * Save person after checking maximum number of addresses
     * @param person
     */
    public Person savePerson(Person person) {
        if (person.getAddresses() != null && person.getAddresses().size() > 2) {
            throw new IllegalArgumentException("Egy személynek maximum 2 címe lehet!");
        }

        if (person.getAddresses() != null) {
            person.getAddresses().forEach(address -> {
                address.setPerson(person);
                if (address.getContacts() != null) {
                    address.getContacts().forEach(contact -> contact.setAddress(address));
                }
            });
        }

        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }
}

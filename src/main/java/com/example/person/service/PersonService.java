package com.example.person.service;

import com.example.person.dto.PersonDto;
import com.example.person.entity.Address;
import com.example.person.entity.Contact;
import com.example.person.entity.Person;
import com.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param personDto
     */
    public Person savePerson(PersonDto personDto) {
        Person person = toEntity(personDto);
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

    public Person toEntity(PersonDto dto) {
        Person person = new Person();
        person.setName(dto.getName());

        if (dto.getAddresses() != null) {
            List<Address> addresses = dto.getAddresses().stream().map(addrDto -> {
                Address address = new Address();
                address.setType(addrDto.getType());
                address.setPerson(person);

                if (addrDto.getContacts() != null) {
                    List<Contact> contacts = addrDto.getContacts().stream().map(contDto -> {
                        Contact contact = new Contact();
                        contact.setValue(contDto.getValue());
                        contact.setType(contDto.getType());
                        contact.setAddress(address);
                        return contact;
                    }).collect(Collectors.toList());
                    address.setContacts(contacts);
                }
                return address;
            }).collect(Collectors.toList());
            person.setAddresses(addresses);
        }
        return person;
    }
}

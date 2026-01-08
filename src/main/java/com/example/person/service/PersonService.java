package com.example.person.service;

import com.example.person.dto.AddressDto;
import com.example.person.dto.ContactDto;
import com.example.person.dto.PersonDto;
import com.example.person.entity.Address;
import com.example.person.entity.Contact;
import com.example.person.entity.Person;
import com.example.person.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PersonDto getPersonById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A személy nem található ezzel az ID-val: " + id));

        return toDto(person);
    }

    /**
     * Save person after checking maximum number of addresses
     *
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

    @Transactional
    public Person updatePerson(Long id, PersonDto personDto) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("A személy nem található: " + id));

        existingPerson.setName(personDto.getName());

        if (personDto.getAddresses() != null && personDto.getAddresses().size() > 2) {
            throw new IllegalArgumentException("Frissítéskor is maximum 2 címe lehet!");
        }

        existingPerson.getAddresses().clear();
        personRepository.saveAndFlush(existingPerson);

        Person tempPerson = toEntity(personDto);

        if (tempPerson.getAddresses() != null) {
            for (Address newAddress : tempPerson.getAddresses()) {
                newAddress.setPerson(existingPerson);
                existingPerson.getAddresses().add(newAddress);
            }
        }

        return personRepository.save(existingPerson);
    }

    private Person toEntity(PersonDto dto) {
        Person person = new Person();
        person.setName(dto.getName());

        if (dto.getAddresses() != null) {
            List<Address> addresses = dto.getAddresses().stream().map(addressDto -> {
                Address address = new Address();
                address.setType(addressDto.getType());
                address.setZipCode(addressDto.getZipCode());
                address.setCity(addressDto.getCity());
                address.setStreet(addressDto.getStreet());
                address.setHouseNumber(addressDto.getHouseNumber());
                address.setPerson(person);

                if (addressDto.getContacts() != null) {
                    List<Contact> contacts = addressDto.getContacts().stream().map(contDto -> {
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

    private PersonDto toDto(Person person) {
        PersonDto dto = new PersonDto();
        dto.setName(person.getName());

        if (person.getAddresses() != null) {
            List<AddressDto> addressDtos = person.getAddresses().stream().map(address -> {
                AddressDto addrDto = new AddressDto();
                addrDto.setZipCode(address.getZipCode());
                addrDto.setCity(address.getCity());
                addrDto.setStreet(address.getStreet());
                addrDto.setHouseNumber(address.getHouseNumber());
                addrDto.setType(address.getType());

                if (address.getContacts() != null) {
                    List<ContactDto> contactDtos = address.getContacts().stream().map(contact -> {
                        ContactDto contactDto = new ContactDto();
                        contactDto.setValue(contact.getValue());
                        contactDto.setType(contact.getType());
                        return contactDto;
                    }).collect(Collectors.toList());
                    addrDto.setContacts(contactDtos);
                }
                return addrDto;
            }).collect(Collectors.toList());
            dto.setAddresses(addressDtos);
        }
        return dto;
    }
}

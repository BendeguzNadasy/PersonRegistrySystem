package com.example.person.unittest.service;

import com.example.person.dto.AddressDto;
import com.example.person.dto.ContactDto;
import com.example.person.dto.PersonDto;
import com.example.person.entity.AddressType;
import com.example.person.entity.ContactType;
import com.example.person.entity.Person;
import com.example.person.repository.PersonRepository;
import com.example.person.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    void savePerson_ShouldSave_WhenValidData() {
        PersonDto inputDto = createPersonDto("Teszt Elek", 1);

        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Person result = personService.savePerson(inputDto);

        assertNotNull(result);
        assertEquals("Teszt Elek", result.getName());
        assertEquals(1L, result.getId());

        assertEquals(1, result.getAddresses().size());
        assertEquals("Tesztelek Város", result.getAddresses().get(0).getCity());

        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void savePerson_ShouldThrowException_WhenTooManyAddresses() {
        PersonDto inputDto = createPersonDto("Túlsokcím Tamás", 3);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> personService.savePerson(inputDto)
        );

        assertEquals("Egy személynek maximum 2 címe lehet!", exception.getMessage());

        verify(personRepository, never()).save(any());
    }


    @Test
    void getPersonById_ShouldReturnDto_WhenExists() {
        Person existingPerson = new Person();
        existingPerson.setId(10L);
        existingPerson.setName("Létező Lajos");
        existingPerson.setAddresses(new ArrayList<>());

        when(personRepository.findById(10L)).thenReturn(Optional.of(existingPerson));

        PersonDto resultDto = personService.getPersonById(10L);

        assertNotNull(resultDto);
        assertEquals("Létező Lajos", resultDto.getName());

        verify(personRepository).findById(10L);
    }

    @Test
    void getPersonById_ShouldThrowException_WhenNotFound() {
        when(personRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, this::execute);

        assertTrue(ex.getMessage().contains("nem található"));
    }

    @Test
    void updatePerson_ShouldUpdateAndFlush_WhenValid() {
        Long id = 1L;
        Person existingPerson = new Person();
        existingPerson.setId(id);
        existingPerson.setName("Régi Név");
        existingPerson.setAddresses(new ArrayList<>());

        PersonDto updateDto = createPersonDto("Új Név", 1);

        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));
        when(personRepository.save(any(Person.class))).thenReturn(existingPerson);

        Person updatedResult = personService.updatePerson(id, updateDto);

        assertEquals("Új Név", updatedResult.getName());
        assertEquals(1, updatedResult.getAddresses().size());

        verify(personRepository).saveAndFlush(existingPerson);
        verify(personRepository).save(existingPerson);
    }

    @Test
    void updatePerson_ShouldThrowException_WhenTooManyAddresses() {
        Long id = 1L;
        Person existingPerson = new Person();
        existingPerson.setId(id);

        PersonDto updateDto = createPersonDto("Sok Címes", 3);

        when(personRepository.findById(id)).thenReturn(Optional.of(existingPerson));

        assertThrows(IllegalArgumentException.class, () -> personService.updatePerson(id, updateDto));

        verify(personRepository, never()).saveAndFlush(any());
        verify(personRepository, never()).save(any());
    }


    @Test
    void deletePerson_ShouldCallRepository() {
        personService.deletePerson(5L);

        verify(personRepository, times(1)).deleteById(5L);
    }

    private PersonDto createPersonDto(String name, int addressCount) {
        PersonDto dto = new PersonDto();
        dto.setName(name);
        List<AddressDto> addresses = new ArrayList<>();

        for (int i = 0; i < addressCount; i++) {
            AddressDto address = new AddressDto();
            address.setCity("Tesztelek Város");
            address.setStreet("Teszt utca " + i);
            address.setType(AddressType.PERMANENT);

            ContactDto contact = new ContactDto();
            contact.setValue("email" + i + "@test.com");
            contact.setType(ContactType.EMAIL);
            address.setContacts(List.of(contact));

            addresses.add(address);
        }
        dto.setAddresses(addresses);
        return dto;
    }

    private void execute() {
        personService.getPersonById(99L);
    }
}
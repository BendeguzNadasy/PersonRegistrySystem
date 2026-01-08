package com.example.person.integrationtest;

import com.example.person.dto.AddressDto;
import com.example.person.dto.PersonDto;
import com.example.person.entity.AddressType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PersonControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPerson_Successful() throws Exception {
        PersonDto personDto = new PersonDto();
        personDto.setName("Elment Elemér");

        AddressDto address = new AddressDto();
        address.setZipCode("1234");
        address.setCity("Budapest");
        address.setStreet("István utca");
        address.setHouseNumber("5");
        address.setType(AddressType.PERMANENT);

        personDto.setAddresses(List.of(address));

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.name", is("Elment Elemér"))) // name matches
                .andExpect(jsonPath("$.addresses", hasSize(1))); // only one address
    }

    @Test
    void createPerson_TooManyAddresses_ShouldFail() throws Exception {
        PersonDto personDto = new PersonDto();
        personDto.setName("Sokcím Tamás");

        List<AddressDto> addresses = new ArrayList<>();
        addresses.add(createAddress("Pécs", AddressType.PERMANENT));
        addresses.add(createAddress("Győr", AddressType.TEMPORARY));
        addresses.add(createAddress("Sopron", AddressType.TEMPORARY));
        personDto.setAddresses(addresses);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDto)))
                .andExpect(status().isBadRequest()); // throw 400 bad request due too many address
    }

    @Test
    void updatePerson_Successful() throws Exception {
        Long existingId = 1L;

        PersonDto updateDto = new PersonDto();
        updateDto.setName("Módosított Elek");

        AddressDto newAddress = createAddress("Eger", AddressType.PERMANENT);
        updateDto.setAddresses(List.of(newAddress));

        mockMvc.perform(put("/api/persons/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Módosított Elek")))
                .andExpect(jsonPath("$.addresses[0].city", is("Eger")));
    }

    @Test
    void deletePerson_Successful() throws Exception {
        Long idToDelete = 2L; // Gipsz Jakab from data.sql

        mockMvc.perform(delete("/api/persons/{id}", idToDelete))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPersonById_Successful() throws Exception {
        mockMvc.perform(get("/api/persons/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists()) // has name
                .andExpect(jsonPath("$.addresses").isArray()); // has addresses
    }

    @Test
    void getPersonById_NotFound() throws Exception {
        mockMvc.perform(get("/api/persons/{id}", 9999L))
                .andExpect(status().isNotFound()); // should throw 404 if 9999 id not exists
    }

    private AddressDto createAddress(String city, AddressType type) {
        AddressDto address = new AddressDto();
        address.setCity(city);
        address.setType(type);
        return address;
    }
}
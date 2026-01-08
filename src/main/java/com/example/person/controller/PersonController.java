package com.example.person.controller;

import com.example.person.dto.PersonDto;
import com.example.person.entity.Person;
import com.example.person.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
@Tag(name = "Személyek Kezelése", description = "Új személyek felvitele, lekérdezése és törlése")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @Operation(summary = "Összes személy listázása", description = "Visszaadja az adatbázisban lévő összes személyt címekkel és elérhetőségekkel együtt.")
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Egy adott személy lekérdezése", description = "ID alapján visszaadja a személy részletes adatait.")
    public ResponseEntity<?> getPersonById(
            @Parameter(description = "A keresett személy ID-ja", example = "1")
            @PathVariable Long id) {

        try {
            PersonDto personDto = personService.getPersonById(id);

            return ResponseEntity.ok(personDto);

        } catch (RuntimeException e) {
            // Ha nincs ilyen ID, 404 Not Found-ot küldünk
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Új személy rögzítése", description = "Létrehoz egy új személyt. Figyelem: maximum 2 cím adható meg!")
    public ResponseEntity<?> createPerson(@RequestBody() PersonDto personDto) {
        try {
            Person savedPerson = personService.savePerson(personDto);
            return ResponseEntity.ok(savedPerson);
        } catch (IllegalArgumentException e) {
            // If the maximum number of addresses exceeded throw error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Személy törlése", description = "ID alapján töröl egy személyt, és a hozzá tartozó összes címet és elérhetőséget (Cascade).")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Személy módosítása", description = "Meglévő személy adatainak felülírása. A megadott címek felülírják a régieket!")
    public ResponseEntity<?> updatePerson(
            @Parameter(description = "A módosítandó személy id-ja", example = "1")
            @PathVariable Long id,

            @RequestBody PersonDto personDto) {

        try {
            Person updatedPerson = personService.updatePerson(id, personDto);
            return ResponseEntity.ok(updatedPerson);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

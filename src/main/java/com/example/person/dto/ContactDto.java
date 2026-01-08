package com.example.person.dto;

import com.example.person.entity.ContactType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ContactDto {

    @Schema(description = "Az elérhetőség értéke", example = "john.doe@email.com")
    private String value;

    @Schema(description = "Típus: EMAIL, PHONE vagy OTHER", example = "EMAIL")
    private ContactType type;
}
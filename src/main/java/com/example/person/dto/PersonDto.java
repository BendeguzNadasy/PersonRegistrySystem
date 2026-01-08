package com.example.person.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Személy létrehozására szolgáló séma")
public class PersonDto {

    @Schema(description = "A személy teljes neve", example = "John Doe")
    private String name;

    @Schema(description = "A személy címei (max 2)")
    private List<AddressDto> addresses;
}

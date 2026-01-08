package com.example.person.dto;

import com.example.person.entity.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class AddressDto {

    @Schema(description = "Cím típusa: PERMANENT vagy TEMPORARY", example = "PERMANENT")
    private AddressType type;

    @Schema(description = "Az ehhez a címhez tartozó elérhetőségek")
    private List<ContactDto> contacts;
}

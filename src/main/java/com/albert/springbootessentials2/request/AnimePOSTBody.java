package com.albert.springbootessentials2.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePOSTBody {
    @NotEmpty(message = "The name must not be empty")
    @Schema(description = "The name of the Anime", example = "Tensei Shitara Slime Datta Ken")
    private String name;
}

package com.albert.springbootessentials2.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePUTBody {
    @Positive(message = "The id must be positive")
    @NotNull(message = "The id must not be null")
    @Schema(description = "The id of the anime")
    private Long id;
    @NotEmpty(message = "The name must not be empty")
    @Schema(description = "The new name of the Anime", example = "Jujutsu Kaisen")
    private String name;
}

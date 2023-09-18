package ru.veqveq.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Schema(description = "Информация о пользователе")
public class UserInfoDto {
    @Schema(hidden = true)
    private String username;

    @Schema(hidden = true)
    private String email;

    @Schema(description = "Имя")
    private String firstName;

    @Schema(description = "Фамилия")
    private String lastName;

    @Schema(description = "Отчетсво")
    private String patronymic;

    @Schema(description = "Дата рождения")
    @Pattern(regexp = "dd-MM-yyyy")
    private LocalDate birthday;

}

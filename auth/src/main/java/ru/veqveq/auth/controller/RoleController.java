package ru.veqveq.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Контроллер управления ролями пользователей")
@RestController
@RequestMapping("/ho/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

}

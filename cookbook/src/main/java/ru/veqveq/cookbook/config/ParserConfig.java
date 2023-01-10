package ru.veqveq.cookbook.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.veqveq.cookbook.config.properties.ParserProperties;

@Configuration
@EnableConfigurationProperties(ParserProperties.class)
public class ParserConfig {
}

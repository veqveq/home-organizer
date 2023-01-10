package ru.veqveq.cookbook.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "parser")
public class ParserProperties {
    private String startUrl;
    private String registerFragment;
    private String paginationParam;
}

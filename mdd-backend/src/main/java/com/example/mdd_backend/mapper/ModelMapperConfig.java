package com.example.mdd_backend.mapper;

import com.example.mdd_backend.dtos.ArticleCreateRequestDTO;
import com.example.mdd_backend.models.DBArticle;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper
            .typeMap(ArticleCreateRequestDTO.class, DBArticle.class)
            .addMappings(mapping -> mapping.skip(DBArticle::setId));

        return modelMapper;
    }
}

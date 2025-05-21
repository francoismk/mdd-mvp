package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateThemeDTO;
import com.example.mdd_backend.dtos.GetThemeDTO;
import com.example.mdd_backend.models.DBTheme;
import com.example.mdd_backend.repositories.ThemeRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final ModelMapper modelMapper;

    public ThemeService(
        ThemeRepository themeRepository,
        ModelMapper modelMapper
    ) {
        this.themeRepository = themeRepository;
        this.modelMapper = modelMapper;
    }

    public GetThemeDTO createTheme(CreateThemeDTO themeDTO) {
        DBTheme theme = modelMapper.map(themeDTO, DBTheme.class);
        DBTheme savedTheme = themeRepository.save(theme);

        return modelMapper.map(savedTheme, GetThemeDTO.class);
    }

    public GetThemeDTO getThemeById(String themeId) {
        DBTheme theme = themeRepository
            .findById(themeId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Theme not found with ID : " + themeId
                )
            );

        return modelMapper.map(theme, GetThemeDTO.class);
    }

    public List<GetThemeDTO> getAllThemes() {
        List<DBTheme> themes = themeRepository.findAll();
        List<GetThemeDTO> themeDtos = themes
            .stream()
            .map(theme -> modelMapper.map(theme, GetThemeDTO.class))
            .collect(Collectors.toList());

        return themeDtos;
    }

    public void deleteTheme(String themeId) {
        themeRepository
            .findById(themeId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "Theme not found with ID : " + themeId
                )
            );

        themeRepository.deleteById(themeId);
    }
}

package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateThemeDTO;
import com.example.mdd_backend.dtos.GetThemeDTO;
import com.example.mdd_backend.services.ThemeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/themes")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping
    public ResponseEntity<GetThemeDTO> createTheme(
        @Valid @RequestBody CreateThemeDTO themeDTO
    ) {
        if (themeDTO == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        GetThemeDTO createdTheme = themeService.createTheme(themeDTO);
        return new ResponseEntity<>(createdTheme, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetThemeDTO> getThemeById(@PathVariable String id) {
        GetThemeDTO theme = themeService.getThemeById(id);

        if (theme == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(theme, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GetThemeDTO>> getAllThemes() {
        List<GetThemeDTO> themes = themeService.getAllThemes();

        if (themes == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(themes, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteThemeById(@PathVariable String id) {
        themeService.deleteTheme(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

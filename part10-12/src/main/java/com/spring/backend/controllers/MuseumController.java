package com.spring.backend.controllers;


import com.spring.backend.models.Artist;
import com.spring.backend.models.Museum;
import com.spring.backend.models.Painting;
import com.spring.backend.repositories.MuseumRepository;
import com.spring.backend.repositories.PaintingRepository;
import com.spring.backend.tools.DataValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class MuseumController {
    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    PaintingRepository paintingRepository;
    @GetMapping("/museums/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable(value = "id") Long museumId)
            throws DataValidationException {
        Museum museum = museumRepository.findById(museumId)
                .orElseThrow(()-> new DataValidationException("Museum с таким индексом не найдена"));
        return ResponseEntity.ok(museum);
    }

    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/museums/{id}/paintings")
    public ResponseEntity<List<Painting>> getMuseumPaintings(@PathVariable(value = "id") Long museumId) {
        Optional<Museum> museum = museumRepository.findById(museumId);
        if (museum.isPresent()) {
            return ResponseEntity.ok(museum.get().paintings);
        }
        return ResponseEntity.ok(new ArrayList<Painting>());
    }

    @PostMapping
    public ResponseEntity<Object> createMuseum(@RequestBody Museum requestMuseum) throws Exception{
        try {
            Museum museum = museumRepository.save(requestMuseum);
            return ResponseEntity.ok(museum);
        }catch (Exception e){
            String error;
            if (e.getMessage().contains("museum.name_UNIQUE"))
                error = "Museum already exists";
            else error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMuseum(@PathVariable("id") Long idMuseum, @RequestBody Museum museumDetails){
        Museum museum;
        Optional<Museum> museumOptional = museumRepository.findById(idMuseum);
        if (museumOptional.isPresent()){
            museum = museumOptional.get();
            museum.setName(museumDetails.getName());
            museum.setLocation(museumDetails.getLocation());
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Museum not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMuseum(@PathVariable("id") Long idMuseum){
        Optional<Museum> museumOptional = museumRepository.findById(idMuseum);
        Map<String, Boolean> resp = new HashMap<>();
        if (museumOptional.isPresent()) {
            museumRepository.delete(museumOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

}

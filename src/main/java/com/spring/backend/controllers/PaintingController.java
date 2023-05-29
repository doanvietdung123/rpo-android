package com.spring.backend.controllers;

import com.spring.backend.models.Artist;
import com.spring.backend.models.Painting;
import com.spring.backend.repositories.ArtistRepository;
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

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class PaintingController {
    @Autowired
    PaintingRepository paintingRepository;
    @Autowired
    ArtistRepository artistRepository;

    @GetMapping("/paintings")
    public Page getAllPaintings(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/paintings/{id}")
    public ResponseEntity getPainting(@PathVariable(value = "id") Long paintingId)
            throws DataValidationException
    {
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(()-> new DataValidationException("Картина с таким индексом не найден"));
        return ResponseEntity.ok(painting);
    }

    @PostMapping("/paintings")
    public ResponseEntity<Object> createPainting(@RequestBody Painting requestPainting) throws Exception {
        try {
            Optional<Artist> optionalArtist = artistRepository.findById(requestPainting.getArtist().getId());
            if (optionalArtist.isPresent()) {
                requestPainting.setArtist(optionalArtist.get());
            }
            Painting painting = paintingRepository.save(requestPainting);
            return ResponseEntity.ok(painting);
        } catch (Exception e){
            String error;
            if (e.getMessage().contains("painting.name_UNIQUE"))
                error = "Painting already exists";
            else
                error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }


    @PostMapping("/deletepaintings")
    public ResponseEntity deletePaintings(@Valid @RequestBody List paintings) {
        paintingRepository.deleteAll();
        return new ResponseEntity(HttpStatus.OK);
    }


    @PutMapping("/paintings/{id}")
    public ResponseEntity<Object> updatePainting(@PathVariable("id") Long idPainting, @RequestBody Painting paintingDetails){
        Painting painting;
        Optional<Painting> paintingOptional = paintingRepository.findById(idPainting);
        if (paintingOptional.isPresent()){
            painting = paintingOptional.get();
            painting.setName(paintingDetails.getName());
            painting.setArtist(paintingDetails.getArtist());
            painting.setMuseum(paintingDetails.getMuseum());
            painting.setYear(paintingDetails.getYear());
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Painting not found");
        }
    }

    @DeleteMapping("paintings/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable("id") Long idPainting){
        Optional<Painting> paintingOptional = paintingRepository.findById(idPainting);
        Map<String, Boolean> resp = new HashMap<>();
        if (paintingOptional.isPresent()) {
            paintingRepository.delete(paintingOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

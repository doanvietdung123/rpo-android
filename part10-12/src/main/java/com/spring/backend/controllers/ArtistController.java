package com.spring.backend.controllers;

import com.spring.backend.models.Artist;
import com.spring.backend.models.Country;
import com.spring.backend.repositories.ArtistRepository;
import com.spring.backend.repositories.CountryRepository;
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
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/artists")
    public Page getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException
    {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist requestArtist) throws Exception {
        try {
            Optional<Country> optionalCountry = countryRepository.findById(requestArtist.getCountry().getId());
            if (optionalCountry.isPresent()) {
                requestArtist.setCountry(optionalCountry.get());
            }
            Artist artist = artistRepository.save(requestArtist);
            return ResponseEntity.ok(artist);
        } catch (Exception e){
            String error;
            if (e.getMessage().contains("artist.name_UNIQUE"))
                error = "Artist already exists";
            else
                error = "Undefined error";
            Map<String, String> errorMap =  new HashMap<>();
            errorMap.put("error", error);
            return ResponseEntity.ok(errorMap);
        }
    }

    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Valid @RequestBody List artists) {
        artistRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Object> updateArtist(@PathVariable("id") Long idArtist, @RequestBody Artist artistDetails){
        Artist artist;
        Optional<Artist> artistOptional = artistRepository.findById(idArtist);
        if (artistOptional.isPresent()){
            artist = artistOptional.get();
            artist.setCentury(artistDetails.getCentury());
            artist.setCountry(artistDetails.getCountry());
            artist.setName(artistDetails.getName());
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Artist not found");
        }
    }

    @DeleteMapping("artists/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable("id") Long idArtist){
        Optional<Artist> artistOptional = artistRepository.findById(idArtist);
        Map<String, Boolean> resp = new HashMap<>();
        if (artistOptional.isPresent()) {
            artistRepository.delete(artistOptional.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }
}

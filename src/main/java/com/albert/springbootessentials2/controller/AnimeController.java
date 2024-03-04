package com.albert.springbootessentials2.controller;

import com.albert.springbootessentials2.domain.Anime;
import com.albert.springbootessentials2.request.EntityID;
import com.albert.springbootessentials2.request.AnimePOSTBody;
import com.albert.springbootessentials2.request.AnimePUTBody;
import com.albert.springbootessentials2.service.AnimeService;
import com.albert.springbootessentials2.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/animes")
public class AnimeController {
    private final AnimeService animeService;
    private final DateUtil dateUtil;

    @Autowired
    public AnimeController(AnimeService animeService, DateUtil dateUtil) {
        this.animeService = animeService;
        this.dateUtil = dateUtil;
    }

    @GetMapping
    @Operation(summary = "Returns a list of all Anime in a pageable way.",
            description = "The default page and size are 0 and 20, respectively.",
            responses = {@ApiResponse(responseCode = "200")},
            tags = {"Listing"})
    public ResponseEntity<Page<Anime>> listAll(@ParameterObject Pageable pageable) {
//        log.info(dateUtil.formatLocalDateTimeToDBPattern(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping(path = "/all")
    @Operation(summary = "Returns a list of all Anime in database.",
            responses = {@ApiResponse(responseCode = "200")},
            tags = {"Listing"})
    public ResponseEntity<List<Anime>> listAllNonPageable() {
        return ResponseEntity.ok(animeService.listAllNonPageable());
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Searches an Anime by it's ID",
            description = "Returns 404 if Anime does not exist in database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "When successful"),
                    @ApiResponse(responseCode = "404", description = "When Anime does not exist in database")
            },
            tags = {"Searching"})
    public ResponseEntity<Anime> findById(@PathVariable long id) {
        return ResponseEntity.ok(animeService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "by-id-user-details/{id}")
    @Operation(summary = "Searches an Anime by it's ID",
            description = "Logs out details about the logged user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "When successful"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials"),
                    @ApiResponse(responseCode = "404", description = "When Anime does not exist in database")
            },
            tags = {"Searching"})
    public ResponseEntity<Anime> findById(@PathVariable long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        log.info(userDetails);
        return ResponseEntity.ok(animeService.findById(id));
    }

    // localhost:8080/animes/find?name=...
    @GetMapping(path = "/find")
    @Operation(summary = "Returns list of Anime matching the provided name",
            description = "Returns an empty list if not match is found",
            responses = {
                    @ApiResponse(responseCode = "200", description = "When successful"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials"),
                    @ApiResponse(responseCode = "404", description = "When Anime does not exist in database")
            },
            tags = {"Listing"})
    public ResponseEntity<List<Anime>> findAllByName(@RequestParam(required = true) String name) {
//        if (name.length() < 2)
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must have 2 or more characters");

        return ResponseEntity.ok(animeService.findAllByName(name));
    }

    @PostMapping("/admin")
    @Operation(summary = "Saves a new Anime in the database",
            description = "Returns the saved Anime",
            responses = {
                    @ApiResponse(responseCode = "201", description = "When successful"),
                    @ApiResponse(responseCode = "400", description = "When a constraint fails"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials")
            },
            tags = {"Saving"})
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePOSTBody animePOSTBody) {
        return new ResponseEntity<>(animeService.save(animePOSTBody), HttpStatus.CREATED);
    }

    @PostMapping("/admin/save-many")
    @Operation(summary = "Saves a list of new Anime in the database",
            description = "Returns a list of saved Anime",
            responses = {
                    @ApiResponse(responseCode = "201", description = "When successful"),
                    @ApiResponse(responseCode = "400", description = "When a constraint fails"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials")
            },
            tags = {"Saving"})
    public ResponseEntity<List<Anime>> saveMany(@RequestBody @Valid List<AnimePOSTBody> animeList) {
        return new ResponseEntity<>(animeService.saveMany(animeList), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Deletes an Anime by it's ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "When successful"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials"),
                    @ApiResponse(responseCode = "404", description = "When Anime does not exist in database")
            },
            tags = {"Deleting"})
    public ResponseEntity<Void> remove(@PathVariable long id) {
        animeService.remove(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/admin/delete-many")
    @Operation(summary = "Deletes list of Anime matching it's IDs",
            responses = {
                    @ApiResponse(responseCode = "204", description = "When successful"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials")
            },
            tags = {"Deleting"})
    public ResponseEntity<Void> removeMany(@RequestBody List<EntityID> entityIDS) {
        animeService.remove(entityIDS);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(path = "/admin")
    @Operation(summary = "Replaces the name of an Anime",
            description = "Uses the ID of the Anime to replace the name",
            responses = {
                    @ApiResponse(responseCode = "204", description = "When successful"),
                    @ApiResponse(responseCode = "400", description = "When a constraint fails"),
                    @ApiResponse(responseCode = "401", description = "When User is not logged in"),
                    @ApiResponse(responseCode = "403", description = "When User is does not have the correct credentials"),
                    @ApiResponse(responseCode = "404", description = "When Anime does not exist in database")
            },
            tags = {"Replacing"})
    public ResponseEntity<Void> replace(@RequestBody @Valid AnimePUTBody animePUTBody) {
        animeService.replace(animePUTBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

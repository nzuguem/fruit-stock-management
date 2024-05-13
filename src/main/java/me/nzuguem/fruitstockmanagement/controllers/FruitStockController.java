package me.nzuguem.fruitstockmanagement.controllers;

import jakarta.validation.Valid;
import me.nzuguem.fruitstockmanagement.exceptions.FruitQuantityNotFoundException;
import me.nzuguem.fruitstockmanagement.models.FruitQuantity;
import me.nzuguem.fruitstockmanagement.services.FruitStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;

@RestController
@RequestMapping("fruits")
public class FruitStockController {

    private final FruitStockService fruitStockService;

    public FruitStockController(FruitStockService fruitStockService) {
        this.fruitStockService = fruitStockService;
    }

    @PostMapping
    public ResponseEntity<FruitQuantity> create(@Valid @RequestBody FruitQuantity fruitQuantity) {

        fruitQuantity = fruitStockService.provide(fruitQuantity);
    
        return ResponseEntity.created(URI.create(STR."/fruits/\{fruitQuantity.code()}"))
            .body(fruitQuantity);
	}

    @GetMapping("{code}")
    public ResponseEntity<FruitQuantity> verify(@PathVariable String code) {

        return ResponseEntity.ok(fruitStockService.verify(code));
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, WebRequest request) {
        
        return switch (ex) {
            case FruitQuantityNotFoundException _ -> ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
            default -> ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
        };

    }
}

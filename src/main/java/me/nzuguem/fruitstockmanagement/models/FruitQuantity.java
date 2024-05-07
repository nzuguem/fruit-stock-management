package me.nzuguem.fruitstockmanagement.models;

import jakarta.validation.constraints.Min;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("stock")
public record FruitQuantity(
        @Id Integer id,
        String code,
        String name,

        @Min(value = 0)
        Integer quantity
) {

    public FruitQuantity {

        if (Objects.isNull(quantity) || quantity < 0) {
            quantity = 0;
        }
    }

    public FruitQuantityEvent toEvent() {
        return new FruitQuantityEvent(System.currentTimeMillis(),this);
    }

    public FruitQuantity subtract(FruitQuantity fruitQuantity) {
        return new FruitQuantity(id, code, name, quantity - fruitQuantity.quantity);
    }

    public FruitQuantity add(FruitQuantity fruitQuantity) {
        return new FruitQuantity(id, code, name, quantity + fruitQuantity.quantity);
    }

    public boolean outOfStock() {
        return quantity == 0;
    }

    public FruitQuantity withCode(String code) {
        return new FruitQuantity(id, code, name, quantity);
    }
}

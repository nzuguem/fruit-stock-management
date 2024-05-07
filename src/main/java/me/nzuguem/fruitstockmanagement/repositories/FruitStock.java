package me.nzuguem.fruitstockmanagement.repositories;

import me.nzuguem.fruitstockmanagement.models.FruitQuantity;
import org.springframework.data.repository.ListCrudRepository;

public interface FruitStock extends ListCrudRepository<FruitQuantity, Integer> {

    FruitQuantity findByCode(String code);
}

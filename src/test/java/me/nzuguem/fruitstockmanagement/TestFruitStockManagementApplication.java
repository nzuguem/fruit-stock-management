package me.nzuguem.fruitstockmanagement;

import me.nzuguem.fruitstockmanagement.configurations.ContainersConfiguration;
import org.springframework.boot.SpringApplication;


public class TestFruitStockManagementApplication {

	public static void main(String[] args) {
		SpringApplication.from(FruitStockManagementApplication::main)
				.with(ContainersConfiguration.class)
				.run(args);
	}

}

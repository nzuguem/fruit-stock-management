package me.nzuguem.fruitstockmanagement;

import io.github.microcks.testcontainers.MicrocksContainersEnsemble;
import me.nzuguem.fruitstockmanagement.configurations.ContainersConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.testcontainers.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ContainersConfiguration.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected MicrocksContainersEnsemble microcksEnsemble;

    @Autowired
    protected ObjectMapper mapper;

    @LocalServerPort
    protected Integer port;

    @BeforeEach
    void setupPort() {
        // Host port exposition should be done here.
        // https://java.testcontainers.org/features/networking/#exposing-host-ports-to-the-container
        Testcontainers.exposeHostPorts(port);
    }
}

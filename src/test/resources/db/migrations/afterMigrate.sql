-- Flyway CallBack SQL - https://documentation.red-gate.com/flyway/flyway-cli-and-api/concepts/callback-concept
-- Make sure that these objects are persisted in the BDD so that the Kafka Consumer (By Microcks) does not fail.
INSERT INTO stock (code, name, quantity) VALUES ('KW', 'Kiwi', 250);
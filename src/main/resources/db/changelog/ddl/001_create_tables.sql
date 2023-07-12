-- liquibase formatted sql
-- changeset sasha:1
CREATE TABLE user_entity
(
    user_id           BIGINT NOT NULL,
    registration_date date,
    CONSTRAINT pk_userentity PRIMARY KEY (user_id)
);
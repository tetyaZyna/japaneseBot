-- liquibase formatted sql
-- changeset sasha:1
CREATE TABLE IF NOT EXISTS "user"
(
    user_id           BIGINT NOT NULL,
    registration_date date,
    CONSTRAINT pk_user PRIMARY KEY (user_id)
);

--changeset sasha:2
CREATE TABLE IF NOT EXISTS hiragana
(
    hieroglyph_id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    hieroglyph          VARCHAR(255),
    hieroglyph_pronouns VARCHAR(255),
    CONSTRAINT pk_hiragana PRIMARY KEY (hieroglyph_id)
);

CREATE TABLE IF NOT EXISTS katakana
(
    hieroglyph_id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    hieroglyph          VARCHAR(255),
    hieroglyph_pronouns VARCHAR(255),
    CONSTRAINT pk_katakana PRIMARY KEY (hieroglyph_id)
);

--changeset sasha:3
ALTER TABLE "user" ADD COLUMN IF NOT EXISTS mode VARCHAR(255) DEFAULT 'TEXT_MODE' NULL;

--changeset sasha:4
CREATE TABLE IF NOT EXISTS task
(
    task_id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    question_number INTEGER NOT NULL,
    question_count  INTEGER NOT NULL,
    user_id         BIGINT,
    CONSTRAINT pk_task PRIMARY KEY (task_id)
);

CREATE TABLE IF NOT EXISTS task_letters
(
    task_id      BIGINT       NOT NULL,
    letter_value VARCHAR(255),
    letter_key   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_task_letters PRIMARY KEY (task_id, letter_key)
);

CREATE TABLE IF NOT EXISTS user_answers
(
    task_id           BIGINT NOT NULL,
    is_answer_correct BOOLEAN
);

ALTER TABLE task
    ADD CONSTRAINT fk_task_on_user FOREIGN KEY (user_id) REFERENCES "user" (user_id);

ALTER TABLE task_letters
    ADD CONSTRAINT fk_task_letters_on_task FOREIGN KEY (task_id) REFERENCES task (task_id);

ALTER TABLE user_answers
    ADD CONSTRAINT fk_user_answers_on_task FOREIGN KEY (task_id) REFERENCES task (task_id);

--changeset sasha:5
CREATE TABLE task_settings
(
    settings_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    settings_name  VARCHAR(255),
    question_count INTEGER,
    letter_group   VARCHAR(255),
    use_letters    BOOLEAN,
    use_pronouns   BOOLEAN,
    CONSTRAINT pk_task_settings PRIMARY KEY (settings_id)
);

ALTER TABLE task ADD COLUMN IF NOT EXISTS settings_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_SETTINGS FOREIGN KEY (settings_id) REFERENCES task_settings (settings_id);

--changeset sasha:6
DROP TABLE IF EXISTS task_letters CASCADE;

CREATE TABLE task_letters
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    task_id      BIGINT,
    letter_key   VARCHAR(255),
    letter_value VARCHAR(255),
    CONSTRAINT pk_task_letters PRIMARY KEY (id)
);

ALTER TABLE task_letters
    ADD CONSTRAINT FK_TASK_LETTERS_ON_TASK FOREIGN KEY (task_id) REFERENCES task (task_id);

--changeset sasha:7
CREATE TABLE alphabets
(
    letter_id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    letter          VARCHAR(255),
    letter_pronouns VARCHAR(255),
    alphabet        VARCHAR(255),
    letter_group    VARCHAR(255),
    CONSTRAINT pk_alphabets PRIMARY KEY (letter_id)
);

--changeset sasha:8
ALTER TABLE task_letters ADD COLUMN IF NOT EXISTS letter_alphabet VARCHAR(255) NULL DEFAULT 'DEFAULT';

--changeset sasha:9
ALTER TABLE task_settings ADD COLUMN IF NOT EXISTS alphabet VARCHAR(255) NOT NULL DEFAULT 'ALL';

--changeset sasha:10
ALTER TABLE task_letters ADD COLUMN IF NOT EXISTS key_type VARCHAR(255) NULL DEFAULT 'PRONOUNS';
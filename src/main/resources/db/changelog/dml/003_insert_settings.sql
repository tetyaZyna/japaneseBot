-- liquibase formatted sql
-- changeset sasha:1
INSERT INTO TASK_SETTINGS (SETTINGS_NAME, QUESTION_COUNT, LETTER_GROUP, USE_LETTERS, USE_PRONOUNS)
VALUES
    ('Hiragana letter only', 10, 'HIRAGANA', true, false),
    ('Hiragana pronouns only', 10, 'HIRAGANA', false, true),
    ('Katakana letter only', 10, 'KATAKANA', true, false),
    ('Katakana pronouns only', 10, 'KATAKANA', false, true);

-- changeset sasha:2
INSERT INTO TASK_SETTINGS (SETTINGS_NAME, QUESTION_COUNT, ALPHABET, LETTER_GROUP, USE_LETTERS, USE_PRONOUNS)
VALUES
    ('Hiragana mix', 10, 'HIRAGANA', 'ALL', true, true),
    ('Katakana mix', 10, 'KATAKANA', 'ALL', true, true);

UPDATE TASK_SETTINGS
SET ALPHABET = 'HIRAGANA', LETTER_GROUP = 'ALL'
WHERE SETTINGS_NAME = 'Hiragana letter only' OR SETTINGS_NAME = 'Hiragana pronouns only';

UPDATE TASK_SETTINGS
SET ALPHABET = 'KATAKANA', LETTER_GROUP = 'ALL'
WHERE SETTINGS_NAME = 'Katakana letter only' OR SETTINGS_NAME = 'Katakana pronouns only';

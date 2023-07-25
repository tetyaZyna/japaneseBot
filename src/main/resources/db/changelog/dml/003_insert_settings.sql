-- liquibase formatted sql
-- changeset sasha:1
INSERT INTO TASK_SETTINGS (SETTINGS_NAME, QUESTION_COUNT, LETTER_GROUP, USE_LETTERS, USE_PRONOUNS)
VALUES
    ('Hiragana letter only', 10, 'HIRAGANA', true, false),
    ('Hiragana pronouns only', 10, 'HIRAGANA', false, true),
    //('Hiragana', 10, 'HIRAGANA', true, true),
    ('Katakana letter only', 10, 'KATAKANA', true, false),
    ('Katakana pronouns only', 10, 'KATAKANA', false, true);
    //('Katakana', 10, 'KATAKANA', true, true);


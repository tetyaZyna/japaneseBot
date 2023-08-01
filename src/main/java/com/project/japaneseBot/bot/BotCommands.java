package com.project.japaneseBot.bot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "Start the bot"),
            new BotCommand("/help", "Help menu"),
            new BotCommand("/register", "Register new user"),
            new BotCommand("/profile", "View your profile"),
            new BotCommand("/katakana", "Open katakana keyboard"),
            new BotCommand("/hiragana", "Open hiragana keyboard"),
            new BotCommand("/task", "Start new task (for registrants only)"),
            new BotCommand("/close", "Close the current activity and return to text mode")
    );

    String HELP_TEXT = """
            The following commands are available to you:

            /help - Help menu
            /register - Register new user
            /profile - View your profile
            /katakana - Open katakana keyboard
            /hiragana - Open hiragana keyboard
            /task - Start new task (for registrants only)
            /close - Close the current activity and return to text mode
            """;

    String SETTING_CREATION_INSTRUCTION = """
            Create your own setting
            Use /setSettings [Name] [Default count] [Alphabet] [Use pronouns] [Use letter]
                        
            [Name] - Write name of your setting. Must not contain spaces, for example My_Setting or MySetting.
            
            [Default count] - Number of questions in the test. Enter any number between 5 and 100.
            
            [Alphabet] - Select the alphabet that will be used.
            Type KATAKANA if you want to use only katakana, type HIRAGANA if you want to use only hiragana and ALL if you want to use both.
            
            [Use pronouns] - Whether to use the pronunciation of letters in test questions. Type true or false.
            
            [Use letter] - Whether to use the letters in test questions. Type true or false.
             
                        
            Note that you must use at least one question type (Use pronouns or Use letter)
                """;
}

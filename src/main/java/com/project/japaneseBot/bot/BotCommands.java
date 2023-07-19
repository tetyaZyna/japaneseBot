package com.project.japaneseBot.bot;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info"),
            new BotCommand("/register", "register"),
            new BotCommand("/profile", "profile"),
            new BotCommand("/katakana", "katakana"),
            new BotCommand("/hiragana", "hiragana")
    );

    String HELP_TEXT = """
            This bot will help to count the number of messages in the chat. The following commands are available to you:

            /start - start the bot
            /help - help menu""";
}

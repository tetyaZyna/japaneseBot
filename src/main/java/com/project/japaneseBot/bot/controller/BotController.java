package com.project.japaneseBot.bot.controller;

import com.project.japaneseBot.bot.BotCommands;
import com.project.japaneseBot.bot.Buttons;
import com.project.japaneseBot.user.repository.UserRepository;
import com.project.japaneseBot.config.BotConfig;
import com.project.japaneseBot.user.entity.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;


@Component
@Slf4j
public class BotController extends TelegramLongPollingBot implements BotCommands {
    BotConfig config;
    UserRepository userRepository;

    public BotController(BotConfig config, UserRepository userRepository) {
        super(config.token());
        this.config = config;
        this.userRepository = userRepository;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId;
        long userId;
        String userName;
        String receivedMessage;
        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            userName = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName, userId);
            }
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            userName = update.getCallbackQuery().getFrom().getFirstName();
            receivedMessage = update.getCallbackQuery().getData();

            botAnswerUtils(receivedMessage, chatId, userName, userId);
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName, long userId) {
        switch (receivedMessage) {
            case "/start" -> startBot(chatId, userName);
            case "/help" -> sendHelpText(chatId);
            case "/register" -> registerUser(chatId,userId);
            case "/profile" -> profile(chatId,userId);
            default -> {
            }
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(Buttons.inlineMarkup());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void sendHelpText(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(BotCommands.HELP_TEXT);

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void registerUser (long chatId, long userId) {
        userRepository.save(UserEntity.builder()
                .userId(userId)
                .registrationDate(LocalDate.now())
                .build());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Created Account");

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void profile (long chatId, long userId) {
        var user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Data of creation: " + user.getRegistrationDate().toString());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.name();
    }
}

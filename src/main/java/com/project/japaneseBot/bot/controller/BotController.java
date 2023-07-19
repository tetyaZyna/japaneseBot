package com.project.japaneseBot.bot.controller;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
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
    Buttons buttons;
    ReadOnlyKatakanaRepository katakanaRepository;
    ReadOnlyHiraganaRepository hiraganaRepository;

    public BotController(BotConfig config, UserRepository userRepository, Buttons buttons,
                         ReadOnlyKatakanaRepository katakanaRepository, ReadOnlyHiraganaRepository hiraganaRepository) {
        super(config.token());
        this.config = config;
        this.userRepository = userRepository;
        this.buttons = buttons;
        this.katakanaRepository = katakanaRepository;
        this.hiraganaRepository = hiraganaRepository;
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

/*    private void botAnswerUtils(String receivedMessage, long chatId, String userName, long userId) {
    if (receivedMessage.startsWith("/katakana ")) {
        String katakanaValue = receivedMessage.substring("/katakana ".length());
        handleKatakanaValue(chatId, katakanaValue);
    } else {
        switch (receivedMessage) {
            case "/start" -> startBot(chatId, userName);
            case "/help" -> sendHelpText(chatId);
            case "/register" -> registerUser(chatId, userId);
            case "/profile" -> profile(chatId, userId);
            case "/katakana" -> katakana(chatId);
            default -> {
            }
        }
    }
}*/

    private void botAnswerUtils(String receivedMessage, long chatId, String userName, long userId) {
        if (receivedMessage.startsWith("/")) {
            switch (receivedMessage) {
                case "/start" -> startBot(chatId, userName);
                case "/help" -> sendHelpText(chatId);
                case "/register" -> registerUser(chatId, userId);
                case "/profile" -> profile(chatId, userId);
                case "/hiragana" -> switchHiragana(chatId);
                case "/katakana" -> switchKatakana(chatId);
                default -> defaultAnswer(chatId);
            }
        } else {

        }
    }

    private void handleMessage(long chatId, String message) {
        message = message.trim();
        if (message.length() == 1) {
            if (katakanaRepository.existsByHieroglyph(message)) {
                handleKatakanaValue(chatId, message);
            } else if (hiraganaRepository.existsByHieroglyph(message)) {
                handleHiraganaValue(chatId, message);
            }

        }
        for (int i = 0; i < message.length(); i++) {
            char letter = message.charAt(i);

        }
    }

    private void defaultAnswer(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Unknown command");
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Hi, " + userName + "! I'm a Telegram bot.'");
        message.setReplyMarkup(buttons.inlineMarkup());

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

    private void switchKatakana(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Keyboard switched to Katakana");
        message.setReplyMarkup(buttons.getKatakanaReplyKeyboard());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void switchHiragana(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Keyboard switched to Hiragana");
        message.setReplyMarkup(buttons.getHiraganaReplyKeyboard());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private String[] handleKatakanaValue(String katakanaValue) {
        String hieroglyphPronouns = katakanaRepository.findByHieroglyph(katakanaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        return new String[]{katakanaValue, hieroglyphPronouns};

/*        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(katakanaValue + " - " + hieroglyphPronouns);
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }*/
    }

    private void handleHiraganaValue(long chatId, String hiraganaValue) {
        String hieroglyphPronouns = hiraganaRepository.findByHieroglyph(hiraganaValue)
                .orElseThrow(RuntimeException::new).getHieroglyphPronouns();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(hiraganaValue + " - " + hieroglyphPronouns);
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

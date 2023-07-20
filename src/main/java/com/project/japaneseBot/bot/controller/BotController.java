package com.project.japaneseBot.bot.controller;

import com.project.japaneseBot.alphabet.repository.ReadOnlyHiraganaRepository;
import com.project.japaneseBot.alphabet.repository.ReadOnlyKatakanaRepository;
import com.project.japaneseBot.bot.buttons.Buttons;
import com.project.japaneseBot.bot.buttons.Keyboards;
import com.project.japaneseBot.bot.init.BotCommands;
import com.project.japaneseBot.bot.model.UserMode;
import com.project.japaneseBot.bot.service.BotService;
import com.project.japaneseBot.bot.service.HandlerService;
import com.project.japaneseBot.config.BotConfig;
import com.project.japaneseBot.dto.SimpleTask;
import com.project.japaneseBot.user.entity.UserEntity;
import com.project.japaneseBot.user.repository.UserRepository;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


@Component
@Slf4j
public class BotController extends TelegramLongPollingBot implements BotCommands {
    BotConfig config;
    Buttons buttons;
    Keyboards keyboards;
    UserRepository userRepository;
    ReadOnlyKatakanaRepository katakanaRepository;
    ReadOnlyHiraganaRepository hiraganaRepository;
    HandlerService handlerService;
    BotService botService;

    public BotController(BotConfig config, UserRepository userRepository, Buttons buttons, Keyboards keyboards,
                         ReadOnlyKatakanaRepository katakanaRepository, ReadOnlyHiraganaRepository hiraganaRepository,
                         HandlerService handlerService, BotService botService) {
        super(config.token());
        this.config = config;
        this.buttons = buttons;
        this.keyboards = keyboards;
        this.userRepository = userRepository;
        this.katakanaRepository = katakanaRepository;
        this.hiraganaRepository = hiraganaRepository;
        this.handlerService = handlerService;
        this.botService = botService;
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
        if (botService.getUserMode(userId).equals(UserMode.GUEST_MODE.name())) {
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
                returnPronouns(chatId, receivedMessage);
            }
        } else if (botService.getUserMode(userId).equals(UserMode.TEXT_MODE.name())) {
            if (receivedMessage.startsWith("/")) {
                switch (receivedMessage) {
                    case "/start" -> startBot(chatId, userName);
                    case "/help" -> sendHelpText(chatId);
                    case "/register" -> registerUser(chatId, userId);
                    case "/profile" -> profile(chatId, userId);
                    case "/hiragana" -> switchHiragana(chatId);
                    case "/katakana" -> switchKatakana(chatId);
                    case "/task" -> startTask(chatId);
                    default -> defaultAnswer(chatId);
                }
            } else {
                returnPronouns(chatId, receivedMessage);
            }
        } else if (botService.getUserMode(userId).equals(UserMode.TASK_MODE.name())) {
            if (receivedMessage.startsWith("/")) {
                switch (receivedMessage) {
                    case "/hiragana" -> switchHiragana(chatId);
                    case "/katakana" -> switchKatakana(chatId);
                    default -> defaultAnswer(chatId);
                }
            } else {
                returnPronouns(chatId, receivedMessage);
            }
        }
    }

    private void startTask(long chatId) {
        int questionCount = 10;
        var letterList = generateLetter(questionCount);
        var pronounsList = findPronouns(letterList);
        SimpleTask task = SimpleTask.builder()
                .questionNumber(1)
                .questionCount(questionCount)
                .letter(letterList)
                .pronouns(pronounsList)
                .isAnswerCorrect(new boolean[questionCount])
                .build();
        //TODO create DB table for tasks
    }

    private List<String> findPronouns(List<String> letterList) {
        List<String> pronouns = new LinkedList<>();
        for (String s : letterList) {
            pronouns.add(katakanaRepository.findByHieroglyph(s)
                    .orElseThrow(RuntimeException::new)
                    .getHieroglyphPronouns());
        }
        return pronouns;
    }

    private List<String> generateLetter (int count) {
        List<String> letter = new LinkedList<>();
        long repositoryLength = katakanaRepository.count();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            letter.add(
                    katakanaRepository.findByHieroglyphId(random.nextLong(repositoryLength + 1))
                            .orElseThrow(RuntimeException::new)
                            .getHieroglyph()
            );
        }
        return letter;
    }


    private void returnPronouns(long chatId, String receivedMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(handlerService.handleMessage(receivedMessage));
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
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
                .mode("TEXT_MODE")
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
        message.setReplyMarkup(keyboards.getKatakanaReplyKeyboard());

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
        message.setReplyMarkup(keyboards.getHiraganaReplyKeyboard());

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

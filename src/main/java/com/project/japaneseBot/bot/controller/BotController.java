package com.project.japaneseBot.bot.controller;

import com.project.japaneseBot.bot.BotCommands;
import com.project.japaneseBot.bot.buttons.Buttons;
import com.project.japaneseBot.bot.buttons.Keyboards;
import com.project.japaneseBot.bot.service.HandlerService;
import com.project.japaneseBot.bot.service.SettingsService;
import com.project.japaneseBot.bot.service.TaskService;
import com.project.japaneseBot.bot.service.UserService;
import com.project.japaneseBot.config.BotConfig;
import com.project.japaneseBot.task.model.entity.TaskSettingsEntity;
import com.project.japaneseBot.task.repository.TaskRepository;
import com.project.japaneseBot.user.model.entity.UserEntity;
import com.project.japaneseBot.user.model.enums.UserMode;
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


@Component
@Slf4j
public class BotController extends TelegramLongPollingBot implements BotCommands {
    BotConfig config;
    Buttons buttons;
    Keyboards keyboards;
    UserRepository userRepository; //TODO refactor: don't use repositories in controller
    TaskRepository taskRepository;
    HandlerService handlerService;
    UserService userService;
    TaskService taskService;
    SettingsService settingsService;

    public BotController(BotConfig config, UserRepository userRepository, Buttons buttons, Keyboards keyboards,
                         TaskRepository taskRepository, HandlerService handlerService, UserService userService,
                         TaskService taskService, SettingsService settingsService) {
        super(config.token());
        this.config = config;
        this.buttons = buttons;
        this.keyboards = keyboards;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.handlerService = handlerService;
        this.userService = userService;
        this.taskService = taskService;
        this.settingsService = settingsService;
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
        UserMode userMode = UserMode.valueOf(userService.getUserMode(userId));
        switch (userMode) {
            case GUEST_MODE -> handleGuestModeCommand(receivedMessage, chatId, userName, userId);
            case TEXT_MODE -> handleTextModeCommand(receivedMessage, chatId, userId, userName);
            case SETT_MODE -> handleSettModeCommand(receivedMessage, chatId, userId);
            case TASK_MODE -> handleTaskModeCommand(receivedMessage, chatId, userId);
            case EDIT_SETT_MODE -> handleEditSettModeCommand(receivedMessage, chatId, userId);
            default -> defaultAnswer(chatId);
        }
    }

    private void handleEditSettModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            if (receivedMessage.startsWith("/setSettings")) {
                createAndSaveSettings(chatId, userId, receivedMessage);
            } else {
                switch (receivedMessage) {
                    case "/hiragana" -> switchHiragana(chatId);
                    case "/katakana" -> switchKatakana(chatId);
                    case "/close" -> returnToTaskMode(chatId, userId);
                    default -> defaultAnswer(chatId);
                }
            }
        }
    }

    private void handleTaskModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            switch (receivedMessage) {
                case "/hiragana" -> switchHiragana(chatId);
                case "/katakana" -> switchKatakana(chatId);
                case "/close" -> returnToTaskMode(chatId, userId);
                default -> defaultAnswer(chatId);
            }
        } else {
            checkAnswer(chatId, userId, receivedMessage);
        }
    }

    private void handleSettModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            switch (receivedMessage) {
                case "/hiragana" -> switchHiragana(chatId);
                case "/katakana" -> switchKatakana(chatId);
                case "/close" -> returnToTaskMode(chatId, userId);
                case "/create" -> startCreatingSettings(chatId, userId);
                default -> defaultAnswer(chatId);
            }
        } else {
            handleSettings(chatId, userId, receivedMessage);
        }
    }

    private void handleTextModeCommand(String receivedMessage, long chatId, long userId, String userName) {
        if (receivedMessage.startsWith("/")) {
            switch (receivedMessage) {
                case "/start" -> startBot(chatId, userName);
                case "/help" -> sendHelpText(chatId);
                case "/register" -> registerUser(chatId, userId);
                case "/profile" -> profile(chatId, userId);
                case "/hiragana" -> switchHiragana(chatId);
                case "/katakana" -> switchKatakana(chatId);
                case "/task" -> startTaskSettings(chatId, userId);
                default -> defaultAnswer(chatId);
            }
        } else {
            returnPronouns(chatId, receivedMessage);
        }
    }

    private void handleGuestModeCommand(String receivedMessage, long chatId, String userName, long userId) {
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
    }

    private void createAndSaveSettings(long chatId, long userId, String receivedMessage) {
        settingsService.createAndSaveSettings(receivedMessage);
        startTaskSettings(chatId, userId);
    }

    private void startCreatingSettings(long chatId, long userId) {
        userService.startEditSettingsMode(userId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("""
                        Set settings name
                        Use /setSettings [Name] [Default count] [Alphabet] [Use pronouns] [Use letter]
                """);
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void handleSettings(long chatId, long userId, String receivedMessage) {
        TaskSettingsEntity settings = settingsService.findSettings(receivedMessage);
        startTask(chatId, userId, settings);
    }

    private void startTaskSettings(long chatId, long userId) {
        userService.startSettingsMode(userId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Chose task");
        message.setReplyMarkup(buttons.inlineSettingsKeyboard());
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void checkAnswer(long chatId, long userId, String receivedMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(taskService.checkAnswer(userId, receivedMessage));
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void returnToTaskMode(long chatId, long userId) {
        userService.startTextMode(userId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Returned to Text mode");
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void startTask(long chatId, long userId, TaskSettingsEntity settings) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(taskService.initialiseTask(userId, settings));
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void returnPronouns(long chatId, String receivedMessage) {
        String response = handlerService.handleMessage(receivedMessage);
        if (!response.isEmpty()){
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(response);
            try {
                execute(message);
                log.info("Reply sent");
            } catch (TelegramApiException e){
                log.error(e.getMessage());
            }
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

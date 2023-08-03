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
import com.project.japaneseBot.user.model.enums.UserMode;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
@Slf4j
public class BotController extends TelegramLongPollingBot implements BotCommands {
    BotConfig config;
    Buttons buttons;
    Keyboards keyboards;
    HandlerService handlerService;
    UserService userService;
    TaskService taskService;
    SettingsService settingsService;

    public BotController(BotConfig config, Buttons buttons, Keyboards keyboards, HandlerService handlerService,
                         UserService userService, TaskService taskService, SettingsService settingsService) {
        super(config.token());
        this.config = config;
        this.buttons = buttons;
        this.keyboards = keyboards;
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
        if (!handleKeyboardCommand(receivedMessage, chatId)) {
            switch (userMode) {
                case GUEST_MODE -> handleGuestModeCommand(receivedMessage, chatId, userName, userId);
                case TEXT_MODE -> handleTextModeCommand(receivedMessage, chatId, userId, userName);
                case SETT_MODE -> handleSettModeCommand(receivedMessage, chatId, userId);
                case TASK_MODE -> handleTaskModeCommand(receivedMessage, chatId, userId);
                case EDIT_SETT_MODE -> handleEditSettModeCommand(receivedMessage, chatId, userId);
                default -> defaultAnswer(chatId);
            }
        }
    }

    private boolean handleKeyboardCommand(String receivedMessage, long chatId) {
        switch (receivedMessage) {
            case "/hiragana" -> {
                return switchHiragana(chatId);
            }
            case "/katakana" -> {
                return switchKatakana(chatId);
            }
            case "/pronouns" -> {
                return switchPronouns(chatId);
            }
        }
        return false;
    }

    private void handleEditSettModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            handleKeyboardCommand(receivedMessage, chatId);
            if (receivedMessage.startsWith("/setSettings")) {
                createAndSaveSettings(chatId, userId, receivedMessage);
            } else if (receivedMessage.equals("/close")) {
                returnToTextMode(chatId, userId);
            } else {
                defaultAnswer(chatId);
            }
        }
    }

    private void handleTaskModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            if (receivedMessage.equals("/close")) {
                returnToTextMode(chatId, userId);
            } else {
                defaultAnswer(chatId);
            }
        } else {
            checkAnswer(chatId, userId, receivedMessage);
        }
    }

    private void handleSettModeCommand(String receivedMessage, long chatId, long userId) {
        if (receivedMessage.startsWith("/")) {
            switch (receivedMessage) {
                case "/close" -> returnToTextMode(chatId, userId);
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
                case "/register" -> registerUser(chatId, userId, userName);
                case "/profile" -> profile(chatId, userId);
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
                case "/register" -> registerUser(chatId, userId, userName);
                case "/profile" -> profile(chatId, userId);
                default -> defaultAnswer(chatId);
            }
        } else {
            returnPronouns(chatId, receivedMessage);
        }
    }

    private void createAndSaveSettings(long chatId, long userId, String receivedMessage) {
        String answer = settingsService.createAndSaveSettings(receivedMessage);
        if (answer == null) {
            sendFeedbackAboutCreatingSettings(chatId,"Error while processing settings. Check if the input is correct.");
        } else {
            sendFeedbackAboutCreatingSettings(chatId, answer);
            startTaskSettings(chatId, userId);
        }
    }

    private void sendFeedbackAboutCreatingSettings(long chatId, String feedback) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(feedback);
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void startCreatingSettings(long chatId, long userId) {
        userService.startEditSettingsMode(userId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(SETTING_CREATION_INSTRUCTION);
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
        message.setText("Chose task settings" +
                "\nOr you can create your own. Use /create");
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

    private void returnToTextMode(long chatId, long userId) {
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
        message.setText("Unknown command. " +
                "Perhaps you should register (/register) or close (/close) your current activity to use this command.");
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
        message.setText("Hi, " + userName + "! I'm a Telegram bot for learning Japanese.'");
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

    private void registerUser (long chatId, long userId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(userService.createUser(userId, userName));

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void profile (long chatId, long userId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(userService.getUserProfile(userId));

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private boolean switchKatakana(long chatId) {
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
        return true;
    }

    private boolean switchHiragana(long chatId) {
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
        return true;
    }

    private boolean switchPronouns(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Keyboard switched to Pronouns");
        message.setReplyMarkup(keyboards.getPronounsReplyKeyboardMarkup());

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
        return true;
    }

    @Override
    public String getBotUsername() {
        return config.name();
    }
}

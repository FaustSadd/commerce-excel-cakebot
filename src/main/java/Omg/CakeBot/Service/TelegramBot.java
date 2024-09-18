package Omg.CakeBot.Service;import Omg.CakeBot.Config.BotConfig;import org.springframework.stereotype.Component;import org.telegram.telegrambots.bots.TelegramLongPollingBot;import org.telegram.telegrambots.meta.api.methods.send.SendMessage;import org.telegram.telegrambots.meta.api.objects.Update;import org.telegram.telegrambots.meta.exceptions.TelegramApiException;@Componentpublic class TelegramBot extends TelegramLongPollingBot {    LogicCore logiccore = new LogicCore(); //Ссылка на объект класса LogicCore    //GoogleSheetsExample googleSheetsExample = new GoogleSheetsExample();    final BotConfig config;    public TelegramBot(BotConfig Config, BotConfig config){        this.config = config;    }    @Override    public void onUpdateReceived(Update update) {        if (update.hasMessage() && update.getMessage().hasText()){            String messageText = update.getMessage().getText();            long chatId = update.getMessage().getChatId();            if (messageText.startsWith("[OMG Cake]: Новый заказ")){//Если текст начинается со след фразы: вызов функции getStandartReceipt                String responseMessage = logiccore.getStandartReceipt(messageText);                sendMessage(chatId, responseMessage);// Отправка сообщения с результатом функции getStandartReceip            }            switch (messageText){                case "/start":                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());                    break;                default: sendMessage(chatId,"pososi");                break;            }        }    }    @Override    public String getBotUsername() {        return config.getBotName();    }    @Override    public String getBotToken() {        return config.getToken();    }    private void startCommandReceived(long chatId, String Name){        String answer = "Hello World, " + Name;        sendMessage(chatId,answer);    }    private void sendMessage(long chatId,String textToSend){ //Рассмотреть возможность наследования ***        SendMessage message = new SendMessage();        message.setChatId(String.valueOf(chatId));        message.setText(textToSend);        try {            execute(message);        }        catch (TelegramApiException e) {        }    }}
package java.com.github.vimcmd.message;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageImpl implements ChatMessage {

    private String from;
    private List<String> recipients;
    private String messageBody;

    public ChatMessageImpl(String messageBody) {
        recipients = new ArrayList<>();
        this.messageBody = messageBody;
    }

    @Override
    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    @Override
    public void addRecipients(List<String> recipientList) {
        this.recipients.addAll(recipientList);
    }

    @Override
    public String getFrom() {
        if (from == null) {
            return "";
        }
        return from;
    }

    @Override
    public void setFrom(String from) {
        // TODO: 04.06.2016 remove or set protected
        this.from = from;
    }

    @Override
    public List<String> getRecipients() {
        return new ArrayList<>(recipients);
    }

    @Override
    public String getMessageBody() {
        if (messageBody == null) {
            return "";
        }
        return messageBody;
    }

    @Override
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}

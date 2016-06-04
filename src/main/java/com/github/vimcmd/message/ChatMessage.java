package java.com.github.vimcmd.message;

import java.util.List;

public interface ChatMessage {

    void addRecipient(String recipient);

    void addRecipients(List<String> recipientList);

    String getFrom();

    void setFrom();

    List<String> getRecipients();

    String getMessageBody();

    void setMessageBody();

}

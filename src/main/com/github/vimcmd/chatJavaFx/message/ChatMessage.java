package com.github.vimcmd.chatJavaFx.message;

import java.util.List;

public interface ChatMessage {

    void addRecipient(String recipient);

    void addRecipients(List<String> recipientList);

    String getFrom();

    void setFrom(String from);

    List<String> getRecipients();

    String getMessageBody();

    void setMessageBody(String messageBody);

}

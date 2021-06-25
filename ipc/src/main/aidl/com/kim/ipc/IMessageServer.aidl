// IMessageServer.aidl
package com.kim.ipc;

// Declare any non-default types here with import statements

interface IMessageServer {
    void sendMessage(String from, String messageType, String messageKey, String messageJsonString);
}
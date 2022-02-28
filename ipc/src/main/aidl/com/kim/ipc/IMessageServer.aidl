// IMessageServer.aidl
package com.kim.ipc;

// Declare any non-default types here with import statements
import com.kim.ipc.Message;
interface IMessageServer {
//    void sendMessage(String from, String messageType, String messageKey, String messageJsonString);
     void sendMsg(inout Message msg);
//     void sendMsgString(String jsonString);
}
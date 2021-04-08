package com.arematics.minecraft.data.redis;

import com.arematics.minecraft.data.service.GlobalMessageReceiveService;
import com.arematics.minecraft.data.service.MessageReceivingService;
import org.bukkit.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalRedisMessageSubscriber implements MessageListener {

    private final Map<String, List<MessageReceivingService>> messageReceivingServices;

    @Autowired
    public GlobalRedisMessageSubscriber(List<GlobalMessageReceiveService> globalMessageReceiveServices){
        this.messageReceivingServices = globalMessageReceiveServices
                .stream()
                .collect(Collectors.groupingBy(MessageReceivingService::messageKey));
    }

    @EventHandler
    public void onMessage(Message message, byte[] pattern) {
        String msg = new String(message.getBody());
        if(!msg.contains(":")) return;
        String[] set = new String(message.getBody()).split(":");
        handleMessage(set[0].trim(), set[1].trim());
    }

    private void handleMessage(String key, String value){
        List<MessageReceivingService> services = this.messageReceivingServices.getOrDefault(key, new ArrayList<>());
        services.forEach(service -> service.onReceive(value));
    }
}

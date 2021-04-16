package com.arematics.minecraft.data.redis;

import com.arematics.minecraft.data.service.MessageReceivingService;
import com.arematics.minecraft.data.service.ModeMessageReceiveService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Order
@Service
public class ModeRedisMessageSubscriber implements MessageListener {

    private static final Logger logger = Bukkit.getLogger();

    private final Map<String, List<MessageReceivingService>> messageReceivingServices;

    @Autowired
    public ModeRedisMessageSubscriber(List<ModeMessageReceiveService> modeMessageReceiveServices){
        this.messageReceivingServices = modeMessageReceiveServices
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

    private void handleMessage(final String key, final String value){
        logger.config("Received mode redis message with key: " + key + " and value: " + value);
        List<MessageReceivingService> services = this.messageReceivingServices.getOrDefault(key, new ArrayList<>());
        services.forEach(service -> service.onReceive(value));
    }
}

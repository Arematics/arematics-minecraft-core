package com.arematics.minecraft.core.utils;

import com.arematics.minecraft.core.messaging.advanced.Format;
import com.arematics.minecraft.core.messaging.advanced.JsonColor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;

/**
 * @author DarkSeraphim
 **/
public class JSONUtil
{
    private static final StringBuilder JSON_BUILDER = new StringBuilder("{\"text\":\"\",\"extra\":[");
 
    private static final int RETAIN = "{\"text\":\"\",\"extra\":[".length();
 
    public static String toJSON(String message)
    {
        if(message == null || message.isEmpty())
            return null;
        if(JSON_BUILDER.length() > RETAIN)
            JSON_BUILDER.delete(RETAIN, JSON_BUILDER.length());
        String[] parts = message.split(Character.toString(ChatColor.COLOR_CHAR));
        boolean first = true;
        String colour = null;
        String format = null;
        boolean ignoreFirst = !parts[0].isEmpty() && ChatColor.getByChar(parts[0].charAt(0)) != null;
        for(String part : parts)
        {
            // If it starts with a colour, just ignore the empty String
            // before it
            if(part.isEmpty())
            {
                continue;
            }
            
            String newStyle = null;
            if(!ignoreFirst)
            {
                newStyle = getStyle(part.charAt(0));
            }
            else
            {
                ignoreFirst = false;    
            }
            
            if(newStyle != null)
            {
                part = part.substring(1);
                if(newStyle.startsWith("\"c"))
                    colour = newStyle;
                else
                    format = newStyle;
            }
            if(!part.isEmpty())
            {
                if(first)
                    first = false;
                else
                {
                    JSON_BUILDER.append(",");
                }
                JSON_BUILDER.append("{");
                if(colour != null)
                {
                    JSON_BUILDER.append(colour);
                    colour = null;
                }
                if(format != null)
                {
                    JSON_BUILDER.append(format);
                    format = null;
                }
                JSON_BUILDER.append(String.format("text:\"%s\"", part));
                JSON_BUILDER.append("}");
            }
        }
        return JSON_BUILDER.append("]}").toString();
    }
 
    private static final StringBuilder STYLE = new StringBuilder();
 
    private static String getStyle(char colour)
    {
        if(STYLE.length() > 0)
            STYLE.delete(0, STYLE.length());
        switch(colour)
        {
            case 'k':
                return "\"obfuscated\": true,";
            case 'l':
                return "\"bold\": true,";
            case 'm':
                return "\"strikethrough\": true,";
            case 'n':
                return "\"underlined\": true,";
            case 'o':
                return "\"italic\": true,";
            case 'r':
                return "\"reset\": true,";
            default:
                break;
        }
        ChatColor cc = ChatColor.getByChar(colour);
        if(cc == null)
            return null;
        return STYLE.append("\"color\":\"").append(cc.name().toLowerCase()).append("\",").toString();
    }
    
    public static String toPlainText(String json){
    	JsonObject jsonobj = (JsonObject) new JsonParser().parse(json);
    	
    	StringBuilder text = new StringBuilder("");
    	
    	if(jsonobj.has("text"))
    		text.append(jsonobj.get("text").getAsString());
    	
    	if(jsonobj.has("extra")){
    		JsonArray extra = jsonobj.get("extra").getAsJsonArray();
    		
    		for(int i = 0; i < extra.size(); i++){
    			JsonObject part = extra.get(i).getAsJsonObject();
    			if(part.has("text"))
    				text.append(part.get("text").getAsString());
    		}
    	}
    	return text.toString();
    }
    
    public static String toCodedPlainText(String json){
    	JsonObject jsonobj = (JsonObject) new JsonParser().parse(json);
    	
    	StringBuilder text = new StringBuilder("");
    	
    	if(jsonobj.has("text"))
    		text.append((jsonobj.has("color") ? "§" + toColorCodeChar(jsonobj.get("color").getAsString()) : "")
    				+ (jsonobj.has("style") ? "§" + toStyleCodeChar(jsonobj.get("style").getAsString()) : "")
    				+ jsonobj.get("text").getAsString());
    	
    	if(jsonobj.has("extra")){
    		JsonArray extra = jsonobj.get("extra").getAsJsonArray();
    		
    		for(int i = 0; i < extra.size(); i++){
    			JsonObject part = extra.get(i).getAsJsonObject();
    			if(part.has("text"))
    				text.append((part.has("color") ? "§" + toColorCodeChar(part.get("color").getAsString()) : "")
    						+ (part.has("style") ? "§" + toStyleCodeChar(part.get("style").getAsString()) : "")
    						+ part.get("text").getAsString());
    		}
    	}
    	return text.toString();
    }
    
    private static char toColorCodeChar(String jsonColor){
        return JsonColor.findByName(jsonColor, JsonColor.BLACK).COLOR_CODE;
    }
    
    private static char toStyleCodeChar(String jsonStyle){
    	for(Format f : Format.values())
    		if(f.FORMAT.equalsIgnoreCase(jsonStyle))
    			return f.STYLE_CODE;
    	
    	return '0';
    }
    
}

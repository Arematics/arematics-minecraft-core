package com.arematics.minecraft.core.messaging.advanced;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class Part implements Cloneable {

    public String TEXT;
    public HoverAction HOVER_ACTION = null;
    public String HOVER_VALUE = null;
    public ClickAction CLICK_ACTION = null;
    public String CLICK_VALUE = null;
    public JsonColor BASE_COLOR = null;
    public final HashSet<Format> MESSAGE_FORMATS = new HashSet<>();

    public Part(String text){
        this(text, new Format[]{});
    }

    public Part(String text, Format... formats){
        this(text, null, formats);
    }

    public Part(String text, JsonColor BASE_COLOR, Format... formats){
        this.TEXT = text;
        this.BASE_COLOR = BASE_COLOR;
        if(!ArrayUtils.isEmpty(formats))
            Arrays.stream(formats).filter(Objects::nonNull).forEach(this.MESSAGE_FORMATS::add);
    }

    private Part(JsonObject jsonObject) throws JsonParseException {
        if(!jsonObject.has("text")) throw new JsonParseException("Missing 'text'-Member in JsonObject!");

        this.TEXT = jsonObject.get("text").getAsString();

        if(jsonObject.has("color")) {
            String color = getStringOrThrow(jsonObject, "color");
            this.BASE_COLOR = JsonColor.findByName(color, this.BASE_COLOR);
        }

        for(Format format : Format.values()) {
            if(jsonObject.has(format.FORMAT)) {
                JsonElement formatElement = jsonObject.get(format.FORMAT);
                if(!formatElement.isJsonPrimitive()) throw new JsonParseException("'" + format.FORMAT + "' is not a boolean!");
                JsonPrimitive formatPrimitive = formatElement.getAsJsonPrimitive();
                if(!formatPrimitive.isBoolean()) throw new JsonParseException("'" + format.FORMAT + "' is not a boolean!");
                if(formatPrimitive.getAsBoolean()) this.MESSAGE_FORMATS.add(format);
            }
        }

        if(jsonObject.has("hoverEvent")) {
            JsonObject eventObject = readJsonPath(jsonObject, "hoverEvent");
            this.HOVER_ACTION = HoverAction.findByAction(getStringOrThrow(eventObject, "action"), this.HOVER_ACTION);
            this.HOVER_VALUE = getStringOrThrow(eventObject, "value");
        }

        if(jsonObject.has("clickEvent")) {
            JsonObject eventObject = readJsonPath(jsonObject, "clickEvent");
            this.CLICK_ACTION = ClickAction.findByAction(getStringOrThrow(eventObject, "action"), this.CLICK_ACTION);
            this.CLICK_VALUE = getStringOrThrow(eventObject, "value");
        }
    }

    public Part setHoverAction(HoverAction action, String value){
        this.HOVER_ACTION = action;
        this.HOVER_VALUE = action == null ? null : value;
        return this;
    }

    public Part setClickAction(ClickAction action, String value){
        this.CLICK_ACTION = action;
        this.CLICK_VALUE = action == null ? null : value;
        return this;
    }

    public Part setText(String text){
        this.TEXT = text;
        return this;
    }

    public Part setBaseColor(JsonColor color){
        this.BASE_COLOR = color;
        return this;
    }

    public Part removeHoverAction(){
        this.HOVER_ACTION = null;
        this.HOVER_VALUE = null;
        return this;
    }

    public Part removeClickAction(){
        this.CLICK_ACTION = null;
        this.CLICK_VALUE = null;
        return this;
    }

    public Part resetFormats() {
        this.MESSAGE_FORMATS.clear();
        return this;
    }

    public boolean hasFormat(Format format) {
        return format != null && this.MESSAGE_FORMATS.contains(format);
    }

    public HashSet<Format> getRawFormatSet() {
        return MESSAGE_FORMATS;
    }

    public Format[] getFormats() {
        return MESSAGE_FORMATS.toArray(new Format[]{});
    }

    private static JsonObject readJsonPath(JsonObject object, String path) throws JsonParseException {
        JsonElement eventElement = object.get(path);
        if(!eventElement.isJsonObject()) throw new JsonParseException("'" + path + "' is not a JsonObject!");
        return eventElement.getAsJsonObject();
    }

    private static String getStringOrThrow(JsonObject object, String key) throws JsonParseException {
        if(!object.has(key)) throw new JsonParseException("'" + key + "' was not found!");
        JsonElement element = object.get(key);
        if(!element.isJsonPrimitive()) throw new JsonParseException("'" + key + "' is not a string!");
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        if(!primitive.isString()) throw new JsonParseException("'" + key + "' is not a string!");
        return primitive.getAsString();
    }

    public String toFormattedText() {
        StringBuilder formattedText = new StringBuilder();

        // Color code:
        formattedText.append("§");
        formattedText.append(BASE_COLOR == null ? 'r' : BASE_COLOR.COLOR_CODE);

        // Format codes:
        for(Format messageFormat : getFormats()) {
            formattedText.append("§");
            formattedText.append(messageFormat.STYLE_CODE);
        }

        // Remaining text:
        formattedText.append(TEXT);

        return formattedText.toString();
    }

    public JsonObject toJsonObject(){
        JsonObject part = new JsonObject();
        part.addProperty("text", TEXT);

        if(this.HOVER_ACTION != null)
            part.add("hoverEvent", buildObject(this.HOVER_ACTION.ACTION, this.HOVER_VALUE));

        if(this.CLICK_ACTION != null)
            part.add("clickEvent", buildObject(this.CLICK_ACTION.ACTION, this.CLICK_VALUE));

        if(BASE_COLOR != null)
            part.addProperty("color", BASE_COLOR.NAME);

        this.MESSAGE_FORMATS.forEach(format -> part.addProperty(format.FORMAT, true));
        return part;
    }

    private JsonObject buildObject(String action, String value){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", action);
        jsonObject.addProperty("value", value);
        return jsonObject;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    @Override
    public Part clone() {
        return new Part(TEXT, BASE_COLOR, MESSAGE_FORMATS.toArray(new Format[]{}))
                .setHoverAction(HOVER_ACTION, HOVER_VALUE)
                .setClickAction(CLICK_ACTION, CLICK_VALUE);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Part)) return false;

        Part otherPart = (Part) other;
        return isSimilar(otherPart) && this.TEXT.equalsIgnoreCase(otherPart.TEXT);
    }

    /**
     * @param other Other part to compare to
     * @return Whether everything is the same EXCEPT the text.
     */
    public boolean isSimilar(Part other) {
        return this.getRawFormatSet().containsAll(other.getRawFormatSet())
                && other.getRawFormatSet().containsAll(this.getRawFormatSet())
                && this.BASE_COLOR == other.BASE_COLOR
                && this.HOVER_ACTION == other.HOVER_ACTION
                && this.HOVER_VALUE.equals(other.HOVER_VALUE)
                && this.CLICK_ACTION == other.CLICK_ACTION
                && this.CLICK_VALUE.equals(other.CLICK_VALUE);
    }

    public Part styleAndColorFromText() {
        JsonColor color = null;
        List<Format> formats = new ArrayList<>();
        for(int i = 0; i < this.TEXT.length(); i++) {
            try{
                final char c = getNextColor(TEXT, i);
                i++;
                if(color == null) {
                    for(JsonColor cColor : JsonColor.values())
                        if(cColor.COLOR_CODE == c) color = cColor;
                }
                Arrays.stream(Format.values()).filter(format -> format.STYLE_CODE == c).forEach(formats::add);
            }catch (IndexOutOfBoundsException e) { break; }
        }
        if(color != null) this.BASE_COLOR = color;
        this.MESSAGE_FORMATS.addAll(formats);
        return this;
    }

    private char getNextColor(String text, int position) throws IndexOutOfBoundsException{
        return text.charAt(text.substring(position).indexOf('§') + 1);
    }
}

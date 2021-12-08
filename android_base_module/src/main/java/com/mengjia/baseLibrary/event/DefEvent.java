package com.mengjia.baseLibrary.event;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/21
 * Time: 11:49
 */
public class DefEvent {
    private String type;
    private String tag;
    private EventData eventData;

    private DefEvent(Builder builder) {
        type = builder.type;
        tag = builder.tag;
        eventData = builder.eventData;
    }

    public void newBuilder(Builder builder){
        type = builder.type;
        tag = builder.tag;
        eventData = builder.eventData;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    public EventData getEventData() {
        return eventData;
    }

    public static final class Builder {
        private String type;
        private String tag;
        private EventData eventData;

        public Builder() {
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder tag(String val) {
            tag = val;
            return this;
        }

        public Builder eventData(EventData val) {
            eventData = val;
            return this;
        }

        public DefEvent build() {
            return new DefEvent(this);
        }
    }

    @Override
    public String toString() {
        return "DefEvent{" +
                "type='" + type + '\'' +
                ", tag='" + tag + '\'' +
                ", eventData=" + eventData +
                '}';
    }
}

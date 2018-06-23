package com.dearzs.app.chat.model;

/**
 * EventBus事件类
 */
public class EntityEvent {
    public static class IMStateEvent {
        private int state;

        public IMStateEvent(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }

    public static class ConversationChangeEvent {
        public ConversationChangeEvent() {
        }
    }
}

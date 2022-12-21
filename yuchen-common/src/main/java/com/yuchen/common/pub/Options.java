package com.yuchen.common.pub;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Options<T> {
    private String key;
    private Class<T> type;
    private T defaultVar;
    private boolean required;
    private String description;

    public static <T> OptionsBuilder<T> builder() {
        OptionsBuilder<T> optionsBuilder = new OptionsBuilder();
        return optionsBuilder;
    }

    public static final class OptionsBuilder<T> {
        private Options<T> options;

        private OptionsBuilder() {
            options = new Options();
        }

        public OptionsBuilder<T> key(String key) {
            options.setKey(key);
            return this;
        }

        public OptionsBuilder<T> defaultVar(T defaultVar) {
            options.setDefaultVar(defaultVar);
            return this;
        }
        public OptionsBuilder<T> description(String description) {
            options.setDescription(description);
            return this;
        }

        public OptionsBuilder<T> required(boolean required) {
            options.setRequired(required);
            return this;
        }

        public OptionsBuilder<T> type(Class<T> type) {
            options.setType(type);
            return this;
        }

        public Options<T> build() {
            if (options.getType() == null) {
                if (options.getDefaultVar() != null) {
                    options.setType((Class<T>) options.getDefaultVar().getClass());
                }
            }
            return options;
        }
    }
}
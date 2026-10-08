package com.aryston.arkea.api.config;

public interface ConfigValues {
    <T> T value(ConfigOption<T> option);
}

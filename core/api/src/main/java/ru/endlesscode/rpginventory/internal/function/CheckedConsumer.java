package ru.endlesscode.rpginventory.internal.function;

import java.util.function.Consumer;

@FunctionalInterface
public interface CheckedConsumer<T> {
    static <T> Consumer<T> wrap(CheckedConsumer<T> function) {
        return t -> {
            try {
                function.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t) throws Exception;
}

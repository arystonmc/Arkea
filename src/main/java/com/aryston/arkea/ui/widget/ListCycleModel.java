package com.aryston.arkea.ui.widget;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public final class ListCycleModel<T> implements CycleModel {
    private final Supplier<List<T>> values;
    private final Supplier<T> current;
    private final Consumer<T> select;
    private final Function<T, Component> label;

    public ListCycleModel(Supplier<List<T>> values, Supplier<T> current, Consumer<T> select, Function<T, Component> label) {
        this.values = values;
        this.current = current;
        this.select = select;
        this.label = label;
    }

    @Override
    public int size() {
        return this.values.get().size();
    }

    @Override
    public int index() {
        return Math.max(0, this.values.get().indexOf(this.current.get()));
    }

    @Override
    public void select(int index) {
        this.select.accept(this.values.get().get(index));
    }

    @Override
    public Component label(int index) {
        return this.label.apply(this.values.get().get(index));
    }
}

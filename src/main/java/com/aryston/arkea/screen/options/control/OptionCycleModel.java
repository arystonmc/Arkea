package com.aryston.arkea.screen.options.control;

import com.aryston.arkea.ui.widget.CycleModel;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;

final class OptionCycleModel<T> implements CycleModel {
    private final OptionInstance<T> option;
    private final Supplier<List<T>> values;
    private final Consumer<T> setter;

    OptionCycleModel(OptionInstance<T> option, Supplier<List<T>> values, Consumer<T> setter) {
        this.option = option;
        this.values = values;
        this.setter = setter;
    }

    @Override
    public int size() {
        return this.values.get().size();
    }

    @Override
    public int index() {
        return Math.max(0, this.values.get().indexOf(this.option.get()));
    }

    @Override
    public void select(int index) {
        List<T> list = this.values.get();
        if (index >= 0 && index < list.size()) {
            this.setter.accept(list.get(index));
        }
    }

    @Override
    public Component label(int index) {
        List<T> list = this.values.get();
        if (index < 0 || index >= list.size() || !list.contains(this.option.get()) && index == 0) {
            return OptionText.value(this.option, this.option.get());
        }
        return OptionText.value(this.option, list.get(index));
    }
}

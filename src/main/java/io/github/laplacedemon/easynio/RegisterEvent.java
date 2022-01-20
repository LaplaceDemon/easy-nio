package io.github.laplacedemon.easynio;

import java.nio.channels.SelectableChannel;

public class RegisterEvent {
    private SelectableChannel selectableChannel;
    private int ops;
    private SelectionKeyConsumer selectionKeyConsumer;

    public RegisterEvent(SelectableChannel selectableChannel, int ops, SelectionKeyConsumer selectionKeyConsumer) {
        this.selectableChannel = selectableChannel;
        this.ops = ops;
        this.selectionKeyConsumer = selectionKeyConsumer;
    }

    public SelectableChannel getSelectableChannel() {
        return selectableChannel;
    }

    public int getOps() {
        return ops;
    }

    public SelectionKeyConsumer getSelectionKeyConsumer() {
        return selectionKeyConsumer;
    }

}

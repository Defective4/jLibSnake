package io.github.defective4.jlibsnake.event;

public interface GameListener {
    boolean collectedPoint(boolean consumePoint);

    void crashedIntoTail();
}

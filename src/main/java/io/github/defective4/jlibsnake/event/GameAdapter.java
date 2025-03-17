package io.github.defective4.jlibsnake.event;

public abstract class GameAdapter implements GameListener {

    @Override
    public boolean collectedPoint(boolean consumePoint) {
        return false;
    }

    @Override
    public void crashedIntoTail() {}

}

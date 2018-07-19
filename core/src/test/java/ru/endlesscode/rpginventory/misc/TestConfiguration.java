package ru.endlesscode.rpginventory.misc;

import ru.endlesscode.rpginventory.configuration.Configurable;

public class TestConfiguration extends Configurable {

    private String aString = "Lorem ipsum dolor sit amet.";
    private int anInt = 5;

    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getNodeName() {
        return "TestNode";
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }
}

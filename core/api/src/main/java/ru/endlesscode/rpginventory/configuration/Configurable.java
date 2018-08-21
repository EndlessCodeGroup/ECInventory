package ru.endlesscode.rpginventory.configuration;

public abstract class Configurable {


    public abstract String getHeader();

    public abstract String getNodeName();

    public String fileName() {
        String string = this.getClass().getSimpleName();
        //Change the first letter to lowercase
        char c[] = string.toCharArray();
        c[0] += 32;
        string = new String(c);
        return string;
    }


    public boolean hasHeader() {
        return this.getHeader() != null && !this.getHeader().isEmpty();
    }
}

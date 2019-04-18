package ru.endlesscode.rpginventory.configuration;

public interface Configurable {

    String getHeader();

    String getNodeName();

    default String fileName() {
        String string = this.getClass().getSimpleName();
        //Change the first letter to lowercase
        char[] c = string.toCharArray();
        c[0] += 32;
        string = new String(c);
        return string;
    }


    default boolean hasHeader() {
        return this.getHeader() != null && !this.getHeader().isEmpty();
    }
}

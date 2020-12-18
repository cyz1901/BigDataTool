package pers.cyz.bigdatatool.gui;
import org.kordamp.ikonli.Ikon;

public enum MyIcon implements Ikon {
    HADOOP("gldc-hadoop29", '\ue70a'),
    CLOSE("gldc-close", '\ue009');

    public static MyIcon findByDescription(String description) {
        for (MyIcon font : values()) {
            if (font.getDescription().equals(description)) {
                return font;
            }
        }
        throw new IllegalArgumentException("Icon description '" + description + "' is invalid!");
    }

    private String description;
    private int code;

    MyIcon(String description, int code) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getCode() {
        return code;
    }
}



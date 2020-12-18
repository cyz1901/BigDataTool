package pers.cyz.bigdatatool.gui;

import org.kordamp.ikonli.AbstractIkonHandler;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.IkonHandler;
import org.kordamp.jipsy.ServiceProviderFor;

import java.io.InputStream;
import java.net.URL;

@ServiceProviderFor(IkonHandler.class)
public class MyIkonliHandler extends AbstractIkonHandler {
    private static final String FONT_RESOURCE = "/static/iconfont.ttf";
    @Override
    public boolean supports(String description) {
        return description != null && description.startsWith("gldc-");
    }

    @Override
    public Ikon resolve(String description) {
        return MyIcon.findByDescription(description);
    }

    @Override
    public URL getFontResource() {
        System.out.println(getClass().getResource("/static/iconfont.ttf"));
        return getClass().getResource(FONT_RESOURCE);
    }

    @Override
    public InputStream getFontResourceAsStream() {
        return getClass().getResourceAsStream(FONT_RESOURCE);
    }

    @Override
    public String getFontFamily() {
        return "iconfont";
    }
}

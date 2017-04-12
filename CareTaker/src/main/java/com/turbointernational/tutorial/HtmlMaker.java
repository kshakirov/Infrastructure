package com.turbointernational.tutorial;


import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

/**
 * Created by kshakirov on 2/6/17.
 */
public class HtmlMaker {
    public static void main(String[] args) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/forgotten_password.twig");
        JtwigModel model = JtwigModel.newModel()
                .with("email", "kshakirov@zoral.com.ua")
                .with("password", "1212121212")
                .with("server", "localhost.com");

        String html = template.render(model);
    }
}

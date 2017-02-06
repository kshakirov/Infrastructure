package com.turbointernational.tutorial;


import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

/**
 * Created by kshakirov on 2/6/17.
 */
public class HtmlMaker {
    public static void main(String[] args) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/example.twig");
        JtwigModel model = JtwigModel.newModel().with("var", "World");

        String html = template.render(model);
        System.out.println(html);
    }
}

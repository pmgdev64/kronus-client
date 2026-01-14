package vn.pmgteam.client.plugin.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientPlugin
{
    String name();
    String author() default "Unknown";
    String version() default "1.0";
}

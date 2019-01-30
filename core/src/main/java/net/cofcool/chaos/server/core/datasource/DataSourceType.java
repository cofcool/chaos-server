package net.cofcool.chaos.server.core.datasource;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.cofcool.chaos.server.core.annotation.Scanned;

/**
 * 动态数据源数据切换
 **/
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
@Scanned
public @interface DataSourceType {

    String value();

}

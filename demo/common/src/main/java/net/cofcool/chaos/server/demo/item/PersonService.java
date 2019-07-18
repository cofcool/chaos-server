package net.cofcool.chaos.server.demo.item;

import net.cofcool.chaos.server.common.core.BaseComponent;
import net.cofcool.chaos.server.common.core.DataAccess;

/**
 * @author CofCool
 */
@BaseComponent
public interface PersonService<T extends Person> extends DataAccess<T> {

}

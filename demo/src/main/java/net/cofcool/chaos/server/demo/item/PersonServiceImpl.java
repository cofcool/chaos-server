package net.cofcool.chaos.server.demo.item;

import net.cofcool.chaos.server.data.jpa.support.SimpleJpaService;
import org.springframework.stereotype.Service;

/**
 * @author CofCool
 */
@Service
public class PersonServiceImpl extends SimpleJpaService<Person, Long, PersonRepository> implements PersonService {

    @Override
    protected Long getEntityId(Person entity) {
        return entity.getId();
    }

}

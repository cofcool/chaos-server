package net.cofcool.chaos.server.demo.item;

import javax.annotation.Resource;
import net.cofcool.chaos.server.data.jpa.support.SimpleJpaService;
import org.springframework.stereotype.Service;

/**
 * @author CofCool
 */
@Service
public class PersonServiceImpl extends SimpleJpaService<Person, Long> implements PersonService {

    @Resource
    private PersonDao personDao;

    @Override
    protected Long getEntityId(Person entity) {
        return entity.getId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        setJpaRepository(personDao);
    }
}

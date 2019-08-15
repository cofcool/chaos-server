package net.cofcool.chaos.server.demo.item;

import java.util.List;
import net.cofcool.chaos.server.common.core.ExecuteResult;
import net.cofcool.chaos.server.common.core.Page;
import net.cofcool.chaos.server.common.core.Result.ResultState;
import net.cofcool.chaos.server.common.core.SimpleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl extends SimpleService<Person> implements PersonService<Person>,
    InitializingBean {

    @Override
    protected Object queryWithPage(Page<Person> condition, Person entity) {
        return Page.empty();
    }

    @Override
    public ExecuteResult<Person> add(Person entity) {
        return null;
    }

    @Override
    public ResultState delete(Person entity) {
        return null;
    }

    @Override
    public ExecuteResult<Person> update(Person entity) {
        return null;
    }

    @Override
    public ExecuteResult<List<Person>> queryAll(Person entity) {
        return null;
    }

    @Override
    public ExecuteResult<Person> queryById(Person entity) {
        return null;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        setPageProcessor((condition, pageSomething) -> (Page<Person>) pageSomething);
    }
}

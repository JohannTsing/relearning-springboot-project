package com.johann.binarytea.transaction;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
@Service
public class MixService {

    private DemoRepository demoRepository;

    public MixService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    //@Transactional //默认 Propagation.REQUIRED; 默认 rollbackFor = RuntimeException.class
    public void doSomething() {
        demoRepository.insertRecordRequired();
        demoRepository.insertRecordRequiresNew();

        // example 1: 【Names: two】
        // 嵌套子事务抛出异常，子事务回滚，父事务未catch这个异常，所以父事务也回滚。Requires_New开启的是一个新事务，不受其他事务影响
        // demoRepository.insertRecordNested();

        // example 2: 【Names: one,two】
        // 嵌套子事务抛出异常，子事务回滚，父事务catch这个异常，所以父事务不回滚。Requires_New开启的是一个新事务，不受其他事务影响
        try {
            demoRepository.insertRecordNested();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // example 3: Names: two
        // 当前事务抛出异常，调用的Requires方法与当前事务是同一个事务，所以需要回滚。Requires_New开启的是一个新事务，不受其他事务影响
        // throw new RuntimeException();
    }
}

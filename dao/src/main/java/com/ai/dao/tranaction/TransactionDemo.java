package com.ai.dao.tranaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransactionDemo {

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // require
    // require new  第二个事务的异常没有处理第一个事务回滚，如果处理了则不回滚   就是第二个事务影响第一个事务，但是第一个事务不影响第二个事务
    // nested   外部事务影响内部事务  内部事务不影响外部事务

}

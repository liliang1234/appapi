/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pzj.settlement.base.entity.AccountBalance;
import com.pzj.settlement.base.entity.vo.AccountBalanceVo;
import com.pzj.settlement.base.service.IAccountService;

/**
 * 
 * @author fanggang
 * @version $Id: AccountApi.java, v 0.1 2016年7月29日 下午4:11:18 fanggang Exp $
 */
@Component
public class AccountApi {

    @Autowired
    private IAccountService accountService;

    public List<AccountBalance> queryAccountBalance(AccountBalanceVo accountBalanceVo) throws Exception {
        return accountService.queryAccountBalance(accountBalanceVo);
    }
}

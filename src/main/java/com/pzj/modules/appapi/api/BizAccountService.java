package com.pzj.modules.appapi.api;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.pzj.customer.entity.Customer;
import com.pzj.modules.appapi.entity.CodeHandle;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.settlement.base.common.enums.AccountType;
import com.pzj.settlement.base.common.enums.ResellerAccountDict;
import com.pzj.settlement.base.entity.AccountBalance;
import com.pzj.settlement.base.entity.vo.AccountBalanceVo;
import com.pzj.settlement.base.service.IAccountService;

/**
 *
 * 资金账户功能
 *
 * @author wangkai
 * @date 2015年11月6日 下午5:21:07
 */
@Component
public class BizAccountService {

    @Autowired
    private IAccountService accountService;

    private Map<String, Object> setAccountResult(Double avaBalance, Double frzBalance, Double frzRebateAmount, Double resellerRebateAmount) {
        Map<String, Object> jsonObject = Maps.newHashMap();
        DecimalFormat format = new DecimalFormat("0.00");
        jsonObject.put("avaBalance", format.format(avaBalance == null ? 0l : avaBalance));
        jsonObject.put("frzBalance", format.format(frzBalance == null ? 0l : frzBalance));
        jsonObject.put("frzRebateAmount", format.format(frzRebateAmount == null ? 0l : frzRebateAmount));
        jsonObject.put("resellerRebateAmount", format.format(resellerRebateAmount == null ? 0l : resellerRebateAmount));
        return jsonObject;
    }

    public JsonEntity getAccountNew(JSONObject data, Customer customer, JsonEntity json) {
        try {
            Double finalPeriod = null;
            List<AccountBalance> accountBalances = getAccountNew(customer, null);
            if (CollectionUtils.isNotEmpty(accountBalances)) {
                AccountBalance accountBalance = accountBalances.get(0);
                finalPeriod = accountBalance.getFinalPeriod();
            }
            Map<String, Object> jsonObject = setAccountResult(finalPeriod, null, null, finalPeriod);
            json.setCode(CodeHandle.SUCCESS.getCode());
            json.setMessage(CodeHandle.SUCCESS.getMessage());
            json.setResponseBody(jsonObject);
        } catch (Exception e) {
            json.setCode(CodeHandle.FAILURE.getCode());
            json.setMessage(CodeHandle.FAILURE.getMessage());
            e.printStackTrace();
        }
        return json;
    }

    public List<AccountBalance> getAccountNew(Customer customer, List<Long> accountDictIds) {
        AccountBalanceVo accountBalanceVo = new AccountBalanceVo();
        accountBalanceVo.setCustomerId(customer.getId());
        accountBalanceVo.setAccountType(AccountType.DISTRIBUTOR.key);

        if (accountDictIds != null)
            accountBalanceVo.setAccountDictIds(accountDictIds);
        else
            accountBalanceVo.setAccountDictId(ResellerAccountDict.OTHER_AVAILABLE.getId());

        try {
            List<AccountBalance> accountBalances = accountService.queryAccountBalance(accountBalanceVo);
            return accountBalances;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 绑定银行卡、支付宝
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity bindbank(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 获取已绑定的银行卡、支付宝信息
     *
     * @param data
     * @param customer
     * @param json
     * @return JsonEntity 返回类型
     * @throws
     */
    public JsonEntity getBank(JSONObject data, Customer customer, JsonEntity json) {
        // TODO Auto-generated method stub
        return null;
    }

}

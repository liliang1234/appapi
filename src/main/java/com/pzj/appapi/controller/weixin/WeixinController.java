package com.pzj.appapi.controller.weixin;

import com.pzj.appapi.controller.common.model.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.sword.wechat4j.common.Config;
import org.sword.wechat4j.token.TokenProxy;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author hesimin
 * @date 2016/10/26
 */
@RestController
@RequestMapping("/appapi/weixin")
public class WeixinController {
    private Logger logger = LoggerFactory.getLogger(WeixinController.class);

    /**
     * 微信url签名
     * @param url
     * @return
     */
    @RequestMapping(value = "urlSign", method = RequestMethod.POST)
    public CommonResult urlSign(String url) {
        Map<String, String> map = new HashMap<>();
        String nonce_str = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        // 注意这里参数名必须全部小写，且必须有序
        String string1 = "jsapi_ticket=" + TokenProxy.jsApiTicket() + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        String signature = sha1(string1);
        map.put("url", url);
        map.put("nonceStr", nonce_str);
        map.put("timestamp", timestamp);
        map.put("signature", signature);
        map.put("appId", Config.instance().getAppid());

        return new CommonResult().setSuccess(true).setData(map);
    }

    private String sha1(String str) {
        MessageDigest crypt = null;
        try {
            crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("", e);
        }
        return byteToHex(crypt.digest());
    }

    /**
     * 将字节数组转换为16进制字符串
     *
     * @author lixuejian 2015年7月21日15:32:16
     * @param hash
     * @return
     */
    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}

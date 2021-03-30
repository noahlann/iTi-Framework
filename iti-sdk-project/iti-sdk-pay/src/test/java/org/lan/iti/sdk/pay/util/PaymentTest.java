package org.lan.iti.sdk.pay.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lan.iti.sdk.pay.Payment;
import org.lan.iti.sdk.pay.model.IResponse;


/**
 * @author I'm
 * @since 2021/3/26
 * description
 */
public class PaymentTest {

    @Test
    public void create() {

        final String testPrimaryKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkFvZxHCeN1FtZxVKi6i3V/W+e72dhQ9vbe1kuPGmPX2mhv/EataS0LyllQ4Qey1qojyNFWMt1nDxwqwQohcuBRhj/X7guTZSidSGgNhDfaDr6mfrdXBrebC/+bI1CMcH/XBDxXkKXIQ+IRycRM7XxefMKPgROybsHqtD5lfEc+TwRmKlSYXJQZQv+ZPIqewjCVQMcjtjYaxMnuVWWrshi1F1TqCKySeASk+3F4FjYKhRziNM/nVbjppTIy5gPWBu6D05OBS+3YSpxBJJC5M7PUG4LQIsV9OK2eDjsqAqD69JhYCQv0gB4lpa2Ibf6yHwr9TStW4+7NS5le+xue/yBAgMBAAECggEAYzOyt/1CeH9q3uHprK6RGW8HvXmU8Xu5/n4bE1XYp6ISVYSaOgCY24orp5ni1U8xtvgaZcFh9++FpsPtVqZUuwnq8PEog23Jr1HLC3XES5xTE9BlltApOlEDzvhHij5Uv5IkeBf5gC6vmj8SAldV5vBIG+gUaKGTHfaKMmoRA+c7J2sc8z4ev5U5/ihQjhKpgswAZ4Z75AAQ8NopUsJsacCTAqqFEFrWFim4eJRaJkBqFpkHr0LgxG+PTSmYpJkMykFiTsfOlyL47/GrVtOyzF9rS7I30WFGfqV2NwquRBMQhQeoLHQX9NrjOvjxBZs6N27UmubzOlIqlwzOEVV2wQKBgQDvvoa2IUQ54SgNk09V8ZzpWtOumJ6B9y5w/XOmqRQK2y48PNADFuoQo/T6rBmNhaJU9seN8ckqxp3WFgeO4lmpFrDCT1zkabsA+F13mtVlwM5bDdEKq/4xd0hhE8ZmNvTU2hxqp6QbVRC7pqRzc+nlqa1TVI5WV8CiidOJedI5mQKBgQCvNzocS7vh2YGQIr4bmJvOsbp3JOUeuOStYDkEp/eRTTuuo90HfpVt2Srr4Ab8+rtHzlpaxdRTw46cNCIKmJGbJeXUB6ND0lObRFAdaYc8cqzMQ8TiISoa6Fx9t2TB4bZ/P+sqetXLLzMFtkCXJJYUzVcpKkIF1tFbGauE/G67KQKBgQC0kesCqKGrenNhbi9WCwnlFLzVByxztQnsnmkKANUQrC7xvdfqS8r1x5fPaepBFoLCvbBlr9OVfU0KH0OIEeH4Ihf18jKD80KfI3G6kQrjfcmu+QQWpp5pAW+pdmx7Ni9HVhQLHMLUt0hIHeZE8uwbR6Y9WjVuwN9dTLPgGroFGQKBgCtz+dBco9qt82jIhPcZEMLg5sp3du74kZXBwEI8WbRKQBvefYlHRk24xuE6bOE+WUXEkjnix/aCEBMDBtIz5iBg9K9xuXavaZafCXNR4X0HP31k7SXbbbLAn3d6DN3cWUMTmgGt9ult6ixnd3tEUogUKKKH3VnPXZpHMndHlzqJAoGBAOEgvLUER38qXnfh7oxZYSmeU/pwn8YgAIjReNLET7welRcf79FmtCf7H/LzlZM86hi64R+YWK74+rw0g/jT7hqiFlc+kMpflXW9qVNvZxH7z2ZYlWGCNFoOeVYAjQs+X3Of7Xo4euJakQeW3j8skJMw5f0x6PRhMjlFQ2gLhz9L";

        IResponse response = Payment.charge(
                Payment.chargeConfigurer()
                        .appId("1282962695032754178")
                        .amount(0.01f)
                        .body("测试描述")
                        .outOrderNo(RandomUtil.randomString(32))
                        .gatewayHost("http://192.168.200.54:26003" + "/test/charge/create")
                        .privateKey(testPrimaryKey)
                        .subject("测试商品")
        );

//        Payment.createCharge(new HashMap<>()).execute();
//
//        Payment.refund().appId("sf").execute();
//
//        Payment.refund(new HashMap<>()).execute();
//
//        Payment.fund().appId("sf").execute();
//
//        Payment.fund(new HashMap<>()).execute();
    }
}

package com.github.yafeiwang1240;

import com.alibaba.fastjson.JSONObject;
import com.github.yafeiwang1240.httpclient.check.HealthChecker;
import com.github.yafeiwang1240.httpclient.check.HttpHealthChecker;
import com.github.yafeiwang1240.httpclient.check.InstanceInfo;
import com.github.yafeiwang1240.httpclient.check.TcpHealthChecker;
import com.github.yafeiwang1240.httpclient.simple.HttpClient;
import com.github.yafeiwang1240.httpclient.simple.HttpXMLClient;
import org.apache.http.client.fluent.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello World!
 */
public class App {

    public static void main(String[] args) throws Exception {
        httpCheck("10.110.206.5", 8992);
    }

    public static void httpCheck(String host, int port) {
        HealthChecker healthChecker = new HttpHealthChecker("/monitor/http.do22");
        boolean check = healthChecker.check(new InstanceInfo(host, port));
        System.out.println(check);
    }

    /**
     * 测试tcp接口
     * @param host
     * @param port
     */
    public static void tcpCheck(String host, int port) {
        HealthChecker tcpHealthChecker = new TcpHealthChecker();
        boolean check = tcpHealthChecker.check(new InstanceInfo(host, port));
        System.out.println(check);
    }

    public static void Check(String host, int port) {
        TcpHealthChecker tcpHealthChecker = new TcpHealthChecker();
        boolean check = tcpHealthChecker.check(new InstanceInfo(host, port));
        System.out.println(check);
    }

    public static void test3() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Long userId = 23464L;
        String task_name = "leo_dw_fact_mail_examine";
        String params = "{\"id\":183, \"name\":\"tab_820_mail_05\",\"name\":\"tab_820_mail_05\", \"sql\":\"insert overwrite table wyf_to select *from test.wyf_from;\"}";
        String comment = "来自任务邮件任务审批";
        Map<String, Object> send = new HashMap<>();
        send.put("userId", userId.intValue());
        send.put("task_name", task_name);
        send.put("params", params);
        send.put("comment", comment);
        Response response = HttpClient.getInstance().execute(HttpClient.EnumHttpMethod.POST, "http://xxx.com/approvalTask/submit.json",
                send, headers, 100000);
        System.out.println(response.returnContent().asString());
    }

    public static void test2() throws Exception {
        Long userId = 23464L;
        String task_name = "leo_dw_fact_mail_examine";
        String params = "{\"id\":183, \"name\":\"tab_820_mail_05\",\"name\":\"tab_820_mail_05\", \"sql\":\"insert overwrite table wyf_to select *from test.wyf_from;\"}";
        String comment = "来自任务邮件任务审批";
        Map<String, Object> send = new HashMap<>();
        send.put("userId", userId.intValue());
        send.put("task_name", task_name);
        send.put("params", params);
        send.put("comment", comment);
        String rtn = HttpXMLClient.post("http://xxx.xxx/approvalTask/submit.json", send);
        System.out.println(rtn);
    }

    public static void test1() {
        System.out.println("Hello World!");
        HttpClient client = HttpClient.getInstance();
        for(int i = 0; i < 1; i++) {
            try {
                Response response = client.execute(HttpClient.EnumHttpMethod.GET, "http://open.iciba.com/dsapi/");
                Map<String, Object> map = JSONObject.parseObject(response.returnContent().asString(), Map.class);
                System.out.println(map.get("note"));
                System.out.println(map.get("content"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(Integer.class.hashCode());
        System.out.println(Integer.class.getName());
        System.out.println(int.class.hashCode());
        System.out.println(int.class.getName());

    }

    public static void test(Integer value) {
        System.out.println(value.getClass());
    }

    public static void test(int value) {
        System.out.println(char.class);
    }
}
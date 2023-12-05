package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        // 创建一个 ChromeDriver 的实例，会自动从环境变量中寻找浏览器驱动
//        WebDriver driver = new FirefoxDriver();
//        // 打开网页
//        driver.get("https://www.baidu.com");
//        //关闭driver进程
//        driver.quit();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        // 访问浏览器页面
        final String URL = "https://work.weixin.qq.com/wework_admin/loginpage_wx";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        ChromeDriver driver = new ChromeDriver(options);
        driver.get(URL);
    }
}
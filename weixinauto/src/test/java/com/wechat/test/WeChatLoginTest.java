package com.wechat.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.qameta.allure.Allure;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

@DisplayName("企业微信登录获取cookies")
public class WeChatLoginTest {

    private final static String URL = "https://work.weixin.qq.com/wework_admin/loginpage_wx";

    private final static Integer TIMEOUT = 30 , SLEEPTIME = 2;

    private final static String TEST_DATA_FILE_PATH = "src/test/resources/data/";

    private final static String ARGUMENT = "--remote-allow-origins=*";
    public static ChromeDriver chromeDriver;


    @AfterAll
    public static void quit() {
        chromeDriver.quit();
    }

    @DisplayName("获取Cookles并写入YAML文件中")
    public  void wirterCookies() throws IOException {

        ChromeOptions chromeOptions = new ChromeOptions();
        //允许任何来源的远程连接，这样可以避免一些跨域问题
        chromeOptions.addArguments(ARGUMENT);

        //初始化Chrome浏览器的驱动
        chromeDriver = new ChromeDriver(chromeOptions);
        Allure.step("打开微信扫描登录地址");
        chromeDriver.get(URL);

        //通过显示等待判断是否登录成功
        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(TIMEOUT), Duration.ofSeconds(SLEEPTIME));
        webDriverWait.until(ExpectedConditions.urlContains("/wework_admin/frame"));

        //获取cookies写入YAML文件
        Set<Cookie> cookies = chromeDriver.manage().getCookies();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.writeValue(new File(TEST_DATA_FILE_PATH+"cookies.yaml"), cookies);

    }

    @Test
    @DisplayName("加载cookies登录系统")
    public void loadCooKies()  {
        ChromeOptions chromeOptions = new ChromeOptions();
        //允许任何来源的远程连接，这样可以避免一些跨域问题
        chromeOptions.addArguments(ARGUMENT);

        //初始化Chrome浏览器的驱动
        chromeDriver = new ChromeDriver(chromeOptions);
        Allure.step("打开微信扫描登录地址");
        chromeDriver.get(URL);


        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        //读取cookies

        List<HashMap<String, Object>> loadCookies = null;
        try {
            loadCookies = objectMapper.readValue(new File("src/test/resources/data/cookies.yaml"), new TypeReference<List<HashMap<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadCookies.stream().forEach(
                cookie -> {
                    chromeDriver.manage().addCookie(new Cookie(cookie.get("name").toString(),cookie.get("value").toString()));
                }
        );

        //刷新页面
        chromeDriver.navigate().refresh();
        //通过显示等待判断是否登录成功
        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(TIMEOUT), Duration.ofSeconds(SLEEPTIME));
        webDriverWait.until(ExpectedConditions.urlContains("/wework_admin/frame"));

        Allure.step("检测是否登录成功");
        By memberTextItems = By.cssSelector(".frame_nav_item_title");
        List<String> memberTexts = chromeDriver.findElements(memberTextItems)
                .stream().map(e -> e.getText()).collect(Collectors.toList());
        assertThat(memberTexts, hasItems("首页"));
        assertThat(memberTexts, hasItems("通讯录"));

    }





}

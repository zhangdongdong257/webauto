package com.wechat.test;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class AddressBookTest {
    static WeChatLoginTest weChatLoginTest = new WeChatLoginTest();
    static ChromeDriver chromeDriver;

    static Integer TIMEOUT = 30,SLEEP_TIME = 2, SEARCH_ELEMENT_TIME = 5;


    @BeforeAll
    public static void login() {
        weChatLoginTest.loadCooKies();
        chromeDriver = WeChatLoginTest.chromeDriver;
    }

    @AfterAll
    public static void quit() {
        chromeDriver.quit();
    }

    @Test
    @DisplayName("添加成员")
    public void addMemberTest(){
        Allure.step("点击通讯录");
        By addressBootText = By.linkText("通讯录");
        chromeDriver.findElement(addressBootText).click();

        //等待通讯录页面加载完成
        WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(TIMEOUT), Duration.ofSeconds(SLEEP_TIME));
        webDriverWait.until(ExpectedConditions.urlContains("wework_admin/frame#contacts"));

        //查找添加按钮
        WebElement addMemberBtn = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".ww_operationBar .js_add_member")));
        Allure.step("点击添加按钮");
        addMemberBtn.click();

        //查找姓名输入框
        WebElement inputNameText = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#username")));
        Allure.step("点击姓名输入框，输入名称");
        String name = "张三";
        inputNameText.sendKeys(name);

        //输入账号和手机号
        Allure.step("通过id定位账号元素");
        By accountInput = By.id("memberAdd_acctid");
        Allure.step("通过xpath定位手机号元素");
        By phoneInput = By.xpath("//*[@id=\"memberAdd_phone\"]");
        Allure.step("通过xpath定位保存元素");
        By saveBtn = By.xpath("//a[@class='qui_btn ww_btn js_btn_save']");
        //获取当前时间戳
        String account = Long.toString(System.currentTimeMillis());
        //截取11位手机号
        String phone = account.substring(0, 11);

        //给账号赋值
        chromeDriver.findElement(accountInput).sendKeys(account);
        //给手机号赋值
        chromeDriver.findElement(phoneInput).sendKeys(phone);
        //点击保存
        chromeDriver.findElement(saveBtn).click();

        //等待用户列表加载完成
        WebDriverWait webDriverWait1 = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME));
        webDriverWait1.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".member_colRight_memberTable_td")));

        Allure.step("断言添加成员信息是否包含在成员列表中");
        By memberTextItems = By.cssSelector(".member_colRight_memberTable_td");
        List<String> memberTexts = chromeDriver.findElements(memberTextItems)
                .stream().map(e -> e.getText()).collect(Collectors.toList());
        assertThat(memberTexts, hasItems(name));
        assertThat(memberTexts, hasItems(phone));

        Allure.step("删除用户，确保下次用例可以继续执行");
        WebElement  nameText = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[text()='张三']")));
        nameText.click();

        WebElement  deleteBnt = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@class='qui_btn ww_btn js_del_member']")));
        deleteBnt.click();

        WebElement  accpetBnt = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@class='qui_btn ww_btn ww_btn_Blue']")));
        accpetBnt.click();

        Allure.step("断言添加成员信息是不否包含在成员列表中");
        WebDriverWait  webDriverWait2 = new WebDriverWait(chromeDriver, Duration.ofSeconds(SEARCH_ELEMENT_TIME));
        webDriverWait2.until(ExpectedConditions.elementToBeClickable(memberTextItems));

        List<String> memberTexts2 = chromeDriver.findElements(memberTextItems)
                .stream().map(e -> e.getText()).collect(Collectors.toList());
        assertThat(memberTexts2, not(name));
    }

}

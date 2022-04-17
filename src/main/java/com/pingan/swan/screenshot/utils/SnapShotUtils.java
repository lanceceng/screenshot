package com.pingan.swan.screenshot.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: 快照工具类
 * @author: WangZhiJun
 * @create: 2019-10-25 14:36
 **/
public class SnapShotUtils {
    private static Logger logger = LoggerFactory.getLogger(SnapShotUtils.class);
    private static final String EQUAL = "=";


    public static String getDriverPath(String path) {
        if (PlatformUtils.isWindows()) {
            logger.info("当前系统：Windows");
            return path + "phantomjs/bin/phantomjs.exe";
        }else {
            if (PlatformUtils.isLinux()) {
                logger.info("当前系统：Linux");
                if (PlatformUtils.is64OsArch()) {
                    logger.info("当前系统：64位");
                    return path + "phantomjs/linux-x86_64/bin/phantomjs";
                } else {
                    return path + "phantomjs/linux-i686/bin/phantomjs";
                }
            } else {
                logger.info("当前系统：Mac");
                return path + "phantomjs/macosx/bin/phantomjs";
            }
        }
    }

    public static String getBase64(String url, String path) {
        //设置必要参数
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //设置请求头
//        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "token", "asdhjklasjdklasjdklasjkdlas");
//        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "token1", "asdhjklasjdklasjdklasjkdlas");
        //js支持
        dcaps.setJavascriptEnabled(true);
        //驱动支持（第二参数表明的是你的phantomjs引擎所在的路径）
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                getDriverPath(path));
        //创建无界面浏览器对象
        PhantomJSDriver driver = new PhantomJSDriver(dcaps);

        //设置隐性等待（作用于全局）
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        //打开页面
        driver.get(url);
        // cookies
        /*Set<Cookie> set = driver.manage().getCookies();
        for (Cookie cookie : set) {
            driver.manage().addCookie(cookie);
        }*/
        String base64  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
        logger.info("成功获取URL快照，快照大小："+imageSize(base64));
        // 1
        /*String desImage = "E:\\tmp\\" + UUID.randomUUID().toString() + ".png";
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtil.copyFile(srcFile, new File(desImage));*/
        // 2
        return base64;
    }

    /**
     * 通过图片base64流判断图片等于多少字节
     * image 图片流
     */
    private static Integer imageSize(String image) {
        // 1.需要计算文件流大小，首先把头部的data:image/png;base64,（注意有逗号）去掉。
        String str = image.substring(22);
        //2.找到等号，把等号也去掉
        int equalIndex = str.indexOf("=");
        if (str.indexOf(EQUAL) > 0) {
            str = str.substring(0, equalIndex);
        }
        //3.原来的字符流大小，单位为字节
        int strLength = str.length();
        //4.计算后得到的文件流大小，单位为字节
        return (strLength - (strLength / 8) * 2);
    }

    public static void main(String[] args) {
        System.out.println(getBase64("https://www.baidu.com","F:/"));
    }
}

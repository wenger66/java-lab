# 初探Protractor

## Protractor用来解决什么问题
Protractor可以模拟用户在浏览器中操作来做自动化测试。 

angular应用可以使用Protractor做自动化测试，Protractor的有些api是专门为angular提供的；非angular应用，也可以用Protractor做自动化测试，只需要避免调用那些专属angular的api就可以了。

我们团队准备使用Protractor来应对前端的自动化测试，为以后开展TDD、BDD的研发活动做准备

## Protractor基本原理

基本原理：JavaScript->Selenium-Server->Chromedriver->Chrome浏览器

1. Protractor首先使用Jasmine 测试框架编写JavaScript

2. Protractor本质是基于Selenium工具/框架来做自动化

【Jasmine】是一种面向BDD的JavaScript测试框架

【Selenium】是一种支持多种编程语言的浏览器自动化测试框架

比如使用Java语言来做自动化测试，原理就是：Java（selenium-java.jar）->Chromedriver->Chrome浏览器

比如使用Python语言来做自动化测试，原理就是：Python（selenium-3.7.0-py2.py3-none-any.whl）->geckodriver->Firefox浏览器

简单了解下即可，不用深入

## Protractor环境

npm下载安装protractor

    npm install -g protractor
    
亲测可以，公司大部分项目都使用制品库作为npm库，但有的本地环境需要把proxy的npm配置删除才能下载

    npm config rm proxy
    
直接运行protractor

插一句，通过angular-cli脚手架搭建的angular环境，与src平级有个e2e（端到端测试）的文件夹，根目录下有protrator.conf.js文件，说明已经有protrator的配置，测试的spec文件指向正是e2e目录

    D:\workspace\vCN-EM-TOPO\topo-iui>protractor protractor.conf.js
    [17:12:02] I/launcher - Running 1 instances of WebDriver
    [17:12:02] I/direct - Using ChromeDriver directly...
    [17:12:02] E/direct - Error code: 135
    [17:12:02] E/direct - Error message: Could not find update-config.json. Run 'webdriver-manager update' to download binaries.
    [17:12:02] E/direct - Error: Could not find update-config.json. Run 'webdriver-manager update' to download binaries.
    at IError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:6:1)
    at ProtractorError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:11:9)
    at BrowserError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:52:9)
    at Direct.getNewDriver (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\driverProviders\direct.js:63:31)
    at Runner.createBrowser (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\runner.js:195:43)
    at q.then.then (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\runner.js:339:29)
    at _fulfilled (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:834:54)
    at self.promiseDispatch.done (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:863:30)
    at Promise.promise.promiseDispatch (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:796:13)
    at C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:556:49
    [17:12:02] E/launcher - Process exited with error code 135
    
报错，看意思是要先运行webdriver-manager update，于是运行webdriver-manager update

    D:\workspace\vCN-EM-TOPO\topo-iui>webdriver-manager update
    events.js:160
    throw er; // Unhandled 'error' event
    ^
    Error: getaddrinfo EAI_AGAIN api.github.com:443
    at Object.exports._errnoException (util.js:1018:11)
    at errnoException (dns.js:33:15)
    at GetAddrInfoReqWrap.onlookup [as oncomplete] (dns.js:76:26)
    
仍然报错，查看webdriver-manager的帮助，update应该是安装的含义，再从github、443感觉webdriver-manager需要下载一些组件

插一句：这个帮助非常非常非常重要

    D:\workspace\vCN-EM-TOPO\topo-iui>webdriver-manager
    Usage: webdriver-manager <command> [options]
     
    Commands:
      clean      removes all downloaded driver files from the out_dir
      start      start up the selenium server
      shutdown   shut down the selenium server
      status     list the current available drivers
      update     install or update selected binaries
      version    get the current version
     
    Options:
      --out_dir                   Location to output/expect
                               [default: C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium]
      --seleniumPort              Optional port for the selenium standalone server
                               [default: 4444]
      --appium-port               Optional port for the appium server
                               [default: 4723]
      --avd-port                  Optional port for android virtual devices.  See mobile.md for details
                               [default: 5554]
      --versions.standalone       Optional seleniuim standalone server version (use 'latest' to get the most recent version)
                               [default: latest]
      --versions.chrome           Optional chrome driver version (use 'latest' to get the most recent version)
                               [default: latest]
      --versions.gecko            Optional gecko driver version
                               [default: latest]
      --versions.android          Optional android sdk version
                               [default: 24.4.1]
      --versions.appium           Optional appium version
                               [default: 1.6.0]
      --chrome_logs               File path to chrome logs
      --logging                   File path to logging properties file
      --android                   Update/use the android sdk
      --avds                      Android virtual devices to emulate.  Use "all" for emulating all possible devices, and "none" for no devices                         [default: all]
      --avd-use-snapshots         Rather than booting a new AVD every time, save/load snapshots of the last time it was used
                               [default: true]
      --started-signifier         A string to be outputted once the selenium server is up and running.  Useful if you are writing a script which uses webdriver-manager.
      --signal-via-ipc            If you are using --started-signifier, this flag will emit the signal string using process.send(), rather than writing it to stdout
      --quiet                     Minimal console output
      --detach                    Once the selenium server is up and running, return control to the parent process and continue running the
    server in the background.
      --versions.ie               Optional internet explorer driver version (use 'latest' to get the most recent version)
                               [default: latest]
      --ie64                      Update: install or update 64-bit IE driver. Start: use installed x64 IE driver.
      --edge                      Use installed Microsoft Edge driver
                               [default: C:\Program Files (x86)\Microsoft Web Driver\MicrosoftWebDriver.exe]
      --already-off-error         Normally if you try to shut down a selenium which is not running, you will get a warning.  This turns it into an error
      --verbose                   Extra console output
      --ignore_ssl                Ignore SSL certificates
      --proxy                     Proxy to use for the install or update command
      --alternate_cdn             Alternate CDN to binaries
      --standalone                Install or update selenium standalone
                               [default: true]
      --chrome                    Install or update chromedriver
                               [default: true]
      --gecko                     Install or update geckodriver
                               [default: true]
      --android-api-levels        Which versions of the android API you want to emulate
                               [default: 24]
      --android-archs             Which architectures you want to use in android emulation.  By default it will try to match os.arch()
                               [default: x86_64]
      --android-platorms          Which platforms you want to use in android emulation
                               [default: google_apis]
      --android-accept-licenses   Automatically accept android licenses
      --ie                        Install or update 32-bit ie driver
      --ie32                      Install or update 32-bit ie driver
      
查看protrator的conf.json，看出些端倪

    {
        "webdriverVersions": {
        "selenium": "2.53.1",
        "chromedriver": "2.27",
        "geckodriver": "v0.13.0",
        "iedriver": "2.53.1",
        "androidsdk": "24.4.1",
        "appium": "1.6.0"
        },
        "cdnUrls": {
        "selenium": "https://selenium-release.storage.googleapis.com/",
        "chromedriver": "https://chromedriver.storage.googleapis.com/",
        "geckodriver": "https://github.com/mozilla/geckodriver/releases/download/",
        "iedriver": "https://selenium-release.storage.googleapis.com/",
        "androidsdk": "http://dl.google.com/android/"
        }
    }
    
结合基本原理，感觉protrator是要下载selenium和chromedriver，下载的位置应该是

    "selenium": "https://selenium-release.storage.googleapis.com/",
    "chromedriver": "https://chromedriver.storage.googleapis.com/",
    
根据经验，下载路径应该是：

    http://chromedriver.storage.googleapis.com/index.html
    http://selenium-release.storage.googleapis.com/index.html
    
手工下载selenium和chromedriver文件，结合webdriver-manager的帮助，out_dir应该是下载放置的路径，所以将下载的文件，放到out_dir的目录下

    --out_dir                   Location to output/expect
                               [default: C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium]
                               
运行protrator，仍然报错，有点进步，报缺少update-config.json，从经验上理解update-config.json就是下载文件后，生成的一个当前安装或升级的配置文件

    D:\workspace\vCN-EM-TOPO\topo-iui>protractor protractor.conf.js
    [14:45:09] I/launcher - Running 1 instances of WebDriver
    [14:45:09] I/direct - Using ChromeDriver directly...
    [14:45:09] E/direct - Error code: 135
    [14:45:10] E/direct - Error message: Could not find update-config.json. Run 'webdriver-manager update' to download binaries.
    [14:45:10] E/direct - Error: Could not find update-config.json. Run 'webdriver-manager update' to download binaries.
    at IError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:6:1)
    at ProtractorError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:11:9)
    at BrowserError (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\exitCodes.js:52:9)
    at Direct.getNewDriver (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\driverProviders\direct.js:63:31)
    at Runner.createBrowser (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\runner.js:195:43)
    at q.then.then (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\built\runner.js:339:29)
    at _fulfilled (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:834:54)
    at self.promiseDispatch.done (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:863:30)
    at Promise.promise.promiseDispatch (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\q\q.js:796:13)
    
忽然想到，既然可以通过公司的网络下载到webdriver-manager所需要的包，是不是应该直接解决github的443问题，直接下载

再次结合webdriver-manager的帮助

    --proxy                     Proxy to use for the install or update command
    
尝试使用代理安装webdriver-manager，仍然报错，但有部分文件能够下载，感觉一步步接近真相

    D:\workspace\vCN-EM-TOPO\topo-iui>webdriver-manager update --proxy http://10048570:xxxxxx@proxynj.zte.com.cn:80
    [14:48:37] E/downloader - undefined
    [14:48:37] I/update - geckodriver: file exists C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium\geckodriver-v0.20.1.zip
    [14:48:37] I/update - geckodriver: unzipping geckodriver-v0.20.1.zip
    (node:9072) UnhandledPromiseRejectionWarning: Unhandled promise rejection (rejection id: 1): Invalid filename
    [14:48:39] I/update - chromedriver: unzipping chromedriver_2.38.zip
    C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\built\lib\files\downloader.js:53
    error.msg = 'Error: corrupt download for ' + fileName +
    ^
    TypeError: Cannot set property 'msg' of null
    at fs.stat (C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\built\lib\files\downloader.js:53:47)
    at FSReqWrap.oncomplete (fs.js:123:15)
    
我们只关注selenium和chromedriver，geckodriver（firefox的布局引擎）暂时不关注，再次结合webdriver-manager的帮助，尝试不下载geckodriver

    D:\workspace\vCN-EM-TOPO\topo-iui>webdriver-manager update --proxy http://10048570:xxxxxx@proxynj.zte.com.cn:80 --gecko false
    [15:00:25] I/update - chromedriver: file exists C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium\chromedriver_2.38.zip
    [15:00:25] I/update - chromedriver: unzipping chromedriver_2.38.zip
    [15:00:26] I/update - chromedriver: chromedriver_2.38.exe up to date
    [15:02:26] E/downloader - Expected response code 200, received: 502
    [15:02:26] I/update - selenium standalone: file exists C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium\selenium-server-standalone-3.12.0.jar
    [15:02:26] I/update - selenium standalone: selenium-server-standalone-3.12.0.jar up to date
    [15:02:27] E/downloader - Expected response code 200, received: 502
    
这时想印证下关于update-config.json的猜测，全中
![protractor](./protractor-1.png)

高高兴兴的第3次去启动protractor，结果仍然报错

    D:\workspace\vCN-EM-TOPO\topo-iui>protractor protractor.conf.js
    [15:02:56] I/launcher - Running 1 instances of WebDriver
    [15:02:56] I/direct - Using ChromeDriver directly...
    [15:03:05] E/launcher - session not created exception: Chrome version must be >= 65.0.3325.0
    (Driver info: chromedriver=2.38.552522 (437e6fbedfa8762dec75e2c5b3ddb86763dc9dcb),platform=Windows NT 6.1.7601 SP1 x86_64)
    [15:03:05] E/launcher - SessionNotCreatedError: session not created exception: Chrome version must be >= 65.0.3325.0
    (Driver info: chromedriver=2.38.552522 (437e6fbedfa8762dec75e2c5b3ddb86763dc9dcb),platform=Windows NT 6.1.7601 SP1 x86_64)
    
了解到chromedriver版本与Chrome版本有一定的对象关系，默认下载的chromedriver版本是2.38，我本机chrome的版本是60.0，所以要么降低chromedriver的版本，要么升级Chrome的版本到v65-67

https://blog.csdn.net/huilan_same/article/details/51896672

|chromedriver版本	|支持的Chrome版本|
| --------   | :----: |
|v2.38	|v65-67|
|v2.37	|v64-66|
|v2.36	|v63-65|
|v2.35|	v62-64|
|v2.34	|v61-63|
|v2.33	|v60-62|
|v2.32	|v59-61|

习惯于webdriver-manager的帮助，看到了这样的参数
    
    --versions.chrome           Optional chrome driver version (use 'latest' to get the most recent version)
    
于是，指定下载chromedriver的版本号2.32

    D:\workspace\vCN-EM-TOPO\topo-iui>webdriver-manager update --proxy http://10048570:xxxxxx@proxynj.zte.com.cn:80 --gecko false --versions.chrome 2.32 --standalone false
    [15:45:11] I/update - chromedriver: file exists C:\Users\10048570\AppData\Roaming\npm\node_modules\protractor\node_modules\webdriver-manager\selenium\chromedriver_2.32.zip
    [15:45:11] I/update - chromedriver: unzipping chromedriver_2.32.zip
    [15:45:12] I/update - chromedriver: chromedriver_2.32.exe up to date
    
protrator启动成功，但随手写的第一个用例跑失败了，真是好事多磨

    topo-iui-new App should display welcome message
    - Error: Timeout - Async callback was not invoked within timeout specified by jasmine.DEFAULT_TIMEOUT_INTERVAL.
    - Failed: Timed out waiting for asynchronous Angular tasks to finish after 60 seconds. This may be because the current page is
    not an Angular application. Please see the FAQ for more details: https://github.com/angular/protractor/blob/master/docs/timeouts.md#waiting-for-angular
    While waiting for element with locator - Locator: By(css selector, title)
    
这个有提示如何解决：https://github.com/angular/protractor/blob/master/docs/timeouts.md#waiting-for-angular，waiting-for-angular的问题是个普遍的问题，大家惠存下这个链接，仔细读下

最后，protractor在运行用例时会自动打开一个chrome的实例，并模拟用户的操作，赞！

    D:\workspace\vCN-EM-TOPO\topo-iui>protractor protractor.conf.js
    [17:36:38] I/launcher - Running 1 instances of WebDriver
    [17:36:38] I/direct - Using ChromeDriver directly...
    Jasmine started
     
      topo-iui App
        √ should filter tree nodes
     
    Executed 1 of 1 spec SUCCESS in 21 secs.
    [17:37:10] I/launcher - 0 instance(s) of WebDriver still running
    [17:37:10] I/launcher - chrome #01 passed
    
## 回顾

1. 通过手工下载selenium和chromedriver，放置到out_dir下是否可以？我觉得是可以的，可以拷贝一个update-config.json

2. 日后需要测试firefox时，geckodriver无法下载的问题还是需要摆到桌面上攻克

3. 截止目前，我们只是本机搞定了protractor，如何结合到CI里？尤其是linux上浏览器问题如何解决？


无论如何，我们已经开启了前端自动化测试之旅，并且收获良多
# Android sdk集成说明

【注意：安卓系统9.0（api 28）以后，默认不再支持Http请求，需要通过配置network_security_config.xml文件解决。如果您的项目中添加此文件并配置后，智齿SKD2.7.9之前的版本出现突然不能使用的情况，您可使用智齿官网上的最新版本，新版本中我们已优化了该配置的影响。

具体情况可查看[详情文档](https://docs.qq.com/doc/DYlVVYk95VmpvWVN0)】

智齿客服SDK为企业提供了一整套完善的智能客服解决方案。智齿客服 SDK 既包含客服业务逻辑，也提供交互界面；企业只需简单两步，便可在App中集成智齿客服，让App拥有7*24小时客服服务能力。

![图片](https://img.sobot.com/mobile/sdk/images/a-0.png)

<智齿客服SDK>

管理员可以在后台「设置-支持渠道-APP」添加APP，然后按照本接入文档说明完成SDK对接。

智齿客服SDK具有以下特性

* 在线咨询：咨询机器人、咨询人工客服（收发图片、发送语音）、发送表情；
* 指定技能组接待；
* 排队或客服不在线时引导用户留言；
* 机器人优先模式下隐藏转人工按钮，N次机器人未知问题问题是显现；
* 客服满意度评价：用户主动满意度评价+用户退出时询问评价；
* 传入用户资料：用户对接lD+基础资料+自定义字段；
* 传入商品来源页：来源页标题+来源页URL；
* 高度自定义UI；
## 1.1 集成流程示意图
![图片](https://img.sobot.com/mobile/sdk/images/a-1-1.png)

## 1.2.文件说明
**SDK包含****sobotsdk****和****AndroidStudio_demo****、和****Doc****相关说明文档。**

## 2.1 手动集成
2.7.14版

2.7.14 普通版 [文档链接](https://shimo.im/doc/jNZ9iojxQk4BSs8E)

2.7.14 电商版 [文档链接](https://shimo.im/doc/FOg7Xjl1BnYQfxwQ)

2.8.2版

普通版：

下载链接：[Android_SDK_2.8.2](https://img.sobot.com/mobile/sdk/Android_SDK_2.8.2.zip)

androidX普通版：

下载链接：[Android_SDK_X_2.8.2](https://img.sobot.com/mobile/sdk/Android_SDK_X_2.8.2.zip)

电商版：

下载链接：[Android_SDK_MALL_2.8.2](https://img.sobot.com/mobile/sdk/Android_SDK_MALL_2.8.2.zip)

androidX电商版：

下载链接：[Android_SDK_MALL_X_2.8.2](https://img.sobot.com/mobile/sdk/Android_SDK_MALL_X_2.8.2.zip)

导入Module

解压下载的智齿Android_SDK_XXX.zip文件，将 sobotsdk 文件直接复制到您的项目中，

然后 Build-->clean projecty一下，之后在build.gradle添加项目依赖 

完成上述步骤之后build.gradle中如下所示：

```js
dependencies {
      implementation project(':sobotsdk')
      implementation 'com.squareup.okhttp3:okhttp:3.12.0'
      implementation 'com.android.support:appcompat-v7:28.0.0'
      implementation 'com.android.support:recyclerview-v7:28.0.0'
      //目前支持常见的3种图片加载库，必须在下面的图片加载库中选择一个添加依赖
      //implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
      //implementation 'com.github.bumptech.glide:glide:3.7.0'
      implementation 'com.squareup.picasso:picasso:2.5.2'
}
```
【注意】由于glide v3版本和v4版本的接口完全不同，因此我们为了方便您的使用，采用了 
特殊的集成方式使sdk可以支持任 意版本的glide。正常情况下，您使用glide 时，直接添加 

glide依赖和sobotsdk的依赖，sdk即 可正常使用。如果报错，那么把glide 升级到4.4.0版 

本以上即可。 

并且参照混淆文件 

（Android_SDK_x.x.x\AndoridStudio\Demo\SobotSDK\sobotsdkdemo\proguard- 

rules.pro）中的混淆配置添加混淆规则。 

在使用4.9.0以上版本的glide时，需额外添加依赖 

"implementation 'com.sobot.chat:sobotsupport-glidev4:1.0.10" 

## 2.2 依赖集成
```js
// 普通版：
implementation  'com.sobot.chat:sobotsdk:2.8.2'
// 电商版：
implementation 'com.sobot.chat:sobotsdk-mall:2.8.2'
```

在build.gradle中如下所示：

```js
dependencies {
      implementation 'com.sobot.chat:sobotsdk:2.8.2'
      implementation 'com.squareup.okhttp3:okhttp:3.12.0'
      implementation 'com.android.support:appcompat-v7:28.0.0'
      implementation 'com.android.support:recyclerview-v7:28.0.0'
      //目前支持常见的3种图片加载库，必须在下面的图片加载库中选择一个添加依赖
      //implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'
      //implementation 'com.github.bumptech.glide:glide:3.7.0'
      implementation 'com.squareup.picasso:picasso:2.5.2'
}
```
【注意】由于glide v3版本和v4版本的接口完全不同，因此我们为了方便您的使用，采用了 

特殊的集成方式使sdk可以支持任 意版本的glide。正常情况下，您使用glide 时，直接添加 

glide依赖和sobotsdk的依赖，sdk即 可正常使用。如果报错，那么把glide 升级到4.4.0版 

本以上即可。 

并且参照混淆文件 

（Android_SDK_x.x.x\AndoridStudio\Demo\SobotSDK\sobotsdkdemo\proguard- 

rules.pro）中的混淆配置添加混淆规则。 

在使用4.9.0以上版本的glide时，需额外添加依赖 

"implementation 'com.sobot.chat:sobotsupport-glidev4:1.0.10" 

## 3.1 域名设置
域名说明：

      * 默认SaaS平台域名为:  https://api.sobot.com

      * 如果您是腾讯云服务，请设置为：https://ten.sobot.com

      * 如果您是本地化部署，请使用自己的部署的服务域名

示例代码：

【注意：设置域名一定要在所有接口请求之前设置，即在初始化之前就必须设置完】

```js
SobotBaseUrl.setApi_Host("域名");
```
## 3.2 获取appkey
登录 [智齿科技管理平台](https://www.sobot.com/console/login) 获取，如图

![图片](https://img.sobot.com/mobile/sdk/images/a-3-2.png)

## 3.3 初始化 
### 3.3.1 普通版：
初始化参数和调用方式：

【注意：启动智齿SDK之前，必须调用初始化接口initSobotSDK，否则将无法启动SDK】

接口：

```js
/**
* 初始化sdk
* @param context 上下文  必填
* @param appkey  用户的appkey  必填 如果是平台版用户需要传总公司的appkey
* @param uid     用户的唯一标识，不能传一样的值，可以为空
*/
SobotApi.initSobotSDK(Context context,String appkey,final String uid);
```
示例代码：

```js
public class App extends Application {
  @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }
    private void initApp() {  
      SobotApi.initSobotSDK(getApplicationContext(),"your appkey"), "");
     }
}
```
### 3.3.2 电商版
初始化参数和调用方式：

【注意：启动智齿SDK之前，必须调用初始化接口initPlatformUnion和initSobotSDK，否则将无法启动SDK,多次执行不会重复调用接口】

接口：

```js
/**
* 初始化平台Id
* @param context 上下文  必填
* @param platformUnionCode   平台Id  必填  请联系对应的客服申请
* @param platformSecretkey 平台标识 秘钥 请联系对应的客服申请
*/
SobotApi.initPlatformUnion(Context context, String platformUnionCode,String platformSecretkey);
```
接口（可选）:

```js
//设置溢出商城主站公司id
SobotApi.setFlow_Company_Id(context,"your flowCompanyId");
//设置溢出商城主站公司技能组Id
SobotApi.setFlow_GroupId(context,"your flowGroupId");
//设置是否溢出到主商户flowType  0-不溢出 , 1-全部溢出，2-忙碌时溢出，3-不在线时溢出,默认不溢出
SobotApi.setFlow_Type(context,"your flowType");
```

示例代码：

```js
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }
    private void initApp() { 
      SobotApi.initPlatformUnion(getApplicationContext(), "1001", "");
      SobotApi.initSobotSDK(getApplicationContext(), getResources().getString(R.string.sobot_appkey), "");
     }
```
## 3.4 启动智齿页面
普通版本和电商版本启动方式一样

接口

```js
Information info = new Information();
//appkey 必传
info.setAppkey("Your appkey");
/**
* @param context 上下文对象
* @param information 初始化参数
*/
SobotApi.startSobotChat(context, information);
```

| 参数名   | 类型   | 描述   | 
|:----|:----|:----|
| context   | Context   | 上下文   | 
| information   | UIViewController   | 初始化参数自定义设置   | 

示例代码：

```js
Information info = new Information();
// appkey 必填 
info.setApp_key(et_appkey.getText().toString());
//注意：用户唯一标识，不能传入一样的值，选填
info.setPartnerid("");
//用户昵称，选填
info.setUser_nick("");
//用户姓名，选填
info.setUser_name("");
//用户电话，选填
info.setUser_tels("");
//用户邮箱，选填
info.setUser_emails("");
//自定义头像，选填
info.setFace("");
//用户QQ，选填
info.setQq("");
//用户备注，选填
info.setRemark("");
//访问着陆页标题，选填
info.setVisit_title("");
//访问着陆页链接地址，选填
info.setVisit_url("");
SobotApi.startSobotChat(context, info);
```
如有特殊需求，SDK 还提供了以 fragment 嵌入的方式集成会话界面，开发者可以更灵活的使用 SDK。示例代码如下（也可参考SobotChatActivity中的实现)

```js
SobotChatFragment fragment = SobotChatFragment.newInstance(informationBundle);
FragmentManager fm = getSupportFragmentManager();
FragmentTransaction transaction = fm.beginTransaction();
// containerId 为 ViewGroup 的 resId
transaction.replace(containerId, fragment);
try {
    transaction.commitAllowingStateLoss();
} catch (Exception e) {
}
```
## 3.5 启动客户服务中心
```js
Information information = new Information();
information.setAppkey("Your AppKey");  //分配给App的的密钥
/**
* @param context 上下文对象
* @param information 初始化参数
*/
SobotApi.openSobotHelpCenter(context, information);
```
效果图如下：
![图片](https://img.sobot.com/mobile/sdk/images/a-3-5.png)

## 4.1 机器人客服
### 4.1.1 对接指定机器人
在后台获取机器人编号：

![图片](https://img.sobot.com/mobile/sdk/images/a-4-1-1.png)

在 SDK 代码中配置：

```js
//设置机器人编号
info.setRobotCode("your robot code");
```
### 4.1.2 自定义接入模式
根据自身业务的需要，可进行以下初始化参数配置，控制接入模式：

```js
//默认false：显示转人工按钮。true：智能转人工
info.setArtificialIntelligence(false);
//当未知问题或者向导问题显示超过(X)次时，显示转人工按钮。
//注意：只有ArtificialIntelligence参数为true时起作用
info.setArtificialIntelligenceNum(X);
//是否使用语音功能 true使用 false不使用   默认为true
info.setUseVoice(true);
//是否使用机器人语音功能 true使用 false不使用 默认为false,需要付费才可以使用
info.setUseRobotVoice(false);
//客服模式控制 -1不控制 按照服务器后台设置的模式运行
//1仅机器人 2仅人工 3机器人优先 4人工优先
info.setInitModeType(-1);
//设置机器人模式下输入关键字转人工
HashSet<String> tmpSet = new HashSet<>();
tmpSet.add("转人工");
tmpSet.add("人工");
info.setTransferKeyWord(tmpSet);
```
### 4.1.3 自定义转人工事件
sdk可以配置转人工拦截器，在转人工前做一些额外的逻辑处理，例如自定义技能组选择dialog。

1.设置拦截器

```js
SobotOption.transferOperatorInterceptor = new SobotTransferOperatorInterceptor() {
    @Override
    public void onTransferStart(final Context context, final SobotTransferOperatorParam param) {
        //do something
    }
};
```
2、修改转人工参数SobotTransferOperatorParam，以下为可修改参数介绍：
```js
//技能组id 
String groupId;
//技能组名称
String groupName;
//转人工后是否提示
boolean isShowTips;
//商品卡片信息
ConsultingContent consultingContent;
```
3、使用转人工参数主动调用转人工接口：
```js
/**
 * 外部主动调用转人工
 * @param context
 * @param param 转人工参数
 *        ConsultingContent consultingContent 商品信息        
 */
SobotApi.transfer2Operator(context, param);
SobotTransferOperatorParam 里新增传入服务总结参数summaryParams
 方法 setSummaryParams(Map<String, String> summaryParams)；
```
### 4.1.4 设置转人工溢出
1.配置参数对象

```js
Information info = new Information();
//设置溢出技能组或客服，最多四层,
List<SobotTransferAction> datas = new ArrayList<>();
//例如:设置专员客服接待；溢出规则和技能组一样
SobotTransferAction firstData = new SobotTransferAction.ServiceBuilder()
        .conditionIntelligentudgement()
        .overflow()
        .designatedServiceId("d679e5b8d45b484e9475a2ca051b44a0")//专员客服ID
        .ServiceBuilder();
//例如:贵宾技能组
SobotTransferAction guibinData = new SobotTransferAction.Builder()
        .designatedSkillId("ae654754311e4fa59sdfdsafdsffc")
        .conditionIntelligentudgement()
        .overflow()
        .Build();

//例如:vip技能组
SobotTransferAction vipData = new SobotTransferAction.Builder()
        .designatedSkillId("ae654754311e4fa590b0e3a4298672fc")
        .conditionIntelligentudgement()
        .overflow()
        .Build();
//例如:普通技能组
SobotTransferAction data = new SobotTransferAction.Builder()
        .designatedSkillId("a71100c5463d42bfb467762bccfef511")
        .conditionIntelligentudgement()
        .overflow()
        .Build();
datas.add(firstData);
datas.add(guibinData);
datas.add(vipData);
datas.add(data);
JSONArray jsonArray = GsonUtil.praseList2Json(datas);
info.setTransferAction(jsonArray.toString());
```
2.配置参数说明
```js
①、设置是否溢出
//设置溢出
overflow()
//设置不溢出
no_overflow()
②、设置指定的技能组
designatedSkillId("技能组id")
   设置指定的客服
designatedServiceId("客服id")


③、设置溢出条件
//指定客服组时：技能组无客服在线
conditionServiceOffline()
//指定客服组时：技能组所有客服忙碌
conditionServiceBusy()
//指定客服组时：技能组不上班
conditionServiceOffWork()
//指定客服组时：智能判断
conditionIntelligentudgement()
```
## 4.2 人工客服
### 4.2.1 对接指定技能组
在后台获取技能组编号：

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-1.png)

在 SDK 代码中配置技能组ID：

```js
//预设技能组编号
info.setSkillSetId("your skillCode");
//预设技能组名称，选填
info.setSkillSetName("your skillName");
```
注意：此字段可选，如果传入技能组ID那么SDK内部转人工之后不在弹技能组的选择框，直接跳转到传入ID所对应的技能组中
### 4.2.2 对接指定客服
在后台获取指定客服ID：

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-2.png)

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-2-1.png)

在 SDK 代码中设置：

```js
//转接类型(0-可转入其他客服，1-必须转入指定客服)
info.setTranReceptionistFlag(1);
//指定客服id
info.setReceptionistId("your Customer service id");
```
注意：
1 choose_adminid ：指定对接的客服，如果不设置，取默认

2 tranReceptionistFlag ：设置指定客服之后是否必须转入指定客服 

0 ：可转入其他客服， 

1： 必须转入指定客服，  

注意：如果设置为1 ，当指定的客服不在线，不能再转接到其他客服

### 4.2.3 设置用户自定义资料和自定义字段
开发者可以直接传入这些用户信息，供客服查看。

在工作台自行配置所需要显示的字段，配置方法如下图：

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-3.png)

```js
//设置用户自定义字段,key必须是后端字段对应的ID
Map<String,String> customerFields = new HashMap<>();
customerFields.put("weixin","your wechat");
customerFields.put("weibo","your weibo");
customerFields.put("sex","女");
customerFields.put("birthday","2017-05-17");
info.setCustomerFields(customerFields);
```
用户自定义资料
```js
//自定义用户料
Map<String, String> customInfo = new HashMap<>();
customInfo.put("资料", "aaaaa");
info.setParams(customInfo);
```
效果图如下：
![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-3-1.png)

### 4.2.4 **设置转接成功后自动发消息**
sdk可以设置转接成功后自动发消息

```js
//设置发送模式
//SobotAutoSendMsgMode.Default  默认 不发送
//SobotAutoSendMsgMode.SendToRobot  只给机器人发送
//SobotAutoSendMsgMode.SendToOperator   只给人工客服发送
//SobotAutoSendMsgMode.SendToAll   全部发送
info.setAutoSendMsgMode(SobotAutoSendMsgMode.SendToAll.setContent("your msg"));
```
### 4.2.5 **设置指定客户排队优先接入**
sdk可以设置当前用户排队优先，当此用户进入排队状态时，将会被优先接待。

```js
//设置排队优先接入 true:优先接入  false:默认值，正常排队
info.setIs_Queue_First(true);
```
### 4.2.6 **设置服务总结自定义字段**
sdk可以配置服务总结自定义字段，可以使客服更快速的对会话进行服务总结。

1、获取自定义字段id

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-6.png)

![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-6-1.png)

2、设置服务总结自定义字段 (转人工支持传入服务总结参数)

```js
//服务总结自定义字段
Map<String, String> summaryInfo = new HashMap<>();
summaryInfo.put("your keyId", "your value");
info.setSummary_params(summaryInfo);
```
### 4.2.7 **设置多轮会话接口参数**
在使用多轮会话功能时,每一个接口我们都会传入 uid 和 mulitParams 两个固定的自定义参数，uid 是用户的唯一标识，mulitParams是自定义字段json字符串、如果用户对接了这两个字段，我们会将这两个字段回传给第三方接口、如果没有我们会传入空字段。

```js
//多轮会话自定义参数
info.setMulti_params("{\"key1\",\"val1\"}");
```
### 4.2.8  商品的咨询信息并支持直接发送消息卡片，仅人工模式下支持
```js
在用户与客服对话时，经常需要将如咨询商品或订单发送给客服以便客服查看。咨询对象目前最多支持发送5个属性(title,imgUrl,fromUrl,describe,lable)，其中(title,fromUrl)为必填字段，如下以商品举例说明：
//咨询内容
ConsultingContent consultingContent = new ConsultingContent();
//咨询内容标题，必填
consultingContent.setSobotGoodsTitle("XXX超级电视50英寸2D智能LED黑色");
//咨询内容图片，选填 但必须是图片地址
consultingContent.setSobotGoodsImgUrl("http://www.li7.jpg");
//咨询来源页，必填
consultingContent.setSobotGoodsFromUrl("www.sobot.com");
//描述，选填
consultingContent.setSobotGoodsDescribe("XXX超级电视 S5");
//标签，选填
consultingContent.setSobotGoodsLable("￥2150");
//转人工后是否自动发送
consultingContent.setAutoSend(true);
//启动智齿客服页面 在Information 添加,转人工发送卡片消息
info.setConsultingContent(consultingContent);
```
效果图如下：
![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-8.png)

### 4.2.9  发送订单卡片，仅人工模式下支持,订单卡片点击事件可拦截
用法一：启动智齿客服时，自动发送订单卡片消息

```js
List<OrderCardContentModel.Goods> goodsList = new ArrayList<>();
goodsList.add(new OrderCardContentModel.Goods("苹果", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
goodsList.add(new OrderCardContentModel.Goods("苹果1111111", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
goodsList.add(new OrderCardContentModel.Goods("苹果2222", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
goodsList.add(new OrderCardContentModel.Goods("苹果33333333", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
OrderCardContentModel orderCardContent = new OrderCardContentModel();
//订单编号（必填）
orderCardContent.setOrderCode("zc32525235425");
//订单状态
//待付款:1 待发货:2 运输中:3  派送中:4  已完成:5  待评价:6 已取消:7
orderCardContent.setOrderStatus(1);
//订单总金额(单位 分)
orderCardContent.setTotalFee(1234);
//订单商品总数
orderCardContent.setGoodsCount("4");
//订单链接
orderCardContent.setOrderUrl("https://item.jd.com/1765513297.html");
//订单创建时间
orderCardContent.setCreateTime(System.currentTimeMillis() + "");
//转人工后是否自动发送
orderCardContent.setAutoSend(true);
//订单商品集合
orderCardContent.setGoods(goodsList);
//订单卡片内容
info.setOrderGoodsInfo(orderCardContent);
```
用法二：转人工后，加号中可添加一个订单按钮，点击后给客服发送一条订单消息

```js
final String ACTION_SEND_ORDERCARD = "sobot_action_send_ordercard";
ChattingPanelUploadView.SobotPlusEntity ordercardEntity = new ChattingPanelUploadView.SobotPlusEntity(ResourceUtils.getDrawableId(getApplicationContext(), "sobot_ordercard_btn_selector"), ResourceUtils.getResString(getApplicationContext(), "sobot_ordercard"), ACTION_SEND_ORDERCARD);
tmpList.add(ordercardEntity);
SobotUIConfig.pulsMenu.operatorMenus = tmpList;
//sSobotPlusMenuListener 只能有一个，否则，下边的会覆盖上边的(例如：加号中
//同时自定义添加位置和订单按钮后，可根据action 判断点击的是哪个按钮，做对应的处理)
SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
    @Override
    public void onClick(View view, String action) {
        if (ACTION_SEND_ORDERCARD.equals(action)) {
            Context context = view.getContext();
            List<OrderCardContentModel.Goods> goodsList = new ArrayList<>();
            goodsList.add(new OrderCardContentModel.Goods("苹果", "https://img.sobot.com/chatres/66a522ea3ef944a98af45bac09220861/msg/20190930/7d938872592345caa77eb261b4581509.png"));
            OrderCardContentModel orderCardContent = new OrderCardContentModel();
            //订单编号（必填）
            orderCardContent.setOrderCode("zc32525235425");
            //订单状态
            //待付款:1 待发货:2 运输中:3 派送中:4 已完成:5 待评价:6 已取消:7
            orderCardContent.setOrderStatus(1);
            //订单总金额(单位是分)
            orderCardContent.setTotalFee(1234);
            //订单商品总数
            orderCardContent.setGoodsCount("4");
            //订单链接
         orderCardContent.setOrderUrl("https://item.jd.com/1765513297.html");
            //订单创建时间
            orderCardContent.setCreateTime(System.currentTimeMillis() + "");
            //订单商品集合
            orderCardContent.setGoods(goodsList);
            SobotApi.sendOrderCardMsg(context, orderCardContent);
        }
    }
};
```

配置订单卡片拦截，也可使用SobotApi.setNewHyperlinkListener(）进行拦截；setOrderCardListene拦截后setNewHyperlinkListener就不会在拦截了

```js
SobotApi.setOrderCardListener(new SobotOrderCardListener() {
    @Override
    public void onClickOrderCradMsg(OrderCardContentModel orderCardContent) {
        ToastUtil.showToast(getApplicationContext(), "点击了订单卡片" );
    }
});
```
效果图如下：
![图片](https://img.sobot.com/mobile/sdk/images/a-4-2-9.png)

### 4.2.10 查看商户客服是否正在和用户聊天 (仅电商版可用)
```js
/**
 * 判断当前用户是否正在与当前商户客服聊天
 *
 * @param appkey 当前商户的appkey
 * @return true 表示正在与当前商户客服聊天
 *           false 表示当前没有与所选商户客服聊天
 */
SobotApi.isActiveOperator(appkey);
```
## 4.3 留言工单相关
### 4.3.1 工作台设置留言界面
在工作台可以设置留言界面

![图片](https://img.sobot.com/mobile/sdk/images/a-4-3-1.png)

### 4.3.2 留言页面用户信息自定义配置
留言中的邮箱、电话、附件这三个参数的校验和显示逻辑可在pc端console页面配置。

![图片](https://img.sobot.com/mobile/sdk/images/a-4-3-2.png)

### 4.3.3 跳转到留言页面
```js
/**
 * 跳转到留言界面
 *
 * @param context   上下文  必填
 * @param info     用户的appkey  必填 如果是平台用户需要传总公司的appkey
 * @param isOnlyShowTicket true只显示留言记录界面，false 请您留言和留言记录界面都显示
 */
 SobotApi.startToPostMsgActivty(Context context,Information info,boolean isOnlyShowTicket);
```
示例代码：
```js
Information info = new Information();
info.setApp_key(et_appkey.getText().toString());/* 必填 */
//工单技能组
info.setLeaveMsgGroupId("6576d173af904d97b1d5d01a11cc66f5");
Map<String,String> map=new HashMap<>();
//自定义字段，key和后端添加字段ID的对应
map.put("834b34870b2e47daa1904d8f63ee55c2","zzz");
info.setLeaveCusFieldMap(map);
SobotApi.startToPostMsgActivty(SobotStartActivity.this, info, false);
```
### 4.3.4 留言页面事件拦截
sdk中留言可跳转到自定义页面，如有此需求，可以使用如下方法进行设置：

```js
SobotApi.setSobotLeaveMsgListener(new SobotLeaveMsgListener() {
    @Override
    public void onLeaveMsg() {
      ToastUtil.showToast(getApplicationContext(),"在这里实现方法，跳转页面");
   }
});
```
### 4.3.5 已完成状态的留言详情界面的回复按钮可通过参数配置是否显示
```js
//已完成状态的留言，是否可持续回复 true 持续回复 ，false 不可继续回复 ；
//默认 true 用户可一直持续回复
SobotApi.setSwitchMarkStatus(MarkConfig.LEAVE_COMPLETE_CAN_REPLY,true);
```
## 4.4 评价
### 4.4.1 设置评价界面
在工作台可以设置 满意度评价界面

![图片](https://img.sobot.com/mobile/sdk/images/a-4-4-1.png)

### 4.4.2 导航栏左侧点击返回时是否弹出满意度评价
```js
//点击返回时是否弹出弹窗(您是否结束会话？)
info.setShowLeftBackPop(true);
//导航栏左侧点击返回时是否弹出满意度评价。true弹出,false不弹;默认false
info.setShowSatisfaction(false);
```
效果图如下：
![图片](https://img.sobot.com/mobile/sdk/images/a-4-4-2.png)![图片](https://img.sobot.com/mobile/sdk/images/a-4-4-2-1.png)

### 4.4.3 导航栏右侧关闭按钮是否显示和点击时是否弹出满意度评价
```js
//设置是否显示导航栏右侧关闭按钮，true显示,false隐藏;默认false
info.setShowCloseBtn(false);
//导航栏右侧点击关闭按钮时是否弹出满意度评价。true弹出,false不弹;默认false
info.setShowCloseSatisfaction(false);
```
### 4.4.4 配置用户提交人工满意度评价后释放会话
```js
/**
 * 配置用户提交人工满意度评价后释放会话
 * @param context 上下文对象
 * @param flag true 表示释放会话  false  表示不释放会话
 */
SobotApi.setEvaluationCompletedExit(context,flag);
```
## 4.5 消息相关
### 4.5.1 发送文本消息
如您的app需要主动发送文本消息给客服，请使用如下代码：

```js
/**
 * 发送文本类信息
 * @param context
 * @param content 文本内容
 */
SobotApi.sendTextMsg(Context context,String content)
```
### 4.5.2 **设置是否开启消息提醒**
当用户不处在聊天界面时，收到客服的消息，APP 可以在通知栏或者聊天入口给出提醒。通知栏提醒可以显示最近一条消息的内容，并提供给用户快速进入 APP 的入口。

```js
/**
* 设置是否开启消息提醒 默认不提醒
* @param context
* @param flag
* @param smallIcon 小图标的id 设置通知栏中的小图片，尺寸一般建议在24×24
* @param largeIcon 大图标的id
*/
public static void setNotificationFlag(Context context,boolean flag,int smallIcon,int largeIcon);
```
用户点击通知栏会发出广播，监听广播实现跳转到指定Activity
```js
action:
常量：ZhiChiConstant.SOBOT_NOTIFICATION_CLICK
或者字符串：“sobot_notification_click”
```
示例代码：
```js
//设置是否开启消息提醒
SobotApi.setNotificationFlag(getApplicationContext(), true, R.drawable.sobot_logo_small_icon, R.drawable.sobot_logo_icon);
```
### 4.5.3 **设置离线消息**
开启离线消息

```js
// 开启通道接受离线消息，开启后会将消息以广播的形式发送过来,如果无需此功能那么可以不做调用
SobotApi.initSobotChannel(getApplicationContext(), "uid");
```
关闭离线消息
```js
// 关闭通道,清除当前会话缓存
SobotApi.disSobotChannel(getApplicationContext());
```
### 4.5.4 **注册广播、获取新收到的信息和未读消息数**
注册广播后，当消息通道连通时，可以获取到新接收到的消息。

1 注册广播

```js
/**
* action:ZhiChiConstants.sobot_unreadCountBrocast
*/
IntentFilter filter = new IntentFilter();
filter.addAction(ZhiChiConstant.sobot_unreadCountBrocast);
contex.registerReceiver(receiver, filter);
```
2 接收新信息和未读消息数
在BroadcastReceiver的onReceive方法中接收信息。

```js
public class MyReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    //未读消息数
    int noReadNum = intent.getIntExtra("noReadCount", 0);
    //新消息内容
    String content = intent.getStringExtra("content");
    LogUtils.i("未读消息数:" + noReadNum + "   新消息内容:" + content);
  }
}
```
当用户不处在聊天界面时，收到客服的消息会将未读消息数保存在本地，如果需要获取本地保存的未读消息数，那么在需要的地方调用该方法即可。如下：
```js
/**
* @param context 上下文对象
* @param uid 用户唯一标识 与information中传的uid一致，
* @return int
*/
SobotApi.getUnreadMsg(Context context,String uid);//如果您没有在information里设置这个uid，请传入null。
```
### 4.5.5 发送位置消息
如您的app需要发送客户的位置信息，请参照以下步骤设置(其中地图定位需要开发者自行开发)：

1、客服聊天界面配置位置发送按钮（显示在点击“+”按钮的菜单面板中，只在转人工后显示），代码如下：

```js
//菜单动作 当点击按钮时会将对应action返回给callback以此作为依据，
//判断用户点击了哪个按钮，可自行定义
final String ACTION_LOCATION = "sobot_action_location";
//配置位置发送按钮
ChattingPanelUploadView.SobotPlusEntity locationEntity = new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_location_btn_selector, ResourceUtils.getResString(getApplicationContext(), "sobot_location"), ACTION_LOCATION);
List<ChattingPanelUploadView.SobotPlusEntity> tmpList = new ArrayList<>();
tmpList.add(locationEntity);
SobotUIConfig.pulsMenu.operatorMenus = tmpList;
```
2、设置位置发送按钮的回调：

```js
//sSobotPlusMenuListener 只能有一个，否则，下边的会覆盖上边的(例如：加号中
//同时自定义添加位置和订单按钮后，可根据action 判断点击的是哪个按钮，做对应的处理)
SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
    @Override
    public void onClick(View view, String action) {
        if (ACTION_LOCATION.equals(action)) {
            Context context = view.getContext();
            //在地图定位页面获取位置信息后发送给客服：
            SobotLocationModel locationData = new SobotLocationModel();
            //地图快照，必须传入本地图片地址，注意：如果不传会显示默认的地图图片
            locationData.setSnapshot(Environment.getExternalStorageDirectory().getAbsolutePath()  +"/1.png");
            //纬度
            locationData.setLat("40.057406655722");
            //经度
            locationData.setLng("116.2964407172");
            //标点名称
            locationData.setLocalName("金码大厦");
            //标点地址
            locationData.setLocalLabel("北京市海淀区六道口金码大厦");
            SobotApi.sendLocation(context, locationData);
        }
    }
};
```
### 4.5.6 自定义超链接的点击事件（拦截范围：帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
2.8.2 之后

```js
// 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
// 可为订单号,商品详情地址等等;客户可自定义规则拦截,返回true时会把自定义的信息返回
// 拦截范围  （帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
SobotApi.setNewHyperlinkListener(new NewHyperlinkListener() {
    @Override
    public boolean onUrlClick(String url) {
        //举例
        if (url.contains("baidu.com")) {
            ToastUtil.showToast(getApplicationContext(), "点击了超链接，url=" + url);
            //如果url链接是百度,拦截
            //do().....
            return true;
        }
        //举例
        if (url.contains("订单编号：123456789")) {
            ToastUtil.showToast(getApplicationContext(), "点击了超链接，url=" + url);
            //如果链接是订单卡片,拦截
            //do().....
            return true;
        }
        return false;
    }
    @Override
    public boolean onEmailClick(String email) {
        ToastUtil.showToast(getApplicationContext(), "点击了邮件，email=" + email);
        return false;
    }
    @Override
    public boolean onPhoneClick(String phone) {
        ToastUtil.showToast(getApplicationContext(), "点击了电话，phone=" + phone);
        return false;
    }
});
```
2.8.2 之前 这个方法 配置后会拦截所有的，不能动态拦截，建议使用新方法（setNewHyperlinkListener）
```js
SobotApi.setHyperlinkListener(new HyperlinkListener() {
  @Override
  public void onUrlClick(String url) {
    LogUtils.i("点击了超链接，url="+url);
  }
  @Override
  public void onEmailClick(String email) {
    LogUtils.i("点击了邮件，email="+email);
  }
  @Override
  public void onPhoneClick(String phone) {
    LogUtils.i("点击了电话，phone="+phone);
  }
});
```
### 4.5.7 监听当前聊天模式的变化
```js
SobotApi.setChatStatusListener(new SobotChatStatusListener() {
    @Override
    public void onChatStatusListener(SobotChatStatusMode chatStatusMode) {
        switch (chatStatusMode) {
            case ZCServerConnectRobot:
                ToastUtil.showToast(getApplicationContext(), "机器人聊天模式");
                break;
            case ZCServerConnectArtificial:
                ToastUtil.showToast(getApplicationContext(), "转人工客服聊天模式");
                break;
            case ZCServerConnectOffline:
                ToastUtil.showToast(getApplicationContext(), "已离线");
                break;
            case ZCServerConnectWaiting:
                ToastUtil.showToast(getApplicationContext(), "仅人工排队中");
                break;
        }
    }
});
```
## 4.6 会话页面自定义UI设置
 为了咨询客服窗口的界面风格能与集成智齿客服 SDK 的 App 整体统一，智齿客服 SDK 提供了简洁的 UI 自定义配置选项

### 4.6.1 配置属性值
以下属性可在application的oncreate()方法中设置

```js
//设置 转人工按钮的图片
SobotUIConfig.sobot_serviceImgId = R.drawable.sobot_icon_manualwork_normal;
//设置 头部文字字体颜色
SobotUIConfig.sobot_titleTextColor = R.color.sobot_color;
//设置 右上角按钮图片
SobotUIConfig.sobot_moreBtnImgId = R.drawable.sobot_delete_hismsg_normal;
//设置 头部背景颜色
SobotUIConfig.sobot_titleBgColor = R.color.sobot_white;
//修改状态栏背景颜色
SobotUIConfig.sobot_statusbar_BgColor = R.color.sobot_white;
//设置 聊天界面底部整体布局背景颜色
SobotUIConfig.sobot_chat_bottom_bgColor = R.color.sobot_white;
//设置 聊天界面左边气泡背景颜色
SobotUIConfig.sobot_chat_left_bgColor = R.color.sobot_common_gray;
//设置 聊天界面左边气泡内文字字体颜色
SobotUIConfig.sobot_chat_left_textColor = R.color.sobot_holo_red_light;
//设置 聊天界面左边气泡内链接文字字体颜色
SobotUIConfig.sobot_chat_left_link_textColor = R.color.sobot_color;
//设置 聊天界面右边气泡背景颜色
SobotUIConfig.sobot_chat_right_bgColor = R.color.sobot_holo_red_light;
//设置 聊天界面右边气泡内链接文字字体颜色
SobotUIConfig.sobot_chat_right_link_textColor = R.color.sobot_color;
//设置 聊天界面右边气泡内文字字体颜色
SobotUIConfig.sobot_chat_right_textColor = R.color.sobot_white;
//聊天界面文件消息类型气泡背景颜色
SobotUIConfig.sobot_chat_file_bgColor = R.color.sobot_holo_red_light;
//设置 toolbar右边第二个按钮是否显示（评价）
SobotUIConfig.sobot_title_right_menu2_display = true;
//修改toolbar右边第二个按钮的图片(R.drawable.sobot_icon_call为拨号图标，默认是评价图标)
SobotUIConfig.sobot_title_right_menu2_bg = R.drawable.sobot_phone;
//设置toolbar右边第二个按钮为拨号功能（有值则为拨号功能，默认是评价功能）
SobotUIConfig.sobot_title_right_menu2_call_num = "185xxxxxxxx";
```
### 4.6.2 动态控制显示标题栏的头像和昵称
默认显示只显示头像

```js
/**
 * 设置聊天界面头部标题栏的昵称模式
 *
 * @param context      上下文对象
 * @param title_type   titile的显示模式
 *    SobotChatTitleDisplayMode.Default:显示客服昵称(默认)
 *    SobotChatTitleDisplayMode.ShowFixedText:显示固定文本
 *    SobotChatTitleDisplayMode.ShowCompanyName:显示console设置的企业名称
 * @param custom_title 如果需要显示固定文本，需要传入此参数，其他模式可以不传
 * @param isShowTitle  是否显示标题
 */
SobotApi.setChatTitleDisplayMode(getApplicationContext(),
        SobotChatTitleDisplayMode.ShowFixedText, "昵称", true);        
      
 
/**
 * 设置聊天界面标题栏的头像模式
 *
 * @param context           上下文对象
 * @param avatar_type       头像的显示模式
 * SobotChatAvatarDisplayMode.Default:显示客服头像(默认)
 * SobotChatAvatarDisplayMode.ShowFixedAvatar:显示固定头像
 * SobotChatAvatarDisplayMode.ShowCompanyAvatar:显示console设置的企业名称
 * @param custom_avatar_url 如果需要显示固定头像，需要传入此参数，其他模式可以不传
 * @param isShowAvatar      是否显示头像
 */
SobotApi.setChatAvatarDisplayMode(getApplicationContext(),SobotChatAvatarDisplayMode.Default, "https://sobot-test.oss-cn-beijing.aliyuncs.com/console/66a522ea3ef944a98af45bac09220861/userImage/20191024164346682.PNG", false);
```
### 4.6.3 控制横竖屏显示开关
```js
//true 横屏 ，false 竖屏； 默认 false 竖屏
SobotApi.setSwitchMarkStatus(MarkConfig.LANDSCAPE_SCREEN,false);
```
## 4.7 其他配置
### 4.7.1 自定义自动应答语
sdk中的自动应答语可以在pc工作台进行动态设置，

如果pc工作台的设置满足不了您的需求，那么您可以使用以下接口在代码中进行本地配置

注意：本地设置本地优先，PC端不在起效

```js
//自定义客服欢迎语,默认为空 （如果传入，优先使用该字段）
SobotApi.setAdmin_Hello_Word(getApplicationContext(), "自定义客服欢迎语");
//自定义机器人欢迎语,默认为空 （如果传入，优先使用该字段）
SobotApi.setRobot_Hello_Word(getApplicationContext(), "自定义机器人欢迎语");
//自定义用户超时提示语,默认为空 （如果传入，优先使用该字段）
SobotApi.setUser_Tip_Word(getApplicationContext(), "自定义用户超时提示语");
//自定义客服超时提示语,默认为空 （如果传入，优先使用该字段）
SobotApi.setAdmin_Tip_Word(getApplicationContext(), "自定义客服超时提示语");
// 自定义客服不在线的说辞,默认为空 （如果传入，优先使用该字段）
SobotApi.setAdmin_Offline_Title(getApplicationContext(), " 自定义客服不在线的说辞");
// 自定义用户超时下线提示语,默认为空 （如果传入，优先使用该字段）
SobotApi.setUser_Out_Word(getApplicationContext(), " 自定义用户超时下线提示语");
```
### 4.7.2 自定义聊天记录显示时间范围
如想设置用户只能看到xx天内的聊天记录，那么可以调用以下方法进行设置:

```js
/**
 * 控制显示历史聊天记录的时间范围
 * @param time  查询时间(例:100-表示从现在起前100分钟的会话)
*/
SobotApi.setScope_time(context,time);
```
### 4.7.3 “+”号面板菜单扩展
客服聊天界面中点击“+”按钮后所出现的菜单面板，可以根据需求自行添加菜单，代码如下：

```js
 private void customMenu(){
        //添加扩展菜单数据
        ArrayList<ChattingPanelUploadView.SobotPlusEntity> objects = new ArrayList<>();
        /**
         * SobotPlusEntity为自定义菜单实体类
         * @param iconResId 菜单图标 drawableId
         * @param name      菜单名称
         * @param action    菜单动作 当点击按钮时会将对应action返回给callback
         *                  以此作为依据，判断用户点击了哪个按钮
         */
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "位置", "action_location"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "签到", "action_sing_in"));
        objects.add(new ChattingPanelUploadView.SobotPlusEntity(R.drawable.sobot_camera_picture_button_selector, "收藏", "action_ollection"));
        //添加数据
        SobotUIConfig.pulsMenu.menus = objects;
        //设置回调
        SobotUIConfig.pulsMenu.sSobotPlusMenuListener = new SobotPlusMenuListener() {
            @Override
            public void onClick(View view, String action) {
                //action与实体类中的action对应
                ToastUtil.showToast(getApplicationContext(), "action:"+action);
            }
        };
    }
```
### 4.7.4  调起拨号界面接口
```js
/**
* @param phone 是传入的电话号码 context 上下文对象
*/
CommonUtils.callUp(String phone, Context context)
```
### 4.7.5** 注销**
用户在应用中退出登陆时需要调用 SDK 的注销操作（只在切换账号时调用），该操作会通知服务器进行推送信息的解绑，避免用户已退出但推送依然发送到当前设备的情况发生。当用于用户退出登录时调用以下方法：

注意：调用此方法会造成通道连接断开，此时用户将无法收到消息。

```js
/**
* @param context 上下文对象
*/
SobotApi.exitSobotChat(context);
```
### 4.7.6 智齿日志显示开关
```js
//日志显示开关 true 打开；false 关闭；默认关闭
LogUtils.isDebug = true;
//日志显示各级别开关 true 显示；false 不显示
LogUtils.allowI = true;
LogUtils.allowD = false;
LogUtils.allowE = false;
LogUtils.allowI = false;
LogUtils.allowV = false;
LogUtils.allowI = false;
```
## 4.8 多语言支持
目前SDK支持英文和中文两种语言，语言会根据当前手机语言自行切换适配，如果当前手机语言不识别，默认使用中文。

如果需要新增语言包，把支持的语言文件放入对应的语言目录下即可，例如；英文路径：sobotsdk/src/main/res/values-en/strings.xml;

[说明：语言文件夹名称为values-的后面加上语言的标示，例如values-en；strings.xml名字不变]

## 4.9 Information类说明
### id 相关：
| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----|:----|:----|:----|
| app_key   | String   | 必须设置，不设置初始化不成功。   | 必填   | 
| choose_adminid   | String   | 指定客服ID   |    | 
| tran_flag   | int   | 定指客服 转接类型    |  0 可转入其他客服  1 必须转入指定客服   | 
| partnerid   | String   | 用户唯一标识   | 对接用户可靠身份，不能写死，不建议为null，如果为空会以设备区别   | 
| robotCode   | String   | 对接机器人ID   |    | 
|    |    |    |    | 

电商版：

设置电商转人工溢出策略，以下属性与transferaction冲突，如果设置transferaction，将覆盖flow_type、flow_companyid、flow_groupid的配置。


| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----:|:----:|:----:|:----|
| customer_code | String | 商户对接id （仅电商版适用，如果没有app_key，请提供此编码） |    | 
| flow_type   | int   | 跨公司转接人工(仅电商版本可用)   |    | 
| flow_companyid   | String   | 转接到的公司ID   |    | 
| flow_groupid   | String   | 转接到的公司技能组   |    | 


### 客服工作台显示：

| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----|:----|:----|:----|
| user_nick   | String   | 昵称   |    | 
| user_name   | String   | 真实姓名   |    | 
| user_tels   | String   | 用户电话   |    | 
| user_emails   | String   | 用户邮箱   |    | 
| qq   | String   | qq   |    | 
| user_sex   | String   | 用户的性别   | 0男 1女   | 
| weibo   | String   | 微博   |    | 
| weixin   | String   | 微信   |    | 
| birthday   | String   | 生日   |    | 
| remark   | String   | 备注   |    | 
| face   | String   | 用户自定义头像   |    | 
| visit_title   | String   | 接入来源页面标题   |    | 
| visit_urL   | String   | 接入的来源URL   |    | 
| params   | String   | 用户资料   |    | 
| customer_fields   | String   | 固定KEY的自定义字段   |    | 
| group_name   | String   | 技能组名称   |    | 
| groupid   | String   | 技能组编号   |    | 

### 说辞相关：
| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----|:----|:----|:----|
| admin_hello_word   | String   | 自定义客服欢迎语,默认为空 （如果传入，优先使用该字段）   |    | 
| robot_hello_word   | String   | 自定义机器人欢迎语,默认为空 （如果传入，优先使用该字段）   |    | 
| user_tip_word   | String   | 自定义用户超时提示语,默认为空 （如果传入，优先使用该字段）   |    | 
| admin_offline_title   | String   | 自定义客服不在线的说辞,默认为空 （如果传入，优先使用该字段）   |    | 
| admin_tip_word   | String   | 自定义客服超时提示语,默认为空 （如果传入，优先使用该字段）   |    | 
| user_out_word   | String   | 自定义用户超时下线提示语,默认为空 （如果传入，优先使用该字段）   |    | 


### 会话页面相关：
| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----|:----|:----|:----|
| service_mode   | int   | 自定义接入模式  1只有机器人,2.仅人工 3.智能客服-机器人优先 4智能客服-人工客服优先   |    | 
| custom_title_url   | String   | 聊天页顶部标题 自定义图像路径    |    | 

### 其他：
| 属性名称 | 数据类型 | 说明 | 备注 | 
|:----|:----|:----|:----|
| transferaction   | String   | 转人工 指定技能组 溢出    |    | 
| summary_params   | String   | 转人工自定义字段   |    | 
| multi_params   | String   | 多轮会话 自定义字段   |    | 
| margs   | String   | 热点引导问题的扩展字段   |    | 
| content   | String   | 自动发送商品订单信息内容   |    | 
| queue_first   | boolean   | 指定客户优先   |    | 
| isArtificialIntelligence   | boolean   | 是否智能转人工,默认false     |    | 
| artificialIntelligenceNum   | int   | 如果是只能转人工，那么未知问题或者向导问题出现多少次时，显示转人工按钮。默认是一次     |    | 
| isUseVoice   | boolean   | 是否使用语音功能 默认true，可以使用语音功能   |    | 
| isUseRobotVoice   | boolean   | 是否使用机器人语音功能 默认false，机器人不可以使用语音功能  会转为文字   |    | 
| isShowSatisfaction   | boolean   | 关闭时是否弹出满意度评价。默认false，弹出，false不弹满意度   |    | 
| isShowCloseSatisfaction   | boolean   | 导航栏关闭按钮关闭时是否弹出满意度评价。默认false，弹出，false不弹满意度。   |    | 
| equipmentId   | String   | 设备编号   |    | 
| tranReceptionistFlag   | int   | 转接类型(0-可转入其他客服，1-必须转入指定客服)   |    | 
| transferKeyWord   | HashSet   | 转人工关键字   |    | 


## 5 Demo源码
您可以通过 [点击这里](https://github.com/ZCSDK/SobotSDK_Android) 下载 Demo 源码

## 6 常见问题链接
   [https://github.com/ZCSDK/FAQ_Android](https://github.com/ZCSDK/FAQ_Android) 
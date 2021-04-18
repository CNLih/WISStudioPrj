# Android学习记录

## Week1

### RecyclerView

https://www.jianshu.com/p/4f9591291365

##### 四大组成

1. Layout Manager：Item的布局。
2. Adapter：为Item提供数据。
3. Item Decoration：Item之间的Divider。
4. Item Animator：添加、删除Item动画。



##### 涉及java语法：

内部类：一个类中嵌套着另外一个类。 它有访问外部类成员的权限

`  OuterClass myOuter = new OuterClass();
OuterClass.InnerClass myInner = myOuter.new InnerClass();`

静态类static：可以不通过创建外部类就可以直接访问

`OuterClass.InnerClass myInner = new OuterClass.InnerClass();`

遇见的bug

```java
private List<News> newsList = new ArrayList<>();    //之前未进行实例化就调用
```



##### recyclerView的分类

添加布局部分：

```java
//纵向线性布局
LinearLayoutManager layoutManager = new LinearLayoutManager(this);
 
//纵向线性布局，网格
GridLayoutManager layoutManager = new GridLayoutManager(this,2);

//瀑布流
StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
```

![RecyclerViewDemo](res\RecyclerViewDemo.png)

## Week2

### 通过url加载网页图片

在主线程中加载网页图片不可行

遇到问题：

android.os.NetworkOnMainThreadException （主线程不可以访问网络）

Only the original thread that created a view hierarchy can touch its views.（子线程中不可以更改view）

解决方法：

[博客](https://blog.csdn.net/lmj623565791/article/details/38476887)

简易的一个流程：当需要加载一张图片，首先把加载图片加入任务队列，然后使用loop线程（子线程）中的hander发送一个消息，提示有任务到达，loop()（子线程）中会接着取出一个任务，去加载图片，当图片加载完成，会使用UI线程的handler发送一个消息去更新UI界面。



用

```java
private Handler mhandler = new Handler(){
    public void handleMessage(Message msg){
        
    };
};
```

警告：

This Handler class should be static or leaks might occur 

（匿名内部类持有外部对象）

然后通过new Handler.Callback()解决

当使用了非静态的匿名 Handler 类，会提示 This Handler class should be static or leaks may occur (anonymous android.os.Handler)

[博客Handler.Callback](https://blog.csdn.net/qq_17513815/article/details/46534429)

涉及内存泄漏这部分由于语法薄弱暂时不深究



##### 改进：通过AsyncTask类进行图片加载

AsyncTask -- 更好的封装

 `class DownloadTask extends AsyncTask<void, Integer, Boolean>`

传入后台任务中的参数、显示进度单位、对结果进行返回

需要重写：

```java
onPreExecute();           //开始前
doInBackground(Params...);//后台进行（不可更新View）
onProgressUpdate(Progress...);
onPostExecute(Result);    //结束
```

`new DownloadTask().excute()`





##### 涉及java语法：

##### 匿名类：

匿名类是指没有类名的内部类

实例：

```java
private Handler handler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }
});
```

其中`Handler.Callback()`官方解释是_Callback interface you can use when instantiating a Handler to avoid having to implement your own subclass of Handler._

##### Java可变参数

[菜鸟教程](https://www.runoob.com/w3cnote/java-varargs-parameter.html)

```java
void foo(String... args);
void foo(String[] args);
```

##### Java方法签名

方法声明的**两个组件构成了方法签名 - 方法的名称和参数类型（法名+形参列表）**

覆盖（重写）必须方法签名一样，变长参数在编译为字节码后，在方法签名中就是以数组形态出现的

##### co-variant协变返回类型

子类覆盖（重写）父类的方法，子类方法的返回值类型，可以是父类方法返回值类型的子类。这种返回值类型就叫做：co-variant。

##### Thread

![img](res\java-thread.jpg)



## Week3

JSONArray

```java
JSONArray jsonArray = new JSONArray();
JSONObject jb = new JSONObject();
jb.put("id", 1);
jb.put("name", "s");
jsonArray.add(jb);
//字符串转json
JSONObject jsonObject = new JSONObject(json);
JSONArray jsonArray = new JSONArray(json);

//遍历
StringBuffer 
```

关于无key的json解析：

["https://cdn.shibe.online/shibes/86c48b9ebf5d0ce072a7fb9fa6d0264eafbec150.jpg","https://cdn.shibe.online/shibes/266b1c82ad75345fba0800e77329a119d9df2cca.jpg","https://cdn.shibe.online/shibes/2d844c1c11130eaa337f8dd25b79b845475e6052.jpg","https://cdn.shibe.online/shibes/8b16885b259877cb3efe23b4cc3f9c8a4e594414.jpg","https://cdn.shibe.online/shibes/b805ad5d22fa4ae77c5190f58bea8bb782a1fe72.jpg]

用`jsonArray.getString(i)`下标的形式即可



##### 涉及java语法：

##### List

以下是实现` List `接口的两个类:

- ArrayList
- LinkedList

```java
List<String> list = new ArrayList<>();
list.add("java");                    //add
int count = list.size();             //size
String element = list.get(i);        //get
List<String> subList = list.subList(1, 3);   //sub
list.remove("CSS");                  //remove
System.out.println(list);

ListIterator<String> fullIterator = list.listIterator();   //迭代器遍list.listIterator(5);
    while (iterator.hasNext()) {
      int index = iterator.nextIndex();
      String element = iterator.next();
      System.out.println("Index=" + index + ", Element=" + element);
    }
    while (iterator.hasPrevious()) {
      int index = iterator.previousIndex();
      String element = iterator.previous();
      System.out.println("Index=" + index + ",  Element=" + element);
    }
list.addAll(list2);
```

遍历list

1、迭代器遍历，方便删除元素

```java
Iterator<Goods> iterator = list.iterator();
while (iterator.hasNext()) {
	Goods good = iterator.next();
	if(good.getGid==20){
		iterator.remove();
	}
}
```

2、增强for循环

```java
for (Goods good : list) {
    if (good.getGid==20) {
        list.remove(good);
        break;
    }
}
```

3、普通for循环

```
for (int i = 0; i < list.size(); i++) {
	Goods good = list.get(i);
	if (good.getGid==20) {
		list.remove(i);
		break;
	}
}
```

4、Lambda表达式

```
list.forEach(one->{
    if (one.getGid()==20) {
        list.remove(one);
        return;
    }
});
```



## Week4

用recyclerview单次加载6张照片

final修饰变量产生同步问题（线程问题）

```
final List<String> tmp = null;
new Thread(new Runnable() {
    @Override
    public void run() {
        tmp = getUrls(n);
    }
}).start();
```

解决办法：利用SynchronousQueue阻塞

#### 缓冲池：

##### 单例模式 [简书](https://www.jianshu.com/p/dde4f1f1f569)

确保一个类只有一个实例

```java
//饿汉模式，在类初始化时自行实例化
public class Singleton {    
     private static final Singleton single = new Singleton();

     private Singleton() {
     }
     //静态工厂方法
     public static Singleton getInstance() {
         return single;
     }
 }
//使用
Singleton.getInstance().xx();
//问题：多线程并发，多线程同时访问这个类，创建多个实例
//改进
public static synchronized Singleton get Instance(){
    if(single == null){
        single = new Singleton();
    }
    return single;
}
//问题：造成同步开销（每次调用instance都调用synchronized锁）
//双重检查加锁
public class Singleton {
    private volatile static Singleton instance = null;
    private Singleton(){}
    public static Singleton getInstance(){
        if(instance == null){   //先检查实例是否存在，如果不存在才进入下面的同步块
            synchronized (Singleton.class) {    //同步块，线程安全的创建实例 
                if(instance == null){   //再次检查实例是否存在，如果不存在才真正的创建实例
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
//关于volatile（插入许多内存屏障指令来保证处理器不发生乱序执行）  获得锁的线程正在执行构造函数的时候，其他的线程执行到第一次检查if (m_instance == null)的时候，会返回false，因为已经在执行构造函数了，就不是null。因此，会把没有构造完全的对象返回给线程使用，这是不安全的。
//静态内部类单例模式
public class Singleton {

    private Singleton(){}
    //静态的成员式内部类，该内部类的实例与外部类的实例没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载
    private static class SingletonHolder{
        private static Singleton instance = new Singleton();    //静态初始化器，由JVM来保证线程安全
    }

    public static Singleton getInstance(){
        return SingletonHolder.instance;
    }
}
//实现Singleton的最佳方法：单元素的枚举类型
//不理解，总之“JVM保证这个方法绝对只调用一次”
public enum SingletonEnum {
   INSTANCE;
   public void doSomething() {
      //doSomething... 
   }
}
//调用
SingletonEnum.INSTANCE.doSomething();
```





#### 附

Rotrofit是一个 RESTful 的 HTTP 网络请求框架的封装

Glide开源图片加载框架



##### 涉及java语法：

[synchronized](https://blog.csdn.net/luoweifu/article/details/46613015)

synchronized是Java中的关键字，是一种同步锁。它修饰s的对象有以下几种：
1. 修饰一个代码块，被修饰的代码块称为同步语句块，其作用的范围是大括号{}括起来的代码，作用的对象是调用这个代码块的对象；`synchronized(this){..}`
2. 修饰一个方法，被修饰的方法称为同步方法，其作用的范围是整个方法，作用的对象是调用这个方法的对象；
3. 修改一个静态的方法，其作用的范围是整个静态方法，作用的对象是这个类的所有对象；
4. 修改一个类，其作用的范围是synchronized后面括号括起来的部分，作用主的对象是这个类的所有对象。

synchronized只锁定对象

```java
SyncThread syncThread = new SyncThread();
Thread thread1 = new Thread(syncThread, "SyncThread1");   //Thread.currentThread().getName(); -- SyncThread1
Thread thread2 = new Thread(syncThread, "SyncThread2");
thread1.start();
thread2.start();
```


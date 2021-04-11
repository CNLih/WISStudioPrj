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



**涉及java语法：**

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


# Android学习记录



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
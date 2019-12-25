package com.testdemo.testjava

/**
 * Create by Greyson
 */
fun main() {
    val paint = Paint()
    val myBook = Book()
    val myThing = Thing()

    val store = Store()
    store.save(myBook)
    store.save(myThing)
    store.save(paint)

    paint.usage

    val store2 = Stores<Goods>()
    store2.save(myBook)
    store2.save(paint)
//    store2.save(myThing)//这里会报错，因为我们已经通过泛型限制了只能存Goods的子类的对象

    val store3 = Stores<Thing>()
    store3.save(myBook)
    store3.save(paint)
    store3.save(myThing)//随时可以修改成其它类，并只有其子类的对象才可存
    store3.get()?.exist()

    var store4: Stores<out Goods>? = null
    store4 = Stores<Goods>()
    store4 = Stores<Paint>()
//    store4 = Stores<Thing>()//会报错

    var store5: Stores<in Goods>? = null
    store4 = Stores<Goods>()
//    store5 = Stores<Paint>()
//    store4 = Stores<Thing>()//都会报错

    //todo 泛型List保存和取数据的实验
//    var list: ArrayList<out Goods> = ArrayList<Thing>()//报错？out不是跟Java的super关键字一样吗？
    var list2: ArrayList<in Goods> = ArrayList<Thing>()
    list2.add(Paint())
    list2.add(Goods())
//    list2.add(Thing())
    /*var goods20: Thing = list2.get(0)
    var goods2: Goods = list2.get(0)
    var goods21: Paint = list2.get(0)*///全部报错，传说中的取出不安全，传入安全？对比下面的

    var list3: ArrayList<out Goods> = ArrayList<Paint>()
    /*list3.add(Paint())
    list3.add(Goods())
    list3.add(Thing())*/ //全部报错
    list3.get(0)
//    var goods3: Paint = list3.get(0)//传说中的取出安全，传入不安全！！
    var goods31: Thing = list3.get(0)
}

class Stores<T> {
    var store: T? = null
    fun save(data: T) {
        store = data
    }

    fun get(): T? {
        return store
    }
}

// 因为继承和多态的特性，Store中的Thing变量也可以存他的子类对象，对Paint、Goods对象；但在Store
// 没有泛型时，如果想把存储的对象的类型限制在Goods的子类中，而不是Thing的子类时，就得重新创建一个类
// ，将Store类里面的存储变量的类型改为Goods，这样就只有Goods的子类的对象才能传入。
class Store {
    var thing: Thing? = null

    fun save(data: Thing) {
        thing = data
    }

    fun get(): Thing? {
        return thing
    }
}

open class Thing {
    var name: String? = null
    open fun exist(): Boolean {
        return false
    }
}

open class Goods : Thing() {
    var usage: String? = null
}

class Paint : Goods() {
    var length = 0
}

class Book : Goods() {
    var thickness = 0
}
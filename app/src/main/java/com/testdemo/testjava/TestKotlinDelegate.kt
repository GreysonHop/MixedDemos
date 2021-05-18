package com.testdemo.testjava

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 官网：https://kotlinlang.org/docs/delegated-properties.html#providing-a-delegate
 */
fun main() {
    val e = Example()
    e.p = "NEW"
    println(e.p)


    // 委托的用法——监听
    var testBy by Delegates.observable(0) { property, oldV, newV ->
        println("参数 testBy 从${oldV}变成${newV}")
    }
    testBy = 110


    val user = User(mapOf(
        "name" to "Caku Greyson",
        "age" to 29
    ))


}

/* Custom Delegate */
class Example {
    var p: String by Delegate()
}

class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}


/* Delegating to another property */
var topLevelInt: Int = 0

class ClassWithDelegate(val anotherClassInt: Int)

/*class MyClass(var memberInt: Int, val anotherClassInstance: ClassWithDelegate) {
    var delegatedToMember: Int by this::memberInt
    var delegatedToTopLevel: Int by ::topLevelInt

    val delegatedToAnotherClass: Int by anotherClassInstance::anotherClassInt
}

var MyClass.extDelegated: Int by ::topLevelInt*/


/** Storing properties in a map
 * One common use case is storing the values of properties in a map.
 * This comes up often in applications like parsing JSON or doing other “dynamic” things.
 * In this case, you can use the map instance itself as the delegate for a delegated property.
 */
class User(val map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

class MutableUser(val map: MutableMap<String, Any?>) {
    var name: String by map
    var age: Int by map
}


/**
 * 使用匿名内部类
 */
fun resourceDelegate(): ReadWriteProperty<Any?, Int> =
        object : ReadWriteProperty<Any?, Int> {
            var curValue = 0
            override fun getValue(thisRef: Any?, property: KProperty<*>): Int = curValue
            override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
                curValue = value
            }
        }

val readOnly: Int by resourceDelegate()  // ReadWriteProperty as val
var readWrite: Int by resourceDelegate()

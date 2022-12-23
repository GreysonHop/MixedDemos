package testJava

import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

/**
 * Create by Greyson on 2021/10/23
 */
class TestKotlinReflect {
    var name = "init"

    /*fun setName(name: String) {
        this.name = name
    }*/

    fun act() {
        println("outer act!")
    }

    fun method(i: Int) {
        println("method for Int: $i")
    }
    fun method(s: String) {
        println("method for String: $s")
    }

    var innerMember = InnerClass()
    inner class InnerClass {
        var innerName = "initInner"
        fun act(code: Int) {
            println("inner act for $code")
        }
    }
}

fun main() {
    val clazz = TestKotlinReflect::class
    val ins = clazz.createInstance()


    val m0: () -> Unit = ins::act
    val m1: TestKotlinReflect.()->Unit = TestKotlinReflect::act

    fun m(x: ()->Unit) {}
    m(m0)


    clazz.declaredFunctions.forEach { func ->
        println("当前方法名：${func.name}")
        println("参数：")
        for (parameter in func.parameters) {
            println("\t${parameter.name}, ${parameter.index}, ${parameter.kind}, ${parameter.type.apply { "??? $classifier - $arguments" }}")
        }

        // kotlin反射找一个方法时，除了方法的名字外，如何通过参数的类型来判断定位具体方法？
        /*when(func.parameters.size) {
            1 -> println("找方法1： ${func.name}(${func.parameters[0].name})")
            else -> println("找方法： ${func.name}(${func.parameters[1].name}[${func.parameters[1].type}])")
        }*/

        if (func.parameters.size == 2 && func.parameters[1].type.javaType == String::class.java) {
            func.call(ins, "fuck!!")
        }

        if (func.parameters.size == 2 && func.parameters[1].type.classifier == Int::class) {
            func.call(ins, 110)
        }

        /*println("type?: ${String::toString}")
        if (func.parameters.size == 2) {
            println("---${func.parameters[1].type.classifier}")

            println("ii__${func.parameters[1].type.classifier == String::class}")
        }*/
        println()
    }

    val outerInstance = TestKotlinReflect()
    println("name=${outerInstance.name}， ${outerInstance.innerMember.innerName}")

val field = outerInstance.javaClass.getDeclaredField("name")
    //field.isAccessible = true
    field.set(outerInstance, "fuck")

    val setName = outerInstance.javaClass.getDeclaredMethod("setName", String::class.java)
    setName.invoke(outerInstance, "f**kName")
    val testMethod = outerInstance.javaClass.getDeclaredMethod("act")
    testMethod.invoke(outerInstance)

    val innerMember = outerInstance.javaClass.getDeclaredField("innerMember")
    innerMember.isAccessible = true
    val innerInstance = innerMember.get(outerInstance)
    val innerName = innerInstance.javaClass.getDeclaredField("innerName")
    innerName.isAccessible = true
    innerName.set(innerInstance, "put into")
    val innerMethod = innerInstance.javaClass.getDeclaredMethod("act", Int::class.java)
    innerMethod.invoke(innerInstance, 110)


    println("new name=${outerInstance.name}, new innerName=${outerInstance.innerMember.innerName}")

}
package testJava

import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

open class TestKotlin {
    fun getString(): Int {
        return this.hashCode()
    }

    //想被继承必须使用open，除了接口（及其方法）
    open fun printThing() {
        println("Test")
    }

    fun <R> myLet(sign: Int, block: (Int) -> R)/* : R */ = block(sign)

    /*fun canNoParam() {//会优先调用这个函数而不是有默认值的带参函数
        println("canNoParam")
    }*/
    @JvmOverloads
    fun canNoParam(param: String = "yes", param2: Int, param3: Boolean) {
        println("canNoParam param = $param")
        printMy()
    }
}

class SunTest : TestKotlin {
    constructor(n: String)

    override fun printThing() {
        println("SunTest")
    }
}

//扩展方法
fun TestKotlin.addMethod(param: String) {
    println("addition receive param = $param")
}

//扩展变量
val SunTest.addition: String
    get() = "la-add" //扩展变量不能直接初始化，必须通过getter

fun hasPrefix(x: Any) = when (x) {
    is String -> x.startsWith("prefix")
    is Int -> x < 0
    else -> false
}

//when的使用方式，以及方法的声明和重载方式：可以只用一个表达式
fun max(any: Any) = when {
    any is Int && any > 0 -> {
        print("oh!")
        any - 1
    }

    any is String && any.startsWith("-") -> {
        print("greyson")
        "$any."
    }

    else -> Any()
}

fun foo() {
    val ints = intArrayOf(1, 2, 3, 4, 5, 6)
    run break1@{
        ints.forEach continue1@{
            if (it == 3) return@continue1 //等于[return forEach]
            if (it == 5) return@break1 //forEach之类的
            println(it)
        }
        println("fun 'forEach' end.")
    }
    println("fun 'run' end.")
}

fun getRunnable(): Runnable {
    return Runnable { /* 不用object的内部类写法 */ }
}

fun main(args: Array<String>) {
    var a = 1
    val s1 = "a is $a"
    a = 2
    val s2 = "${s1.replace("is", "was")}, but now is $a"
    println("$s2 - ${hasPrefix("prefixcc")}")

    foo()

    val testInstance = TestKotlin()
    println("Test's code = ${TestKotlin().getString()}\nTest's canNoParam invoke:")
    testInstance.canNoParam(param2 = 0, param3 = false)
    testInstance.canNoParam("", 0, true)

    var test = SunTest(s1)
    test.printThing()
    test.addMethod("addadd")
    println("扩展的变量：${test.addition}")
    println(test.myLet(111) { println("自己定义的Lambda表达式，参数为：$it"); return@myLet "no" })

    var testpp = 1
    println("-p = ${-testpp}, unary = ${testpp.unaryMinus()}")

    val testJava = TestJava()
    testJava.setOnClickListener { }
    testJava.setOnClickListener { println(it) }
    var testJavaResult: String
    testJavaResult = testJava.getInstance(1) ?: ""
    println("now the testJavaResult is $testJavaResult")


    val (x: Float, y: Float) = "".let {
        0f to 0.1f
    }
    println("show me the x[$x] and y[$y]")

    val clazz = TestKotlin::class
    clazz.declaredFunctions
    clazz.constructors.forEach { it.call() }
    String::length.javaField


    val f: TestKotlin.() -> Unit = TestKotlin::printThing
    val fo: () -> Unit = ::foo

}

fun main2() = runBlocking<Unit> {
    launch {  }
}
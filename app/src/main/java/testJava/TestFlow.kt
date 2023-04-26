package testJava

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Create by Greyson on 2022/08/28
 */
class TestFlow {

    private val commonFlow = flow<Int> {
        println("greyson: commonFlow emit data.")
        emit(110)
    }

    fun testCommonFlow() {
        (1..3).asFlow().map {
            println("greyson: intRange asFlow map.")
            it * 3
        }
    }


    private val sharedFlow = MutableSharedFlow<Int>()

    fun testSharedFlow() {
        println("greyson: commonFlow emit data.")
        CoroutineScope(Dispatchers.Main).launch {
            sharedFlow.emit(10086)
        }
    }


    private val stateFlow = MutableStateFlow<Int>(110) //todo 调用泛型方法时明确类型。Java 做不到！

    fun testStateFlow() {
        stateFlow.value = 2
    }

}

fun produceSomething() : Flow<Int> = flow {
    for (i in 0..3) {
        print("emit$i")
        emit(i)
    }
}

fun main() = runBlocking {
    produceSomething().collect {
        print("collect: $it")
    }
}
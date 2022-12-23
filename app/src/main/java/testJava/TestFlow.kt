package testJava

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

/**
 * Create by Greyson on 2022/08/28
 */
class TestFlow {

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
package com.testdemo

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AlgorithmUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(3, lengthOfLongestSubstring("pwwkew"))
        assertEquals(4, findInMountainArray(3, intArrayOf(1,2,4,5,3,1)))
        assertEquals(4, 2 + 2)
    }

    fun lengthOfLongestSubstring(s: String): Int {
        if (s.length == 1) return 1

        var maxLength = 0
        val charSet = TreeSet<Char>()
        for (i in 0 until s.length - 1) {
            charSet.clear()
            charSet.add(s[i])

            for (j in i + 1 until s.length) {
                if (charSet.contains(s[j])) {
                    if (charSet.size > maxLength) {
                        maxLength = charSet.size
                    }
                    break
                }

                charSet.add(s[j])

                if (j == s.length - 1) {//如果到最后一个元素了还没重复
                    if (charSet.size > maxLength) {
                        maxLength = charSet.size
                    }
                }
            }
        }

        return maxLength
    }

    fun twoSum(nums: IntArray, target: Int): IntArray {
        val result = intArrayOf(-1, -1)
        for (i in 0 until nums.size - 1) {
            result[0] = nums[i]

            for (j in i + 1 until nums.size) {
                if (result[0] + nums[j] == target) {
                    result[1] = nums[j]
                    return result;
                }
            }

        }
        return result
    }

    fun twoSum2(nums: IntArray, target: Int): IntArray {
        val result = intArrayOf(-1, -1)
        val map = mutableMapOf<Int, Int>()
        nums.mapIndexed { index, value ->
            val anotherN = target - value
            if (map.containsKey(anotherN) && map[anotherN] != index) {
                result[0] = map[anotherN]!!
                result[1] = index
                return@mapIndexed
            }
            map[value] = index
        }
        return result
    }

    private fun findInMountainArray(target: Int, mountainArr: IntArray): Int {
        val length = mountainArr.size
        var start = 0
        var end = length - 1
        var mid: Int
        var topIndex = 0
        while (start < end) {//代替递归实现二分查找山顶
            mid = (start + end) / 2//每次循环的中心
            if (mountainArr[mid] > mountainArr[mid + 1]) {//山顶在mid之前（包括mid)
                end = mid
                topIndex = mid
            } else {
                start = mid + 1
                topIndex = mid + 1
            }
        }

        //下面开始二分查找目标值的下标
        var targetIndex = -1
        start = 0
        end = topIndex
        var startVal: Int
        var endVal: Int
        var midVal: Int
        while (start <= end) {//先从前半段开始找
            startVal = mountainArr[start]
            if (startVal == target) {
                targetIndex = start
                break
            }
            endVal = mountainArr[end]
            if (endVal == target) {
                targetIndex = end
                break
            }

            mid = (start + end) / 2//每次循环的中心
            midVal = mountainArr[mid]
            if (midVal == target) {
                targetIndex = mid
                break
            }

            if (target > endVal) {//因为==等于的情况上面已经判断过了，所以下面可以不判断
                break
            } else if (target > midVal) {
                start = mid + 1
                end -= 1
            } else if (target > startVal) {
                start += 1
                end = mid - 1
            } else {//target < startVal
                break
            }
        }

        if (targetIndex != -1) {
            return targetIndex
        }

        //从后段查找target，这时StartVal比EndVal大！
        start = topIndex
        end = length - 1
        while (start <= end) {
            startVal = mountainArr[start]
            if (startVal == target) {
                targetIndex = start
                break
            }
            endVal = mountainArr[end]
            if (endVal == target) {
                targetIndex = end
                break
            }

            mid = (start + end) / 2//每次循环的中心
            midVal = mountainArr[mid]
            if (midVal == target) {
                targetIndex = mid
                break
            }

            if (target > startVal) {
                break
            } else if (target > midVal) {
                start += 1
                end = mid - 1
            } else if (target > endVal) {
                start = mid + 1
                end -= 1
            } else {
                break
            }
        }

        return targetIndex
    }
}

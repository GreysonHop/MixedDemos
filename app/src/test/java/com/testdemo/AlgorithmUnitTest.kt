package com.testdemo

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.math.abs
import kotlin.math.max

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AlgorithmUnitTest {
    @Test
    fun addition_isCorrect() {
        val root = TreeNode(3)
        val node2 = TreeNode(5)
        val node3 = TreeNode(1)
        val node4 = TreeNode(6)
        val node5 = TreeNode(2)
        val node6 = TreeNode(0)
        val node7 = TreeNode(8)
        val node8 = TreeNode(7)
        val node9 = TreeNode(4)

        root.left = node2
        root.right = node3
        node2.left = node4
        node2.right = node5
        node3.left = node6
        node3.right = node7
        node5.left = node8
        node5.right = node9
        assertEquals(node2, lowestCommonAncestor(root, node2, node9))
        //        printTree(root)
        //
        //        assertEquals(3, lengthOfLongestSubstring("pwwkew"))
        //        assertEquals(4, findInMountainArray(3, intArrayOf(1,2,4,5,3,1)))
        //
        //        assertEquals(4, 2 + 2)


    }

    //不触动警报情报下盗窃最多金额（盗窃连续的两间房子即报警）
    fun rob(nums: IntArray): Int {
        if (nums.isEmpty()) return 0

        var maxSum = 0
        val maxMap = mutableMapOf<Int, Int>()
        var currentMax = 0
        nums.forEachIndexed { index, i ->
            currentMax = when (index) {
                0 -> {
                    i
                }
                1 -> {
                    max(maxMap[0] ?: 0, i)
                }
                else -> {
                    Math.max(maxMap[index - 1] ?: 0, (maxMap[index - 2] ?: 0) + i)
                }
            }
            maxMap[index] = currentMax
            maxSum = Math.max(maxSum, maxMap[index] ?: 0)
        }

        return maxSum
    }

    @Test
    fun testValidPalindrome() {
        assertTrue(validPalindrome("aa"))
        assertTrue(validPalindrome("a"))
        assertTrue(validPalindrome("aca"))
        assertTrue(validPalindrome("abca"))
        assertFalse(validPalindrome("cabca"))
        assertFalse(validPalindrome("dmaadedaeeddeeadedafad"))
        assertTrue(validPalindrome("aguokepatgbnvfqmgmlcupuufxoohdfpgjdmysgvhmvffcnqxjjxqncffvmhvgsymdjgpfdhooxfuupuculmgmqfvnbgtapekouga"))
    }

    //验证回文字符串（在最多可删除一个字符的情况下）
    fun validPalindrome(s: String): Boolean {
        var start = 0
        var end = s.length - 1
        var remove = false //已经删除一个
        while (start < end) {
            if (s[start] != s[end]) {
                println("greyson: start=$start,val=${s[start]} end=$end,val=${s[end]}")
                if (!remove) {

                    if (s[start + 1] == s[end]) {
                        if (start + 2 < end - 1) {
                            if (s[start + 2] == s[end - 1]) {
                                start++
                                remove = true
                                continue
                            }
                        } else {
                            return true
                        }
                    }

                    if (s[start] == s[end - 1]) {
                        if (start + 1 < end - 2) {
                            if (s[start + 1] == s[end - 2]) {
                                end--
                                remove = true
                                continue
                            }
                        } else {
                            return true
                        }


                    } else {
                        return false
                    }

                } else {
                    return false
                }

            }
            start++
            end--
        }
        return true
    }

    @Test
    fun testSubarraysDivByK() {
        assertEquals(7, subarraysDivByK(intArrayOf(4, 5, 0, -2, -3, 1), 5))
        assertEquals(2, subarraysDivByK(intArrayOf(1, 1, 1), 2))
        assertEquals(2, subarraysDivByK(intArrayOf(1, 0, 1), 2))
        assertEquals(3, subarraysDivByK(intArrayOf(1, 0, -2), 2))
        assertEquals(2, subarraysDivByK(intArrayOf(2, -2, 2, -4), 6))
    }

    //和为K的倍数的子数组数目
    fun subarraysDivByK(A: IntArray, K: Int): Int {
        val map = mutableMapOf<Int, Int>() //记录当前轮询位置之前的所有不重复的前缀合，并与K取余后作为key，出现次数作为value
        var preSum = 0 //当前位置的前缀合
        var preSumYuShu = 0 //当前位置的前缀合与K取余后的值,取正数
        var count = 0
        map[0] = 1 //第一元素的前缀合，与K取余的值

        for (i in A.indices) {
            preSum += A[i]
            preSumYuShu = preSum % K

            val index = when {
                preSumYuShu == 0 -> {
                    0
                }
                preSumYuShu > 0 -> {
                    -(K - preSumYuShu)
                }
                else -> {
                    K + preSumYuShu
                }
            }
            if (map.contains(index) && index != 0) {
                count += map[index] ?: 0
            }

            if (map.contains(preSumYuShu)) {
                count += map[preSumYuShu] ?: 0
            }
            map[preSumYuShu] = 1 + (map[preSumYuShu] ?: 0)
        }

        return count
    }

    //查找最小公共树根结点
    fun lowestCommonAncestor(root: TreeNode?, p: TreeNode?, q: TreeNode?): TreeNode? {
        findTree(root, p, q)
        return finalR
    }

    var finalR: TreeNode? = null
    fun findTree(root: TreeNode?, p: TreeNode?, q: TreeNode?): Boolean {
        if (root == null) {
            return false
        }

        val leftFound = findTree(root.left, p, q)
        val rightFound = findTree(root.right, p, q)

        val isRoot = root == p || root == q

        if (finalR == null && (
                        leftFound && rightFound
                                || leftFound && isRoot
                                || rightFound && isRoot
                        )
        ) {
            finalR = root //既然找到目标，能否快速跳出所有递归？
            return true
        }
        return leftFound || rightFound || isRoot
    }

    fun printTree(root: TreeNode?) { //todo 这里是中序遍历，请实现后序遍历！
        var node = root
        val stack = Stack<TreeNode>()

        while (node != null || stack.isNotEmpty()) {
            while (node != null) {
                stack.push(node)
                node = node.left
            }

            if (stack.isNotEmpty()) {
                node = stack.peek()
                println("greyson: ${node.`val`}")
                node = node.right
            }
        }
    }

    class TreeNode(var `val`: Int = 0) {
        var left: TreeNode? = null
        var right: TreeNode? = null
    }

    //最长不重复子数组的长度
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

                if (j == s.length - 1) { //如果到最后一个元素了还没重复
                    if (charSet.size > maxLength) {
                        maxLength = charSet.size
                    }
                }
            }
        }

        return maxLength
    }

    //打印出和为target的两个数
    fun twoSum(nums: IntArray, target: Int): IntArray {
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

    //在山脉数组中查找target的下标
    private fun findInMountainArray(target: Int, mountainArr: IntArray): Int {
        val length = mountainArr.size
        var start = 0
        var end = length - 1
        var mid: Int
        var topIndex = 0
        while (start < end) { //代替递归实现二分查找山顶
            mid = (start + end) / 2 //每次循环的中心
            if (mountainArr[mid] > mountainArr[mid + 1]) { //山顶在mid之前（包括mid)
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
        while (start <= end) { //先从前半段开始找
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

            mid = (start + end) / 2 //每次循环的中心
            midVal = mountainArr[mid]
            if (midVal == target) {
                targetIndex = mid
                break
            }

            if (target > endVal) { //因为==等于的情况上面已经判断过了，所以下面可以不判断
                break
            } else if (target > midVal) {
                start = mid + 1
                end -= 1
            } else if (target > startVal) {
                start += 1
                end = mid - 1
            } else { //target < startVal
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

            mid = (start + end) / 2 //每次循环的中心
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

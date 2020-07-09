package com.testdemo

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.math.max

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AlgorithmUnitTest {

    @Test
    fun commonTest() {
        /**
         *           3
         *         /   \
         *        5     1
         *      /  \   / \
         *     6   2  0  8
         *    /   / \     \
         *  10   7   4     9
         */
        val root = TreeNode(3)
        val node2 = TreeNode(5)
        val node3 = TreeNode(1)
        val node4 = TreeNode(6)
        val node5 = TreeNode(2)
        val node6 = TreeNode(0)
        val node7 = TreeNode(8)
        val node8 = TreeNode(7)
        val node9 = TreeNode(4)
        val node10 = TreeNode(10)
        val node11 = TreeNode(9)

        root.left = node2
        root.right = node3
        node2.left = node4
        node2.right = node5
        node3.left = node6
        node3.right = node7
        node4.left = node10
        node5.left = node8
        node5.right = node9
        node7.right = node11
        assertEquals(node2, lowestCommonAncestor(root, node2, node9))
        printTree1(root)
        printTree0(root)
        printTree2(root)

        assertEquals(3, lengthOfLongestSubstring("pwwkew"))
        assertEquals(4, findInMountainArray(3, intArrayOf(1, 2, 4, 5, 3, 1)))
        assertEquals("fl", longestCommonPrefix(arrayOf("flower", "flow", "flight")))
    }

    @Test
    fun testAddBinary() {
        assertEquals("100", addBinary("11", "1010111000"))
    }

    private fun addBinary(a: String, b: String): String {
        var index = 0
        var move = false
        val maxLong = Math.max(a.length, b.length)
        val resultStr = StringBuilder()

        while (index < maxLong || move) { //如果最高位相加有进位，则继续计算更高一位的值
            val char1 = if (index < a.length) a[a.length - index - 1] else '0'
            val char2 = if (index < b.length) b[b.length - index - 1] else '0'

            var jinwei = false
            val c = if (char1 != char2) {
                if (move) { //前面有进位，加上当前的1，也得进位
                    jinwei = true
                    '0'
                } else {
                    '1'
                }

            } else if (char1 == '0') {
                if (move) '1' else '0'

            } else {
                jinwei = true
                if (move) {
                    '1'
                } else {
                    '0'
                }
            }

            index++
            move = jinwei
            resultStr.insert(0, c)
        }

        return resultStr.toString()
    }

    @Test
    fun testTranslateNum() {
        assertEquals(3, translateNum(2147483647))
        assertEquals(1, translateNum(-1))
        assertEquals(1, translateNum(0))
        assertEquals(2, translateNum(-10))
        assertEquals(2, translateNum(-101))
        assertEquals(5, translateNum(12258))
        assertEquals(2, translateNum(5108))
        assertEquals(4, translateNum(1000100001))
        assertEquals(4, translateNum(1492916348))
    }

    //面试题46. 把数字翻译成字符串
    fun translateNum(num: Int): Int {
        if (num > -10 && num < 10) { //单位数只有一种情况
            return 1
        }
        val numAbs = Math.abs(num)

        var time = 1
        val nums = arrayListOf<Int>() //将数字存为单位数的数组

        while (numAbs / time > 0 && nums.size < 10) { //int的最长位数为10
            nums.add(numAbs / time % 10)
            time *= 10
        }

        val countForIndex = mutableMapOf<Int, Int>()
        countForIndex[0] = 1 //从数组的右边到左边，下标为key，value则为能译成字符串的种类数
        nums.mapIndexed { index, value ->
            if (index < 1) {
                countForIndex[index] = 1

            } else if (index == 1) {
                countForIndex[index] = if (value == 0 || value * 10 + nums[index - 1] > 25) 1 else 2

            } else {
                if (value == 0) {
                    countForIndex[index] = countForIndex[index - 1] ?: 1
                } else {
                    countForIndex[index] = (countForIndex[index - 1] ?: 1) + //当前数字单独译成字符
                            if (value * 10 + nums[index - 1] <= 25) { //当前数字为十位数、上一个数字为个位数的两位数能译成字符
                                countForIndex[index - 2] ?: 1 //则获取这两个数字以外的数字能组成字符的情况数
                            } else {
                                0
                            }
                }

            }
        }

        return countForIndex[countForIndex.size - 1] ?: 1
    }

    //多个字符串的最长共同前缀
    fun longestCommonPrefix(strs: Array<String>): String {
        if (strs.size == 1) {
            return strs[0]
        } else if (strs.isEmpty()) {
            return ""
        }
        val result = StringBuilder()
        var index = 0 //遍历到所有字符串的第几个字符

        goto@ while (true) {
            var currentChar = '-' //只用来初始化，没什么用处的字符
            for (i in strs.indices) {
                val str = strs[i]
                if (str.length <= index) {
                    break@goto
                }

                if (i == 0) {
                    currentChar = str[index] //保存字符串数组的第一个字符串的当前遍历字符

                } else if (str[index] != currentChar) {
                    break@goto//某个字符不相等，则已经是最长相同前缀了。退出while

                } else if (i == strs.size - 1) {
                    result.append(str[index])
                }
            }

            index++
        }

        return result.toString()
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

    //前序遍历
    fun printTree0(root: TreeNode?) {
        var node = root
        val stack = ArrayDeque<TreeNode>()
        while (node != null || stack.isNotEmpty()) {
            while (node != null) {
                println("greyson前序遍历: ${node.`val`}")
                stack.push(node)
                node = node.left
            }

            if (stack.isNotEmpty()) {
                node = stack.pop()
                node = node.right
            }
        }
    }

    //中序遍历
    fun printTree1(root: TreeNode?) {
        var node = root
        val stack = ArrayDeque<TreeNode>()

        while (node != null || stack.isNotEmpty()) {
            while (node != null) {
                stack.push(node)
                node = node.left
            }

            if (stack.isNotEmpty()) {
                node = stack.pop()
                println("greyson中序遍历: ${node.`val`}")
                node = node.right
            }
        }
    }

    //后序遍历
    fun printTree2(root: TreeNode?) {
        var node = root
        val stack = ArrayDeque<TreeNode>()
        var lastVisitNode: TreeNode? = null
        while (node != null || stack.isNotEmpty()) {
            while (node != null) {
                stack.push(node)
                node = node.left
            }

            if (stack.isNotEmpty()) {
                if (stack.peek().right != null && stack.peek().right != lastVisitNode) {
                    node = stack.peek().right //右子树不为空并且还未遍历过，则先遍历右子树

                } else { //如果右子树为空或已经遍历过，则直接打印出自己
                    lastVisitNode = stack.pop()
                    println("greyson后序遍历: ${lastVisitNode.`val`}")
                }
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

    @Test
    fun testThreeSumClosest() {
        assertEquals(2, threeSumClosest(intArrayOf(-1, 2, 1, -4), 1))
        assertEquals(-1, threeSumClosest(intArrayOf(-1, 2, 1, -4), 0))
        assertEquals(-3, threeSumClosest(intArrayOf(-1, 2, 1, -4), -2))
    }

    //三个数之和最接近目标值
    fun threeSumClosest(nums: IntArray, target: Int): Int {
        nums.sort()
        var bestSum: Int? = null

        for (i in nums.indices) { //遍历三个加数中的第一个数
            if (i > 0 && nums[i - 1] == nums[i]) {
                continue
            }

            var j = i + 1
            var k = nums.size - 1 //两个变量分别为另外两个加数的下标

            while (j < k) {
                val sum = nums[i] + nums[j] + nums[k]
                if (target == sum) {//跟目标值完全一样则视为最接近目标值的结果，直接返回
                    return target
                }

                if (bestSum == null || Math.abs(sum - target) < Math.abs(bestSum - target)) {
                    bestSum = sum
                }

                if (sum > target) {
                    var k0 = k - 1
                    while (j < k0 && nums[k0] == nums[k]) {//寻找比下标k的值还小的值所对应的下标
                        k0--
                    }
                    k = k0

                } else {
                    var j0 = j + 1
                    while (j0 < k && nums[j0] == nums[j]) {//寻找比下标j的值还大的值所对应的下标
                        j0++
                    }
                    j = j0
                }
            }
        }

        return bestSum!!
    }


    @Test
    fun testMinSubArrayLen() {
        assertEquals(2, minSubArrayLen(7, intArrayOf(2, 3, 1, 2, 4, 3)))
        assertEquals(1, minSubArrayLen(7, intArrayOf(2, 3, 1, 7, 4, 3)))
        assertEquals(1, minSubArrayLen(7, intArrayOf(7, 3, 1, 7, 4, 3)))
        assertEquals(0, minSubArrayLen(3, intArrayOf(1, 1)))
        assertEquals(6, minSubArrayLen(80, intArrayOf(10, 5, 13, 4, 8, 4, 5, 11, 14, 9, 16, 10, 20, 8)))
    }

    //子数组之和大于等于目标值，求满足条件的最短子数组长度
    fun minSubArrayLen(s: Int, nums: IntArray): Int {
        if (nums.isEmpty()) {
            return 0
        }

        var minLen = 0
        var start = 0 //窗口的开端
        var end = 0
        var curSum = 0 //当前窗口内数组的值之和

        while (end < nums.size) {
            curSum += nums[end]

            if (curSum >= s) {
                minLen = with(end - start + 1) {
                    if (minLen != 0) {
                        coerceAtMost(minLen)
                    } else {
                        this
                    }
                }

                do {
                    curSum -= nums[start]
                    start++
                    if (curSum >= s) {
                        minLen = minLen.coerceAtMost(end - start + 1)
                    } else {
                        break
                    }
                } while (start < end)
            }

            end++
        }

        return minLen
    }

    @Test
    fun testUniquePathsWithObstacles() {
        assertEquals(0, uniquePathsWithObstacles(Array(1) { IntArray(1) { 1 } }))
    }

    //矩阵左上角到右角的路径
    fun uniquePathsWithObstacles(obstacleGrid: Array<IntArray>): Int {
        if (obstacleGrid.isEmpty()) {
            return 0
        }

        if (obstacleGrid[0].isEmpty()) {
            println("greyson:" + obstacleGrid[0].size)
        }

        val m = obstacleGrid.size //多少行
        val n = obstacleGrid[0].size
        val routeArray = Array(m) { Array(n) { 0 } }

        for (i in 0 until m) {
            for (j in 0 until n) {
                if (obstacleGrid[i][j] != 1) {

                    if (i == 0 && j == 0) {
                        routeArray[i][j] = 1

                    } else if (i == 0) { //第一行的元素
                        routeArray[i][j] = routeArray[i][j - 1]

                    } else if (j == 0) { //第一列
                        routeArray[i][j] = routeArray[i - 1][j]

                    } else { //不在第一行和第一列的元素
                        routeArray[i][j] = routeArray[i - 1][j] + routeArray[i][j - 1]

                    }

                }
            }
        }

        return routeArray[m - 1][n - 1]
    }

}

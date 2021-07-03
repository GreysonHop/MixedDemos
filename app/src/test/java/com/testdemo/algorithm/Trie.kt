package com.testdemo.algorithm

/**
 * Create by Greyson on 2021/06/28
 * 前缀树/字典树
 */
class Trie() {

    /** Initialize your data structure here. */
    private val root = TrieNode()

    /** Inserts a word into the trie. */
    fun insert(word: String) {
        if (word.isBlank()) return

        var pre = root
        word.forEachIndexed { i, c ->
            val index = c - 'a' // 当前要插入的字母在字母表的第几位

            val cur = pre.children[index] ?: TrieNode().also { pre.children[index] = it }

            if (i == word.lastIndex) {
                cur.flag = true
            }

            pre = cur
        }
    }

    /** Returns if the word is in the trie. */
    fun search(word: String): Boolean {
        return searchNode(word)?.flag ?: false
    }

    /** Returns if there is any word in the trie that starts with the given prefix. */
    fun startsWith(prefix: String): Boolean {
        return searchNode(prefix) != null
    }

    private fun searchNode(word: String): TrieNode? {
        var ret: TrieNode? = null
        var parent = root
        for (c in word) {
            parent.children[c - 'a']?.let {
                parent = it
                ret = it

            } ?: run { // word有一个字母在字典树中相应位置没找到
                return null
            }
        }
        return ret
    }

    class TrieNode {
        var flag = false // 标明是否存在一个完整的单词
        var children = Array<TrieNode?>(26) { null } // 26个字母
    }
}


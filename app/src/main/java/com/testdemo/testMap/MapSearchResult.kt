package com.testdemo.testMap

import com.google.gson.annotations.SerializedName

/**
 * Create by Greyson
 * 谷歌地图搜索结果实体类
 */
class MapSearchResult {
    var next_page_token: String? = null
    var results: List<Result> = arrayListOf()

    class Result {
        var id: String? = null
        var name: String? = null
        var place_id: String? = null
        var icon: String? = null
        var vicinity: String? = null//邻近区域
        @SerializedName("plus_code")
        var plusCode: PlusCode? = null
    }

    class PlusCode {
        var compound_code = ""
        var global_code = ""
    }

}

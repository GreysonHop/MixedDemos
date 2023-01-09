package com.testdemo.framework.glide;

import android.util.Log;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

import javax.annotation.Nonnull;

/**
 * Create by Greyson on 2022/12/24
 * 自定义 Glide 拓展方法。访问时如下：
 * <pre><code> GlideApp.with(this)
 * .load(url)
 * .cacheSource()
 * .into(imageView);</code></pre>
 * 注释结束。
 * <pre><code>This is a code block.</code></pre>
 */
@GlideExtension
class CustomExtension {

    private CustomExtension() {
    } // utility class


    @Nonnull
    @GlideOption
    BaseRequestOptions<?> cacheSource(BaseRequestOptions<?> options) {
        return options.diskCacheStrategy(DiskCacheStrategy.DATA);
    }

    // 你可以为方法任意添加参数，但要保证第一个参数为 RequestOptions
    @GlideOption
    BaseRequestOptions<?> customMethod(BaseRequestOptions<?> options, String tag) {
        // 不做任何处理，只是测试
        Log.d("greyson", "Glide里面的自定义 Option");
        return options;
    }

}

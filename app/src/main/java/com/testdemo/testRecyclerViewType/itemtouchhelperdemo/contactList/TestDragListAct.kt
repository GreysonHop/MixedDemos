package com.testdemo.testRecyclerViewType.itemtouchhelperdemo.contactList

import android.animation.ValueAnimator
import android.util.Log
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.*
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActTestDragListBinding
import com.testdemo.testRecyclerViewType.itemtouchhelperdemo.helper.ItemDragHelperCallback

/**
 * Created by Greyson on 2021/02/25
 */
class TestDragListAct : BaseBindingActivity<ActTestDragListBinding>() {
    override fun getViewBinding(): ActTestDragListBinding {
        return ActTestDragListBinding.inflate(layoutInflater)
    }

    override fun initView() {
        val items = ArrayList<String>()
        for (index in 0..150) {
            items.add("${index}.item")
        }

        binding.rvCollectContact.layoutManager = GridLayoutManager(this, 5)

        val callback: ItemDragHelperCallback = object : ItemDragHelperCallback() {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val movedView = viewHolder.itemView
                val targetView = target.itemView
                Log.d("greyson", "onMove() ${movedView.x}-${movedView.y}, ${targetView.x}-${targetView.y}")
                return super.onMove(recyclerView, viewHolder, target)
            }

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                Log.d("greyson", "select finish: $actionState")
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPos: Int, target: RecyclerView.ViewHolder, toPos: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                Log.d("greyson", "move finish: from $fromPos to $toPos")
            }
        }
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(binding.rvCollectContact)
        binding.rvCollectContact.adapter = ContactsAdapter(this, items).apply {
            onItemClickListener = { item, position ->
                Toast.makeText(this@TestDragListAct, item, Toast.LENGTH_SHORT).show()
            }
        }

        // MyPagerSnapHelper().attachToRecyclerView(this)
        // GridPagerSnapHelper().setRow(5).setColumn(5).attachToRecyclerView(rvCollectContact)

        binding.rvCollectContact.post {
            var oneLineHeight = 120
            binding.rvCollectContact.layoutManager?.apply {
                val child = getChildAt(0)
                if (child != null) {
                    oneLineHeight = getDecoratedMeasuredHeight(child) + child.marginTop + child.marginBottom
                    // oneLineHeight = getTopDecorationHeight(child)
                }
            }

            val originHeight = binding.rvCollectContact.height
            var isCollapse = false
            binding.ivCollapse.setOnClickListener {

                if (isCollapse) {
                    ValueAnimator.ofInt(oneLineHeight, originHeight).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Int
                            binding.rvCollectContact.layoutParams = binding.rvCollectContact.layoutParams.apply { height = value }
                        }
                        binding.rvCollectContact.suppressLayout(false)
                        duration = 300
                        start()
                    }

                    // ObjectAnimator.ofInt(rvCollectContact, "height", oneLineHeight, height).setDuration(1500).start()

                } else if (oneLineHeight != 0) {
                    ValueAnimator.ofInt(originHeight, oneLineHeight).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Int
                            binding.rvCollectContact.layoutParams = binding.rvCollectContact.layoutParams.apply { height = value }
                        }
                        addListener(onEnd = { binding.rvCollectContact.suppressLayout(true) })
                        duration = 300
                        start()
                    }
                    /*rvCollectContact.startAnimation(
                        ScaleAnimation(1f, 1f, 1f, 0.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f).apply {
                            duration = 1500
                        }
                    )*/

                    // ObjectAnimator.ofInt(rvCollectContact, "height", height, oneLineHeight).setDuration(1500).start()

                }
                isCollapse = !isCollapse
            }
        }

    }
}
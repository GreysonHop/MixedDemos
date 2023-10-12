package com.testdemo.testPictureSelect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.luck.picture.lib.entity.LocalMedia
import com.testdemo.BaseBindingActivity
import com.testdemo.databinding.ActTestPictureSelectBinding
import com.testdemo.myInflate
import kotlinx.android.synthetic.main.act_test_picture_select.*
import kotlinx.android.synthetic.main.nim_picture_panel.*
import java.io.*
import java.util.*

class TestPictureSelectAct : BaseBindingActivity<ActTestPictureSelectBinding>(), PictureSelectPanel.OnSendClickListener {
    // Request code for selecting a PDF document.
    val PICK_PDF_FILE = 2
    private lateinit var panel : PictureSelectPanel

    // TODO: Greyson_2023/10/12 一种ViewBinding实例化的简捷方式。还没投入正式使用。
    val binding2 by myInflate(ActTestPictureSelectBinding::inflate)

    @RequiresApi(26)
    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }

    override fun getViewBinding(): ActTestPictureSelectBinding {
        return ActTestPictureSelectBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.btnReadFile.setOnClickListener {
            val uriPath = "content://com.android.externalstorage.documents/document/primary%3ADocuments%2FtestDoc.docx"
            val uri = Uri.parse(Uri.decode(uriPath))
            if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
                openFile(uri)
            }


            // val uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Documents/testDoc.docx")

            /*val fileName = "test.txt"
            val filePath = "/storage/emulated/0/Documents/$fileName"
            val file = File(filePath)
            if (!file.exists()) {
                Log.w("greyson", "testAndroidQ: 文件不存在")
                return@out
            }
            val uri = Uri.fromFile(file)*/

            // myRead(uri)
            // Log.d("voidtech", "MainActivity-onListItemClick: " + readTextFromUri(uri))

        }
        panel = PictureSelectPanel(this, pictureLayout)
        panel.setOnSendClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.data
            }
        } else {
            panel.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSendImage(list: MutableList<LocalMedia>?) {
        val isCompress = panel.isCompressMode
        list?.forEach {
            if (isCompress) {
                Log.w("greyson", "send compress: ${it.compressPath}")
            } else {
                Log.w("greyson", "send origin: ${it.path}")
            }
        }
    }

    override fun onSendVideo(list: MutableList<LocalMedia>?) {
    }

    fun onClick(view: View) {
        if (pictureTV.isSelected) {
            pictureTV.isSelected = false
            panel.hide()
        } else {
            pictureTV.isSelected = true
            panel.show()
        }
    }


    private fun myRead(uri: Uri) {
        val context = applicationContext
        var scanner: Scanner? = null
        try {
            val cursor = context.contentResolver.query(
                uri,
                arrayOf(MediaStore.Files.FileColumns.DATA),
                null, null, null
            )
            if (cursor?.moveToFirst() == true) {
                cursor.getString(0)
            }
            cursor?.close()

            val `is`: InputStream = context.contentResolver.openInputStream(uri) ?: return
            scanner = Scanner(BufferedInputStream(`is`))
            while (scanner.hasNextLine()) {
                Log.d("greyson", "content: " + scanner.nextLine())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                scanner?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }
}
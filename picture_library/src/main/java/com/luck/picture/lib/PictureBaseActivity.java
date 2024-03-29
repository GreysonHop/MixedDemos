package com.luck.picture.lib;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.luck.picture.lib.compress.CompressConfig;
import com.luck.picture.lib.compress.CompressImageOptions;
import com.luck.picture.lib.compress.CompressInterface;
import com.luck.picture.lib.compress.LubanOptions;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureBaseActivity extends FragmentActivity {
    protected Context mContext;
    protected PictureSelectionConfig config;
    protected int spanCount, maxSelectNum, minSelectNum, compressQuality,
            selectionMode, mimeType, videoSecond, compressMaxKB, compressMode,
            compressGrade, compressWidth, compressHeight, aspect_ratio_x, aspect_ratio_y,
            recordVideoSecond, videoQuality, cropWidth, cropHeight;
    protected boolean isGif, isCamera, enablePreview, enableCrop, isCompress,
            enPreviewVideo, checkNumMode, openClickSound, numComplete, camera, freeStyleCropEnabled,
            circleDimmedLayer, hideBottomControls, rotateEnabled, scaleEnabled, previewEggs, statusFont,
            showCropFrame, showCropGrid, previewStatusFont;
    protected String cameraPath;
    protected String originalPath;
    protected PictureDialog dialog;
    protected PictureDialog compressDialog;
    protected List<LocalMedia> selectionMedias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            config = (PictureSelectionConfig) savedInstanceState.getSerializable(PictureConfig.EXTRA_CONFIG);
            cameraPath = savedInstanceState.getString(PictureConfig.BUNDLE_CAMERA_PATH);
            originalPath = savedInstanceState.getString(PictureConfig.BUNDLE_ORIGINAL_PATH);
        } else {
            config = PictureSelectionConfig.getInstance();
        }
        int themeStyleId = config.themeStyleId;
        setTheme(themeStyleId);
        super.onCreate(savedInstanceState);
        mContext = this;
        initConfig();
    }

    /**
     * 获取配置参数
     */
    private void initConfig() {
        camera = config.camera;
        statusFont = AttrsUtils.getTypeValueBoolean
                (this, R.attr.picture_statusFontColor);
        previewStatusFont = AttrsUtils.getTypeValueBoolean
                (this, R.attr.picture_preview_statusFontColor);
        mimeType = config.mimeType;
        selectionMedias = config.selectionMedias;
        selectionMode = config.selectionMode;
        if (selectionMode == PictureConfig.SINGLE) {
            selectionMedias = new ArrayList<>();
        }
        spanCount = config.imageSpanCount;
        isGif = config.isGif;
        isCamera = config.isCamera;
        freeStyleCropEnabled = config.freeStyleCropEnabled;
        maxSelectNum = config.maxSelectNum;
        minSelectNum = config.minSelectNum;
        enablePreview = config.enablePreview;
        enPreviewVideo = config.enPreviewVideo;
        checkNumMode = config.checkNumMode = AttrsUtils.getTypeValueBoolean
                (this, R.attr.picture_style_checkNumMode);
        openClickSound = config.openClickSound;
        videoSecond = config.videoSecond;
        enableCrop = config.enableCrop;
        isCompress = config.isCompress;
        compressQuality = config.cropCompressQuality;
        numComplete = AttrsUtils.getTypeValueBoolean(this, R.attr.picture_style_numComplete);
        compressMaxKB = config.compressMaxkB;
        compressMode = config.compressMode;
        compressGrade = config.compressGrade;
        compressWidth = config.compressWidth;
        compressHeight = config.compressHeight;
        recordVideoSecond = config.recordVideoSecond;
        videoQuality = config.videoQuality;
        cropWidth = config.cropWidth;
        cropHeight = config.cropHeight;
        aspect_ratio_x = config.aspect_ratio_x;
        aspect_ratio_y = config.aspect_ratio_y;
        circleDimmedLayer = config.circleDimmedLayer;
        showCropFrame = config.showCropFrame;
        showCropGrid = config.showCropGrid;
        rotateEnabled = config.rotateEnabled;
        scaleEnabled = config.scaleEnabled;
        previewEggs = config.previewEggs;
        hideBottomControls = config.hideBottomControls;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(PictureConfig.BUNDLE_CAMERA_PATH, cameraPath);
        outState.putString(PictureConfig.BUNDLE_ORIGINAL_PATH, originalPath);
        outState.putSerializable(PictureConfig.EXTRA_CONFIG, config);
    }

    protected void startActivity(Class clz, Bundle bundle) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    protected void startActivity(Class clz, Bundle bundle, int requestCode) {
        if (!DoubleUtils.isFastDoubleClick()) {
            Intent intent = new Intent();
            intent.setClass(this, clz);
            intent.putExtras(bundle);
            startActivityForResult(intent, requestCode);
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * loading dialog
     */
    protected void showPleaseDialog() {
        if (!isFinishing()) {
            dismissDialog();
            dialog = new PictureDialog(this);
            dialog.show();
        }
    }

    /**
     * dismiss dialog
     */
    protected void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * compress loading dialog
     */
    protected void showCompressDialog() {
        if (!isFinishing()) {
            dismissCompressDialog();
            compressDialog = new PictureDialog(this);
            compressDialog.show();
        }
    }

    /**
     * dismiss compress dialog
     */
    protected void dismissCompressDialog() {
        if (compressDialog != null && compressDialog.isShowing()) {
            compressDialog.dismiss();
        }
    }


    /**
     * compressImage
     */
    protected void compressImage(final List<LocalMedia> result) {
        showCompressDialog();
        CompressConfig compress_config = CompressConfig.ofDefaultConfig();
        DebugUtil.i("compressImage--->", compressMaxKB + "");
        switch (compressMode) {
            case PictureConfig.SYSTEM_COMPRESS_MODE:
                // 系统自带压缩
                compress_config.enablePixelCompress(true);
                compress_config.enableQualityCompress(true);
                compress_config.setMaxSize(compressMaxKB);
                break;
            case PictureConfig.LUBAN_COMPRESS_MODE:
                // luban压缩
                DebugUtil.i("compressImage WH--->", compressHeight + "\n" + compressHeight);
                LubanOptions option = new LubanOptions.Builder()
                        .setMaxHeight(compressHeight)
                        .setMaxWidth(compressWidth)
                        .setMaxSize(compressMaxKB)
                        .setGrade(compressGrade)
                        .create();
                compress_config = CompressConfig.ofLuban(option);
                break;
        }

        CompressImageOptions.compress(this, compress_config, result,
                new CompressInterface.CompressListener() {
                    @Override
                    public void onCompressSuccess(List<LocalMedia> images) {
                        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                        onResult(images);
                    }

                    @Override
                    public void onCompressError(List<LocalMedia> images, String msg) {
                        RxBus.getDefault().post(new EventEntity(PictureConfig.CLOSE_PREVIEW_FLAG));
                        onResult(result);
                    }
                }).compress();
    }

    /**
     * 去裁剪
     *
     * @param originalPath
     */
    protected void startCrop(String originalPath) {
        UCrop.Options options = new UCrop.Options();
        int toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
        int statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
        int titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(circleDimmedLayer);
        options.setShowCropFrame(showCropFrame);
        options.setShowCropGrid(showCropGrid);
        options.setCompressionQuality(compressQuality);
        options.setHideBottomControls(hideBottomControls);
        options.setFreeStyleCropEnabled(freeStyleCropEnabled);
        boolean isHttp = PictureMimeType.isHttp(originalPath);
        Uri uri = isHttp ? Uri.parse(originalPath) : Uri.fromFile(new File(originalPath));
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")))
                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                .withMaxResultSize(cropWidth, cropHeight)
                .withOptions(options)
                .start(this);
    }

    /**
     * 多图去裁剪
     *
     * @param list
     */
    protected void startCrop(ArrayList<String> list) {
        UCropMulti.Options options = new UCropMulti.Options();
        int toolbarColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_toolbar_bg);
        int statusColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_status_color);
        int titleColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_crop_title_color);
        options.setToolbarColor(toolbarColor);
        options.setStatusBarColor(statusColor);
        options.setToolbarWidgetColor(titleColor);
        options.setCircleDimmedLayer(circleDimmedLayer);
        options.setShowCropFrame(showCropFrame);
        options.setShowCropGrid(showCropGrid);
        options.setScaleEnabled(scaleEnabled);
        options.setRotateEnabled(rotateEnabled);
        options.setHideBottomControls(true);
        options.setCompressionQuality(compressQuality);
        options.setCutListData(list);
        options.setFreeStyleCropEnabled(freeStyleCropEnabled);
        String path = list.size() > 0 ? list.get(0) : "";
        boolean isHttp = PictureMimeType.isHttp(path);
        Uri uri = isHttp ? Uri.parse(path) : Uri.fromFile(new File(path));
        UCropMulti.of(uri, Uri.fromFile(new File(getCacheDir(), System.currentTimeMillis() + ".jpg")))
                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
                .withMaxResultSize(cropWidth, cropHeight)
                .withOptions(options)
                .start(this);
    }

    /**
     * 判断拍照 图片是否旋转
     *
     * @param degree
     * @param file
     */
    protected void rotateImage(int degree, File file) {
        if (degree > 0) {
            // 针对相片有旋转问题的处理方式
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();//获取缩略图显示到屏幕上
                opts.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                Bitmap bmp = PictureFileUtils.rotaingImageView(degree, bitmap);
                PictureFileUtils.saveBitmapFile(bmp, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * compress or callback
     *
     * @param result
     */
    protected void handlerResult(List<LocalMedia> result) {
        if (isCompress) {
            compressImage(result);
        } else {
            onResult(result);
        }
    }


    /**
     * 如果没有任何相册，先创建一个最近相册出来
     *
     * @param folders
     */
    protected void createNewFolder(List<LocalMediaFolder> folders) {
        if (folders.size() == 0) {
            // 没有相册 先创建一个最近相册出来
            LocalMediaFolder newFolder = new LocalMediaFolder();
            String folderName = getString(R.string.picture_camera_roll);
            newFolder.setName(folderName);
            newFolder.setPath("");
            newFolder.setFirstImagePath("");
            folders.add(newFolder);
        }
    }

    /**
     * 将图片插入到相机文件夹中
     *
     * @param path
     * @param imageFolders
     * @return
     */
    protected LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();

        for (LocalMediaFolder folder : imageFolders) {
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * return image result
     *
     * @param images
     */
    protected void onResult(List<LocalMedia> images) {
        if (camera && selectionMode == PictureConfig.MULTIPLE)
            images.addAll(selectionMedias);
        Intent intent = PictureSelector.putIntentResult(images);
        setResult(RESULT_OK, intent);
        closeActivity();
        dismissCompressDialog();
    }

    /**
     * Close Activity
     */
    protected void closeActivity() {
        finish();
        if (camera) {
            overridePendingTransition(0, R.anim.a3);
        } else {
            overridePendingTransition(0, R.anim.slide_bottom_out);
        }
    }
}

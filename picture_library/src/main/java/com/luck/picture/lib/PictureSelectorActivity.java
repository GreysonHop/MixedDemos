package com.luck.picture.lib;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luck.picture.lib.adapter.PictureAlbumDirectoryAdapter;
import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.EventEntity;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.observable.ImagesObservable;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.rxbus2.RxBus;
import com.luck.picture.lib.rxbus2.Subscribe;
import com.luck.picture.lib.rxbus2.ThreadMode;
import com.luck.picture.lib.tools.AttrsUtils;
import com.luck.picture.lib.tools.DebugUtil;
import com.luck.picture.lib.tools.DoubleUtils;
import com.luck.picture.lib.tools.LightStatusBarUtils;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.luck.picture.lib.widget.CustomToast;
import com.luck.picture.lib.widget.FolderPopWindow;
import com.luck.picture.lib.widget.PhotoPopupWindow;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropMulti;
import com.yalantis.ucrop.model.CutInfo;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PictureSelectorActivity extends PictureBaseActivity implements View.OnClickListener,
        PictureAlbumDirectoryAdapter.OnItemClickListener,
        PictureImageGridAdapter.OnPhotoSelectChangedListener, PhotoPopupWindow.OnItemClickListener {
    private final static String TAG = PictureSelectorActivity.class.getSimpleName();
    private ImageView picture_left_back;
    private TextView picture_title, picture_right, picture_tv_ok, tv_empty,
            picture_tv_img_num, picture_id_preview;
    private RelativeLayout rl_picture_title, rl_bottom;
    private LinearLayout id_ll_ok;
    private RecyclerView picture_recycler;
    private PictureImageGridAdapter adapter;
    private List<LocalMedia> images = new ArrayList<>();
    private List<LocalMediaFolder> foldersList = new ArrayList<>();
    private FolderPopWindow folderWindow;
    private Animation animation = null;
    private boolean anim = false;
    private int preview_textColor, complete_textColor;
    private RxPermissions rxPermissions;
    private PhotoPopupWindow popupWindow;
    private LocalMediaLoader mediaLoader;


    //EventBus 3.0 回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventBus(EventEntity obj) {
        switch (obj.what) {
            case PictureConfig.UPDATE_FLAG:
                // 预览时勾选图片更新回调
                List<LocalMedia> selectImages = obj.medias;
                anim = selectImages.size() > 0 ? true : false;
                int position = obj.position;
                DebugUtil.i(TAG, "刷新下标::" + position);
                adapter.bindSelectImages(selectImages);
                adapter.notifyItemChanged(position);
                break;
            case PictureConfig.PREVIEW_DATA_FLAG:
                List<LocalMedia> medias = obj.medias;
                if (medias.size() > 0) {
                    // 取出第1个判断是否是图片，视频和图片只能二选一，不必考虑图片和视频混合
                    String pictureType = medias.get(0).getPictureType();
                    if (isCompress && pictureType.startsWith(PictureConfig.IMAGE)) {
                        compressImage(medias);
                    } else {
                        onResult(medias);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().register(this);
        }
        rxPermissions = new RxPermissions(this);
        LightStatusBarUtils.setLightStatusBar(this, statusFont);
        if (camera) {
            if (savedInstanceState == null) {
                rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    onTakePhoto();
                                } else {
                                    showToast(getString(R.string.picture_camera));
                                    closeActivity();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        });
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.picture_empty);
        } else {
            setContentView(R.layout.picture_selector);
            initView();
        }
    }

    /**
     * init views
     */
    private void initView() {
        preview_textColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_preview_textColor);
        complete_textColor = AttrsUtils.getTypeValueColor(this, R.attr.picture_complete_textColor);
        rl_picture_title = (RelativeLayout) findViewById(R.id.rl_picture_title);
        picture_left_back = (ImageView) findViewById(R.id.picture_left_back);
        picture_title = (TextView) findViewById(R.id.picture_title);
        picture_right = (TextView) findViewById(R.id.picture_right);
        picture_tv_ok = (TextView) findViewById(R.id.picture_tv_ok);
        picture_id_preview = (TextView) findViewById(R.id.picture_id_preview);
        picture_tv_img_num = (TextView) findViewById(R.id.picture_tv_img_num);
        picture_recycler = (RecyclerView) findViewById(R.id.picture_recycler);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        id_ll_ok = (LinearLayout) findViewById(R.id.id_ll_ok);
        tv_empty = (TextView) findViewById(R.id.tv_empty);
        rl_bottom.setVisibility(selectionMode == PictureConfig.SINGLE ? View.GONE : View.VISIBLE);
        isNumComplete(numComplete);
        if (mimeType == PictureMimeType.ofAll()) {
            popupWindow = new PhotoPopupWindow(this);
            popupWindow.setOnItemClickListener(this);
        }
        picture_id_preview.setOnClickListener(this);
        picture_id_preview.setVisibility(mimeType == PictureConfig.TYPE_VIDEO
                ? View.GONE : View.VISIBLE);
        picture_left_back.setOnClickListener(this);
        picture_right.setOnClickListener(this);
        id_ll_ok.setOnClickListener(this);
        picture_title.setOnClickListener(this);
        picture_title.setText(getString(R.string.picture_camera_roll));
        folderWindow = new FolderPopWindow(this, selectionMode == PictureConfig.SINGLE ? 0 : 94);
        folderWindow.setPictureTitleView(picture_title);
        folderWindow.setOnItemClickListener(this);
        picture_recycler.setHasFixedSize(true);
        picture_recycler.addItemDecoration(new GridSpacingItemDecoration(spanCount,
                ScreenUtils.dip2px(this, 2), false));
        picture_recycler.setLayoutManager(new GridLayoutManager(this, spanCount));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        ((SimpleItemAnimator) picture_recycler.getItemAnimator())
                .setSupportsChangeAnimations(false);
        mediaLoader = new LocalMediaLoader(this, mimeType, isGif, videoSecond);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        showPleaseDialog();
                        if (aBoolean) {
                            readLocalMedia();
                        } else {
                            showToast(getString(R.string.picture_jurisdiction));
                            dismissDialog();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        StringUtils.tempTextFont(tv_empty);
        adapter = new PictureImageGridAdapter(mContext, config);
        adapter.bindSelectImages(selectionMedias);
        changeImageNumber(selectionMedias);
        picture_recycler.setAdapter(adapter);
        adapter.setOnPhotoSelectChangedListener(PictureSelectorActivity.this);
        String titleText = picture_title.getText().toString().trim();
        if (isCamera) {
            isCamera = StringUtils.isCamera(titleText);
        }
    }


    /**
     * none number style
     */
    private void isNumComplete(boolean numComplete) {
        picture_tv_ok.setText(numComplete ? getString(R.string.picture_done_front_num, 0, maxSelectNum)
                : getString(R.string.picture_please_select));
        if (!numComplete) {
            animation = AnimationUtils.loadAnimation(this, R.anim.modal_in);
        }
        animation = numComplete ? null : AnimationUtils.loadAnimation(this, R.anim.modal_in);
    }

    /**
     * get LocalMedia s
     */
    protected void readLocalMedia() {
        mediaLoader.loadAllMedia(new LocalMediaLoader.LocalMediaLoadListener() {
            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                DebugUtil.i("loadComplete:" + folders.size());
                if (folders.size() > 0) {
                    foldersList = folders;
                    LocalMediaFolder folder = folders.get(0);
                    folder.setChecked(true);
                    folderWindow.bindFolder(folders);
                    List<LocalMedia> localImg = folder.getImages();
                    // 这里解决有些机型会出现拍照完，相册列表不及时刷新问题
                    // 因为onActivityResult里手动添加拍照后的照片，
                    // 如果查询出来的图片大于或等于当前adapter集合的图片则取更新后的，否则就取本地的
                    if (localImg.size() >= images.size()) {
                        images = localImg;
                    }
                }
                if (adapter != null) {
                    adapter.bindImagesData(images);
                    tv_empty.setVisibility(folders.size() > 0
                            ? View.INVISIBLE : View.VISIBLE);
                }
                dismissDialog();
            }
        });
    }

    /**
     * open camera
     */
    public void startCamera() {
        // 防止快速点击，但是单独拍照不管
        if (!DoubleUtils.isFastDoubleClick() || camera) {
            switch (mimeType) {
                case PictureConfig.TYPE_ALL:
                    // 如果是全部类型下，单独拍照就默认图片 (因为单独拍照不会new此PopupWindow对象)
                    if (popupWindow != null) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        popupWindow.showAsDropDown(rl_picture_title);
                    } else {
                        startOpenCamera();
                    }
                    break;
                case PictureConfig.TYPE_IMAGE:
                    // 拍照
                    startOpenCamera();
                    break;
                case PictureConfig.TYPE_VIDEO:
                    // 录视频
                    startOpenCameraVideo();
                    break;
            }
        }
    }

    /**
     * start to camera、preview、crop
     */
    public void startOpenCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = PictureFileUtils.createCameraFile(this,
                    mimeType == PictureConfig.TYPE_ALL ? PictureConfig.TYPE_IMAGE : mimeType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * start to camera、video
     */
    public void startOpenCameraVideo() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File cameraFile = PictureFileUtils.createCameraFile(this, mimeType ==
                    PictureConfig.TYPE_ALL ? PictureConfig.TYPE_VIDEO : mimeType);
            cameraPath = cameraFile.getAbsolutePath();
            Uri imageUri = parUri(cameraFile);
            DebugUtil.i(TAG, "video second:" + recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, recordVideoSecond);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
            startActivityForResult(cameraIntent, PictureConfig.REQUEST_CAMERA);
        }
    }

    /**
     * 生成uri
     *
     * @param cameraFile
     * @return
     */
    private Uri parUri(File cameraFile) {
        Uri imageUri;
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //通过FileProvider创建一个content类型的Uri
            imageUri = FileProvider.getUriForFile(mContext, authority, cameraFile);
        } else {
            imageUri = Uri.fromFile(cameraFile);
        }
        return imageUri;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.picture_left_back || id == R.id.picture_right) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                closeActivity();
            }
        }
        if (id == R.id.picture_title) {
            if (folderWindow.isShowing()) {
                folderWindow.dismiss();
            } else {
                if (images != null && images.size() > 0) {
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    folderWindow.notifyDataCheckedStatus(selectedImages);
                    folderWindow.showAsDropDown(rl_picture_title);
                }
            }
        }
        if (id == R.id.picture_id_preview) {
            List<LocalMedia> selectedImages = adapter.getSelectedImages();

            List<LocalMedia> medias = new ArrayList<>();
            for (LocalMedia media : selectedImages) {
                medias.add(media);
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(PictureConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
            bundle.putBoolean(PictureConfig.EXTRA_BOTTOM_PREVIEW, true);
            startActivity(PicturePreviewActivity.class, bundle, UCropMulti.REQUEST_MULTI_CROP);
            overridePendingTransition(R.anim.a5, 0);
        }

        if (id == R.id.id_ll_ok) {
            List<LocalMedia> images = adapter.getSelectedImages();
            String pictureType = images.size() > 0 ? images.get(0).getPictureType() : "";
            // 如果设置了图片最小选择数量，则判断是否满足条件
            int size = images.size();
            if (minSelectNum > 0 && selectionMode == PictureConfig.MULTIPLE) {
                if (size < minSelectNum) {
                    boolean eqImg = pictureType.startsWith("image");
                    String str = eqImg ? getString(R.string.picture_min_img_num, minSelectNum)
                            : getString(R.string.picture_min_video_num, minSelectNum);
                    showToast(str);
                    return;
                }
            }
            boolean eqImg = pictureType.startsWith(PictureConfig.IMAGE);
            if (enableCrop && eqImg) {
                // 是图片和选择压缩并且是多张，调用批量压缩
                ArrayList<String> medias = new ArrayList<>();
                for (LocalMedia media : images) {
                    medias.add(media.getPath());
                }
                startCrop(medias);
            } else if (isCompress && eqImg) {
                // 图片才压缩，视频不管
                compressImage(images);
            } else {
                onResult(images);
            }
        }
    }


    @Override
    public void onItemClick(String folderName, List<LocalMedia> images) {
        boolean camera = StringUtils.isCamera(folderName);
        camera = isCamera ? camera : false;
        adapter.setShowCamera(camera);
        picture_title.setText(folderName);
        adapter.bindImagesData(images);
        folderWindow.dismiss();
        if (images.size() == 0) {
            new CustomToast().showToast(this,
                    getString(R.string.picture_warning));
        }
    }

    @Override
    public void onTakePhoto() {
        // 启动相机拍照,先判断手机是否有拍照权限
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    startCamera();
                } else {
                    showToast(getString(R.string.picture_camera));
                    if (camera) {
                        closeActivity();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onChange(List<LocalMedia> selectImages) {
        changeImageNumber(selectImages);
    }

    @Override
    public void onPictureClick(LocalMedia media, int position) {
        List<LocalMedia> images = adapter.getImages();
        startPreview(images, position);
    }

    /**
     * preview image and video
     *
     * @param previewImages
     * @param position
     */
    public void startPreview(List<LocalMedia> previewImages, int position) {
        LocalMedia media = previewImages.get(position);
        String pictureType = media.getPictureType();
        Bundle bundle = new Bundle();
        List<LocalMedia> result = new ArrayList<>();
        int mediaType = PictureMimeType.isPictureType(pictureType);
        DebugUtil.i(TAG, "mediaType:" + mediaType);
        switch (mediaType) {
            case PictureConfig.TYPE_IMAGE:
                // image
                if (selectionMode == PictureConfig.SINGLE) {
                    if (enableCrop) {
                        originalPath = media.getPath();
                        boolean gif = PictureMimeType.isGif(pictureType);
                        if (gif) {
                            result.add(media);
                            handlerResult(result);
                        } else {
                            startCrop(originalPath);
                        }
                    } else {
                        result.add(media);
                        handlerResult(result);
                    }
                } else {
                    List<LocalMedia> selectedImages = adapter.getSelectedImages();
                    ImagesObservable.getInstance().saveLocalMedia(previewImages);
                    bundle.putSerializable(PictureConfig.EXTRA_SELECT_LIST, (Serializable) selectedImages);
                    bundle.putInt(PictureConfig.EXTRA_POSITION, position);
                    startActivity(PicturePreviewActivity.class, bundle, UCropMulti.REQUEST_MULTI_CROP);
                    overridePendingTransition(R.anim.a5, 0);
                }
                break;
            case PictureConfig.TYPE_VIDEO:
                // video
                if (selectionMode == PictureConfig.SINGLE) {
                    result.add(media);
                    onResult(result);
                } else {
                    bundle.putString("video_path", media.getPath());
                    startActivity(PictureVideoPlayActivity.class, bundle);
                }
                break;
        }
    }


    /**
     * change image selector state
     *
     * @param selectImages
     */
    public void changeImageNumber(List<LocalMedia> selectImages) {
        // 如果选择的视频没有预览功能
        String pictureType = selectImages.size() > 0
                ? selectImages.get(0).getPictureType() : "";
        boolean isVideo = PictureMimeType.isVideo(pictureType);
        picture_id_preview.setVisibility(isVideo ? View.GONE : View.VISIBLE);
        boolean enable = selectImages.size() != 0;
        if (enable) {
            id_ll_ok.setEnabled(true);
            picture_id_preview.setEnabled(true);
            picture_id_preview.setTextColor(preview_textColor);
            picture_tv_ok.setTextColor(complete_textColor);
            if (numComplete) {
                picture_tv_ok.setText(getString
                        (R.string.picture_done_front_num, selectImages.size(), maxSelectNum));
            } else {
                if (!anim) {
                    picture_tv_img_num.startAnimation(animation);
                }
                picture_tv_img_num.setVisibility(View.VISIBLE);
                picture_tv_img_num.setText(selectImages.size() + "");
                picture_tv_ok.setText(getString(R.string.picture_completed));
                anim = false;
            }
        } else {
            id_ll_ok.setEnabled(false);
            picture_id_preview.setEnabled(false);
            picture_tv_ok.setTextColor(ContextCompat.getColor(mContext, R.color.tab_color_false));
            picture_id_preview.setTextColor
                    (ContextCompat.getColor(mContext, R.color.tab_color_false));
            if (numComplete) {
                picture_tv_ok.setText(getString(R.string.picture_done_front_num, 0, maxSelectNum));
            } else {
                picture_tv_img_num.setVisibility(View.INVISIBLE);
                picture_tv_ok.setText(getString(R.string.picture_please_select));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<LocalMedia> medias = new ArrayList<>();
            LocalMedia media;
            String imageType;
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    String cutPath = resultUri.getPath();
                    media = new LocalMedia(originalPath, 0, false, 0, 0, mimeType);
                    media.setCutPath(cutPath);
                    media.setCut(true);
                    imageType = PictureMimeType.createImageType(cutPath);
                    media.setPictureType(imageType);
                    medias.add(media);
                    DebugUtil.i(TAG, "cut createImageType:" + imageType);
                    handlerResult(medias);
                    break;
                case UCropMulti.REQUEST_MULTI_CROP:
                    List<CutInfo> cuts = UCropMulti.getOutput(data);
                    for (CutInfo c : cuts) {
                        media = new LocalMedia();
                        imageType = PictureMimeType.createImageType(c.getPath());
                        media.setCut(true);
                        media.setPath(c.getPath());
                        media.setCutPath(c.getCutPath());
                        media.setPictureType(imageType);
                        media.setMimeType(mimeType);
                        medias.add(media);
                    }
                    handlerResult(medias);
                    break;
                case PictureConfig.REQUEST_CAMERA:
                    // on take photo success
                    final File file = new File(cameraPath);
                    String toType = PictureMimeType.fileToType(file);
                    DebugUtil.i(TAG, "camera result:" + toType);
                    int degree = PictureFileUtils.readPictureDegree(file.getAbsolutePath());
                    rotateImage(degree, file);
                    // 生成新拍照片或视频对象
                    media = new LocalMedia();
                    media.setPath(cameraPath);
                    int duration = 0;
                    String pictureType;

                    // 保存图片至手机，在刷新相册
                    ContentValues values;
                    if (toType.startsWith(PictureConfig.VIDEO)) {
                        duration = PictureMimeType.getLocalVideoDuration(cameraPath);
                        pictureType = PictureMimeType.createVideoType(cameraPath);
                        DebugUtil.i(TAG, "camera video createImageType:" + pictureType);
                        values = new ContentValues(3);
                        values.put(MediaStore.Video.Media.MIME_TYPE, pictureType);
                        values.put(MediaStore.Video.Media.DATA, cameraPath);
                        values.put(MediaStore.Video.Media.DURATION, duration);
                        // Add a new record (identified by uri)
                        getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                values);
                    } else {
                        pictureType = PictureMimeType.createImageType(cameraPath);
                        DebugUtil.i(TAG, "camera image createImageType:" + pictureType);
                        values = new ContentValues(2);
                        values.put(MediaStore.Images.Media.MIME_TYPE, pictureType);
                        values.put(MediaStore.Images.Media.DATA, cameraPath);
                        // Add a new record (identified by uri)
                        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values);
                    }

                    media.setPictureType(pictureType);
                    media.setDuration(duration);
                    media.setMimeType(mimeType);

                    // 因为加入了单独拍照功能，所有如果是单独拍照的话也默认为单选状态
                    if (selectionMode == PictureConfig.SINGLE || camera) {
                        // 如果是单选 拍照后直接返回
                        boolean eqImg = toType.startsWith(PictureConfig.IMAGE);
                        if (enableCrop && eqImg) {
                            // 去裁剪
                            originalPath = cameraPath;
                            startCrop(cameraPath);
                        } else if (isCompress && eqImg) {
                            // 去压缩
                            medias.add(media);
                            compressImage(medias);
                            if (adapter != null) {
                                images.add(0, media);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            // 不裁剪 不压缩 直接返回结果
                            medias.add(media);
                            onResult(medias);
                        }
                    } else {
                        // 多选 返回列表并选中当前拍照的
                        images.add(0, media);
                        if (adapter != null) {
                            List<LocalMedia> selectedImages = adapter.getSelectedImages();
                            // 没有到最大选择量 才做默认选中刚拍好的
                            if (selectedImages.size() < maxSelectNum) {
                                pictureType = selectedImages.size() > 0 ? selectedImages.get(0).getPictureType() : "";
                                boolean toEqual = PictureMimeType.mimeToEqual(pictureType, media.getPictureType());
                                // 类型相同或还没有选中才加进选中集合中
                                if (toEqual || selectedImages.size() == 0) {
                                    if (selectedImages.size() < maxSelectNum) {
                                        selectedImages.add(media);
                                        adapter.bindSelectImages(selectedImages);
                                        changeImageNumber(selectedImages);
                                    }
                                }
                                tv_empty.setVisibility(images.size() > 0
                                        ? View.INVISIBLE : View.VISIBLE);

                                adapter.notifyDataSetChanged();
                            }
                            // 解决部分手机拍照完Intent.ACTION_MEDIA_SCANNER_SCAN_FILE不及时刷新问题手动添加
                            manualSaveFolder(media);
                        }
                    }

                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(file)));

                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (camera) {
                closeActivity();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable throwable = (Throwable) data.getSerializableExtra(UCrop.EXTRA_ERROR);
            showToast(throwable.getMessage());
        }
    }

    /**
     * 手动添加拍照后的相片到图片列表，并设为选中
     *
     * @param media
     */
    private void manualSaveFolder(LocalMedia media) {
        createNewFolder(foldersList);
        LocalMediaFolder folder = getImageFolder(media.getPath(), foldersList);
        LocalMediaFolder cameraFolder = foldersList.size() > 0 ? foldersList.get(0) : null;
        if (cameraFolder != null && folder != null) {
            // 相机胶卷
            cameraFolder.setFirstImagePath(media.getPath());
            cameraFolder.setImageNum(cameraFolder.getImageNum() + 1);
            // 拍照相册
            int num = folder.getImageNum() + 1;
            folder.setImageNum(num);
            folder.getImages().add(0, media);
            folder.setFirstImagePath(cameraPath);
            folderWindow.bindFolder(foldersList);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RxBus.getDefault().isRegistered(this)) {
            RxBus.getDefault().unregister(this);
        }
        ImagesObservable.getInstance().clearLocalMedia();
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0:
                // 拍照
                startOpenCamera();
                break;
            case 1:
                // 录视频
                startOpenCameraVideo();
                break;
        }
    }
}

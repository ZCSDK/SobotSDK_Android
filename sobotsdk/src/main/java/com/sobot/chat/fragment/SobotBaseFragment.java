package com.sobot.chat.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sobot.chat.SobotUIConfig;
import com.sobot.chat.activity.SobotCameraActivity;
import com.sobot.chat.api.ZhiChiApi;
import com.sobot.chat.core.channel.SobotMsgManager;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.listener.PermissionListener;
import com.sobot.chat.listener.PermissionListenerImpl;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.widget.image.SobotRCImageView;

import java.io.File;

/**
 * @author Created by jinxl on 2018/2/1.
 */
public abstract class SobotBaseFragment extends Fragment {
    public static final int REQUEST_CODE_CAMERA = 108;

    public ZhiChiApi zhiChiApi;
    protected File cameraFile;

    private Activity activity;
    //权限回调
    public PermissionListener permissionListener;

    public SobotBaseFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (activity == null) {
            activity = getActivity();
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zhiChiApi = SobotMsgManager.getInstance(getContext().getApplicationContext()).getZhiChiApi();
    }

    @Override
    public void onDestroyView() {
        OkHttpUtils.getInstance().cancelTag(SobotBaseFragment.this);
        OkHttpUtils.getInstance().cancelTag(ZhiChiConstant.SOBOT_GLOBAL_REQUEST_CANCEL_TAG);
        super.onDestroyView();
    }


    public SobotBaseFragment getSobotBaseFragment() {
        return this;
    }

    //返回布局id
    /*protected abstract int getContentViewResId();

    protected void initBundleData(Bundle savedInstanceState) {
    }

    protected abstract void initView();

    protected abstract void initData();*/

    public int getResId(String name) {
        return ResourceUtils.getIdByName(getContext(), "id", name);
    }

    public int getResDrawableId(String name) {
        return ResourceUtils.getIdByName(getContext(), "drawable", name);
    }

    public int getResLayoutId(String name) {
        return ResourceUtils.getIdByName(getContext(), "layout", name);
    }

    public int getResStringId(String name) {
        return ResourceUtils.getIdByName(getContext(), "string", name);
    }

    public int getResDimenId(String name) {
        return ResourceUtils.getIdByName(getContext(), "dimen", name);
    }

    public String getResString(String name) {
        return getResources().getString(getResStringId(name));
    }

    public float getDimens(String name) {
        return getResources().getDimension(getResDimenId(name));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE:
                try {
                    for (int i = 0; i < grantResults.length; i++) {
                        //判断权限的结果，如果有被拒绝，就return
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            String permissionTitle = "sobot_no_permission_text";
                            if (permissions[i] != null && permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                permissionTitle = "sobot_no_write_external_storage_permission";
//                                ToastUtil.showToast(getContext().getApplicationContext(), getResString("sobot_no_write_external_storage_permission"));
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                permissionTitle = "sobot_no_write_external_storage_permission";
//                                ToastUtil.showToast(getContext().getApplicationContext(), getResString("sobot_no_write_external_storage_permission"));
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.RECORD_AUDIO)) {
                                permissionTitle = "sobot_no_record_audio_permission";
//                                ToastUtil.showToast(getContext().getApplicationContext(), getResString("sobot_no_record_audio_permission"));
                            } else if (permissions[i] != null && permissions[i].equals(Manifest.permission.CAMERA)) {
                                permissionTitle = "sobot_no_camera_permission";
//                                ToastUtil.showToast(getContext().getApplicationContext(), getResString("sobot_no_camera_permission"));
                            }
                            //调用权限失败
                            if (permissionListener != null) {
                                permissionListener.onPermissionErrorListener(getSobotActivity(), getResString(permissionTitle));
                            }
                            return;
//                            permissionListener = null;
                        }
                    }
                    if (permissionListener != null) {
                        permissionListener.onPermissionSuccessListener();
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * @param resourceId
     * @param textId
     */
    protected void showRightMenu(View tmpMenu, int resourceId, String textId) {
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView rightMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            rightMenu.setText(textId);
        } else {
            rightMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            rightMenu.setCompoundDrawables(null, null, img, null);
        } else {
            rightMenu.setCompoundDrawables(null, null, null, null);
        }

        rightMenu.setVisibility(View.VISIBLE);
    }


    /**
     * 显示头像
     *
     * @param avatarIV
     * @param avatarUrl
     * @param isShow
     */
    protected void showAvatar(SobotRCImageView avatarIV, String avatarUrl, boolean isShow) {
        if (!TextUtils.isEmpty(avatarUrl)) {
            SobotBitmapUtil.display(getContext(), avatarUrl, avatarIV);
        }
        if (isShow) {
            avatarIV.setVisibility(View.VISIBLE);
        } else {
            avatarIV.setVisibility(View.GONE);
        }
    }

    /**
     * @param resourceId
     * @param textId
     */
    protected void showLeftMenu(View tmpMenu, int resourceId, String textId) {
        if (tmpMenu == null || !(tmpMenu instanceof TextView)) {
            return;
        }
        TextView leftMenu = (TextView) tmpMenu;
        if (!TextUtils.isEmpty(textId)) {
            leftMenu.setText(textId);
        } else {
            leftMenu.setText("");
        }

        if (resourceId != 0) {
            Drawable img = getResources().getDrawable(resourceId);
            if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleTextColor) {
                img = ScreenUtils.tintDrawable(getContext(), img, SobotUIConfig.sobot_titleTextColor);
            }
            img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
            leftMenu.setCompoundDrawables(img, null, null, null);
        } else {
            leftMenu.setCompoundDrawables(null, null, null, null);
        }

        applyTitleTextColor(leftMenu);
    }

    /**
     * 检查存储权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getContext().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请READ_EXTERNAL_STORAGE权限
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE}, ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查录音权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStorageAndAudioPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getContext().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查拍摄权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStorageAudioAndCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getContext().getApplicationContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}
                        , ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}
                        , ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}
                        , ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查拍照权限
     *
     * @return true, 已经获取权限;false,没有权限,尝试获取
     */
    protected boolean checkStorageAndCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23 && CommonUtils.getTargetSdkVersion(getContext()) >= 23) {
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
            if (ContextCompat.checkSelfPermission(getSobotActivity(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ZhiChiConstant.SOBOT_PERMISSIONS_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    /**
     * 通过照相上传图片
     */
    public void selectPicFromCamera() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastUtil.showCustomToast(getContext().getApplicationContext(), getResString("sobot_sdcard_does_not_exist"),
                    Toast.LENGTH_SHORT);
            return;
        }

        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                if (checkStorageAudioAndCameraPermission()) {
                    //如果有拍照所需的权限，跳转到拍照界面
                    startActivityForResult(SobotCameraActivity.newIntent(getSobotBaseFragment().getContext()), REQUEST_CODE_CAMERA);
                }
            }
        };

        if (!checkStorageAudioAndCameraPermission()) {
            return;
        }

        // 打开拍摄页面
        startActivityForResult(SobotCameraActivity.newIntent(getContext()), REQUEST_CODE_CAMERA);
    }

    /**
     * 调用系统相机拍照
     */
    public void selectPicFromCameraBySys() {
        if (!CommonUtils.isExitsSdcard()) {
            ToastUtil.showCustomToast(getContext(), getResString("sobot_sdcard_does_not_exist"),
                    Toast.LENGTH_SHORT);
            return;
        }
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                if (checkStorageAndCameraPermission() && isCameraCanUse()) {
                    cameraFile = ChatUtils.openCamera(getSobotActivity(), getSobotBaseFragment());
                }
            }
        };
        if (!checkStorageAndCameraPermission()) {
            return;
        }
        cameraFile = ChatUtils.openCamera(getSobotActivity(), this);
    }


    /**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                if (checkStoragePermission()) {
                    ChatUtils.openSelectPic(getSobotActivity(), getSobotBaseFragment());
                }
            }
        };
        if (!checkStoragePermission()) {
            return;
        }
        ChatUtils.openSelectPic(getSobotActivity(), SobotBaseFragment.this);
    }

    /**
     * 从图库获取视频
     */
    public void selectVedioFromLocal() {
        permissionListener = new PermissionListenerImpl() {
            @Override
            public void onPermissionSuccessListener() {
                if (checkStoragePermission()) {
                    ChatUtils.openSelectVedio(getSobotActivity(), getSobotBaseFragment());
                }
            }
        };
        if (!checkStoragePermission()) {
            return;
        }
        ChatUtils.openSelectVedio(getSobotActivity(), SobotBaseFragment.this);
    }


    protected void applyTitleTextColor(TextView view) {
        if (view == null) {
            return;
        }

        if (SobotUIConfig.DEFAULT != SobotUIConfig.sobot_titleTextColor) {
            view.setTextColor(getResources().getColor(SobotUIConfig.sobot_titleTextColor));
        }
    }

    public static boolean isCameraCanUse() {

        boolean canUse = false;
        Camera mCamera = null;

        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }

        if (mCamera != null) {
            mCamera.release();
            canUse = true;
        }

        return canUse;
    }

    /**
     * 返回activity
     *
     * @return
     */
    public Activity getSobotActivity() {
        Activity activity = getActivity();
        if (activity == null) {
            return this.activity;
        }
        return activity;

    }

}
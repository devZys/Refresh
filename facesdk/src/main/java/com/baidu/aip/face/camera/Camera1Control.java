/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.baidu.aip.face.PreviewView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 5.0以下相机API的封装。
 * //修改横竖屏问题
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class Camera1Control implements ICameraControl {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int MAX_PREVIEW_SIZE = 2048;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);             //正常横屏摄像头
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private int displayOrientation = 0;
    private int cameraId = 0;
    private int flashMode;
    private AtomicBoolean takingPicture = new AtomicBoolean(false);

    private Context context;
    private Camera camera;

    private Camera.Parameters parameters;
    private PermissionCallback permissionCallback;
    private Rect previewFrame = new Rect();

//    private int preferredWidth = 1280;
//    private int preferredHeight = 720;

    private int preferredWidth = 1920;
    private int preferredHeight = 1080;

    @ICameraControl.CameraFacing
    private int cameraFacing = CAMERA_FACING_FRONT;//CAMERA_FACING_FRONT;

    private boolean usbCamera = false;

    @Override
    public void setDisplayOrientation(@CameraView.Orientation int displayOrientation) {
        this.displayOrientation = displayOrientation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshPermission() {
        startPreview(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlashMode(@FlashMode int flashMode) {
        if (this.flashMode == flashMode) {
            return;
        }
        this.flashMode = flashMode;
        updateFlashMode(flashMode);
    }

    @Override
    public int getFlashMode() {
        return flashMode;
    }

    @Override
    public void setCameraFacing(@CameraFacing int cameraFacing) {
        this.cameraFacing = cameraFacing;
    }

    @Override
    public void setUsbCamera(boolean usbCamera) {
        this.usbCamera = usbCamera;
    }

    @Override
    public void start() {
        startCamera();
    }

    public void isUsbCamera() {

    }

    private SurfaceTexture surfaceTexture;

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (permissionCallback != null) {
                permissionCallback.onRequestPermission();
            }
            return;
        }
        if (camera == null) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == cameraFacing) {
                    cameraId = i;
                }
            }
            camera = Camera.open(cameraId);
        }
        if (parameters == null) {
            parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        surfaceTexture = new SurfaceTexture(11);
        parameters.setRotation(0); // TODO
        int rotation = ORIENTATIONS.get(displayOrientation);
        camera.setDisplayOrientation(rotation);
        try {
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    previewView.setPreviewSize(size.width, size.height);
                    onFrameListener.onPreviewFrame(data, 0, size.width, size.height);       //横屏摄像头
                }
            });
            camera.setPreviewTexture(surfaceTexture);
            if (textureView != null) {
                surfaceTexture.detachFromGLContext();
                textureView.setSurfaceTexture(surfaceTexture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        opPreviewSize(preferredWidth, preferredHeight);
    }

    private TextureView textureView;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setTextureView(TextureView textureView) {
        this.textureView = textureView;
        if (surfaceTexture != null) {
            surfaceTexture.detachFromGLContext();
            textureView.setSurfaceTexture(surfaceTexture);
        }
    }

    @Override
    public void stop() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    @Override
    public void pause() {
        if (camera != null) {
            camera.stopPreview();
        }
        setFlashMode(FLASH_MODE_OFF);
    }

    @Override
    public void resume() {
        takingPicture.set(false);
        if (camera == null) {
            startCamera();
        }
    }

    private OnFrameListener onFrameListener;

    @Override
    public void setOnFrameListener(OnFrameListener listener) {
        this.onFrameListener = listener;
    }

    @Override
    public void setPreferredPreviewSize(int width, int height) {
        this.preferredWidth = Math.max(width, height);
        this.preferredHeight = Math.min(width, height);
    }

    @Override
    public View getDisplayView() {
        return null;
    }


    private PreviewView previewView;

    @Override
    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
        setTextureView(previewView.getTextureView());
    }

    @Override
    public PreviewView getPreviewView() {
        return previewView;
    }

    @Override
    public void takePicture(final OnTakePictureCallback onTakePictureCallback) {
        if (takingPicture.get()) {
            return;
        }

        switch (displayOrientation) {
            case CameraView.ORIENTATION_PORTRAIT:
                parameters.setRotation(90);
                break;
            case CameraView.ORIENTATION_HORIZONTAL:
                parameters.setRotation(0);
                break;
            case CameraView.ORIENTATION_INVERT:
                parameters.setRotation(180);
                break;
            default:
                parameters.setRotation(90);
                break;
        }
        Camera.Size picSize =
                getOptimalSize(preferredWidth, preferredHeight, camera.getParameters().getSupportedPictureSizes());
        parameters.setPictureSize(picSize.width, picSize.height);
        camera.setParameters(parameters);
        takingPicture.set(true);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                camera.cancelAutoFocus();
                try {
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            camera.startPreview();
                            takingPicture.set(false);
                            if (onTakePictureCallback != null) {
                                onTakePictureCallback.onPictureTaken(data);
                            }
                        }
                    });
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    camera.startPreview();
                    takingPicture.set(false);
                }
            }
        });
    }

    @Override
    public void setPermissionCallback(PermissionCallback callback) {
        this.permissionCallback = callback;
    }

    public Camera1Control(Context context) {
        this.context = context;
    }

    // 开启预览
    private void startPreview(boolean checkPermission) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (checkPermission && permissionCallback != null) {
                permissionCallback.onRequestPermission();
            }
            return;
        }
        camera.startPreview();
    }

    private void opPreviewSize(int width, @SuppressWarnings("unused") int height) {
        Camera.Size optSize;
        if (parameters != null && camera != null && width > 0) {
            optSize = getOptimalSize(width, height, camera.getParameters().getSupportedPreviewSizes());

            parameters.setPreviewSize(optSize.width, optSize.height);
            //            camera.stopPreview();
            try {
                camera.setParameters(parameters);
            } catch (RuntimeException e) {
                e.printStackTrace();

            }
            camera.startPreview();
        }
    }

    private Camera.Size getOptimalSize(int width, int height, List<Camera.Size> sizes) {

        Camera.Size pictureSize = sizes.get(0);

        List<Camera.Size> candidates = new ArrayList<>();

        for (Camera.Size size : sizes) {
            if (size.width >= width && size.height >= height && size.width * height == size.height * width) {
                // 比例相同
                candidates.add(size);
            } else if (size.height >= width && size.width >= height && size.width * width == size.height * height) {
                // 反比例
                candidates.add(size);
            }
        }
        if (!candidates.isEmpty()) {
            return Collections.min(candidates, sizeComparator);
        }

        for (Camera.Size size : sizes) {
            if (size.width > width && size.height > height) {
                return size;
            }
        }

        return pictureSize;
    }

    private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
        }
    };

    private void updateFlashMode(int flashMode) {
        switch (flashMode) {
            case FLASH_MODE_TORCH:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                break;
            case FLASH_MODE_OFF:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case ICameraControl.FLASH_MODE_AUTO:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            default:
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
        }
        camera.setParameters(parameters);
    }

    protected int getSurfaceOrientation() {
        @CameraView.Orientation
        int orientation = displayOrientation;
        switch (orientation) {
            case CameraView.ORIENTATION_PORTRAIT:
                return 90;
            case CameraView.ORIENTATION_HORIZONTAL:
                return 0;
            case CameraView.ORIENTATION_INVERT:
                return 180;
            default:
                return 90;
        }
    }

    @Override
    public Rect getPreviewFrame() {
        return previewFrame;
    }


    /**
     * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）
     *
     * @param isPortrait    是否竖屏
     * @param surfaceWidth  需要被进行对比的原宽
     * @param surfaceHeight 需要被进行对比的原高
     * @param preSizeList   需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    public static Camera.Size getCloselyPreSize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Camera.Size> preSizeList) {
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for (Camera.Size size : preSizeList) {
            if ((size.width == reqTmpWidth) && (size.height == reqTmpHeight)) {
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }
        return retSize;
    }

}

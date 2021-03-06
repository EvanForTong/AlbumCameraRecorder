package com.zhongjh.albumcamerarecorder.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.zhongjh.albumcamerarecorder.MainActivity;
import com.zhongjh.albumcamerarecorder.R;
import com.zhongjh.albumcamerarecorder.album.engine.ImageEngine;
import com.zhongjh.albumcamerarecorder.album.engine.impl.GlideEngine;
import com.zhongjh.albumcamerarecorder.album.engine.impl.PicassoEngine;

import gaode.zhongjh.com.common.entity.SaveStrategy;
import gaode.zhongjh.com.common.enums.MimeType;

import com.zhongjh.albumcamerarecorder.listener.OnMainListener;
import com.zhongjh.albumcamerarecorder.settings.api.GlobalSettingApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_BEHIND;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_FULL_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LOCKED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;

/**
 * 用于构建媒体具体公共设置 API。
 * Created by zhongjh on 2018/9/28.
 */
public final class GlobalSetting implements GlobalSettingApi {

    private final MultiMediaSetting mMultiMediaSetting;
    private final GlobalSpec mGlobalSpec;

    // www.代替枚举的@IntDef用法
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @IntDef({
            SCREEN_ORIENTATION_UNSPECIFIED,
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT,
            SCREEN_ORIENTATION_USER,
            SCREEN_ORIENTATION_BEHIND,
            SCREEN_ORIENTATION_SENSOR,
            SCREEN_ORIENTATION_NOSENSOR,
            SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            SCREEN_ORIENTATION_FULL_SENSOR,
            SCREEN_ORIENTATION_USER_LANDSCAPE,
            SCREEN_ORIENTATION_USER_PORTRAIT,
            SCREEN_ORIENTATION_FULL_USER,
            SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {
    }

    /**
     * 在上下文中构造新的规范生成器。
     *
     * @param multiMediaSetting 在 requester context wrapper.
     * @param mimeTypes         设置为选择的 {@link MimeType} 类型
     */
    GlobalSetting(MultiMediaSetting multiMediaSetting, @NonNull Set<MimeType> mimeTypes) {
        mMultiMediaSetting = multiMediaSetting;
        mGlobalSpec = GlobalSpec.getCleanInstance();
        mGlobalSpec.setMimeTypeSet(mimeTypes);
//        mGlobalSpec.orientation = SCREEN_ORIENTATION_UNSPECIFIED;
    }

    @Override
    public GlobalSetting albumSetting(AlbumSetting albumSetting) {
        mGlobalSpec.albumSetting = albumSetting;
        return this;
    }

    @Override
    public GlobalSetting cameraSetting(CameraSetting cameraSetting) {
        mGlobalSpec.cameraSetting = cameraSetting;
        return this;
    }

    @Override
    public GlobalSetting recorderSetting(RecorderSetting recorderSetting) {
        mGlobalSpec.recorderSetting = recorderSetting;
        return this;
    }

    @Override
    public GlobalSetting theme(@StyleRes int themeId) {
        mGlobalSpec.themeId = themeId;
        return this;
    }

    @Override
    public GlobalSetting defaultPosition(int defaultPosition) {
        mGlobalSpec.defaultPosition = defaultPosition;
        return null;
    }


    @Override
    public GlobalSetting maxSelectablePerMediaType(int maxImageSelectable, int maxVideoSelectable, int maxAudioSelectable) {
        mGlobalSpec.maxImageSelectable = maxImageSelectable;
        mGlobalSpec.maxVideoSelectable = maxVideoSelectable;
        mGlobalSpec.maxAudioSelectable = maxAudioSelectable;
        return this;
    }

    @Override
    public GlobalSetting allStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.saveStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting pictureStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.pictureStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting videoStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.videoStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting audioStrategy(SaveStrategy saveStrategy) {
        mGlobalSpec.audioStrategy = saveStrategy;
        return this;
    }

    @Override
    public GlobalSetting imageEngine(ImageEngine imageEngine) {
        mGlobalSpec.imageEngine = imageEngine;
        return this;
    }

    @Override
    public GlobalSetting isCutscenes(boolean isCutscenes) {
        return null;
    }

    @NonNull
    @Override
    public GlobalSetting setOnMainListener(@Nullable OnMainListener listener) {
        mGlobalSpec.onMainListener = listener;
        return this;
    }

    @Override
    public void forResult(int requestCode) {
        Activity activity = mMultiMediaSetting.getActivity();
        if (activity == null) {
            return;
        }

        int numItems = 0;// 数量
        // 根据相关配置做相应的初始化
        if (mGlobalSpec.albumSetting != null) {
            numItems++;
        }
        if (mGlobalSpec.cameraSetting != null) {
            numItems++;
        }
        if (mGlobalSpec.recorderSetting != null && numItems <= 0) {
            if (mGlobalSpec.maxAudioSelectable > 0) {
                numItems++;
            } else {
                if (mGlobalSpec.onMainListener != null) {
                    mGlobalSpec.onMainListener.onOpenFail("录音已经达到上限");
                } else {
                    Toast.makeText(activity.getApplicationContext(), "录音已经达到上限", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (numItems <= 0) {
            throw new IllegalStateException("必须在这三项 albumSetting、cameraSetting、recorderSetting设置其中一项 ");
        }

        Intent intent = new Intent(activity, MainActivity.class);

        Fragment fragment = mMultiMediaSetting.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
            if (mGlobalSpec.isCutscenes)
                activity.overridePendingTransition(R.anim.activity_open, 0);
        }

    }


}

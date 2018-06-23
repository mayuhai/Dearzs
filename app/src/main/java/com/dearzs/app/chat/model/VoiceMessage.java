package com.dearzs.app.chat.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.chat.adapter.ChatAdapter;
import com.dearzs.app.chat.utils.FileUtil;
import com.dearzs.app.chat.utils.MediaUtil;
import com.tencent.TIMMessage;
import com.tencent.TIMSoundElem;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 语音消息数据
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";

    public VoiceMessage(TIMMessage message){
        this.message = message;
    }


    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param data 语音数据
     */
    public VoiceMessage(long duration, byte[] data){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setData(data);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration, String filePath){
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context 显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        LinearLayout linearLayout = new LinearLayout(BaseApplication.getInstance());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        ImageView voiceIcon = new ImageView(BaseApplication.getInstance());
        voiceIcon.setBackgroundResource(message.isSelf()? R.drawable.right_voice: R.drawable.left_voice);
        final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

        TextView tv = new TextView(BaseApplication.getInstance());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(BaseApplication.getInstance().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        tv.setText(String.valueOf(((TIMSoundElem) message.getElement(0)).getDuration()) + "’");
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams imageLp = new LinearLayout.LayoutParams(width, height);
        if (message.isSelf()){
            linearLayout.addView(tv);
            imageLp.setMargins(10, 0, 0, 0);
            voiceIcon.setLayoutParams(imageLp);
            linearLayout.addView(voiceIcon);
        }else{
            voiceIcon.setLayoutParams(imageLp);
            linearLayout.addView(voiceIcon);
            lp.setMargins(10, 0, 0, 0);
            tv.setLayoutParams(lp);
            linearLayout.addView(tv);
        }
        clearView(viewHolder);
        getBubbleView(viewHolder).addView(linearLayout);
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoiceMessage.this.playAudio(frameAnimatio);


            }
        });
        showStatus(viewHolder);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        return BaseApplication.getInstance().getString(R.string.summary_voice);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void playAudio(final AnimationDrawable frameAnimatio) {
        TIMSoundElem elem = (TIMSoundElem) message.getElement(0);

        elem.getSound(new TIMValueCallBack<byte[]>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(byte[] bytes) {
                try{
                    File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
                    FileOutputStream fos = new FileOutputStream(tempAudio);
                    fos.write(bytes);
                    fos.close();
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaUtil.getInstance().play(fis);
                    frameAnimatio.start();
                    MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                        @Override
                        public void onStop() {
                            frameAnimatio.stop();
                            frameAnimatio.selectDrawable(0);
                        }
                    });
                }catch (IOException e){

                }
            }
        });
    }
}

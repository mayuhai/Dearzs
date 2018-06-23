package com.dearzs.app.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dearzs.app.R;
import com.dearzs.app.chat.presenters.MembersDialog;
import com.dearzs.app.chat.presenters.iml.LiveView;
import com.dearzs.app.chat.ui.widget.CircleImageView;
import com.dearzs.app.chat.utils.MemberInfo;
import com.dearzs.app.chat.utils.UIUtils;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.commonlib.utils.log.LogUtil;

import java.util.ArrayList;


/**
 * 成员列表适配器
 */
public class MembersAdapter extends ArrayAdapter<MemberInfo> {
    private static final String TAG = MembersAdapter.class.getSimpleName();
    private LiveView mLiveView;
    private MembersDialog membersDialog;
    private Context mContext;

    public MembersAdapter(Context context, int resource, ArrayList<MemberInfo> objects, LiveView liveView, MembersDialog dialog) {
        super(context, resource, objects);
        mLiveView = liveView;
        membersDialog = dialog;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.members_item_layout, null);
            holder = new ViewHolder();
            holder.headIcon = (CircleImageView) convertView.findViewById(R.id.head_icon);
            holder.name = (TextView) convertView.findViewById(R.id.item_name);
            holder.videoCtrl = (TextView) convertView.findViewById(R.id.video_chat_ctl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MemberInfo data = getItem(position);
        final String selectId = data.getUserId();
        showHeadIcon(mContext, holder.headIcon, data.getAvatar());
        holder.name.setText(data.getUserName());
        if (data.getIsOnVideoChat() == true) {
            holder.videoCtrl.setBackgroundResource(R.mipmap.btn_video_disconnect);

        } else {
            holder.videoCtrl.setBackgroundResource(R.mipmap.btn_video_connection);

        }
        holder.videoCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.LogD(TAG, "select item:  " + selectId);

                if (data.getIsOnVideoChat() == false) {//不在房间中，发起邀请
                    if (mLiveView.showInviteView(selectId)) {
//                        data.setIsOnVideoChat(true);
                        view.setBackgroundResource(R.mipmap.btn_video_disconnect);

                    }
                } else {
                    mLiveView.cancelInviteView(selectId);
//                    data.setIsOnVideoChat(false);
                    view.setBackgroundResource(R.mipmap.btn_video_connection);
                    mLiveView.cancelMemberView(selectId);
                }
                membersDialog.dismiss();

            }
        });


        return convertView;
    }

    public final class ViewHolder {
        public CircleImageView headIcon;
        public TextView name;
        public TextView videoCtrl;
    }

    private void showHeadIcon(Context context, CircleImageView view, String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.un_icon);
            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
            view.setImageBitmap(cirBitMap);
        } else {
            LogUtil.LogD(TAG, "load icon: " + avatar);
            ImageLoaderManager.getInstance().displayImage(avatar, view);
        }
    }

}

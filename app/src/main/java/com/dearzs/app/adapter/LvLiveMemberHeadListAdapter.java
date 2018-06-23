package com.dearzs.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.chat.utils.MemberInfo;
import com.dearzs.app.util.ImageLoaderManager;

import java.util.List;

/**
 * 直播界面---房间成员头像列表适配器
 * Created by luyanlong on 2016/10/28.
 */
public class LvLiveMemberHeadListAdapter extends QuickAdapter<MemberInfo> {
    private boolean mIsHost;

    public LvLiveMemberHeadListAdapter(Context context, int layoutResId, List<MemberInfo> carList, boolean isHost){
        super(context, layoutResId, carList);
        mIsHost = isHost;
    }

    public List<MemberInfo> getData(){
        return data;
    }

    @Override
    protected void convert(BaseAdapterHelper helper, final MemberInfo item) {
        if(item == null){
            return;
        }
        ImageView headPhoto = helper.getView(R.id.iv_item_live_member_head);
        ImageLoaderManager.getInstance().displayImage(item.getAvatar(), headPhoto);
        final ImageView headTag = helper.getView(R.id.iv_item_live_member_head_tag);

        if (item.getIsOnVideoChat()) {
            headTag.setBackgroundResource(R.mipmap.consultation_video_being);
        } else {
            headTag.setVisibility(mIsHost ? View.VISIBLE : View.GONE);
            headTag.setBackgroundResource(R.mipmap.consultation_video_invitation);
        }

//        setListener(helper, item);
    }

    public void notifyItemData(int pos, MemberInfo item){
        getData().set(pos, item);
        notifyDataSetChanged();
    }

    public void notifyItemData(String userId, boolean isOnLine){
        if(!TextUtils.isEmpty(userId)){
            int position = -1;
            List<MemberInfo> data = getData();
            for(int i=0;i<getData().size();i++){
                if(data.get(i) != null && data.get(i) != null && userId.equals(data.get(i).getUserId()) ){
                    position = i;
                    break;
                }
            }
            if(position >= 0 && position < data.size()){
                MemberInfo memberInfo = getItem(position);
                memberInfo.setIsOnVideoChat(isOnLine);
                data.set(position, memberInfo);
                notifyDataSetChanged();
            }
        }
    }

//    private void setListener(BaseAdapterHelper helper, final MemberInfo item){
//        final ImageView headTag = helper.getView(R.id.iv_item_live_member_head_tag);
//        helper.setOnClickListener(R.id.iv_item_live_member_head, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (item.getIsOnVideoChat() == false) {//不在房间中，发起邀请
//                    if (mLiveView.showInviteView(item.getUserId())) {
////                        data.setIsOnVideoChat(true);
//                        headTag.setImageResource(R.mipmap.consultation_video_invitation);
//                    }
//                } else {
//                    mLiveView.cancelInviteView(item.getUserId());
////                    data.setIsOnVideoChat(false);
//                    headTag.setImageResource(R.mipmap.consultation_video_being);
//                    mLiveView.cancelMemberView(item.getUserId());
//                }
//            }
//        });
//    }


}

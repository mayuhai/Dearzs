package com.dearzs.app.chat.presenters;

import android.content.Context;

import com.dearzs.app.chat.avcontrollers.QavsdkControl;
import com.dearzs.app.chat.model.MySelfInfo;
import com.dearzs.app.chat.presenters.iml.MembersDialogView;
import com.dearzs.app.chat.utils.MemberInfo;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupMemberInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 16/5/24.
 */
public class GetMemberListHelper extends Presenter {
    private Context mContext;
    private MembersDialogView mMembersDialogView;
    private static final String TAG = GetMemberListHelper.class.getSimpleName();
    private ArrayList<MemberInfo> mDialogMembers = new ArrayList<MemberInfo>();

    public GetMemberListHelper(Context context, MembersDialogView dialogView) {
        mContext = context;
        mMembersDialogView = dialogView;
    }

    public void getMemberList() {
        TIMGroupManager.getInstance().getGroupMembers("" + MySelfInfo.getInstance().getMyRoomNum(), new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int i, String s) {
                LogUtil.LogD(TAG, "get MemberList ");
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                LogUtil.LogD(TAG, "get MemberList ");
                getMemberListInfo(timGroupMemberInfos);
            }
        });
    }


    /**
     * 拉取成员列表信息
     *
     * @param timGroupMemberInfos
     */
    private void getMemberListInfo(List<TIMGroupMemberInfo> timGroupMemberInfos) {
        mDialogMembers.clear();
        getMemberInfo(timGroupMemberInfos);
    }

    private void getMemberInfo(final List<TIMGroupMemberInfo> timGroupMemberInfos) {
        if (timGroupMemberInfos == null) return;
        List<String> memberIdArr = new ArrayList<String>();
        for (TIMGroupMemberInfo item : timGroupMemberInfos) {
            if (item.getUser().equals(MySelfInfo.getInstance().getId())) {
                continue;
            }
            memberIdArr.add(item.getUser());
        }
        //如果房间中只有主播，则直接回调，否则获取房间成员信息
        if (memberIdArr != null && memberIdArr.size() == 0) {
            mMembersDialogView.showMembersList(mDialogMembers);
        } else {
            TIMFriendshipManager.getInstance().getUsersProfile(memberIdArr, new TIMValueCallBack<List<TIMUserProfile>>() {
                @Override
                public void onError(int i, String s) {
                    LogUtil.LogD("getMemberInfo", "===" + s);
                }

                @Override
                public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                    if (timGroupMemberInfos == null) return;
                    for (TIMUserProfile profile : timUserProfiles) {
                        MemberInfo member = new MemberInfo();
                        member.setUserId(profile.getIdentifier());
                        member.setAvatar(profile.getFaceUrl());
                        member.setUserName(profile.getNickName());
                        if (QavsdkControl.getInstance().containIdView(profile.getIdentifier())) {
                            member.setIsOnVideoChat(true);
                        }
                        mDialogMembers.add(member);
                    }
                    mMembersDialogView.showMembersList(mDialogMembers);
                }
            });
        }
    }


    @Override
    public void onDestory() {
//        mMembersDialogView =null;
        mContext = null;
    }
}

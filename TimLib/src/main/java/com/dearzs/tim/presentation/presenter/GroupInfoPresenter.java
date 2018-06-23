package com.dearzs.tim.presentation.presenter;

import com.dearzs.tim.presentation.viewfeatures.GroupInfoView;
import com.tencent.TIMGroupDetailInfo;
import com.tencent.TIMGroupManager;
import com.tencent.TIMValueCallBack;

import java.util.List;


/**
 * 群信息逻辑
 */
public class GroupInfoPresenter implements TIMValueCallBack<List<TIMGroupDetailInfo>> {

    private GroupInfoView view;
    private boolean isInGroup;
    private List<String> groupIds;

    public GroupInfoPresenter(GroupInfoView view,List<String> groupIds,boolean isInGroup){
        this.view = view;
        this.isInGroup = isInGroup;
        this.groupIds = groupIds;
    }


    public void getGroupDetailInfo(){
        if (isInGroup) {
            TIMGroupManager.getInstance().getGroupDetailInfo(groupIds, this);
        }else{
            TIMGroupManager.getInstance().getGroupPublicInfo(groupIds, this);
        }
    }



    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
        view.showGroupInfo(timGroupDetailInfos);
    }
}

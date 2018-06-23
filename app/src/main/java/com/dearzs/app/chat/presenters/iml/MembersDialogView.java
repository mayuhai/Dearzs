package com.dearzs.app.chat.presenters.iml;

import com.dearzs.app.chat.utils.MemberInfo;
import com.dearzs.tim.presentation.viewfeatures.MvpView;

import java.util.ArrayList;

/**
 * 成员列表回调
 */
public interface MembersDialogView extends MvpView {

    void showMembersList(ArrayList<MemberInfo> data);

}

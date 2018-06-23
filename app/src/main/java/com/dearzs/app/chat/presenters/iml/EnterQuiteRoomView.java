package com.dearzs.app.chat.presenters.iml;

import com.dearzs.app.chat.model.LiveInfoJson;
import com.dearzs.tim.presentation.viewfeatures.MvpView;

/**
 * 进出房间回调接口
 */
public interface EnterQuiteRoomView extends MvpView {


    void enterRoomComplete(int id_status, boolean succ);

    void quiteRoomComplete(int id_status, boolean succ, LiveInfoJson liveinfo);

    void memberQuiteLive(String[] list);

    void memberJoinLive(String[] list);

    void alreadyInLive(String[] list);


}

package com.dearzs.app.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.dearzs.app.BaseApplication;
import com.dearzs.app.R;
import com.dearzs.app.base.BaseActivity;
import com.dearzs.app.entity.EntityDiseaseDpmtInfo;
import com.dearzs.app.entity.EntityUserInfo;
import com.dearzs.app.entity.resp.RespGetDiseaseCategory;
import com.dearzs.app.util.Constant;
import com.dearzs.app.util.ReqManager;
import com.dearzs.app.util.ToastUtil;
import com.dearzs.app.util.Utils;
import com.dearzs.app.widget.TitleBarView;
import com.dearzs.app.widget.expendholder.FirstLevelHolder;
import com.dearzs.app.widget.expendholder.IconHolder;
import com.dearzs.app.widget.expendholder.SecondLevelHolder;
import com.dearzs.app.widget.expendholder.ThirdLevelHolder;
import com.dearzs.commonlib.okhttp.callback.Callback;
import com.dearzs.commonlib.utils.log.LogUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by luyanlong on 2016/9/1.
 * 擅长疾病选择界面
 */
public class GoodAtActivity extends BaseActivity {
    private TreeNode mRootNodeView;
    private ViewGroup mContainerView;
    private AndroidTreeView mTreeView;
    private EntityUserInfo mUserInfo;
    List<String> mSelectedDiseases = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_disease_good_at);

        addCenter(TitleBarView.TITLE_TYPE_CENTER_TXT, "擅长疾病");
        addLeftBtn(TitleBarView.TITLE_TYPE_LEFT_IV_BACK, null);
        addRightBtn(TitleBarView.TITLE_TYPE_RIGHT_TXT, "确定");
    }

    @Override
    public void initView() {
        super.initView();
        mContainerView = (ViewGroup) findViewById(R.id.container);
        mTreeView = new AndroidTreeView(GoodAtActivity.this);
    }

    @Override
    public void onRightBtnClick() {
        super.onRightBtnClick();
        List<String> selectedValues = mTreeView.getSelectedValues(String.class);
        StringBuffer selectedDisease = new StringBuffer();
        for (int i = 0; i < selectedValues.size(); i++) {
            selectedDisease.append(selectedValues.get(i));
            selectedDisease.append(",");
        }
        if (selectedDisease.length() > 0) {
            mUserInfo.setLabel(selectedDisease.substring(0, selectedDisease.length() - 1));
            EventBus.getDefault().post(selectedDisease.substring(0, selectedDisease.length() - 1));
            finish();
        } else {
            ToastUtil.showLongToast("您还没有选择您擅长的疾病");
        }
    }

    @Override
    public void initData() {
        super.initData();
        String selectedNode = getIntent().getExtras().getString(Constant.KEY_DISEASE_STR);
        if (!TextUtils.isEmpty(selectedNode)) {
            mSelectedDiseases = Arrays.asList(selectedNode.split(","));
        }
        mUserInfo = BaseApplication.getInstance().getUserInfo();
        ReqManager.getInstance().getDiseaseCategoryList(reqDiseaseCategoryListCall, Utils.getUserToken(GoodAtActivity.this));
    }

    private void fillThirdFolder(TreeNode folder, String name, boolean isChecked, int relLevel) {
        TreeNode file1 = new TreeNode(name).setViewHolder(new ThirdLevelHolder(GoodAtActivity.this, relLevel));
        folder.addChildren(file1);
        mTreeView.selectNode(file1, isChecked);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", mTreeView.getSaveState());
    }

    //疾病分类列表接口回调
    Callback<RespGetDiseaseCategory> reqDiseaseCategoryListCall = new Callback<RespGetDiseaseCategory>() {
        @Override
        public void onError(Call call, Exception e) {
            onFailure(e.toString());
            closeProgressDialog();
        }

        @Override
        public void onResponse(RespGetDiseaseCategory response) {
            closeProgressDialog();
            if (onSuccess(response)) {
                if (response.getResult() != null && response.getResult().getList() != null) {
                    bindViewData(response.getResult().getList());
                }
            }
        }

        @Override
        public void onBefore(Request request) {
            super.onBefore(request);
            showProgressDialog();
        }
    };

    private void bindViewData(List<EntityDiseaseDpmtInfo> list) {
        mRootNodeView = TreeNode.root();
        mTreeView.setDefaultAnimation(true);
        mTreeView.setRoot(mRootNodeView);
        addLeveViews(list);
        mTreeView.setSelectionModeEnabled(true);
        mContainerView.addView(mTreeView.getView());
    }

    private void addLeveViews(List<EntityDiseaseDpmtInfo> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            String firstLevelTitle = dataList.get(i).getDepartment();
            if (!TextUtils.isEmpty(firstLevelTitle)) {
                TreeNode firstLevelNode = new TreeNode(new IconHolder.IconTreeItem(0, firstLevelTitle)).setViewHolder(new FirstLevelHolder(GoodAtActivity.this));

                List<EntityDiseaseDpmtInfo.EntityDiseaseType> diseaseTypes = dataList.get(i).getTypes();
                if (diseaseTypes != null && diseaseTypes.size() > 0) {
                    for (int j = 0; j < diseaseTypes.size(); j++) {
                        String[] thirdLevelList = diseaseTypes.get(j).getList();
                        String secondLevelTitle = diseaseTypes.get(j).getName();
                        if (thirdLevelList != null && thirdLevelList.length > 0) {
                            if (!TextUtils.isEmpty(secondLevelTitle)) {
                                TreeNode secondLevelNode = new TreeNode(new IconHolder.IconTreeItem(0, secondLevelTitle)).setViewHolder(new SecondLevelHolder(GoodAtActivity.this));

                                String thirdLevelName = diseaseTypes.get(j).getName();
                                if (!TextUtils.isEmpty(thirdLevelName)) {
                                    String[] diseases = diseaseTypes.get(j).getList();
                                    if (diseases != null) {
                                        for (int t = 0; t < diseases.length; t++) {
                                            if (mSelectedDiseases != null && mSelectedDiseases.size() > 0) {
                                                fillThirdFolder(secondLevelNode, diseases[t], mSelectedDiseases.contains(diseases[t]), 3);
                                            } else {
                                                fillThirdFolder(secondLevelNode, diseases[t], false, 3);
                                            }
                                        }
                                    }
                                }
                                firstLevelNode.addChild(secondLevelNode);
                                if (mSelectedDiseases != null && mSelectedDiseases.size() > 0 && diseaseTypes.get(j) != null && diseaseTypes.get(j).getList() != null) {
                                    mTreeView.selectNode(secondLevelNode, mTreeView.getSelected(secondLevelNode).size() == diseaseTypes.get(j).getList().length);
                                }
                            }
                        } else {
                            if (mSelectedDiseases != null && mSelectedDiseases.size() > 0) {
                                fillThirdFolder(firstLevelNode, secondLevelTitle, mSelectedDiseases.contains(secondLevelTitle), 2);
                            } else {
                                fillThirdFolder(firstLevelNode, secondLevelTitle, false, 2);
                            }
                        }
                    }
                    mRootNodeView.addChildren(firstLevelNode);
                    if (mSelectedDiseases != null && mSelectedDiseases.size() > 0 && diseaseTypes != null) {
                        mTreeView.selectNode(firstLevelNode, mTreeView.getSelected(firstLevelNode, true).size() == diseaseTypes.size());
                    }
                } else {
                    String[] diseases = dataList.get(i).getList();
                    if (diseases != null && diseases.length > 0) {
                        for (int k = 0; k < diseases.length; k++) {
                            String secondLevelTitle = diseases[k];
                            if (mSelectedDiseases != null && mSelectedDiseases.size() > 0) {
                                fillThirdFolder(firstLevelNode, secondLevelTitle, mSelectedDiseases.contains(secondLevelTitle), 3);
                            } else {
                                fillThirdFolder(firstLevelNode, secondLevelTitle, false, 3);
                            }
                        }
                        mRootNodeView.addChildren(firstLevelNode);
                        if (mSelectedDiseases != null && mSelectedDiseases.size() > 0 && diseases != null) {
                            mTreeView.selectNode(firstLevelNode, mTreeView.getSelected(firstLevelNode, true).size() == diseases.length);
                        }
                    } else {
                        if (mSelectedDiseases != null && mSelectedDiseases.size() > 0) {
                            fillThirdFolder(mRootNodeView, firstLevelTitle, mSelectedDiseases.contains(firstLevelTitle), 1);
                        } else {
                            fillThirdFolder(mRootNodeView, firstLevelTitle, false, 1);
                        }
                    }
                }
                if (mSelectedDiseases != null && mSelectedDiseases.size() > 0 && diseaseTypes != null) {
                    mTreeView.selectNode(firstLevelNode, mTreeView.getSelected(firstLevelNode, true).size() == diseaseTypes.size());
                }
            }
        }
    }

    /**
     * Activity跳转
     *
     * @param b
     */
    public static void startIntent(Context ctx, Bundle b) {
        Intent intent = new Intent();
        intent.setClass(ctx, GoodAtActivity.class);
        if (null != b) {
            intent.putExtras(b);
        }
        ctx.startActivity(intent);
    }
}

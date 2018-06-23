package com.dearzs.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.adapter.basic.BaseAdapterHelper;
import com.dearzs.app.adapter.basic.QuickAdapter;
import com.dearzs.app.entity.EntityPatientInfo;
import com.dearzs.app.util.ImageLoaderManager;
import com.dearzs.app.widget.CircleImageView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * 客户列表adapter
 */
public class LvPatientListAdapter extends QuickAdapter<EntityPatientInfo> implements SectionIndexer {
    private List<EntityPatientInfo> mDataList;
    private Context mContext;
    public LvPatientListAdapter(Context context, int layoutResId, List<EntityPatientInfo> dataList) {
        super(context, layoutResId, dataList);
        mDataList = dataList;
        mContext = context;
    }

    @Override
    protected void convert(BaseAdapterHelper helper, EntityPatientInfo item) {
        CircleImageView userPic = helper.getView(R.id.item_iv_patient_pic);
        TextView userName = helper.getView(R.id.item_iv_patient_name);
        ImageView userSex = helper.getView(R.id.item_iv_patient_sex);
        TextView tvLetter = helper.getView(R.id.custom_cell_catalog);
        ImageView newTag = helper.getView(R.id.iv_patient_new_tag);
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(helper.getPosition());

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (helper.getPosition() == getPositionForSection(section) && !"#".equals(item.getSortLetters())) {
            tvLetter.setVisibility(View.VISIBLE);
            tvLetter.setText(item.getSortLetters());
        } else {
            tvLetter.setVisibility(View.GONE);
        }
        newTag.setVisibility(item.getIsNew() == 1 ? View.VISIBLE : View.GONE);
        ImageLoaderManager.getInstance().displayImage(item.getAvatar(), userPic);
        userSex.setImageResource(item.getGender() == 1 ? R.mipmap.ic_male : R.mipmap.ic_female);
        userName.setText(item.getName());
        setListener(helper, item);
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<EntityPatientInfo> list) {
        mDataList = list;
        replaceAll(list);
        notifyDataSetChanged();
    }

    private void setListener(BaseAdapterHelper helper, final EntityPatientInfo item){
    }
    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(!TextUtils.isEmpty(mDataList.get(position).getSortLetters())){
           return mDataList.get(position).getSortLetters().charAt(0);
        } else {
            return -1;
        }
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).getSortLetters();
            if(!TextUtils.isEmpty(sortStr)){
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            } else {
                return -1;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}

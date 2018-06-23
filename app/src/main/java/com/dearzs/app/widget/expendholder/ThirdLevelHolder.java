package com.dearzs.app.widget.expendholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.util.DimenUtils;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class ThirdLevelHolder extends TreeNode.BaseNodeViewHolder<String> {
    private TextView tvValue;
    private CheckBox nodeSelector;
    private int mRelLevel = 3;      //真实的级别，默认是第三级
    private Context mContext;

    public ThirdLevelHolder(Context context, int relLevel) {
        super(context);
        mRelLevel = relLevel;
        mContext = context;
    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_third_level, null, false);
        final View topLineArea = view.findViewById(R.id.third_level_top_line);
        final ImageView leftImg = (ImageView) view.findViewById(R.id.picture_wrapper);
        final View secondLevel = view.findViewById(R.id.second_level_bottom_line);
        final View thirdLevel = view.findViewById(R.id.third_level_bottom_line);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value);
        int textSize = (mRelLevel == 3 || mRelLevel == 2) ? 14 : 16;
        tvValue.setTextSize((float) textSize);

        topLineArea.setVisibility(mRelLevel == 1 ? View.VISIBLE : View.GONE);
        secondLevel.setVisibility(mRelLevel == 2 ? View.VISIBLE : View.GONE);
        thirdLevel.setVisibility(mRelLevel == 3 ? View.VISIBLE : View.GONE);

        int topMargin = mRelLevel == 1 ? DimenUtils.dip2px(mContext, 13) : (mRelLevel == 2 ? DimenUtils.dip2px(mContext, 16) : DimenUtils.dip2px(mContext, 20));
        int leftMargin = mRelLevel == 1 ? DimenUtils.dip2px(mContext, 12) : (mRelLevel == 2 ? DimenUtils.dip2px(mContext, 14) : DimenUtils.dip2px(mContext, 36));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.leftMargin = leftMargin;
        params.setMargins(leftMargin, topMargin ,0 ,topMargin);
        leftImg.setLayoutParams(params);

        leftImg.setImageResource(mRelLevel == 1 ? R.mipmap.ic_good_at_first_level_point : (mRelLevel == 2 ? R.mipmap.ic_good_at_second_level_point : R.mipmap.ic_good_at_third_level_point));

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
            }
        });
        nodeSelector.setChecked(node.isSelected());

        return view;
    }


    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}

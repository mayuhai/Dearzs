package com.dearzs.app.widget.expendholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dearzs.app.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/13/15.
 */
public class FirstLevelHolder extends TreeNode.BaseNodeViewHolder<IconHolder.IconTreeItem> {
    private CheckBox mCb;
    private ImageView mArrowView;
    private TreeNode mNode;
    private View mBottomLine;

    public FirstLevelHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconHolder.IconTreeItem value) {
        mNode = node;
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_first_level, null, false);
        mBottomLine = view.findViewById(R.id.first_level_bottom_line);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        mCb = (CheckBox)view.findViewById(R.id.profile_checkbox);
        mArrowView = (ImageView) view.findViewById(R.id.profile_arrow);
        tvValue.setText(value.text);

        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, isChecked);
                    getTreeView().expandNode(n);
                }
            }
        });
        mCb.setChecked(node.isSelected());
        return view;
    }



    @Override
    public void toggle(boolean active) {
        mBottomLine.setVisibility(mNode.isExpanded() ? View.VISIBLE : View.GONE);
        mArrowView.setImageResource(active ? R.mipmap.ic_good_at_open_big : R.mipmap.ic_good_at_close_big);
    }
    @Override
    public int getContainerStyle() {
        return R.style.TreeNodeStyleCustom;
    }
}

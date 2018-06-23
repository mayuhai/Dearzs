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
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SecondLevelHolder extends TreeNode.BaseNodeViewHolder<IconHolder.IconTreeItem> {
    private TextView tvValue;
    private ImageView arrowView;
    private CheckBox nodeSelector;

    public SecondLevelHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconHolder.IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_second_level, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);
        arrowView = (ImageView) view.findViewById(R.id.arrow_icon);
        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                for (TreeNode n : node.getChildren()) {
                    getTreeView().selectNode(n, isChecked);
                }
            }
        });
        nodeSelector.setChecked(node.isSelected());

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setImageResource(active ? R.mipmap.ic_good_at_open_small : R.mipmap.ic_good_at_close_small);
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }
}

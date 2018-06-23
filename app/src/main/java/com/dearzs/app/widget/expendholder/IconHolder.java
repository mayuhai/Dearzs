package com.dearzs.app.widget.expendholder;

import android.content.Context;
import android.view.View;

import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconHolder extends TreeNode.BaseNodeViewHolder<IconHolder.IconTreeItem> {
    public IconHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
//        final LayoutInflater inflater = LayoutInflater.from(context);
//        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        return null;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}

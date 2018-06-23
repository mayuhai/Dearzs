package com.dearzs.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dearzs.app.R;
import com.dearzs.app.util.DimenUtils;
import com.dearzs.commonlib.utils.DisplayUtil;
import com.dearzs.commonlib.utils.LayoutUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单流程图布局
 *
 * @author Lyl
 */
public class LayoutSignTree extends LinearLayout {
    private final static int DEFAULT_TREE_NODE_NUM = 6;
    private int[] mNumImages;
    private Context mContext;
    /**
     * 节点数量（默认值为7）
     */
    private int mTreeNodeNum = DEFAULT_TREE_NODE_NUM;
    /**
     * 当前选择的节点索引
     */
    private int mCurrentSelectIndex = 0;
    /**
     * 节点集合
     */
    private List<TextView> mTreeNodeList = new ArrayList<>();
    /**
     * 节点对应的文字
     */
    private String[] mNodeTips;

    public LayoutSignTree(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        // 将该布局强制设置横向
        int orientation = getOrientation();
        if (orientation != HORIZONTAL) {
            setOrientation(HORIZONTAL);
        }
        setGravity(Gravity.CENTER);
//        inflateTreeNode();
    }

    /**
     * 向父布局中填充节点
     */
    private void inflateTreeNode() {
        if(mNumImages == null || mNodeTips == null){
            return;
        }
        // 清除所有的子布局
        removeAllViews();
        mTreeNodeList.clear();
        // 开始填充子布局
        for (int i = 0; i < mTreeNodeNum; i++) {
            View child = View.inflate(getContext(), R.layout.layout_item_sign_tree, null);
            // 设置节点的文本内容
            TextView txtSignTreeNode = (TextView) child.findViewById(R.id.txt_sign_tree_node);
            txtSignTreeNode.setBackgroundResource(mNumImages[i]);
            mTreeNodeList.add(txtSignTreeNode);
            // 设置节点的提示文字
            if (mNodeTips != null && mNodeTips.length == mTreeNodeNum) {
                TextView txtSignTreeTips = (TextView) child.findViewById(R.id.txt_sign_tree_coin);
                txtSignTreeTips.setText(mNodeTips[i]);
                LayoutUtil.reMeasureWidth(txtSignTreeTips, (DisplayUtil.getScreenWidth(mContext) - DimenUtils.dip2px(mContext, 10)) / mTreeNodeNum);
            }
            // 如果是最后一个子布局，则隐藏间隔线
            if (i == mTreeNodeNum - 1) {
                child.findViewById(R.id.view_sign_tree_line_right).setVisibility(View.INVISIBLE);
            } else if(i == 0){
                child.findViewById(R.id.view_sign_tree_line_left).setVisibility(View.INVISIBLE);
            }

            addView(child);
        }
    }

    /**
     * 设置节点的数量
     *
     * @param treeNodeNum
     */
    public void setTreeNodeNum(int treeNodeNum, String[] nodeTips, int numImages[]) {
        // 如果设置数量相等，则不需要重新填充
        if (nodeTips == null || nodeTips.length != treeNodeNum) {
            return;
        }
        this.mNodeTips = nodeTips;
        this.mTreeNodeNum = treeNodeNum;
        this.mNumImages = numImages;
        inflateTreeNode();
    }

    /**
     * 设置选中节点的索引(从1开始)
     *
     * @param index
     */
    public void setSelectIndex(int index) {
        mCurrentSelectIndex = 0;
        System.out.println("setSelectIndex index = " + index);
        // 判断索引是否大于节点数或者是否等于0或者等于当前已经选中的节点数,则不作任何处理
        if (index < 0 || index > mTreeNodeNum || mCurrentSelectIndex == index) {
            return;
        }

        if (index == 0) {

        }else if (index == mTreeNodeNum) {
            for (int i = 0; i < mTreeNodeNum; i++) {
                TextView node = mTreeNodeList.get(i);
//                node.setEnabled(true);
//                node.setText(String.valueOf(i));

                //此时显示对号
                node.setBackgroundResource(R.mipmap.ic_order_step_finish_white);
            }
        }else if (index > mCurrentSelectIndex) {
                for (int i = mCurrentSelectIndex; i < index; i++) {
                    TextView node = mTreeNodeList.get(i);
                    if(i == index - 1){
                        //此时显示...
                        node.setBackgroundResource(R.mipmap.ic_order_step_wait_white);
                    } else {
                        //此时显示对号
                        node.setBackgroundResource(R.mipmap.ic_order_step_finish_white);
                    }
//                node.setEnabled(false);
//                node.setText("");
                }
            }
//        else {
//            for (int i = index; i < mCurrentSelectIndex; i++) {
//                TextView node = mTreeNodeList.get(i);
//                node.setEnabled(true);
//                node.setText(String.valueOf(i));
//            }
//        }

        mCurrentSelectIndex = index;
    }
}

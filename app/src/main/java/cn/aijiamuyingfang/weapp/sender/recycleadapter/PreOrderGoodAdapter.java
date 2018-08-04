package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;

import java.util.List;

import cn.aijiamuyingfang.commons.domain.shoporder.PreOrderGood;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;


/**
 * Created by pc on 2018/5/7.
 */

public class PreOrderGoodAdapter extends CommonAdapter<PreOrderGood> {

    public PreOrderGoodAdapter(Context context, List<PreOrderGood> data) {
        super(context, data, R.layout.adapter_item_preorder_good);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHolder, PreOrderGood itemData, int position) {
        viewHolder.setText(R.id.good_name, itemData.getGood().getName());
        viewHolder.setText(R.id.good_count, itemData.getCount() + "");
    }
}

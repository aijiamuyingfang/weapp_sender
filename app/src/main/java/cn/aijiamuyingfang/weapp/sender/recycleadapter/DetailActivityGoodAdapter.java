package cn.aijiamuyingfang.weapp.sender.recycleadapter;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

import cn.aijiamuyingfang.commons.domain.shoporder.ShopOrderItem;
import cn.aijiamuyingfang.weapp.manager.widgets.GlideUtils;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.CommonAdapter;
import cn.aijiamuyingfang.weapp.manager.widgets.recycleview.adapter.RecyclerViewHolder;
import cn.aijiamuyingfang.weapp.sender.R;

/**
 * Created by pc on 2018/5/7.
 */

public class DetailActivityGoodAdapter extends CommonAdapter<ShopOrderItem> {

    public DetailActivityGoodAdapter(Context context, List<ShopOrderItem> data) {
        super(context, data, R.layout.adapter_item_detail_activity_good);
    }

    @Override
    protected void convert(RecyclerViewHolder viewHoloder, ShopOrderItem itemData, int position) {
        GlideUtils.load(mContext, itemData.getGood().getCoverImg(), (ImageView) viewHoloder.getView(R.id.good_coverimg));
        viewHoloder.setText(R.id.good_name, itemData.getGood().getName());
        viewHoloder.setText(R.id.good_price, "￥" + itemData.getGood().getPrice());
        viewHoloder.setText(R.id.good_count, itemData.getGood().getPack() + " " + itemData.getGood().getLevel() + "  *" + itemData.getCount());
    }
}

package qumi.com.qtalk.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import qumi.com.qtalk.R;
import qumi.com.qumitalk.service.Db.Session;
import qumi.com.qtalk.view.CircleImageView;

public class AddFriendAdapter extends BaseAdapter {
	private Context mContext;
	private List<Session> lists;

	public AddFriendAdapter(Context context, List<Session> lists) {
		this.mContext = context;
		this.lists = lists;
	}

	@Override
	public int getCount() {
		if (lists != null) {
			return lists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.fragment_news_item,null);
			holder = new Holder();
			holder.iv = (CircleImageView) convertView.findViewById(R.id.user_head);
			holder.tv_name = (TextView) convertView.findViewById(R.id.user_name);
			holder.tv_tips = (TextView) convertView.findViewById(R.id.tips);
			holder.tv_content = (TextView) convertView.findViewById(R.id.content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		
		Session session = lists.get(position);
		holder.tv_name.setText(session.getFromUser());
		holder.tv_content.setText("加我吧...");
		return convertView;
	}

	class Holder {
		CircleImageView iv;
		TextView tv_name;
		TextView tv_tips;
		TextView tv_content;
		TextView tv_time;
	}

}

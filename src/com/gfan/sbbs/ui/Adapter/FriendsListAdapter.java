package com.gfan.sbbs.ui.Adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gfan.sbbs.bean.User;
import com.gfan.sbbs.ui.main.R;

public class FriendsListAdapter extends BaseAdapter {
	private List<User> friends;
	private Context context;
	private LayoutInflater mInflater;

	public FriendsListAdapter(Context context) {
		friends = new ArrayList<User>();
		this.context = context;
	}

	public FriendsListAdapter(LayoutInflater mInflater) {
		this.friends = new ArrayList<User>();
		this.mInflater = mInflater;
	}

	@Override
	public int getCount() {
		return friends.size();
	}

	@Override
	public Object getItem(int position) {
		return friends.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refresh(List<User> list) {
		this.friends = list;
		notifyDataSetChanged();
	}

	public void refresh() {
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			if (null == mInflater) {
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}
			convertView = mInflater.inflate(R.layout.friends_item, null);
			holder = new ViewHolder();
			holder.friend_id = (TextView) convertView
					.findViewById(R.id.friend_id);
			holder.friend_status = (TextView) convertView
					.findViewById(R.id.friend_status);
			holder.friend_nickName = (TextView) convertView
					.findViewById(R.id.friend_nick);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.friend_id.setText(friends.get(position).getId());
		holder.friend_nickName.setText("("
				+ friends.get(position).getNickName() + ")");
		holder.friend_status.setText(friends.get(position).getStatus());
		return convertView;
	}

	private static class ViewHolder {
		TextView friend_id;
		TextView friend_nickName;
		TextView friend_status;
	}
}
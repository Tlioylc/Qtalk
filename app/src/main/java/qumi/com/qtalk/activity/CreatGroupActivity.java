package qumi.com.qtalk.activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

import qumi.com.qtalk.R;
import qumi.com.qumitalk.service.DataBean.UserBean;
import qumi.com.qumitalk.service.QtalkClient;

public class CreatGroupActivity extends Activity {

    private RecyclerView friendList;
    private Button createView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_group);

        friendList = findViewById(R.id.friend_list);
        createView = findViewById(R.id.btn_create);
        editText = findViewById(R.id.group_name);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
        friendList.setLayoutManager(linearLayoutManager);
//
        final FriendAdapter friendAdapter = new FriendAdapter();
//


        friendAdapter.init(QtalkClient.getInstance().getQMMContactsManager().getAllContactsFromServer());
        friendList.setAdapter(friendAdapter);
//

        createView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> inviteList = friendAdapter.getInviteList();
                try {
					QtalkClient.getInstance().getQMGoupChatManager().createChatGroup(editText.getText().toString(),"",inviteList,"XXX邀请您加入群聊");
				}catch (Exception e){
					Toast.makeText(CreatGroupActivity.this,"已创建或加入该群聊",Toast.LENGTH_SHORT).show();
				}
            }
        });

    }



    public class CustomHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private CheckBox checkBox;
        public CustomHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.contact_list_item_name);
            checkBox = itemView.findViewById(R.id.cpntact_list_item_check);
            checkBox.setVisibility(View.VISIBLE);
        }

        public void initView(final UserBean userBean){
            userName.setText(userBean.getNickName());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        userBean.setIsChecked(1);
                    }else {
                        userBean.setIsChecked(0);
                    }
                }
            });
        }
    }

    public class FriendAdapter extends RecyclerView.Adapter<CustomHolder>{
        private List<UserBean> userList;

        public void init(List<UserBean> userList){
            this.userList = userList;
            notifyDataSetChanged();
        }

        public ArrayList<String> getInviteList(){
            ArrayList<String> list = new ArrayList<>();

            for(UserBean userBean : userList){
                if(userBean.getIsChecked() == 1){
                    list.add(userBean.getNickName());
                }
            }

            return list;
        }

        @NonNull
        @Override
        public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View ghostView = LayoutInflater.from(CreatGroupActivity.this).inflate(R.layout.fragment_constact_child,parent, false);
            ghostView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return new CustomHolder(ghostView);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
            holder.initView(userList.get(position));
        }

        @Override
        public int getItemCount() {
            if(userList == null || userList.size() == 0)
                return 0;
            else
                return userList.size();
        }
    }
}

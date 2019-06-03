package com.example.bookingball.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingball.MyAcount;
import com.example.bookingball.R;
import com.example.bookingball.activitys.AddBillActivity;
import com.example.bookingball.database.SettleBill;
import com.example.bookingball.database.UnpaidBill;

import java.util.List;

public class SettleBillAdapter extends RecyclerView.Adapter<SettleBillAdapter.ViewHolder> {
    private Context context;
    private List<SettleBill> settleBillList;
    private SelectPopupWindow popupView;

    public SettleBillAdapter(List<SettleBill> settleBills) {
        settleBillList = settleBills;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView totalText;
        TextView createDateText;
        TextView typeText;
        CardView cardView;
        Button selectButton; //右上角选择按钮

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v;
            nameText = v.findViewById(R.id.settle_name_item);
            totalText = v.findViewById(R.id.settle_total_item);
            createDateText = v.findViewById(R.id.settle_create_date_item);
            typeText = v.findViewById(R.id.settle_type_item);
            selectButton = v.findViewById(R.id.settle_select_button);
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加点击事件
                    popupView.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                    popupView.setCancelButtonListener(new View.OnClickListener() {//取消
                        @Override
                        public void onClick(View v) {
                            popupView.dismiss();
                        }
                    });
                    //设置删除按钮监听器
                    popupView.setDeleteButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setTitle("警告")
                                    .setMessage("确定删除此账单吗？删除后将无法恢复")
                                    .setCancelable(true)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            settleBillList.get(getLayoutPosition()).delete();//从数据库删除
                                            settleBillList.remove(getLayoutPosition());//删除此账单
                                            popupView.dismiss();
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "成功删除", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .show();
                        }
                    });
                    //设置修改按键监听器
                    popupView.setUpdateButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, AddBillActivity.class);
                            intent.putExtra("settleBill", settleBillList.get(getLayoutPosition()));
                            popupView.dismiss();
                            context.startActivity(intent);
                        }
                    });
                    //处理设为未结按钮点击事件
                    popupView.getSettleButton().setText("设为未结");
                    popupView.setSettleButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (settleBillList.get(getLayoutPosition()).getType().equals("收入"))
                                Toast.makeText(context, "工资收入不能设为未结", Toast.LENGTH_SHORT).show();
                            else {
                                int position=getLayoutPosition();
                                SettleBill settleBill = settleBillList.get(position);
                                UnpaidBill unpaidBill = new UnpaidBill();
                                unpaidBill.setUserName(MyAcount.userName);
                                unpaidBill.setName(settleBill.getName());
                                unpaidBill.setType(settleBill.getType());
                                unpaidBill.setCreateDate(settleBill.getCreateTime());
                                unpaidBill.setRemarks(settleBill.getRemarks());
                                unpaidBill.setTotal(settleBill.getTotal());
                                unpaidBill.setTel(settleBill.getTel());
                                unpaidBill.save();
                                settleBillList.get(position).delete();
                                settleBillList.remove(position);
                                popupView.dismiss();
                                notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null) {
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.settle_bill_item, viewGroup, false);
        View popView = View.inflate(context, R.layout.popup_menu, null);
        popupView = new SelectPopupWindow(popView);
        popupView.setTouchable(true);
        popupView.setFocusable(true);
        popupView.setOutsideTouchable(true);
        popupView.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettleBill settleBill = settleBillList.get(position);
        holder.nameText.setText("对象：" + settleBill.getName());
        holder.typeText.setText("类型：" + settleBill.getType());
        holder.totalText.setText("金额：" + settleBill.getTotal() + "");
        holder.createDateText.setText("创建日期：" + settleBill.getCreateTime());
    }

    @Override
    public int getItemCount() {
        return settleBillList.size();
    }
}

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

public class UnpaidBillAdapter extends RecyclerView.Adapter<UnpaidBillAdapter.ViewHolder> {
    private Context context;
    private List<UnpaidBill> unpaidBillList;
    private SelectPopupWindow popupView;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nameText;
        TextView returnedText;
        TextView createDateText;
        TextView surplusText; //剩余金额
        Button selectButton; //右上角选择按钮

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v;
            nameText = v.findViewById(R.id.unpaid_name_item);
            returnedText = v.findViewById(R.id.unpaid_returned_item);
            createDateText = v.findViewById(R.id.unpaid_create_date_item);
            surplusText = v.findViewById(R.id.unpaid_surplus_item);
            selectButton = v.findViewById(R.id.unpaid_select_button);
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
                                            unpaidBillList.get(getLayoutPosition()).delete();//从数据库删除
                                            unpaidBillList.remove(getLayoutPosition());//删除此账单
                                            notifyDataSetChanged();
                                            popupView.dismiss();
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
                            intent.putExtra("unpaidBill", unpaidBillList.get(getLayoutPosition()));
                            popupView.dismiss();
                            context.startActivity(intent);
                        }
                    });
                    //处理设为已结按钮点击事件
                    popupView.getSettleButton().setText("设为已结");
                    popupView.setSettleButtonListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int position=getLayoutPosition();
                            UnpaidBill unpaidBill = unpaidBillList.get(position);
                            SettleBill settleBill = new SettleBill();
                            settleBill.setUserName(MyAcount.userName);
                            settleBill.setName(unpaidBill.getName());
                            settleBill.setCreateTime(unpaidBill.getCreateDate());
                            settleBill.setTotal(unpaidBill.getTotal());
                            settleBill.setRemarks(unpaidBill.getRemarks());
                            settleBill.setTel(unpaidBill.getTel());
                            settleBill.setType(unpaidBill.getType());
                            settleBill.save();
                            unpaidBillList.get(position).delete();//从数据库删除
                            unpaidBillList.remove(position);//删除此账单
                            popupView.dismiss();
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }
    }

    public UnpaidBillAdapter(List<UnpaidBill> unpaidBills) {
        unpaidBillList = unpaidBills;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnpaidBill unpaidBill = unpaidBillList.get(position);
        holder.nameText.setText("对象：" + unpaidBill.getName());
        holder.returnedText.setText("已还金额：" + unpaidBill.getReturned() + "");
        holder.surplusText.setText("剩余金额：" + (unpaidBill.getTotal() - unpaidBill.getReturned()) + "");
        holder.createDateText.setText("创建日期：" + unpaidBill.getCreateDate());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (context == null) {
            context = viewGroup.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.unpaid_bill_item, viewGroup, false);
        View popView = View.inflate(context, R.layout.popup_menu, null);
        popupView = new SelectPopupWindow(popView);
        popupView.setTouchable(true);
        popupView.setFocusable(true);
        popupView.setOutsideTouchable(true);
        popupView.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return unpaidBillList.size();
    }

}

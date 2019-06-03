package com.example.bookingball;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static List<Activity> activityList=new ArrayList<>();
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void removeAll(){
        for(Activity activity:activityList){
            if(activity!=null){
                activity.finish();
            }
        }
        activityList.clear();
    }
}

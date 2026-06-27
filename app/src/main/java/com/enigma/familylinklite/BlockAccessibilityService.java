package com.enigma.familylinklite;

import android.accessibilityservice.AccessibilityService;
import android.app.*;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.os.*;
import android.view.accessibility.AccessibilityEvent;
import java.text.*;import java.util.*;

public class BlockAccessibilityService extends AccessibilityService{
    Handler h=new Handler(Looper.getMainLooper());
    Runnable timeoutCheck=new Runnable(){public void run(){enforceTimeout();h.postDelayed(this,10000);}};
    public void onServiceConnected(){super.onServiceConnected();h.post(timeoutCheck);notifySmall("Accessibility blocking active","FamilyLink can now push blocked apps away.");}
    public void onDestroy(){h.removeCallbacksAndMessages(null);super.onDestroy();}
    public void onInterrupt(){}
    public void onAccessibilityEvent(AccessibilityEvent e){
        if(e==null||e.getPackageName()==null)return;
        String pkg=e.getPackageName().toString();
        if(pkg.equals(getPackageName()))return;
        long now=System.currentTimeMillis();
        long lockUntil=getSharedPreferences("rules",0).getLong("lock_until",0);
        if(lockUntil>now){performGlobalAction(GLOBAL_ACTION_HOME);notifySmall("Tablet timeout active","Locked until "+time(lockUntil));return;}
        long until=getSharedPreferences("rules",0).getLong("blocked_until_"+pkg,0);
        if(until>now){performGlobalAction(GLOBAL_ACTION_HOME);notifySmall("App blocked",pkg+" is unavailable until "+time(until));}
    }
    void enforceTimeout(){long until=getSharedPreferences("rules",0).getLong("lock_until",0);if(until>System.currentTimeMillis()){try{DevicePolicyManager dpm=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);ComponentName admin=new ComponentName(this,AdminReceiver.class);if(dpm!=null&&dpm.isAdminActive(admin))dpm.lockNow();}catch(Exception ignored){}}}
    String time(long t){return new SimpleDateFormat("HH:mm",Locale.US).format(new Date(t));}
    void notifySmall(String title,String text){try{NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);String ch="child_server";if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(ch,"FamilyLink Child Server",NotificationManager.IMPORTANCE_LOW));nm.notify(3,new Notification.Builder(this,ch).setContentTitle(title).setContentText(text).setSmallIcon(android.R.drawable.ic_dialog_info).build());}catch(Exception ignored){}}
}

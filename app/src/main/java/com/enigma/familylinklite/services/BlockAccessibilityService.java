package com.enigma.familylinklite.services;

import android.accessibilityservice.AccessibilityService;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.*;
import android.view.accessibility.AccessibilityEvent;
import com.enigma.familylinklite.AttentionActivity;
import java.text.*;import java.util.*;

public class BlockAccessibilityService extends AccessibilityService{
    Handler h=new Handler(Looper.getMainLooper());
    Runnable timeoutCheck=new Runnable(){public void run(){enforceTimeout();h.postDelayed(this,900);}};
    public void onServiceConnected(){super.onServiceConnected();h.post(timeoutCheck);notifySmall("Accessibility blocking active","Parental-Link can now cover blocked apps.");}
    public void onDestroy(){h.removeCallbacksAndMessages(null);super.onDestroy();}
    public void onInterrupt(){}
    public void onAccessibilityEvent(AccessibilityEvent e){
        if(e==null)return;
        enforceTimeout();
        if(e.getPackageName()==null)return;
        String pkg=e.getPackageName().toString();
        if(pkg.equals(getPackageName()))return;
        long now=System.currentTimeMillis();
        long lockUntil=getSharedPreferences("rules",0).getLong("lock_until",0);
        if(lockUntil>now){showLimitationScreen(lockUntil);return;}
        long until=getSharedPreferences("rules",0).getLong("blocked_until_"+pkg,0);
        if(until>now){showBlockedAppScreen(pkg,until);}
    }
    void enforceTimeout(){long until=getSharedPreferences("rules",0).getLong("lock_until",0);if(until>System.currentTimeMillis())showLimitationScreen(until);}
    void showLimitationScreen(long until){
        String mode=getSharedPreferences("rules",0).getString("lock_mode","");
        String title="Device disabled";
        String text="Your parent disabled this device until "+time(until)+".";
        if("timeout".equals(mode)){title="Timeout active";text="This device is in timeout until "+time(until)+".";}
        else if("bedtime".equals(mode)){title="Bedtime mode";text="Bedtime mode is active until "+time(until)+".";}
        showBlockingScreen(until,title,text,"limitation");
    }
    void showBlockedAppScreen(String pkg,long until){
        String label=appLabel(pkg);
        String text=label+" is blocked until "+time(until)+".\n\nChoose another app or ask your parent.";
        showBlockingScreen(until,"App blocked",text,"blocked_"+pkg);
    }
    void showBlockingScreen(long until,String title,String text,String reason){
        long now=System.currentTimeMillis();
        long last=getSharedPreferences("rules",0).getLong("last_blocking_screen_launch_"+reason,0);
        if(now-last<700)return;
        getSharedPreferences("rules",0).edit().putLong("last_blocking_screen_launch_"+reason,now).apply();
        try{
            Intent i=new Intent(this, AttentionActivity.class);
            i.putExtra("title",title);i.putExtra("text",text);i.putExtra("blocking",true);i.putExtra("reason",reason);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            notifySmall(title,"Parental-Link screen active until "+time(until));
        }catch(Exception ignored){}
    }
    String appLabel(String pkg){try{PackageManager pm=getPackageManager();ApplicationInfo ai=pm.getApplicationInfo(pkg,0);return pm.getApplicationLabel(ai).toString();}catch(Exception e){return pkg;}}
    String time(long t){return new SimpleDateFormat("HH:mm",Locale.US).format(new Date(t));}
    void notifySmall(String title,String text){try{NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);String ch="child_server";if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(ch,"Parental-Link Child Server",NotificationManager.IMPORTANCE_LOW));nm.notify(3,new Notification.Builder(this,ch).setContentTitle(title).setContentText(text).setSmallIcon(android.R.drawable.ic_dialog_info).build());}catch(Exception ignored){}}
}

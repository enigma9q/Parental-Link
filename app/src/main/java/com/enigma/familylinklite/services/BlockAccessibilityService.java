package com.enigma.familylinklite.services;

import android.accessibilityservice.AccessibilityService;
import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.*;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import com.enigma.familylinklite.AttentionActivity;
import com.enigma.familylinklite.R;
import java.text.*;import java.util.*;

public class BlockAccessibilityService extends AccessibilityService{
    Handler h=new Handler(Looper.getMainLooper());
    long lastSplitCollapseAt=0;
    Runnable timeoutCheck=new Runnable(){public void run(){enforceTimeout();enforceVisibleBlockedApps();h.postDelayed(this,700);}};
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
        if(lockUntil>now){rememberReturnPackage(pkg);showLimitationScreen(lockUntil);return;}
        long until=getSharedPreferences("rules",0).getLong("blocked_until_"+pkg,0);
        if(until>now){showBlockedAppScreen(pkg,until);}
        enforceVisibleBlockedApps();
    }
    void enforceTimeout(){long until=getSharedPreferences("rules",0).getLong("lock_until",0);if(until>System.currentTimeMillis()){collapseSplitScreenIfNeeded();showLimitationScreen(until);}}
    void enforceVisibleBlockedApps(){
        long now=System.currentTimeMillis();
        if(Build.VERSION.SDK_INT<21)return;
        try{
            List<AccessibilityWindowInfo> windows=getWindows();
            if(windows==null)return;
            if(getSharedPreferences("rules",0).getLong("lock_until",0)>now){
                collapseSplitScreenIfNeeded(windows);
                return;
            }
            for(AccessibilityWindowInfo w:windows){
                if(w==null)continue;
                AccessibilityNodeInfo root=w.getRoot();
                String pkg=packageFromNode(root);
                if(pkg.length()>0&&!pkg.equals(getPackageName())){
                    long until=getSharedPreferences("rules",0).getLong("blocked_until_"+pkg,0);
                    if(until>now){showBlockedAppScreen(pkg,until);return;}
                }
            }
        }catch(Exception ignored){}
    }
    void collapseSplitScreenIfNeeded(){try{if(Build.VERSION.SDK_INT>=21)collapseSplitScreenIfNeeded(getWindows());}catch(Exception ignored){}}
    void collapseSplitScreenIfNeeded(List<AccessibilityWindowInfo> windows){
        try{
            if(windows==null||windows.size()<2)return;
            long now=System.currentTimeMillis();
            if(now-lastSplitCollapseAt<1800)return;
            lastSplitCollapseAt=now;
            performGlobalAction(GLOBAL_ACTION_HOME);
            notifySmall("Split screen blocked","Parental-Link returned to the lock screen.");
        }catch(Exception ignored){}
    }
    String packageFromNode(AccessibilityNodeInfo node){
        try{
            if(node==null)return "";
            CharSequence pkg=node.getPackageName();
            return pkg==null?"":pkg.toString();
        }catch(Exception e){return "";}
    }
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
    void rememberReturnPackage(String pkg){try{if(pkg==null||pkg.length()==0||pkg.equals(getPackageName())||launcherPackages().contains(pkg))return;getSharedPreferences("rules",0).edit().putString("return_to_package",pkg).apply();}catch(Exception ignored){}}
    Set<String> launcherPackages(){Set<String> out=new HashSet<>();try{Intent home=new Intent(Intent.ACTION_MAIN);home.addCategory(Intent.CATEGORY_HOME);List<ResolveInfo> infos=getPackageManager().queryIntentActivities(home,0);if(infos!=null){for(ResolveInfo ri:infos){if(ri!=null&&ri.activityInfo!=null&&ri.activityInfo.packageName!=null)out.add(ri.activityInfo.packageName);}}}catch(Exception ignored){}return out;}
    String appLabel(String pkg){try{PackageManager pm=getPackageManager();ApplicationInfo ai=pm.getApplicationInfo(pkg,0);return pm.getApplicationLabel(ai).toString();}catch(Exception e){return pkg;}}
    String time(long t){return new SimpleDateFormat("HH:mm",Locale.US).format(new Date(t));}
    void notifySmall(String title,String text){try{NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);String ch="child_server";if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel(ch,"Parental-Link Child Server",NotificationManager.IMPORTANCE_LOW));nm.notify(3,new Notification.Builder(this,ch).setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.ic_notification_link).build());}catch(Exception ignored){}}
}

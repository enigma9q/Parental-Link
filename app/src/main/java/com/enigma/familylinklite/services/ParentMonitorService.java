package com.enigma.familylinklite.services;

import android.app.*;import android.content.*;import android.os.*;import com.enigma.familylinklite.MainActivity;import com.enigma.familylinklite.R;import com.enigma.familylinklite.ui.StatusFormatter;

public class ParentMonitorService extends Service{
    public static final int NOTIFICATION_ID=3;
    static final String CHANNEL="parent_monitor";
    public IBinder onBind(Intent i){return null;}
    public void onCreate(){super.onCreate();createChannel();}
    public int onStartCommand(Intent intent,int flags,int startId){
        android.content.SharedPreferences p=getSharedPreferences("p",0);
        String title="Parental-Link";
        String text=notificationSummary(p);
        boolean permanent=p.getBoolean("parentPermanentNotification",false);
        try{
            Notification n=notification(title,text,permanent);
            if(permanent)startForeground(NOTIFICATION_ID,n);
            else getSystemService(NotificationManager.class).notify(NOTIFICATION_ID,n);
        }catch(Exception ignored){}
        if(permanent)return START_STICKY;
        final int sid=startId;
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            try{getSystemService(NotificationManager.class).cancel(NOTIFICATION_ID);}catch(Exception ignored){}
            try{stopSelf(sid);}catch(Exception ignored){}
        },3000L);
        return START_NOT_STICKY;
    }
    public void onDestroy(){try{getSystemService(NotificationManager.class).cancel(NOTIFICATION_ID);}catch(Exception ignored){}super.onDestroy();}

    String notificationSummary(android.content.SharedPreferences p){
        String multi=p.getString("devicesNotificationSummary","").trim();
        if(multi.length()>0)return multi;
        String childIp=p.getString("childIp","").trim();
        String parentKey=p.getString("parentKey","").trim();
        boolean removed=p.getBoolean("childDeviceRemoved",false);
        boolean mismatch=p.getBoolean("pairingMismatch",false);
        boolean removalPending=p.getBoolean("childRemovalRequested",false)||p.getBoolean("childRemovalPending",false);
        if(mismatch)return "Possible pairing mismatch\nTap to troubleshoot or remove stale device";
        if(p.getBoolean("childUnlockRequestPending",false)){return "Unlock request - "+p.getString("childUnlockRequestReason","limitation");}
        if(childIp.length()==0||parentKey.length()==0||removed){
            if(removalPending)return "Child removal pending";
            String latest=p.getString("latestLog","");
            if(latest!=null&&latest.toLowerCase(java.util.Locale.US).contains("removed"))return "No paired child device";
            return "No paired child device";
        }
        String nick=p.getString("childNickname","Child tablet");
        long last=p.getLong("lastStatusMs",0);
        long mins=p.getLong("todayMinutes",-1);
        String app=cleanAppName(p.getString("lastCurrentApp",""));
        String status="unknown"; if(mismatch){status="repair";}else if(last>0){long age=System.currentTimeMillis()-last;status=age>10L*60L*1000L?"stale":"online";}
        String today=mins>=0?String.format(java.util.Locale.US,"%02d:%02d",mins/60,mins%60):"--:--";
        String strip=StatusFormatter.dashboardStrip(p,p.getInt("parentVolume",50));
        if(last==0){String latest=p.getString("latestLog","");return latest.length()>0?latest:"No child status yet";}
        long age=System.currentTimeMillis()-last;
        if(age>10L*60L*1000L){
            return "1 device - "+nick+" "+status+" - Last sync "+ageText(age)+" ago\n"+strip;
        }
        return "1 device - "+nick+" "+status+" - "+today+" today"+(app.length()>0?" - "+app:"")+"\n"+strip;
    }

    String ageText(long ms){long m=ms/60000L;if(m<1)return "just now";if(m<60)return m+"m";long h=m/60;return h+"h "+(m%60)+"m";}

    Notification notification(String title,String text,boolean ongoing){
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this,CHANNEL).setContentTitle(title).setContentText(firstLine(text)).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(R.drawable.ic_notification_link).setContentIntent(pi).setOngoing(ongoing).build();
    }
    String firstLine(String text){if(text==null)return "";int n=text.indexOf('\n');return n>=0?text.substring(0,n):text;}
    String cleanAppName(String raw){
        if(raw==null)return "";
        String x=raw.trim();
        if(x.length()==0)return "";
        if(x.equalsIgnoreCase("Unavailable"))return "Unavailable";
        int par=x.indexOf(" (");
        if(par>0)x=x.substring(0,par).trim();
        if(x.startsWith("com.")||x.contains(".")){
            try{android.content.pm.PackageManager pm=getPackageManager();android.content.pm.ApplicationInfo ai=pm.getApplicationInfo(x,0);return pm.getApplicationLabel(ai).toString();}catch(Exception ignored){}
        }
        return x;
    }

    void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL,"Parental-Link Parent Monitor",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(ch);}}
}

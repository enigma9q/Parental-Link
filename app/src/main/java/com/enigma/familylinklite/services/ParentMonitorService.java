package com.enigma.familylinklite.services;

import android.app.*;import android.content.*;import android.os.*;import com.enigma.familylinklite.MainActivity;import com.enigma.familylinklite.ui.StatusFormatter;

public class ParentMonitorService extends Service{
    static final String CHANNEL="parent_monitor";
    public IBinder onBind(Intent i){return null;}
    public void onCreate(){super.onCreate();createChannel();}
    public int onStartCommand(Intent intent,int flags,int startId){
        android.content.SharedPreferences p=getSharedPreferences("p",0);
        String title="Parental-Link";
        String text=notificationSummary(p);
        startForeground(3,notification(title,text));
        return START_STICKY;
    }

    String notificationSummary(android.content.SharedPreferences p){
        String multi=p.getString("devicesNotificationSummary","").trim();
        if(multi.length()>0)return multi;
        String childIp=p.getString("childIp","").trim();
        String parentKey=p.getString("parentKey","").trim();
        boolean removed=p.getBoolean("childDeviceRemoved",false);
        boolean mismatch=p.getBoolean("pairingMismatch",false);
        boolean removalPending=p.getBoolean("childRemovalRequested",false)||p.getBoolean("childRemovalPending",false);
        if(mismatch)return "🔴 Possible pairing mismatch\nTap to troubleshoot or remove stale device";
        if(p.getBoolean("childUnlockRequestPending",false)){return "Unlock request • "+p.getString("childUnlockRequestReason","limitation");}
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
        String status="⚫";
        if(mismatch){status="🔴";}else if(last>0){long age=System.currentTimeMillis()-last;status=age>10L*60L*1000L?"🟡":"🟢";}
        String today=mins>=0?String.format(java.util.Locale.US,"%02d:%02d",mins/60,mins%60):"--:--";
        String strip=StatusFormatter.dashboardStrip(p,p.getInt("parentVolume",50));
        if(last==0){String latest=p.getString("latestLog","");return latest.length()>0?latest:"No child status yet";}
        long age=System.currentTimeMillis()-last;
        if(age>10L*60L*1000L){
            return "1 device • "+nick+" "+status+" • Last sync "+ageText(age)+" ago
"+strip;
        }
        return "1 device • "+nick+" "+status+" • "+today+" today"+(app.length()>0?" • "+app:"")+"
"+strip;
    }

    String ageText(long ms){long m=ms/60000L;if(m<1)return "just now";if(m<60)return m+"m";long h=m/60;return h+"h "+(m%60)+"m";}

    Notification notification(String title,String text){
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this,CHANNEL).setContentTitle(title).setContentText(firstLine(text)).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(android.R.drawable.ic_dialog_info).setContentIntent(pi).setOngoing(true).build();
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

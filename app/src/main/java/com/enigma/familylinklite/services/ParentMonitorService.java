package com.enigma.familylinklite.services;

import android.app.*;import android.content.*;import android.graphics.BitmapFactory;import android.os.*;import com.enigma.familylinklite.MainActivity;import com.enigma.familylinklite.R;import com.enigma.familylinklite.ui.StatusFormatter;

public class ParentMonitorService extends Service{
    public static final int NOTIFICATION_ID=3;
    static final String CHANNEL="parent_monitor";
    public IBinder onBind(Intent i){return null;}
    public void onCreate(){super.onCreate();createChannel();}
    public int onStartCommand(Intent intent,int flags,int startId){
        android.content.SharedPreferences p=getSharedPreferences("p",0);
        String title=notificationTitle(p);
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

    String notificationTitle(android.content.SharedPreferences p){
        String childIp=p.getString("childIp","").trim();
        String parentKey=p.getString("parentKey","").trim();
        boolean removed=p.getBoolean("childDeviceRemoved",false);
        if(childIp.length()==0||parentKey.length()==0||removed)return "P-L Devices: none paired";
        long last=p.getLong("lastStatusMs",0);
        long age=last>0?System.currentTimeMillis()-last:Long.MAX_VALUE;
        boolean connected=last>0&&age<=10L*60L*1000L;
        return connected?"P-L Devices: 1 connected":"P-L Devices: 1 unavailable";
    }

    String notificationSummary(android.content.SharedPreferences p){
        String multi=p.getString("devicesNotificationSummary","").trim();
        String childIp=p.getString("childIp","").trim();
        String parentKey=p.getString("parentKey","").trim();
        boolean removed=p.getBoolean("childDeviceRemoved",false);
        boolean mismatch=p.getBoolean("pairingMismatch",false);
        boolean removalPending=p.getBoolean("childRemovalRequested",false)||p.getBoolean("childRemovalPending",false);
        if(mismatch)return "Possible pairing mismatch\nTap to troubleshoot or remove stale device";
        if(p.getBoolean("childUnlockRequestPending",false)){return "Unlock request - "+p.getString("childUnlockRequestReason","limitation");}
        if(childIp.length()==0||parentKey.length()==0||removed){
            if(removalPending)return "Child removal pending";
            return "No paired child device";
        }
        String nick=p.getString("childNickname","Child tablet");
        String icon=normalDeviceIcon(p.getString("childIcon","\uD83D\uDCFA"));
        long last=p.getLong("lastStatusMs",0);
        long age=last>0?System.currentTimeMillis()-last:Long.MAX_VALUE;
        boolean connected=last>0&&age<=10L*60L*1000L;
        if(multi.length()>0&&connected)return multi;
        if(!connected){
            return icon+" "+nick+" - Not connected";
        }
        return icon+" "+nick+" - Online - "+powerStatus(p)+" - "+dndStatus(p);
    }

    String powerStatus(android.content.SharedPreferences p){int battery=p.getInt("remoteBattery",-1);boolean charging=p.getBoolean("remoteCharging",false);return (charging?"\uD83D\uDD0C":"\uD83D\uDD0B")+(battery>=0?battery+"%":"--");}
    String dndStatus(android.content.SharedPreferences p){return "\uD83D\uDD15"+StatusFormatter.conciseDndStatus(p);}
    String normalDeviceIcon(String icon){String v=icon==null?"":icon.trim();if(v.length()==0)return "\uD83D\uDCFA";if(v.codePointCount(0,v.length())<=2&&!v.matches("[A-Za-z ]+"))return v;String low=v.toLowerCase(java.util.Locale.US);if(low.contains("phone"))return "\uD83D\uDCF2";if(low.contains("tablet"))return "\uD83D\uDCFA";if(low.contains("baby"))return "\uD83D\uDC76";if(low.contains("girl"))return "\uD83D\uDC67";if(low.contains("boy"))return "\uD83D\uDC66";if(low.contains("teen")||low.contains("kid")||low.contains("child"))return "\uD83E\uDDD2";if(low.contains("rainbow"))return "\uD83C\uDF08";if(low.contains("star"))return "\u2B50";if(low.contains("dog"))return "\uD83D\uDC36";if(low.contains("cat"))return "\uD83D\uDC31";if(low.contains("unicorn"))return "\uD83E\uDD84";return "\uD83D\uDCFA";}

    Notification notification(String title,String text,boolean ongoing){
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE);
        Notification n=new Notification.Builder(this,CHANNEL).setContentTitle(title).setContentText(firstLine(text)).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(R.drawable.ic_notification_link).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).setContentIntent(pi).setOngoing(ongoing).setOnlyAlertOnce(true).build();
        if(ongoing)n.flags|=Notification.FLAG_ONGOING_EVENT|Notification.FLAG_NO_CLEAR;
        return n;
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

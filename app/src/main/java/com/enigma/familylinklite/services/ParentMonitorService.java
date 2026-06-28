package com.enigma.familylinklite.services;

import android.app.*;import android.content.*;import android.os.*;import com.enigma.familylinklite.MainActivity;

public class ParentMonitorService extends Service{
    static final String CHANNEL="parent_monitor";
    public IBinder onBind(Intent i){return null;}
    public void onCreate(){super.onCreate();createChannel();}
    public int onStartCommand(Intent intent,int flags,int startId){
        android.content.SharedPreferences p=getSharedPreferences("p",0);
        String nick=p.getString("childNickname","Child tablet");
        long last=p.getLong("lastStatusMs",0);
        long mins=p.getLong("todayMinutes",-1);
        String app=p.getString("lastCurrentApp","");
        String status="⚫";
        if(last>0){long age=System.currentTimeMillis()-last;status=age>10L*60L*1000L?"🟡":"🟢";}
        String title="Parental-Link • "+nick;
        String today=mins>=0?String.format(java.util.Locale.US,"%02d:%02d today",mins/60,mins%60):"today --:--";
        String text=status+" "+today+(app.length()>0?" • "+app:" • no app yet");
        if(last==0)text=getSharedPreferences("p",0).getString("latestLog","Dashboard active");
        startForeground(3,notification(title,text));
        return START_STICKY;
    }
    Notification notification(String title,String text){
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this,CHANNEL).setContentTitle(title).setContentText(text).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(android.R.drawable.ic_dialog_info).setContentIntent(pi).setOngoing(true).build();
    }
    void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL,"Parental-Link Parent Monitor",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(ch);}}
}

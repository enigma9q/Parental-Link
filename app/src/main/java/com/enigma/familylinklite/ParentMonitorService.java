package com.enigma.familylinklite;

import android.app.*;import android.content.*;import android.os.*;

public class ParentMonitorService extends Service{
    static final String CHANNEL="parent_monitor";
    public IBinder onBind(Intent i){return null;}
    public void onCreate(){super.onCreate();createChannel();}
    public int onStartCommand(Intent intent,int flags,int startId){
        String title="FamilyLink Lite • Parent Control";
        String text=getSharedPreferences("p",0).getString("latestLog","Dashboard active");
        startForeground(3,notification(title,text));
        return START_STICKY;
    }
    Notification notification(String title,String text){
        Intent i=new Intent(this,MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this,CHANNEL).setContentTitle(title).setContentText(text).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(android.R.drawable.ic_dialog_info).setContentIntent(pi).setOngoing(true).build();
    }
    void createChannel(){if(Build.VERSION.SDK_INT>=26){NotificationChannel ch=new NotificationChannel(CHANNEL,"FamilyLink Parent Monitor",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(ch);}}
}

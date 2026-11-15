package com.misalud.app;

import android.app.*;
import android.content.*;
import android.os.Build;
import java.text.*;
import java.util.*;

public class AlarmReceiver extends android.content.BroadcastReceiver {
    static final String CHANNEL="misalud_recordatorios";
    static PendingIntent pending(Context c,String action,long id,long when,int request){
        Intent i=new Intent(c,AlarmReceiver.class).setAction(action).putExtra("id",id).putExtra("when",when);
        return PendingIntent.getBroadcast(c,request,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void schedule(Context c,ContentValues m){
        if(m==null || Db.num(m,"active")!=1 || System.currentTimeMillis()>Db.num(m,"end_at")+86399999L)return;
        long id=Db.num(m,"_id"),start=Db.num(m,"start_at"),end=Db.num(m,"end_at");
        long step=Math.max(1,Db.num(m,"every_hours"))*3600000L;
        long now=System.currentTimeMillis(),next=start;
        if(next<=now)next=start+((now-start)/step+1)*step;
        if(next>end+86399999L)return;
        scheduleAt(c,"medicine",id,next,(int)(id*10+1));
    }
    static void scheduleVisit(Context c,ContentValues v){
        if(v==null)return;
        long id=Db.num(v,"_id"),when=Db.num(v,"visit_at")-Db.num(v,"remind_minutes")*60000L;
        if(when>System.currentTimeMillis())scheduleAt(c,"visit",id,when,(int)(id*10+2));
    }
    static void scheduleAt(Context c,String action,long id,long when,int code){
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am!=null)am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pending(c,action,id,when,code));
    }
    static void cancel(Context c,String action,long id,int code){
        AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(am!=null)am.cancel(pending(c,action,id,0,code));
    }
    static void channel(Context c){
        if(Build.VERSION.SDK_INT>=26){NotificationChannel nc=new NotificationChannel(CHANNEL,"Recordatorios de salud",NotificationManager.IMPORTANCE_HIGH);nc.setDescription("Medicamentos y consultas");
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(nc);}
    }
    @Override public void onReceive(Context c,Intent i){
        if(i==null)return;
        String action=i.getAction();long id=i.getLongExtra("id",-1),when=i.getLongExtra("when",System.currentTimeMillis());if(id<0)return;
        Db db=new Db(c);
        if("taken".equals(action)||"skipped".equals(action)){
            ContentValues d=new ContentValues();d.put("medicine_id",id);d.put("scheduled_at",when);d.put("recorded_at",System.currentTimeMillis());d.put("status","taken".equals(action)?"Tomado":"No tomado");db.add("doses",d);
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)(id+1000));return;
        }
        if("snooze".equals(action)){
            scheduleAt(c,"snoozed",id,when,(int)(id*10+3));
            ((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).cancel((int)(id+1000));return;
        }
        channel(c);
        String title,body;
        if("visit".equals(action)){
            ContentValues v=db.one("visits",id);if(v==null)return;
            title="Próxima consulta o visita";body=Db.str(v,"title")+" · "+new SimpleDateFormat("dd/MM HH:mm",Locale.getDefault()).format(new Date(Db.num(v,"visit_at")));
        }else{
            ContentValues m=db.one("medicines",id);if(m==null||Db.num(m,"active")!=1||System.currentTimeMillis()>Db.num(m,"end_at")+86399999L)return;
            ContentValues p=db.one("patients",Db.num(m,"patient_id"));
            title="Medicamento de "+Db.str(p,"short_name");body=Db.str(m,"name")+" · "+Db.str(m,"instructions");
            if("medicine".equals(action))schedule(c,m);
        }
        Intent open=new Intent(c,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi=PendingIntent.getActivity(c,(int)(id+40000),open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder n=new Notification.Builder(c,CHANNEL).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(body).setStyle(new Notification.BigTextStyle().bigText(body)).setAutoCancel(true).setContentIntent(pi).setPriority(Notification.PRIORITY_HIGH);
        if(!"visit".equals(action)){
            n.addAction(new Notification.Action.Builder(null,"Lo tomó",pending(c,"taken",id,when,(int)(id*10+4))).build());
            n.addAction(new Notification.Action.Builder(null,"No lo tomó",pending(c,"skipped",id,when,(int)(id*10+5))).build());
            n.addAction(new Notification.Action.Builder(null,"+10 min",pending(c,"snooze",id,System.currentTimeMillis()+600000L,(int)(id*10+6))).build());
        }
        try{((NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE)).notify((int)(id+("visit".equals(action)?20000:1000)),n.build());}catch(SecurityException ignored){}
    }
}

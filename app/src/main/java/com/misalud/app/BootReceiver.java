package com.misalud.app;
import android.content.*;
public class BootReceiver extends android.content.BroadcastReceiver {
    @Override public void onReceive(Context c,Intent i){
        Db db=new Db(c);
        for(ContentValues m:db.rows("medicines","active=1",null,null))AlarmReceiver.schedule(c,m);
        for(ContentValues v:db.rows("visits",null,null,null))AlarmReceiver.scheduleVisit(c,v);
    }
}

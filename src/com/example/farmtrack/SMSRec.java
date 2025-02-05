package com.example.farmtrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

public class SMSRec extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) 
	{
		Bundle b=arg1.getExtras();
		SmsMessage []msg=null;
		String str="",alert_msg="Alert! Some intrusional activity has taken place in your Farm.";
		String msgbody="";
		boolean flg=false;
		SharedPreferences sp = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);		
		String num = sp.getString("ph_no", "none");
		if(b!=null)
		{
			Object[] pdus=(Object[])b.get("pdus");
			msg=new SmsMessage[pdus.length];
			for(int i=0;i<msg.length;i++)
			{
				msg[i]=SmsMessage.createFromPdu((byte[])pdus[i]);
				str+="Messgae From "+msg[i].getOriginatingAddress();
				msgbody = ""+msg[i].getMessageBody().toString();
				if(msg[i].getOriginatingAddress().equals("+91"+num)) 
				{
					if(msgbody.charAt(0)=='A')
						flg=true;
					if(msgbody.equalsIgnoreCase("Your system is ON"))				
						Toast.makeText(context, "System ON", Toast.LENGTH_SHORT).show();
					if(msgbody.equalsIgnoreCase("Your system is OFF"))				
						Toast.makeText(context, "System OFF", Toast.LENGTH_SHORT).show();
				
					abortBroadcast();
				}
				
				str+=" :";
				str+=msg[i].getMessageBody().toString();
				str+="\n";
			}

			if(flg){//here we launch mainactvy and then frm home we launch alert

				String dir = "none";
		        //set direction
 		      SharedPreferences.Editor editor = sp.edit();                                                         			
		
				if(msgbody.contains("North"))
					dir = "North";
				if(msgbody.contains("South"))
					dir = "South";
				if(msgbody.contains("East"))
					dir = "East";
				if(msgbody.contains("West"))
					dir = "West";
					
			      editor.putString("direction",dir);
			      editor.commit();
				
				
				    Intent launchIntent = new Intent(context, MainActivity.class);
				    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
		//		    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY );
					launchIntent.putExtra("alert", "start_alert");
				    context.startActivity(launchIntent);
					
					Intent broadcastIntent=new Intent();
					broadcastIntent.setAction("SMS_REC_ACTION");
					broadcastIntent.putExtra("sms", str);
					context.sendBroadcast(broadcastIntent);
			}
		}
	}

}

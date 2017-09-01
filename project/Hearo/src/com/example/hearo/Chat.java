package com.example.hearo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Chat extends Activity {

	// HOME IP - 93.172.103.99
	BufferedReader in;
    PrintWriter out;
    TextView msg;
    Button send;
    EditText input;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		msg = (TextView) findViewById(R.id.msg);
		send = (Button) findViewById(R.id.sendButton);
		input = (EditText) findViewById(R.id.toSendText);
		new ChatRecieving().execute();
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				out.println(input.getText().toString().trim());
				input.setText("");
			}
			
		});
		
	}

	private String getServerAddress()
	{
		return "93.172.103.99";
	}
	
	private String getName()
	{
		return new Random().nextInt(1000) + "";
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class ChatRecieving extends AsyncTask<Void,String,Void>
	{
        Socket socket = null;
        
        ChatRecieving()
        {	        
        	String serverAddress = getServerAddress();
        	try {
				socket = new Socket(serverAddress, 5555);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		@Override
		protected Void doInBackground(Void... params) {

	        // Make connection and initialize streams
			
	        try {
				in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        try {
				out = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // Process all messages from server, according to the protocol.
	        while (true) {
	            String line = null;
				try {
					line = in.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            if (line.startsWith("SUBMITNAME")) {
	                out.println(getName());
	            } else if (line.startsWith("NAMEACCEPTED")) {
	            	publishProgress("0","NAMEACCEPTED");
	            } else if (line.startsWith("MESSAGE")) {
	            	publishProgress("1",line);
	            }
	        }
		}
		protected void onProgressUpdate(String... x)
		{
			if(x[0].equals("0"))
				   msg.setText("Your in.");
			if(x[0].equals("1"))
				   msg.setText(x[1]);
			}
	}
}

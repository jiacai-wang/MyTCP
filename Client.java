package TCP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import TCP.MySegment;

public class Client {
	public static int port=8888;
	static long startTime=0;
	static long finishTime=0;
	static int seq=0;
	static int ackno=0;
	static int window=5;
	static byte[] content=new byte[50];
	static int acked=-1;
	static boolean ack=false;
	static boolean pauseflag=true;
	static int error=0;
	
	static Frame frame =new Frame("Client");
	static Label label1=new Label("received:");
    static Label label2=new Label("Current window size: "+window);
    static Choice windowSize=new Choice();
    static Button start=new Button("Set");
	
	//使三分之一的包出错
	public static void send(String content,OutputStreamWriter osw) throws IOException, InterruptedException
	{
		Random random=new Random();
		if(random.nextInt(100)>30)
		{
			osw.write(content+"\n");
			osw.flush();
			Thread.sleep(200);
		}
		else
		{
			content="00"+content.substring(2);
			osw.write(content+"\n");
			osw.flush();
			Thread.sleep(200);
		}
	}
	
	public static void main(String []args) throws IOException, InterruptedException
	{

		
		
		frame.setLayout(new GridLayout(4,3));
		frame.setTitle("Client");
		frame.setSize(500, 150);
		frame.setLocation(800, 300);
		frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        
        frame.add(label1);
        frame.add(label2);
        
        windowSize.add("1");
        windowSize.add("3");
        windowSize.add("5");
        windowSize.add("7");
        windowSize.add("9");
        windowSize.select(2);
        frame.add(windowSize);
        
        start.setLocation(2, 1);
        frame.add(start);
        start.addActionListener(new ButtonHandler());
        
        
		Socket socket=null;
		for(int i=0;i<50;i++)
		{
			content[i]=-1;
		}
		try
		{
			socket=new Socket("127.0.0.1",port);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		OutputStreamWriter osw=new OutputStreamWriter(socket.getOutputStream());
		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		StringBuilder receivedcache=new StringBuilder(" ");
		for(int i=0;i<50;i++) receivedcache.append(' ');
		startTime=System.currentTimeMillis();
		while(true)
		{
			String got=br.readLine();
			String[] segkey=got.split("--");
			MySegment seg=new MySegment(Integer.parseInt(segkey[0]),Integer.parseInt(segkey[1]),Integer.parseInt(segkey[2]),Integer.parseInt(segkey[3]),Boolean.parseBoolean(segkey[4]),Integer.parseInt(segkey[5]),Byte.parseByte(segkey[6]));
			if(seg.Check==Integer.parseInt(segkey[7]))
			{
				content[seg.SequenceNumber]=seg.Content;
				acked=seg.SequenceNumber==acked+1?seg.SequenceNumber:acked;
				while(acked<48&&content[acked+1]!=-1) acked++;
				System.out.println("received segment "+seg.SequenceNumber);
				receivedcache.setCharAt(seg.SequenceNumber,  (char)('0'+seg.Content));
				label1.setText("received:"+receivedcache.toString());
			}
			else
			{
				error++;
				label1.setText("received:"+receivedcache.toString());
			}
			MySegment segACK=new MySegment(socket.getLocalPort(),socket.getPort(),seg.SequenceNumber,acked,true,window,(byte)0);
			send(segACK.toSend,osw);
			System.out.println("sent ACK of "+segACK.AcknowledgementNumber);
			if(acked==49)
			{
				send(segACK.toSend,osw);
				send(segACK.toSend,osw);
				send(segACK.toSend,osw);
				send(segACK.toSend,osw);
				finishTime=System.currentTimeMillis();
				break;
			}
		}
		long seconds=(finishTime-startTime)/1000;
		label2.setText("Transfer speed: "+50/seconds+"Byte/s. Total error number: "+error);
		socket.close();
	}
	
}
class ButtonHandler implements ActionListener {
     public void actionPerformed(ActionEvent e) {
    	 Client.window=Integer.parseInt(Client.windowSize.getSelectedItem());
    	 Client.pauseflag=false;
    	 Client.label2.setText("Current window size: "+Client.window);
     }
}


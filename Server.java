package TCP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.awt.*;

import TCP.MySegment;

public class Server {
	public static int port=8888;
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
		int ackno=0;
		int window=1;
		StringBuilder content =new StringBuilder("0");
		boolean ack=false;
		int acked=-1;
		for(int i=1;i<50;i++) content.append((char)('0'+i%10));
		
		Frame frame =new Frame("Server");
		frame.setLayout(new GridLayout(2,1));
		frame.setTitle("Server");
		frame.setSize(500, 100);
		frame.setLocation(800, 200);
		frame.setAlwaysOnTop(true);
        frame.setVisible(true);
        Label toSend=new Label("toSend:  "+content.toString());
        frame.add(toSend);
        
		ServerSocket server=new ServerSocket(port);
		Socket socket=server.accept();
		OutputStreamWriter osw=new OutputStreamWriter(socket.getOutputStream());
		BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		while(true)
		{
			String[] segkey=null;
			for(int i=0;i<50;i++)
			{
				if(i>acked&&i<=window+acked)
				{
					MySegment seg=new MySegment(socket.getLocalPort(),socket.getPort(),i,ackno,ack,window,(byte)(i%10));
					send(seg.toSend,osw);
					System.out.println("sent segment "+seg.SequenceNumber);
					segkey=br.readLine().split("--");
					MySegment segACK=new MySegment(Integer.parseInt(segkey[0]),Integer.parseInt(segkey[1]),Integer.parseInt(segkey[2]),Integer.parseInt(segkey[3]),Boolean.parseBoolean(segkey[4]),Integer.parseInt(segkey[5]),Byte.parseByte(segkey[6]));
					if(segACK.Check==Integer.parseInt(segkey[7]))
					{
						if(segACK.ACK)
						{
							acked=segACK.AcknowledgementNumber>acked?segACK.AcknowledgementNumber:acked;
							window=segACK.Window;
							System.out.println("received ACK of "+segACK.AcknowledgementNumber);
							
						}
					}
				}
			}
			toSend.setText("toSend:  "+content.substring(acked+1).toString());
			if(acked==49)
			{
				toSend.setText("All data sent to Client successfully!");
				Thread.sleep(3000);
				break;
			}
		}
		socket.close();
		server.close();
	}
}

package TCP;

public class MySegment {
	public short SourcePort;
	public short DestinationPort;
	public int SequenceNumber;
	public int AcknowledgementNumber;
	public boolean ACK;
	public short Window;
	public byte Content;
	public int Check;
	public String toSend;
	
	public MySegment()
	{
		SourcePort=0;
		DestinationPort=0;
		SequenceNumber=0;
		AcknowledgementNumber=0;
		ACK=false;
		Window=0;
		Content=0;
		Check=SourcePort^DestinationPort^SequenceNumber^AcknowledgementNumber^Window^Content;
		toSend=Integer.toString(SourcePort)+"--"+Integer.toString(DestinationPort)+"--"+Integer.toString(SequenceNumber)+"--"+Integer.toString(AcknowledgementNumber)+"--"+Boolean.toString(ACK)+"--"+Integer.toString(Window)+"--"+Byte.toString(Content)+"--"+Integer.toString(Check);
	}

	public MySegment(int sourcePort, int destinationPort, int sequenceNumber, int acknowledgementNumber, boolean ack, int window, byte content) {
		SourcePort = (short)sourcePort;
		DestinationPort = (short)destinationPort;
		SequenceNumber = sequenceNumber;
		AcknowledgementNumber = acknowledgementNumber;
		ACK = ack;
		Window = (short)window;
		Content = content;
		Check=SourcePort^DestinationPort^SequenceNumber^AcknowledgementNumber^Window^Content;
		toSend=Integer.toString(sourcePort)+"--"+Integer.toString(destinationPort)+"--"+Integer.toString(sequenceNumber)+"--"+Integer.toString(acknowledgementNumber)+"--"+Boolean.toString(ack)+"--"+Integer.toString(window)+"--"+Byte.toString(content)+"--"+Integer.toString(Check);
	}
	
}

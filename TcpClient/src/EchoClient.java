import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;



public class EchoClient implements DoByProtocal{
	@Override
	public void doByprotocal(Socket socket, Stopable stopable) {
		if (socket==null || stopable==null) {
			return;
		}
		try {
			BufferedReader reader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			BufferedReader brConsole = new BufferedReader(new InputStreamReader(System.in));
			String line, msg;
			//get welcome message
			line=reader.readLine();
			msg=String.format("Received message:%s from:%s, port:%d",line, socket.getInetAddress(),socket.getPort());
			System.out.println(msg);
			while (!stopable.shouldStop())  {
				try {
					if ((line=brConsole.readLine())!=null) {
						writer.write(line+"\n");
						writer.flush();
						if (line.equals("quit")) {
							break;
						}
						line=reader.readLine();
						msg=String.format("Received echo message:%s from:%s, port:%d",line, socket.getRemoteSocketAddress(),socket.getPort());
						System.out.println(msg+"\n");
					}
				}
				catch(SocketTimeoutException ioe) {
					//do nothing
				}
			}
			writer.flush();
			reader.close();
			writer.close();
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	public static void main(String[] args) {
		try {
			if (args.length<2) {
				return;
			}
			EchoClient echo=new EchoClient();
			TcpClient client=new TcpClient( args[0], Integer.parseInt(args[1]),echo);
			client.start();
			/*
			String cmd=null;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while((cmd=br.readLine())!=null) {
				if (cmd.equals("bye")) {
					break;
				}
			}
			*/
			client.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

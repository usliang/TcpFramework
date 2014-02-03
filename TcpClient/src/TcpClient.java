import java.net.*;

/**
 * @author lliang
 *
 */
public class TcpClient {
	//member variables
	private String serverIp;
	private int serverPort;
	private DoByProtocal task;
	private boolean isRunning;
	private Socket socket;
	private Stopper stopper;
	private static final int TIME_OUT=10*1000;
	
	
	//constructor
	/**
	 * @param serverIp
	 * @param serverPort
	 * @param task
	 * @throws IllegalArgumentException
	 */
	public TcpClient(String serverIp, int serverPort, DoByProtocal task) throws IllegalArgumentException {
		if (serverIp==null || task==null) {
			throw new IllegalArgumentException("Either serverIp argument or task argument is null");
		}
		this.serverIp=serverIp;
		this.serverPort=serverPort;
		this.task=task;
		isRunning=false;
		stopper=new Stopper(false);
	}
	
	
	public synchronized void start() {
		if (!isRunning) {
			try {
				socket=new Socket(serverIp,serverPort);
				socket.setSoTimeout(TIME_OUT);
				isRunning=true;
				stopper.signaleStart();
				task.doByprotocal(socket, stopper);;
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	/**
	 * 
	 */
	public void stop() {
		stopper.stop();
		if (socket!=null) {
			try{
				isRunning=false;
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
			
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}

class Stopper implements Stopable{
	private volatile boolean stopSignal;
	public Stopper(boolean stopSignal) {
		this.stopSignal=stopSignal;
	}
	@Override
	public boolean shouldStop() {
		return stopSignal;
	}
	public void stop() {
		stopSignal=true;
	}
	public void signaleStart() {
		stopSignal=false;
	}
}
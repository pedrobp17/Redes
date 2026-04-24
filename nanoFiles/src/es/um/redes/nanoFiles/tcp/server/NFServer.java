package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.FileDatabase;



public class NFServer implements Runnable {

	public static final int PORT = 10000;


	private Thread serverThread; //no se si es del todo correcto, preguntar al profesor
	private ServerSocket serverSocket = null;
	private volatile boolean stopping=false; //para controlar si cerramos el socket nosotros o se cierra por accidente

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		
		InetSocketAddress address=new InetSocketAddress(PORT);
		
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */

		serverSocket=new ServerSocket();
		serverSocket.bind(address);
		
		System.out.println("Server is listening on port: "+PORT);


	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			
			try {
				Socket socket = serverSocket.accept();
				System.out.println("\nNew client connected: " +
					socket.getInetAddress().toString() + ":" + socket.getPort());	
				
				DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
				DataInputStream dis=new DataInputStream(socket.getInputStream());
				
				dos.writeInt(dis.readInt()); //para el test del entero
				
				serveFilesToClient(socket);
			}
			catch(IOException e) {
				System.out.println("Server exception: "+ e.getMessage());
				e.printStackTrace();
			}
			
			/*
			 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
			 * comunicación con dicho cliente para servir los ficheros solicitados se debe
			 * implementar en el método serveFilesToClient, al cual hay que pasarle el
			 * socket devuelto por accept.
			 */


		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		
		try {
			while (true) {
					Socket socket = serverSocket.accept();
					System.out.println("\nNew client connected: " +
						socket.getInetAddress().toString() + ":" + socket.getPort());	
					NFServerThread st = new NFServerThread(socket);
					st.start();
			}
		}
		catch(SocketException e) {
			if(stopping) {
				System.out.println("Server socket has been closed");
			}
			else {
				System.out.println("Server exception: "+e.getMessage());
			}
		}
		catch(IOException e) {
			System.out.println("Server exception: "+ e.getMessage());
			e.printStackTrace();
		}
		
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */
		
		



	}
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */

	public boolean startServer() {
		boolean started = false;
		if(serverThread==null || !serverThread.isAlive()) {
			serverThread=new Thread(this); //de nuevo, no se si esto es del todo correcto
			serverThread.start();
			System.out.println("Server started running");
			started = true;
		}
		else {
			System.out.println("Server is already running");
		}
		return started;
		
	}
	
	public void stopServer() {//vale con cerrar el socket, ya que lanzara una excepcion y terminara el bucle de run
		
		try {
            if (serverSocket != null && !serverSocket.isClosed()) {
            	stopping=true;
                serverSocket.close();
                System.out.println("Server stopped");
            }
        } catch (IOException e) {
            System.err.println("Error while stopping server: " + e.getMessage());
        }
		
	}

	public int getServerPort(){
		return PORT;
	}

	public boolean isActive() {
		return serverThread.isAlive();
	}
	
	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		
		try {
		
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			
			while(socket.isConnected()) {
				PeerMessage messageFromClient= PeerMessage.readMessageFromInputStream(dis);
				byte op=messageFromClient.getOpcode();
				
				switch(op) {
					case PeerMessageOps.OPCODE_PEER_FILES_REQ: {
						
						FileInfo[] peerFiles=NanoFiles.db.getFiles();
						PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILES_REPLY, peerFiles);
						messageToClient.writeMessageToOutputStream(dos);
						
						
						break;
					}
					case PeerMessageOps.OPCODE_PEER_FILE_DL_REQ: {
						
						String subHash=messageFromClient.getSubHash();
						FileInfo[] peerFiles=NanoFiles.db.getFiles();
						FileInfo[] matchingFiles=FileInfo.lookupHashSubstring(peerFiles, subHash);
						
						if(matchingFiles.length==1) {
							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY, matchingFiles[0].fileHash,
									matchingFiles[0].fileSize, matchingFiles[0].fileName);
							
							messageToClient.writeMessageToOutputStream(dos);
						}
						else if(matchingFiles.length>0){
							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR);
							messageToClient.setErrorInfo("Several files match the subhash");
							messageToClient.writeMessageToOutputStream(dos);
						}
						else {

							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR);
							messageToClient.setErrorInfo("No file matches the subhash");
							messageToClient.writeMessageToOutputStream(dos);
						}
						
						break;
					}
					case PeerMessageOps.OPCODE_PEER_FILE_DL_FILE: {
						
						String hash=messageFromClient.getSubHash();
						long offset=messageFromClient.getOffset();
						long length=messageFromClient.getLength();
						
						FileInfo[] peerFiles=NanoFiles.db.getFiles();
						FileInfo[] matchingFiles=FileInfo.lookupHashSubstring(peerFiles, hash);
						
						if(matchingFiles.length==1) {
							
							FileInfo fichero=matchingFiles[0];

							int cantidad= (int) Math.min(length, fichero.fileSize - offset);
							System.out.println("Cantidad a leer en el bloque : "+cantidad);
							
							RandomAccessFile f=new RandomAccessFile(fichero.filePath, "r");
							byte[] data=new byte[cantidad];
							
							f.seek(offset);
							f.readFully(data);
							f.close();
							
							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_DATA, data);
							messageToClient.writeMessageToOutputStream(dos);
							
							
						}
						else if(matchingFiles.length>0){
							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR);
							messageToClient.setErrorInfo("Several files match the subhash");
							messageToClient.writeMessageToOutputStream(dos);
						}
						else {

							PeerMessage messageToClient=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR);
							messageToClient.setErrorInfo("No file matches the subhash");
							messageToClient.writeMessageToOutputStream(dos);
						}
						
						break;
						
					}
					default: {
						
						System.err.println("Invalid message format");
						
						break;
					}
					
				}
			}
		}
		catch(IOException e) {
			System.out.println("Server exception: "+e.getMessage());
			e.printStackTrace();
		}
		/*
		 * TODO: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
		 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
		 * tipo de mensaje recibido, enviando los correspondientes mensajes de
		 * respuesta.
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
		 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
		 * compartidos. Los ficheros compartidos se pueden obtener con
		 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
		 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
		 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
		 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
		 * de su hash completo.
		 */



	}




}

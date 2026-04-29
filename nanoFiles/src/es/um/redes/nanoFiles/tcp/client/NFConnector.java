package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.FileNameUtil;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	private DataOutputStream dos;
	private DataInputStream dis;



	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		
		socket=new Socket(serverAddr.getAddress(), serverAddr.getPort());
		
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */

		dos=new DataOutputStream(socket.getOutputStream());
		dis=new DataInputStream(socket.getInputStream());


	}

	public void close() {
		try {
			socket.close();
			dos.close();
			dis.close();
		}
		catch(IOException e) {
			System.out.println("Connector closed unsuccessfully");
		}
	}
	
	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */
		
		int integer=2;
		
		try {
			dos.writeInt(integer);
			int receivedInteger=dis.readInt();
			if(receivedInteger==integer) {
				System.out.println("Integer test successful!");
			}
			else {
				System.out.println("Integer test failed...");
			}
		}
		catch(IOException e) {
			System.out.println("Integer sending failed: "+e.getMessage());
		}
	}

	public FileInfo[] getPeerFilesList() {
		
		FileInfo[] response=null;
		
		PeerMessage messageToServer=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILES_REQ);
		try {
			messageToServer.writeMessageToOutputStream(dos);
			PeerMessage messageFromServer=PeerMessage.readMessageFromInputStream(dis);
			
			if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILES_REPLY) {
				
				FileInfo[] filesList=messageFromServer.getPeerFilesList();
				System.out.println("List obtained successfully");
				response=filesList;
			}
			else {
				System.out.println("Failed to obtained the files list");
				response=null;
			}
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		
		
		return response;
		
	}

	public boolean downloadSubHash(String subHash) { //esto no sirve al final
		
		boolean success=false;
		
		PeerMessage messageToServer=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_REQ, subHash);
		
		try {
			messageToServer.writeMessageToOutputStream(dos);
			PeerMessage messageFromServer=PeerMessage.readMessageFromInputStream(dis);
			
			if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY) {
				String completeHash=messageFromServer.getSubHash();
				String fileName=messageFromServer.getFileName();
				long fileSize=messageFromServer.getFileSize();
						
				long offset=0;
				
				File outputFile = FileNameUtil.chooseAvailableName(fileName).toFile();
				FileOutputStream fos = new FileOutputStream(outputFile, true); //el true es vital para los chunks, para que no sobreescriba
				
				try {
					while(offset<fileSize) {
					
						long cantidad = Math.min(PeerMessage.MAX_CHUNK_SIZE, fileSize - offset);
						
						messageToServer=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_FILE, completeHash, offset, cantidad);
						messageToServer.writeMessageToOutputStream(dos);
						messageFromServer=PeerMessage.readMessageFromInputStream(dis);
						
						if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_DATA) {
							byte[] receivedData=messageFromServer.getData();
							 
						    fos.write(receivedData);
						    offset+=receivedData.length;
						    System.out.println("Chunk written successfully");
							 
						}
						else {
							
							if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR) {
								String error=messageFromServer.getErrorInfo();
								
								if(!error.isBlank()) {
									System.out.println(error);
								}
							}
							
							System.out.println("Download failed");
							success=false;
							break;
						}
					}
					
					
					System.out.println("Download succeded");
				    success=true;	
					
				} catch(IOException e) {
					System.out.println("Disk writing error: "+ e.getMessage());
					e.printStackTrace();
				}finally {
					fos.close();
				}
				
				
				
			}
			else {
				
				if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR) {
					String error=messageFromServer.getErrorInfo();
					
					if(!error.isBlank()) {
						System.out.println(error);
					}
				}
				
				System.out.println("Download failed");
				success=false;
			}
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}

		return success;
	}

	//se pide como precondicion que el chunk sea valido
	
	public boolean downloadSingleChunk(String hash, long offset, long length, RandomAccessFile raf) {
		
		boolean success=false;
		
		try {
			PeerMessage messageToServer=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_FILE, hash, offset, length);
			messageToServer.writeMessageToOutputStream(dos);
			PeerMessage messageFromServer=PeerMessage.readMessageFromInputStream(dis);
			
			if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_DATA) {
				byte[] receivedData=messageFromServer.getData();
				raf.seek(offset);
				raf.write(receivedData);
				System.out.println("Chunk successfully downloaded");
				success=true;
			}
			else if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR) {
				String error=messageFromServer.getErrorInfo();
				
				if(!error.isBlank()) {
					System.out.println(error);
				}
				
			}
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			success=false;
		}
		
		if(!success) System.out.println("Chunk failed to download");
		
		return success;
		
	}

	public FileInfo getFileInfo(String subHash) {
		
		FileInfo info=new FileInfo();
		
		PeerMessage messageToServer=new PeerMessage(PeerMessageOps.OPCODE_PEER_FILE_DL_REQ, subHash);
		
		try {
			messageToServer.writeMessageToOutputStream(dos);
			PeerMessage messageFromServer=PeerMessage.readMessageFromInputStream(dis);
			
			if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY) {
				info.fileHash=messageFromServer.getSubHash();
				info.fileName=messageFromServer.getFileName();
				info.fileSize=messageFromServer.getFileSize();
			}
			else if(messageFromServer.getOpcode()==PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR) {
				String error=messageFromServer.getErrorInfo();
				
				if(!error.isBlank()) {
					System.out.println(error);
				}
				
				return null;
			}
		}
		catch(IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
		
		return info;
		
	}
	
	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}

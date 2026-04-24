package es.um.redes.nanoFiles.udp.message;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)
	public static final int CHUNK_MAX_SIZE=40000;
	
	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	private static final String FIELDNAME_PROTOCOL_ID="protocolid";
	private static final String FIELDNAME_FILE="file";
	private static final String FIELDNAME_LAST="last";
	private static final String FIELDNAME_SERVER="server";
	private static final String FIELDNAME_SERVER_NICKNAME="server_nickname";
	private static final String FIELDNAME_PEER="peer";
	private static final String FIELDNAME_SUBHASH="subhash";
	private static final String FIELDNAME_FILENAME="file_name";
	private static final String FIELDNAME_DATA="data";
	private static final String FIELDNAME_BLOCK_NUMBER="block_number";
	private static final String FIELDNAME_ACK_NUMBER="ack_number";
	private static final String FIELDNAME_ERROR_INFO="error_info";
	private static final String FIELDNAME_NICKNAME="nickname";
	
	/*
	 * TODO: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */



	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	private String protocolId;
	private boolean isLast = true;
	/*
	 * TODO: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */
	
	private List<FileInfo> fileList;

	private String serverNickname;
	private int serverPort;

	private Map<String, InetSocketAddress> peerList;
	
	private String subHash;
	private String fileName;
	private byte[] data;
	private long blockNumber;
	private long ackNumber;
	
	private String errorInfo;
	private String nickname;
	
	public DirMessage(String op) {
		operation = op;
		fileList=new ArrayList<>();
		peerList=new HashMap<>();
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */




	public String getOperation() {
		return operation;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public void setProtocolID(String protocolIdent) {
		if (!operation.equals(DirMessageOps.OPERATION_PING)) {
			throw new RuntimeException(
					"DirMessage: setProtocolId called for message of unexpected type (" + operation + ")");
		}
		protocolId = protocolIdent;
	}

	public String getProtocolId() {



		return protocolId;
	}

	public boolean addFile(FileInfo file) {
		return fileList.add(file);
	}
	
	public List<FileInfo> getFileList() {
		return Collections.unmodifiableList(fileList);
	}
	
	public void setLast( boolean last ) {
		this.isLast = last;
	}

	public boolean getLast() {
		return isLast;
	}
	
	public void setServerNickname(String sn) {
		serverNickname=sn;
	}
	
	public String getServerNickname() {
		return serverNickname;
	}
	
	public void setServerPort(int sp) {
		serverPort=sp;
	}
	
	public int getServerPort() {
		return serverPort;
	}

	public void addPeerList(String key, InetSocketAddress value) {
		peerList.put(key, value);
	}
	
	public String getSubHash() {
		return subHash;
	}
	
	public void setSubHash(String sh) {
		subHash=sh;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fn) {
		fileName=fn;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] d) {
		data=d;
	}
	
	public long getAckNumber() {
		return ackNumber;
	}
	
	public void setAckNumber(long an) {
		ackNumber=an;
	}
	
	public long getBlockNumber(){
		return blockNumber;
	}
	
	public void setBlockNumber(long bn) {
		blockNumber=bn;
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
	
	public void setErrorInfo(String ei) {
		
		if(ei.contains("\n")) throw new IllegalArgumentException("The information string must not contain returns");
		
		errorInfo = ei;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nn) {
		nickname=nn;
	}
	
	public Map<String, InetSocketAddress> getPeerList(){
		return Collections.unmodifiableMap(peerList);
	}
	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;
		FileInfo file=null;
		InetSocketAddress address=null;

		String[] values;

		for (String line : lines) {
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value);
				break;
			}
			case FIELDNAME_PROTOCOL_ID: {
				m.setProtocolID(value);
				break;
			}
			case FIELDNAME_FILE: {
				
				values=value.split(",");
				
				if(values.length==3) {
					file=new FileInfo(values[2], values[0], Long.parseLong(values[1]), "");
					m.addFile(file);
				}
				else {
					throw new IllegalArgumentException("The message format is incorrect");
				}
				
				break;
			}
			case FIELDNAME_LAST: {
				m.setLast(Boolean.parseBoolean(value));
				break;
			}
			case FIELDNAME_SERVER: {
				
				values=value.split(",");
				
				if(values.length==2) {
					m.setServerNickname(values[0]);
					m.setServerPort(Integer.parseInt(values[1]));
				}
				else {
					throw new IllegalArgumentException("The message format is incorrect");
				}
				
				break;
				
			}
			case FIELDNAME_SERVER_NICKNAME: {
				m.setServerNickname(value);
				break;
			}
			case FIELDNAME_PEER: {
				
				values=value.split(",");
				
				if(values.length==3) {
					address=new InetSocketAddress(values[1], Integer.parseInt(values[2]));
					m.addPeerList(values[0], address);
				}
				else {
					throw new IllegalArgumentException("The message format is incorrect");
				}	
					
				break;
			}	
			case FIELDNAME_SUBHASH: {
				m.setSubHash(value);
				break;
			}
			case FIELDNAME_FILENAME: {
				m.setFileName(value);
				break;
			}
			case FIELDNAME_DATA: {
				m.setData(Base64.getDecoder().decode(value));
				break;
			}
			case FIELDNAME_BLOCK_NUMBER: {
				m.setBlockNumber(Long.parseLong(value));
				break;
			}
			case FIELDNAME_ACK_NUMBER: {
				m.setAckNumber(Long.parseLong(value));
				break;
			}
			case FIELDNAME_ERROR_INFO: {
				m.setErrorInfo(value);
				break;
			}
			case FIELDNAME_NICKNAME: {
				m.setNickname(value);
				break;
			}
			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}




		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: (Boletín MensajesASCII) En función de la operación del mensaje, crear
		 * una cadena la operación y concatenar el resto de campos necesarios usando los
		 * valores de los atributos del objeto.
		 */


		switch(operation) {
			case DirMessageOps.OPERATION_PING: {
				sb.append(FIELDNAME_PROTOCOL_ID + DELIMITER + protocolId + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_DIRFILES_OK: {
				sb.append(FIELDNAME_LAST + DELIMITER + isLast + END_LINE);
				for(FileInfo file: fileList) {
					sb.append(FIELDNAME_FILE + DELIMITER + file.fileName + "," + file.fileSize + "," + file.fileHash + END_LINE);
				}
				break;
			}
			case DirMessageOps.OPERATION_SERVE: {
				sb.append(FIELDNAME_SERVER + DELIMITER + serverNickname + "," + serverPort + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_SERVE_OK: {
				
				sb.append(FIELDNAME_SERVER_NICKNAME + DELIMITER + serverNickname + END_LINE);
				
				break;
			}
			case DirMessageOps.OPERATION_PEERS_OK: {
				for(String peer : peerList.keySet()) {
					sb.append(FIELDNAME_PEER + DELIMITER + peer + "," + peerList.get(peer).getHostString() + "," + peerList.get(peer).getPort() + END_LINE);
				}
				
				break;
			}
			case DirMessageOps.OPERATION_DIRDL_REQ: {
				sb.append(FIELDNAME_SUBHASH + DELIMITER + subHash + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_DIRDL_REPLY: {
				sb.append(FIELDNAME_BLOCK_NUMBER + DELIMITER + blockNumber + END_LINE);
				sb.append(FIELDNAME_DATA + DELIMITER + Base64.getEncoder().encodeToString(data) + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_DIRDL_OK: {
				sb.append(FIELDNAME_FILENAME + DELIMITER + fileName + END_LINE);
				sb.append(FIELDNAME_SUBHASH + DELIMITER + subHash + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_DIRDL_ACK: {
				sb.append(FIELDNAME_ACK_NUMBER + DELIMITER + ackNumber + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_DIRDL_ERROR: {
				sb.append(FIELDNAME_ERROR_INFO + DELIMITER + errorInfo + END_LINE);
				break;
			}
			case DirMessageOps.OPERATION_QUIT: {
				sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
				break;
			}
			default: {
				
				if(operation.equals(DirMessageOps.OPERATION_INVALID)) {
					System.err.println("Invalid operation");
				}
				break;
			}
			
		}
		

		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}

}

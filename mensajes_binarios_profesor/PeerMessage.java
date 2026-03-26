package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {

	private byte opcode;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */
	private FileInfo[] peerFiles; 
	
	private String subHash;

	private long offset;
	private int length;
	private byte[] data;
		

	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
	public PeerMessage(byte op, FileInfo[] ficheros) {
		assert(op == PeerMessageOps.OPCODE_PEER_FILES_REPLY);
		this.opcode = op;
		this.peerFiles = ficheros;
	}
	
	public PeerMessage(byte op, String hash) {
		assert(op == PeerMessageOps.OPCODE_PEER_FILE_DL_REQ || op == PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY);
		this.opcode = op;
		this.subHash = hash;
	}

	
	public PeerMessage(byte opcode, String fullHash, int offset, int length) {
		assert(opcode == PeerMessageOps.OPCODE_PEER_FILE_DL_FILE);
		this.opcode = opcode;
		this.offset = offset;
		this.length = length;
		this.subHash = fullHash;
	}

	public PeerMessage(byte opcode, byte[] data) {
		assert(opcode == PeerMessageOps.OPCODE_PEER_FILE_DL_DATA);
		this.opcode = opcode;
		this.data = data;
	}

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}



	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode);
		
		switch (opcode) {
		case PeerMessageOps.OPCODE_PEER_FILES_REQ:
			break;
			
		case PeerMessageOps.OPCODE_PEER_FILES_REPLY:
			// Procesamos lo siguiente
			int tamanyo = dis.readInt();
			byte[] bDatos = new byte[tamanyo];
			dis.readFully(bDatos);
			message.setPeerFiles(FileInfo.deserializeList(bDatos));
			break;
			
		case PeerMessageOps.OPCODE_PEER_FILE_DL_REQ:
			// Procesamos lo siguiente
			int tamanoHash = dis.readInt();
			byte[] bHash = new byte[tamanoHash];
			dis.readFully(bHash);
			message.setSubHash(new String(bHash));
			break;
		
		case PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY:
			// Procesamos lo siguiente
			int tamanoHashReply = dis.readInt();
			byte[] bHashReply = new byte[tamanoHashReply];
			dis.readFully(bHashReply);
			message.setSubHash(new String(bHashReply));
			break;


		case PeerMessageOps.OPCODE_PEER_FILE_DL:	
			// Procesamos lo siguiente
			int tamanoHashDL = dis.readInt();
			byte[] bHashDL = new byte[tamanoHashDL];
			dis.readFully(bHashDL);
			message.setSubHash(new String(bHashDL));
			break;	
			
		case PeerMessageOps.OPCODE_PEER_FILE_DL_FILE:
			// Procesamos lo siguiente
			int tamanoHashFile = dis.readInt();
			byte[] bHashFile = new byte[tamanoHashFile];
			dis.readFully(bHashFile);
			message.setSubHash(new String(bHashFile));
			message.setOffset(dis.readLong());
			message.setLength(dis.readInt());
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL_DATA:
			int tamanyoData = dis.readInt();
			byte[] bData = new byte[tamanyoData];
			dis.readFully(bData);
			message.setData(bData);
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR:
			// No hay nada más que leer
			break;

		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		
		
		return message;
	}
	
	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		
		switch (opcode) {
		case PeerMessageOps.OPCODE_PEER_FILES_REQ:
			break;
			
		case PeerMessageOps.OPCODE_PEER_FILES_REPLY:
			byte[] bFiles = FileInfo.serializeList(getPeerFiles());
			dos.writeInt(bFiles.length);
			dos.write(bFiles);
			break;
		
		case PeerMessageOps.OPCODE_PEER_FILE_DL_REQ:
			byte[] bHash = getSubHash().getBytes();
			dos.writeInt(bHash.length);
			dos.write(bHash);
			break;
			
		case PeerMessageOps.OPCODE_PEER_FILE_DL_REPLY:
			byte[] bHashReply = getSubHash().getBytes();
			dos.writeInt(bHashReply.length);
			dos.write(bHashReply);
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL:
			byte[] bHashDL = getSubHash().getBytes();
			dos.writeInt(bHashDL.length);
			dos.write(bHashDL);
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL_FILE:
			byte[] bHashFile = getSubHash().getBytes();
			dos.writeInt(bHashFile.length);
			dos.write(bHashFile);
			dos.writeLong(getOffset());
			dos.writeInt(getLength());
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL_DATA:
			byte[] bData = getData();
			dos.writeInt(bData.length);
			dos.write(bData);
			break;

		case PeerMessageOps.OPCODE_PEER_FILE_DL_ERROR:
			// No hay nada más que escribir
			break;

		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}

	
	
	
	
	
	
	
	

		
	public FileInfo[] getPeerFiles() {
		return peerFiles;
	}

	public void setPeerFiles(FileInfo[] peerFiles) {
		this.peerFiles = peerFiles;
	}

	public String getSubHash() {
		return subHash;
	}

	public void setSubHash(String subHash) {
		this.subHash = subHash;
	}
		
public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setOpcode(byte opcode) {
		this.opcode = opcode;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}	





}

package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_PING = "ping";
	public static final String OPERATION_PING_OK = "ping_ok";
	public static final String OPERATION_PING_ERROR = "ping_error";
	public static final String OPERATION_DIRFILES="dirfiles";
	public static final String OPERATION_DIRFILES_OK="dirfiles_ok";
	public static final String OPERATION_DIRFILES_ERROR="dirfiles_error";
	public static final String OPERATION_SERVE="serve";
	public static final String OPERATION_SERVE_OK="serve_ok";
	public static final String OPERATION_SERVE_ERROR="serve_error";
	public static final String OPERATION_PEERS="peers";
	public static final String OPERATION_PEERS_OK="peers_ok";
	public static final String OPERATION_PEERS_ERROR="peers_error";
	
	// TODO: definir las operaciones del protocolo de directorio

	


}

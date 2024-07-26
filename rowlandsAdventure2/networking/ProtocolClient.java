package rowlandsAdventure2.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.character.CharacterState;
import tage.Time;
import tage.networking.client.GameConnectionClient;

public class ProtocolClient extends GameConnectionClient {
	private GhostManager ghostManager;
	private CharacterController characterController;
	private UUID id;
	private boolean isClientConnected = false;

	private CharacterState decodedState = new CharacterState();

	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, GhostManager gm, CharacterController charcon)
			throws IOException {
		super(remoteAddr, remotePort, protocolType);
		id = UUID.randomUUID();
		ghostManager = gm;
		characterController = charcon;
	}

	public UUID getID() {
		return id;
	}

	@Override
	protected void processPacket(Object message) {
		String strMessage = (String) message;
		System.out.println("message received -->" + strMessage);
		if (strMessage == null){
			return;
		}
		String[] messageTokens = strMessage.split(",");

		// Game specific protocol to handle the message
		if (messageTokens.length > 0) {
			// Handle JOIN message
			// Format: (join,success) or (join,failure)
			if (messageTokens[0].compareTo("join") == 0) {
				if (messageTokens[1].compareTo("success") == 0) {
					System.out.println("join success confirmed");
					isClientConnected = true;
					Time.setServerStart(Long.parseLong(messageTokens[2]));
					sendCreateMessage(characterController.getState(), characterController.getTextureIndex());
				}
				if (messageTokens[1].compareTo("failure") == 0) {
					System.out.println("join failure confirmed");
					isClientConnected = false;
				}
			}

			// Handle BYE message
			// Format: (bye,remoteId)
			if (messageTokens[0].compareTo("bye") == 0) { // remove ghost avatar with id = remoteId
															// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				ghostManager.removeGhostAvatar(ghostID);
			}

			// Handle CREATE message
			// Format: (create,remoteId,x,y,z)
			// AND
			// Handle DETAILS_FOR message
			// Format: (dsfr,remoteId,x,y,z)
			if (messageTokens[0].compareTo("create") == 0 || (messageTokens[0].compareTo("dsfr") == 0)) { 
				// create a new ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);

				// Parse out the position into a Vector3f
				decodedState.decode(messageTokens[2]);
				int texIndex = Integer.parseInt(messageTokens[3]);

				//try {
					ghostManager.createGhostAvatar(ghostID, decodedState, texIndex);
				//} catch (IOException e) {
				//	System.out.println("error creating ghost avatar");
				//}
			}

			// Handle WANTS_DETAILS message
			// Format: (wsds,remoteId)
			if (messageTokens[0].compareTo("wsds") == 0) {
				// Send the local client's avatar's information
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				sendDetailsForMessage(ghostID, characterController.getState(), characterController.getTextureIndex());
			}

			// Handle MOVE message
			// Format: (move,remoteId,x,y,z)
			if (messageTokens[0].compareTo("move") == 0) {
				// move a ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);

				// Parse out the position into a Vector3f
				decodedState.decode(messageTokens[2]);

				ghostManager.updateGhostAvatar(ghostID, decodedState);
			}
		}
	}

	// The initial message from the game client requesting to join the
	// server. localId is a unique identifier for the client. Recommend
	// a random UUID.
	// Message Format: (join,localId)

	public void sendJoinMessage() {
		try {
			sendPacket(new String("join," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Informs the server that the client is leaving the server.
	// Message Format: (bye,localId)

	public void sendByeMessage() {
		try {
			sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Informs the server of the client�s Avatar�s position. The server
	// takes this message and forwards it to all other clients registered
	// with the server.
	// Message Format: (create,localId,x,y,z) where x, y, and z represent the
	// position

	public void sendCreateMessage(CharacterState state, int texI) {
		try {
			String message = new String("create," + id.toString() + ",");
			message += state.encode() + ",";
			message += texI;

			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Informs the server of the local avatar's position. The server then
	// forwards this message to the client with the ID value matching remoteId.
	// This message is generated in response to receiving a WANTS_DETAILS message
	// from the server.
	// Message Format: (dsfr,remoteId,localId,x,y,z) where x, y, and z represent the
	// position.

	public void sendDetailsForMessage(UUID remoteId, CharacterState state, int texI) {
		try {
			String message = new String("dsfr," + remoteId.toString() + "," + id.toString() + ",");
			message += state.encode() + ",";
			message += texI;

			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Informs the server that the local avatar has changed position.
	// Message Format: (move,localId,x,y,z) where x, y, and z represent the
	// position.

	public void sendMoveMessage(CharacterState state) {
		try {
			String message = new String("move," + id.toString() + ",");
			message += state.encode();

			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected(){
		return isClientConnected;
	}
}

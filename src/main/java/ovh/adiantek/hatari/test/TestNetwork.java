package ovh.adiantek.hatari.test;

import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class TestNetwork {
	private static final Logger L = (Logger) LogManager
			.getLogger(TestNetwork.class);
	private static NetworkManager manager;
	private static boolean logged;

	public static void main(String[] args) {
		L.setLevel(Level.TRACE);
		connect("mc.teamcraft24.pl", 25565);

	}

	private static void update(NetworkManager manager) {
		if (manager != null) {
			if (manager.isChannelOpen()) {
				manager.processReceivedPackets();
			} else if (manager.getExitMessage() != null) {
				manager.getNetHandler().onDisconnect(manager.getExitMessage());
			}
		}
	}

	private static int tick = 0;

	private static void send(Packet p) {
		manager.scheduleOutboundPacket(p, new GenericFutureListener[0]);
	}

	public static void runTick() {
		if (tick == 0) {
			send(new C01PacketChatMessage("/login barwa"));
		} else if (tick == 1) {
	//		send(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, p_78769_1_.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
		}

		if (tick % 20 == 19) {
			send(new C00PacketKeepAlive());
		}
		tick++;
	}

	public static void connect(String host, int port) {
		EnumConnectionState.values();
		Session session = new Session("barwnik4", "barwnik4", "FML", "legacy");
		L.info("Connecting to " + host + ", " + port);
		InetAddress inetaddress = null;
		try {
			inetaddress = InetAddress.getByName(host);
			manager = NetworkManager.provideLanClient(inetaddress, port);
			manager.setNetHandler(new NetHandlerLoginClientL());
			send(new C00Handshake(5, host, port, EnumConnectionState.LOGIN));
			send(new C00PacketLoginStart(session.func_148256_e()));
			while (true) {
				update(manager);
				if (logged)
					runTick();
			}
		} catch (Exception exception) {
			L.error("Couldn\'t connect to server", exception);

		}
	}

	private static class NetHandlerLoginClientL implements
			INetHandlerLoginClient, INetHandlerPlayClient {
		@Override
		public void onDisconnect(IChatComponent msg) {
			L.info("Disconnected: " + msg.getUnformattedText());
			System.exit(0);
		}

		@Override
		public void onNetworkTick() {
		}

		@Override
		public void handleDisconnect(S00PacketDisconnect packet) {
			L.info("Disconnected: "
					+ packet.func_149603_c().getUnformattedText());
			manager.closeChannel(packet.func_149603_c());
		}

		@Override
		public void onConnectionStateTransition(EnumConnectionState from,
				EnumConnectionState to) {
			L.info("Switching protocol from " + from + " to " + to);
			if (to == EnumConnectionState.PLAY) {

				logged = true;
			}
		}

		@Override
		public void handleEncryptionRequest(S01PacketEncryptionRequest p) {
			System.out.println("Encrypt?");
		}

		@Override
		public void handleLoginSuccess(S02PacketLoginSuccess packet) {
			manager.setConnectionState(EnumConnectionState.PLAY);
		}

		@Override
		public void handleSpawnObject(S0EPacketSpawnObject p_147235_1_) {
		}

		@Override
		public void handleSpawnExperienceOrb(
				S11PacketSpawnExperienceOrb p_147286_1_) {
		}

		@Override
		public void handleSpawnGlobalEntity(
				S2CPacketSpawnGlobalEntity p_147292_1_) {
		}

		@Override
		public void handleSpawnMob(S0FPacketSpawnMob p_147281_1_) {
		}

		@Override
		public void handleScoreboardObjective(
				S3BPacketScoreboardObjective p_147291_1_) {
		}

		@Override
		public void handleSpawnPainting(S10PacketSpawnPainting p_147288_1_) {
		}

		@Override
		public void handleSpawnPlayer(S0CPacketSpawnPlayer p_147237_1_) {
		}

		@Override
		public void handleAnimation(S0BPacketAnimation p_147279_1_) {
		}

		@Override
		public void handleStatistics(S37PacketStatistics p_147293_1_) {
		}

		@Override
		public void handleBlockBreakAnim(S25PacketBlockBreakAnim p_147294_1_) {
		}

		@Override
		public void handleSignEditorOpen(S36PacketSignEditorOpen p_147268_1_) {
		}

		@Override
		public void handleUpdateTileEntity(S35PacketUpdateTileEntity p_147273_1_) {
		}

		@Override
		public void handleBlockAction(S24PacketBlockAction p_147261_1_) {
		}

		@Override
		public void handleBlockChange(S23PacketBlockChange p_147234_1_) {
		}

		@Override
		public void handleChat(S02PacketChat packet) {
			L.info(packet.func_148915_c().getUnformattedText());
		}

		@Override
		public void handleTabComplete(S3APacketTabComplete p_147274_1_) {
		}

		@Override
		public void handleMultiBlockChange(S22PacketMultiBlockChange p_147287_1_) {
		}

		@Override
		public void handleMaps(S34PacketMaps p_147264_1_) {
		}

		@Override
		public void handleConfirmTransaction(
				S32PacketConfirmTransaction p_147239_1_) {
		}

		@Override
		public void handleCloseWindow(S2EPacketCloseWindow p_147276_1_) {
		}

		@Override
		public void handleWindowItems(S30PacketWindowItems p_147241_1_) {
		}

		@Override
		public void handleOpenWindow(S2DPacketOpenWindow p_147265_1_) {
		}

		@Override
		public void handleWindowProperty(S31PacketWindowProperty p_147245_1_) {
		}

		@Override
		public void handleSetSlot(S2FPacketSetSlot p_147266_1_) {
		}

		@Override
		public void handleCustomPayload(S3FPacketCustomPayload p_147240_1_) {
		}

		@Override
		public void handleDisconnect(S40PacketDisconnect p_147253_1_) {
		}

		@Override
		public void handleUseBed(S0APacketUseBed p_147278_1_) {
		}

		@Override
		public void handleEntityStatus(S19PacketEntityStatus p_147236_1_) {
		}

		@Override
		public void handleEntityAttach(S1BPacketEntityAttach p_147243_1_) {
		}

		@Override
		public void handleExplosion(S27PacketExplosion p_147283_1_) {
		}

		@Override
		public void handleChangeGameState(S2BPacketChangeGameState p_147252_1_) {
		}

		@Override
		public void handleKeepAlive(S00PacketKeepAlive p_147272_1_) {
		}

		@Override
		public void handleChunkData(S21PacketChunkData p_147263_1_) {
		}

		@Override
		public void handleMapChunkBulk(S26PacketMapChunkBulk p_147269_1_) {
		}

		@Override
		public void handleEffect(S28PacketEffect p_147277_1_) {
		}

		@Override
		public void handleJoinGame(S01PacketJoinGame p_147282_1_) {
		}

		@Override
		public void handleEntityMovement(S14PacketEntity p_147259_1_) {
		}

		@Override
		public void handlePlayerPosLook(S08PacketPlayerPosLook p_147258_1_) {
		}

		@Override
		public void handleParticles(S2APacketParticles p_147289_1_) {
		}

		@Override
		public void handlePlayerAbilities(S39PacketPlayerAbilities p_147270_1_) {
		}

		@Override
		public void handlePlayerListItem(S38PacketPlayerListItem p_147256_1_) {
		}

		@Override
		public void handleDestroyEntities(S13PacketDestroyEntities p_147238_1_) {
		}

		@Override
		public void handleRemoveEntityEffect(
				S1EPacketRemoveEntityEffect p_147262_1_) {
		}

		@Override
		public void handleRespawn(S07PacketRespawn p_147280_1_) {
		}

		@Override
		public void handleEntityHeadLook(S19PacketEntityHeadLook p_147267_1_) {
		}

		@Override
		public void handleHeldItemChange(S09PacketHeldItemChange p_147257_1_) {
		}

		@Override
		public void handleDisplayScoreboard(
				S3DPacketDisplayScoreboard p_147254_1_) {
		}

		@Override
		public void handleEntityMetadata(S1CPacketEntityMetadata p_147284_1_) {
		}

		@Override
		public void handleEntityVelocity(S12PacketEntityVelocity p_147244_1_) {
		}

		@Override
		public void handleEntityEquipment(S04PacketEntityEquipment p_147242_1_) {
		}

		@Override
		public void handleSetExperience(S1FPacketSetExperience p_147295_1_) {
		}

		@Override
		public void handleUpdateHealth(S06PacketUpdateHealth p_147249_1_) {
		}

		@Override
		public void handleTeams(S3EPacketTeams p_147247_1_) {
		}

		@Override
		public void handleUpdateScore(S3CPacketUpdateScore p_147250_1_) {
		}

		@Override
		public void handleSpawnPosition(S05PacketSpawnPosition p_147271_1_) {
		}

		@Override
		public void handleTimeUpdate(S03PacketTimeUpdate p_147285_1_) {
		}

		@Override
		public void handleUpdateSign(S33PacketUpdateSign p_147248_1_) {
		}

		@Override
		public void handleSoundEffect(S29PacketSoundEffect p_147255_1_) {
		}

		@Override
		public void handleCollectItem(S0DPacketCollectItem p_147246_1_) {
		}

		@Override
		public void handleEntityTeleport(S18PacketEntityTeleport p_147275_1_) {
		}

		@Override
		public void handleEntityProperties(S20PacketEntityProperties p_147290_1_) {
		}

		@Override
		public void handleEntityEffect(S1DPacketEntityEffect p_147260_1_) {
		}
	}
}

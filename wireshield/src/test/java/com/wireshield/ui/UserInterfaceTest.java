package com.wireshield.ui;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.wireshield.enums.connectionStates;
import com.wireshield.localfileutils.SystemOrchestrator;
import com.wireshield.wireguard.WireguardManager;

public class UserInterfaceTest {
    private static boolean isToolkitInitialized = false;
    private UserInterface userInterface;

    @Mock
    private SystemOrchestrator mockSystemOrchestrator;
    @Mock
    private WireguardManager mockWireguardManager;
    @Mock
    private Button mockMinimizeButton;
    @Mock
    private Button mockCloseButton;
    @Mock
    private Stage mockStage;
    
    @Before
    public void setUp() {
        // Initialize JavaFX toolkit only once.
        if (!isToolkitInitialized) {
            Platform.startup(() -> {});
            isToolkitInitialized = true;
        }        
        userInterface = new UserInterface();
        MockitoAnnotations.initMocks(this);
        Scene mockScene = mock(Scene.class);
        mockStage = mock(Stage.class);
        when(mockScene.getWindow()).thenReturn(mockStage);
        // Setup the minimize button mock.
        mockMinimizeButton = mock(Button.class);
        when(mockMinimizeButton.getScene()).thenReturn(mockScene);
        userInterface.minimizeButton = mockMinimizeButton;
        
        // Setup the close button mock.
        mockCloseButton = mock(Button.class);
        when(mockCloseButton.getScene()).thenReturn(mockScene);
        userInterface.closeButton = mockCloseButton;
        
        // Setup mock systemOchestrator.
        UserInterface.so = mockSystemOrchestrator;
        // Initialize JavaFX components.
        Platform.runLater(() -> {
            userInterface.vpnButton = new Button("Start VPN");
            userInterface.peerListView = new ListView<>();
            userInterface.logsArea = new TextArea();
            userInterface.avStatusLabel = new Label();
            userInterface.homePane = new AnchorPane();
        });
    }

    @After
    public void tearDown() {
        // Resetta i mock dopo ogni test
        reset(mockSystemOrchestrator, mockWireguardManager);
    }

    @Test
    public void testMinimizeWindow() throws InterruptedException {
        // Act: Call method.
        userInterface.minimizeWindow();
        // Assert: Verify if setIconified has been called.
        verify(mockStage).setIconified(true);
    }
    
    @Test
    public void testInitialize_disablesPeerListWhenConnected() throws InterruptedException {
        when(mockSystemOrchestrator.getConnectionStatus()).thenReturn(connectionStates.CONNECTED);
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            userInterface.initialize();
            assertTrue(userInterface.peerListView.isDisable());
            verify(mockSystemOrchestrator).getConnectionStatus();
            latch.countDown();
        });

        assertTrue("Timeout nel thread JavaFX", latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    public void testChangeVPNState_startVPN() throws InterruptedException {
        when(mockSystemOrchestrator.getWireguardManager()).thenReturn(mockWireguardManager);
        when(mockSystemOrchestrator.getConnectionStatus()).thenReturn(connectionStates.DISCONNECTED);
        when(mockWireguardManager.getLog()).thenReturn("Mock Log");

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            userInterface.initialize();
            userInterface.selectedPeerFile = "test.conf";
            userInterface.changeVPNState();
            assertEquals("Stop VPN", userInterface.vpnButton.getText());
            assertTrue(userInterface.peerListView.isDisable());
            latch.countDown();
        });

        assertTrue("Timeout nel thread JavaFX", latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    public void testChangeVPNState_stopVPN() throws InterruptedException {
        when(mockSystemOrchestrator.getWireguardManager()).thenReturn(mockWireguardManager);
        when(mockSystemOrchestrator.getConnectionStatus()).thenReturn(connectionStates.CONNECTED);

        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            userInterface.initialize();
            userInterface.changeVPNState();
            assertEquals("Start VPN", userInterface.vpnButton.getText());
            assertFalse(userInterface.peerListView.isDisable());
            latch.countDown();
        });

        assertTrue("Timeout nel thread JavaFX", latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    public void testViewHome_updatesPeerList() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            userInterface.viewHome();
            latch.countDown();
        });

        assertTrue("Timeout nel thread JavaFX", latch.await(5, java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    public void testHandleFileSelection_noFileSelected() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            userInterface.viewHome();
            latch.countDown();
        });

        assertTrue("Timeout nel thread JavaFX", latch.await(5, java.util.concurrent.TimeUnit.SECONDS));

        Platform.runLater(() -> userInterface.handleFileSelection(null));
    }

    
    @Test
    public void testStartDynamicLogUpdate_logsUpdated() throws InterruptedException {
        when(mockWireguardManager.getLog()).thenReturn("Test Log");
        when(mockSystemOrchestrator.getWireguardManager()).thenReturn(mockWireguardManager);

        Platform.runLater(() -> userInterface.startDynamicLogUpdate());

        // Wait for the log update.
        Thread.sleep(2000);

        Platform.runLater(() -> assertEquals("Test Log", userInterface.logsArea.getText()));
    }
}
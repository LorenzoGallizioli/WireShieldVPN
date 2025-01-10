package com.wireshield.ui;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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

import com.wireshield.av.AntivirusManager;
import com.wireshield.av.ScanReport;
import com.wireshield.enums.connectionStates;
import com.wireshield.enums.runningStates;
import com.wireshield.enums.warningClass;
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
    private Button mockMinimizeButton, mockCloseButton;
    @Mock
    private Stage mockStage;
    @Mock
    private AntivirusManager mockAntivirusManager;
    @Mock
    private ObservableList<String> mockAvFilesList;
    @Mock
    private Label mockAvStatusLabel;
    @Mock
    private AnchorPane mockAvPane, mockSettingsPane, mockLogsPane;
    
    private AutoCloseable closeable;
    
    @Before
    public void setUp() {
        // Initialize JavaFX toolkit only once.
        if (!isToolkitInitialized) {
            Platform.startup(() -> {});
            isToolkitInitialized = true;
        }        
        userInterface = spy(new UserInterface());
        closeable = MockitoAnnotations.openMocks(this);        
        // Setup mocks.
        Scene mockScene = mock(Scene.class);
        mockStage = mock(Stage.class);
        mockMinimizeButton = mock(Button.class);
        mockCloseButton = mock(Button.class);
        mockAntivirusManager = mock(AntivirusManager.class);
        mockAvFilesList = mock(ObservableList.class);
        mockAvStatusLabel = mock(Label.class);
        mockAvPane = mock(AnchorPane.class);
        mockSettingsPane = mock(AnchorPane.class);
        mockLogsPane = mock(AnchorPane.class);
        when(mockScene.getWindow()).thenReturn(mockStage);
        when(mockMinimizeButton.getScene()).thenReturn(mockScene);
        when(mockCloseButton.getScene()).thenReturn(mockScene);
        when(mockSystemOrchestrator.getAntivirusManager()).thenReturn(mockAntivirusManager);
        userInterface.minimizeButton = mockMinimizeButton;
        userInterface.closeButton = mockCloseButton;
        userInterface.settingsPane = mockSettingsPane;
        userInterface.logsPane = mockLogsPane;        
        userInterface.avFilesList = mockAvFilesList;
        userInterface.avStatusLabel = mockAvStatusLabel;
        userInterface.avPane = mockAvPane;
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
    public void tearDown() throws Exception {
        // Resetta i mock dopo ogni test
        reset(mockSystemOrchestrator, mockWireguardManager);
        closeable.close();
    }

    @Test
    public void testMinimizeWindow() {
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
    public void testViewAv_withAVUpStatus() {
        // Set UP Status.
        runningStates avStatus = runningStates.UP;
        when(mockSystemOrchestrator.getAVStatus()).thenReturn(avStatus);

        ScanReport report1 = mock(ScanReport.class);
        when(report1.getFile()).thenReturn(new File("file1.txt"));
        when(report1.getWarningClass()).thenReturn(warningClass.DANGEROUS);

        ScanReport report2 = mock(ScanReport.class);
        when(report2.getFile()).thenReturn(new File("file2.txt"));
        when(report2.getWarningClass()).thenReturn(warningClass.CLEAR);

        List<ScanReport> reports = Arrays.asList(report1, report2);
        when(mockAntivirusManager.getFinalReports()).thenReturn(reports);

        // Act: execcute viewAV function.
        userInterface.viewAv();

        // Assert
        verify(mockAvStatusLabel).setText("UP");
        verify(mockAvFilesList).clear();

    }

    @Test
    public void testViewAv_withAVDownStatus() {
        // Arrange
        runningStates avStatus = runningStates.DOWN;
        when(mockSystemOrchestrator.getAVStatus()).thenReturn(avStatus);

        // Act
        userInterface.viewAv();

        // Assert
        verify(mockAvStatusLabel).setText("DOWN");
        verify(mockAvFilesList, never()).clear();
        verify(mockAvFilesList, never()).addAll(anyList());
        verify(mockAvPane).toFront();
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
    public void testViewLogs() {

        // Act
        userInterface.viewLogs();

        // Assert: Check if toFront() has been called.
        verify(mockLogsPane).toFront();

        // Check if startDynamicLogUpdate has been called.
        verify(userInterface).startDynamicLogUpdate();
    }
    
    @Test
    public void testViewSettings() {
        // Act
        userInterface.viewSettings();

        // Assert
        verify(mockSettingsPane).toFront();
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
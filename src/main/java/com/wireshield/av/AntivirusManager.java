package com.wireshield.av;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.wireshield.enums.runningStates;
import java.util.List;
import java.util.ArrayList;

public class AntivirusManager {
    private ClamAV clamAV;
    private VirusTotal virusTotal;
    private ScanReport finalReports;
    private Map<File, Boolean> scanBuffer;  // Usare una mappa per tracciare lo stato di scansione
    private List<File> filesToRemove = new ArrayList<>();
    private runningStates scannerStatus;

    public AntivirusManager() {
        this.scanBuffer = new HashMap<>();  // Usa una mappa per tracciare i file e se sono stati scansionati
        this.scannerStatus = runningStates.DOWN;
    }

    // Metodo per aggiungere un file al buffer solo se non è già stato scansionato
    public void addFileToScanBuffer(File file) {
        if (file != null && file.exists()) {
            // Se il file non è già presente nel buffer, aggiungilo
            if (!scanBuffer.containsKey(file)) {
                scanBuffer.put(file, false);  // Aggiungi il file e impostalo come non scansionato
                System.out.println("File added to scan buffer: " + file.getName());
            } else {
                System.out.println("File is already in the scan buffer: " + file.getName());
            }
        } else {
            System.out.println("Invalid file or file does not exist.");
        }
    }

    // Metodo per eseguire la scansione dei file
    public void performScan() {
        
        for (Map.Entry<File, Boolean> entry : scanBuffer.entrySet()) {
            File file = entry.getKey();
            Boolean isScanned = entry.getValue();

            // Se il file non è stato scansionato, esegui la scansione
            if (isScanned == false) {
                System.out.println("Scanning file: " + file.getName());
                ScanReport report = new ScanReport();  // Supponiamo che ScanReport sia un oggetto che contiene i dettagli della scansione

                // Esegui la scansione del file con ClamAV e VirusTotal
                if (clamAV != null) {
                    clamAV.analyze(file, report);
                }
                if (virusTotal != null) {
                    virusTotal.analyze(file, report);
                }

                // Segna il file come scansionato
                scanBuffer.put(file, true);  // Imposta il file come scansionato
                System.out.println("Scan completed for file: " + file.getName());

                // Aggiungi il file alla lista di quelli da rimuovere
                filesToRemove.add(file);
            } else {
                System.out.println("File already scanned: " + file.getName());
            }
        }

        // Rimuovi i file scansionati dal buffer dopo la scansione
        for (File file : filesToRemove) {
            scanBuffer.remove(file);
            System.out.println("Deleted file: " + file.getName());
        }
    }


    // Metodo per ottenere il rapporto
    protected ScanReport getReport() {
        return finalReports;
    }

    // Metodo per ottenere lo stato dell'antivirus
    protected runningStates getStatus() {
        return scannerStatus;
    }

    // Metodo per ottenere il buffer di file
    public Map<File, Boolean> getScanBuffer() {
        return scanBuffer;
    }
}

package com.wireshield.av;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.wireshield.enums.runningStates;
import java.util.List;
import java.util.ArrayList;

public class AntivirusManager {

	// Variabile statica per memorizzare l'unica istanza
	private static AntivirusManager instance;

	private ClamAV clamAV;
	private VirusTotal virusTotal;
	private ScanReport finalReports;
	private Map<File, Boolean> scanBuffer;
	private runningStates scannerStatus;
	List<File> filesToRemove = new ArrayList<>();

	// Costruttore privato per evitare istanze multiple
	private AntivirusManager() {
		this.scanBuffer = new HashMap<>();
		this.scannerStatus = runningStates.DOWN;
	}

	// Metodo pubblico per ottenere l'istanza del Singleton
	public static synchronized AntivirusManager getInstance() {
		if (instance == null) {
			instance = new AntivirusManager(); // Crea l'istanza solo se non esiste già
		}
		return instance;
	}

	// Metodo per aggiungere un file al buffer solo se non è già stato scansionato
	public void addFileToScanBuffer(File file) {
		if (file != null && file.exists()) {
			if (!scanBuffer.containsKey(file)) {
				scanBuffer.put(file, false);
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

		// Itera attraverso la mappa scanBuffer
		for (Map.Entry<File, Boolean> entry : scanBuffer.entrySet()) {
			File file = entry.getKey();
			Boolean isScanned = entry.getValue();

			// Se il file non è stato scansionato
			if (!isScanned) {
				System.out.println("Scanning file: " + file.getName());
				ScanReport report = new ScanReport(); // Supponiamo che ScanReport sia un oggetto che contiene i
														// dettagli della scansione

				// Esegui la scansione con ClamAV e VirusTotal
				if (clamAV != null) {
					clamAV.analyze(file, report);
				}
				if (virusTotal != null) {
					virusTotal.analyze(file, report);
				}

				// Segna il file come scansionato
				scanBuffer.put(file, true);
				System.out.println("Scan completed for file: " + file.getName());

				// Aggiungi il file alla lista dei file da rimuovere (non rimuoverlo
				// direttamente dalla mappa durante l'iterazione)
				filesToRemove.add(file);
			} else {
				System.out.println("File already scanned: " + file.getName());
			}
		}

		// Dopo l'iterazione, rimuovi i file scansionati dal buffer
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

	/* TESTING MAIN
	 * 
	public static void main(String[] args) {
		// Crea alcuni file di esempio per il test
		File file1 = new File("testfile1.txt");
		File file2 = new File("testfile2.txt");

		// Per scopi di testing, creiamo dei file temporanei
		try {
			if (file1.createNewFile()) {
				System.out.println("Created file: " + file1.getName());
			}
			if (file2.createNewFile()) {
				System.out.println("Created file: " + file2.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Ottieni l'istanza di AntivirusManager
		AntivirusManager antivirusManager = AntivirusManager.getInstance();

		// Aggiungi i file al buffer di scansione
		antivirusManager.addFileToScanBuffer(file1);
		antivirusManager.addFileToScanBuffer(file2);

		// Esegui la scansione dei file
		antivirusManager.performScan();

		// Verifica lo stato del buffer
		System.out
				.println("Scan buffer after scan: " + antivirusManager.getScanBuffer().size() + " file(s) remaining.");
	} 
	*/
}

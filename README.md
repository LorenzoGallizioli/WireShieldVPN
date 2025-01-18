# 🌐 WireShield

**Titolo del Progetto:** Client VPN in Java per connessioni WireGuard con scansione antivirus integrata per una navigazione sicura


# 📖 Indice

1. [🌐 WireShield - Introduzione](#-wireshield---introduzione)  
2. [🧑‍💻 Chi Siamo](#-chi-siamo)  
3. [📝 Descrizione del Progetto](#-descrizione-del-progetto)  
4. [🔑 Funzionalità Principali](#-funzionalità-principali)  
   - [4.1 Connessione VPN con WireGuard](#1-connessione-vpn-con-wireguard)  
   - [4.2 Scansione Antivirus Integrata](#2-scansione-antivirus-integrata)  
   - [4.3 Interfaccia Utente (UI)](#3-interfaccia-utente-ui)  
   - [4.4 Logging e Notifiche](#4-logging-e-notifiche)  
5. [🚀 Guida all'Installazione](#-guida-allinstallazione)  
   - [5.1 Prerequisiti](#prerequisiti)  
6. [🛠️ Come Usare l'Applicazione](#%EF%B8%8F-come-usare-lapplicazione)  
7. [📚 Riferimenti e Approfondimenti](#-riferimenti-e-approfondimenti)  
8. [📞 Contatti](#-contatti)

&nbsp;
## 🧑‍💻 Chi Siamo

Siamo **Davide Bonsembiante**, **Lorenzo Gallizioli** e **Thomas Paganelli**, studenti universitari al **3° anno di Ingegneria Informatica** presso l'**Università degli Studi di Bergamo**. Questo progetto è stato sviluppato come progetto per il corso di **Ingegneria del Software**.


&nbsp;
## 📝 Descrizione del Progetto

Il progetto consiste in un'applicazione **client Java** progettata per connettersi a una VPN tramite il protocollo **WireGuard** e integrare un sistema di **scansione antivirus** che analizzi i file scaricati attraverso la rete. 

Con questo strumento, vogliamo offrire una soluzione che combini:

- **Privacy**: Una connessione sicura tramite **WireGuard**, un protocollo di VPN moderno e sicuro.
- **Sicurezza**: Un sistema di scansione antivirus per analizzare i file da malware che possono infettare i sistemi informatici utilizzando **ClamAV** e **VirusTotal**.
- **Open Source**: Una soluzione completamente open-source, liberamente utilizzabile da chiunque.

&nbsp;
## 🔑 Funzionalità Principali

### 1. Connessione VPN con WireGuard
L’applicazione permette la configurazione e la gestione di una connessione VPN attraverso WireGuard. Le funzionalità principali sono:
   - **Configurazione con chiavi**: l'utente può configurare la connessione tramite chiavi pubbliche e private.
   - **Caricamento file di configurazione**: supporto per file `.conf` di WireGuard, che semplifica il setup della connessione.

### 2. Scansione Antivirus Integrata
Per proteggere i file scaricati durante l’utilizzo della VPN, il client offre una doppia opzione per la scansione antivirus:
   - **Integrazione con ClamAV**: scansione antivirus open-source con ClamAV.
   - **Integrazione con VirusTotal API**: verifica degli hash dei file (MD5/SHA256) tramite chiamate all’API di VirusTotal, che permette di esaminare file sospetti senza inviarli completamente, riducendo così il rischio di trasmissioni non sicure.

### 3. Interfaccia Utente (UI)
L’applicazione presenta un’interfaccia **JavaFX** che consente all’utente di:
   - **Configurare la connessione VPN**: l'utente può facilmente configurare e gestire la connessione VPN direttamente dall'interfaccia grafica.
   - **Monitorare la connessione**: visualizzazione di informazioni in tempo reale sulla VPN, come la latenza, la velocità di connessione e i dettagli del traffico.
   - **Visualizzare i risultati delle scansioni antivirus**: l'interfaccia fornisce feedback chiari e immediati riguardo ai risultati delle scansioni antivirus, indicando se i file sono sicuri o contengono malware.

### 4. Logging e Notifiche
L’applicazione tiene traccia di tutte le operazioni e fornisce notifiche in tempo reale:
   - **Logging completo**: tutti gli eventi, comprese le scansioni antivirus e i risultati delle analisi, vengono registrati in un file di log per una tracciabilità completa delle operazioni.
   - **Notifiche di sicurezza**: notifiche automatiche vengono inviate all'utente se viene rilevato malware durante le scansioni antivirus.

&nbsp;
## 🚀 Guida all'Installazione

### Prerequisiti

> ⚠️ **Permessi Amministrativi**: Poiché l’applicazione interagisce con WireGuard, è necessario eseguire il programma con privilegi elevati (root/sudo) per gestire le connessioni di rete.

1. **ClamAV**: Necessario per la scansione antivirus tramite `clamscan` ([Segui la guida per installare clamAV sul tuo PC](https://github.com/LorenzoGallizioli/WireShield/blob/7e6f6c54f63fd79cc4b99bfd91c4ab223ffa6286/wireshield/bin/ClamAV.md))
.
2. **Java 11** o versione successiva: L’applicazione è sviluppata in Java e utilizza funzionalità correlate come JavaFX.
3. **API Key di VirusTotal** (opzionale): Per integrare le scansioni tramite l'API di VirusTotal, è necessaria una chiave API.

&nbsp;
## 🛠️ Come Usare l'Applicazione

### 1. Clona WireShield
`git clone https://github.com/LorenzoGallizioli/WireShield.git`

### 2. Verifica di aver seguito tutti i passaggi indicati nei [prerequisiti](#prerequisiti).

### 3. Avvia l'applicazione
> ⚠️ **Permessi Amministrativi**: Poiché l’applicazione interagisce con WireGuard, è necessario eseguire il programma con privilegi elevati (root/sudo) per gestire le connessioni di rete.
Al primo avvio non sarà presente nessun peer.

### 4. Carica un peer
Il peer deve essere caricato cliccando sul bottone '+' nella **Home** di WireShield e deve essere un file di configurazione wireguard (.conf).

### 4. Avvia la connessione VPN
Puoi avviare o interrompere la connessione VPN direttamente dalla scheda **Home** dell'interfaccia utente e controllare i log nella scehda **Logs**.

### 5. Esegui una scansione antivirus
Una volta stabilita la connessione VPN, puoi scansionare i file scaricati tramite **ClamAV** e **VirusTotal** semplicemente scaricando un file dal web e attendendo che WireShield effettui la scansione. Il sistema ti notificherà immediatamente in caso di rilevamento di malware e troverai il risultato della scansione, al termine della stessa, nella sezione **Antivirus**.

&nbsp;
## 📚 Riferimenti e Approfondimenti

- **WireGuard**: [Sito Ufficiale](https://www.wireguard.com/)
- **ClamAV**: [Sito Ufficiale](https://www.clamav.net/)
- **VirusTotal API**: [Documentazione API](https://developers.virustotal.com/)

&nbsp;
## 📞 Contatti

Per ulteriori informazioni o domande sul progetto, potete contattarci via email:

- **Davide Bonsembiante** - [d.bonsembiante@studenti.unibg.it](mailto:d.bonsembiante@studenti.unibg.it])
- **Lorenzo Gallizioli** - [l.gallizioli@studenti.unibg.it](mailto:l.gallizioli@studenti.unibg.it)
- **Thomas Paganelli** - [t.paganelli@studenti.unibg.it](mailto:t.paganelli@studenti.unibg.it)

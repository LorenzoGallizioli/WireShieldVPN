# **Organizzazione Progetto - Modello SCRUM**
## **Indice**
1. [**Sprint Planning**](#sprint-planning)
2. [**Backlog del Prodotto**](#backlog-del-prodotto)
3. [**Sprint n°1**](#sprint-n1--18112024---9122024)
   - [Ruoli del team](#ruoli-del-team)
   - [Sprint Overview](#sprint-overview)
4. [**Retrospettive**](#retrospettive)
5. [**Sprint n°1**](#sprint-n1--18112024---9122024)
   - [Week Scrum n°1](#1-week-scrum-19-novembre-2024)
   - [Week Scrum n°2](#2-week-scrum-26-novembre-2024)
6. [**Sprint n°2**](#sprint-n2--18112024---9122024)
   - [Week Scrum n°1](#1-week-scrum-03-dicembre-2024)

&nbsp;

## **Sprint Planning**
*Pianificazione generale delle attività.*

- **Sprint Totali**: ??

- **Durata di uno Sprint**: 2 settimane

- **Obiettivo Globale**: realizzare il progetto funzionante e curato nei minimi dettagli

&nbsp;

## **Backlog del Prodotto**
*Lista prioritaria delle funzionalità da implementare.*

| **ID** | **Titolo**             | **Descrizione**                                                                 | **Priorità** |
|--------|------------------------|-------------------------------------------------------------------------------|--------------|
| 1      | Gestore VPN            | Implementazione della comunicazione con servizio WireGuard                    | Alta         |
| 2      | Gestione dei peer      | Gestione dell'archivio dei peer WireGuard e la modifica degli stessi           | Media        |
| 3      | Gestione Antivirus     | Implementazione del controllo dei file scaricati mediante ClamAV e poi VirusTotal come ulteriore controllo | Alta        |
| 4      | Implementazione UI/UX  | Implementazione di una dashboard funzionante che permetta l'utilizzo user-friendly del sistema e creazione di una sezione statistica per visualizzare i dettagli della connessione | Media        |
| 5      | Controllo della cartella Download | Implementazione di un sistema che permetta di effettuare un controllo continuo e costante della cartella Download | Media         |


&nbsp;

---
# Sprint n°1: &nbsp;&nbsp; 19/11/2024 - 03/12/2024
## **Ruoli del Team**

- **Product Owner**: 
  - Lorenzo Gallizioli
  - Davide Bonsembiante
  - Thomas Paganelli 

- **Scrum Master**: Lorenzo Gallizioli - Facilitatore dei processi SCRUM.

- **Team di Sviluppo**: 
  - Lorenzo Gallizioli
  - Davide Bonsembiante
  - Thomas Paganelli

- **Week Scrum**: ogni martedì, orario da definirsi sul gruppo whatsapp.

&nbsp;

# 1° Week Scrum 19 Novembre 2024

| **Titolo**                      | **Compiti**                                                                                       | **Sviluppatore**         |
|----------------------------------|--------------------------------------------------------------------------------------------------|--------------------------|
| Diagramma dei casi d'uso         | Implementazione del diagramma dei casi d'uso                                                     | Lorenzo Gallizioli       |
| Diagramma delle macchine a stati| Implementazione del diagramma delle macchine a stati                                              | Lorenzo Gallizioli       |
| Diagramma di sequenza           | Implementazione del diagramma di sequenza                                                       | Davide Bonsembiante      |
| Linee Guida -> Documento Ufficiale | Conversione e modifica del documento "Linee Guida" in Documento Ufficiale del progetto con relative aggiunte e approfondimenti per ogni capitolo | Davide Bonsembiante      |
| Diagramma delle classi          | Implementazione del diagramma delle classi                                                        | Thomas Paganelli         |
| Diagramma delle classi -> Struttura del progetto   | Conversione del diagramma delle classi in codice per implementare la struttura del progetto       | Thomas Paganelli         |

&nbsp;



# 2° Week Scrum 26 Novembre 2024

| **Titolo**                      | **Compiti**                                                                                       | **Sviluppatore**         |
|----------------------------------|--------------------------------------------------------------------------------------------------|--------------------------|
| Diagramma delle classi -> Struttura del progetto   | Conversione del diagramma delle classi in codice per implementare la struttura del progetto       | Thomas Paganelli         |
| Diagramma di comunicazione   | Implementazione del diagramma di comunicazione       | Lorenzo Gallizioli         |
| Diagramma di attività   | Implementazione del diagramma di attività      | Davide Bonsembiante      |
| Aggiornamento Sprint Backlog   | Aggiornamento dello Sprint Backlog con le novità nate dal Week Scrum      | Davide Bonsembiante         |



&nbsp;
---
# Sprint n°2: &nbsp;&nbsp; 03/12/2024 - 17/12/2024
## **Ruoli del Team**

- **Product Owner**: 
  - Lorenzo Gallizioli
  - Davide Bonsembiante
  - Thomas Paganelli 

- **Scrum Master**: Thomas Paganelli - Facilitatore dei processi SCRUM.

- **Team di Sviluppo**: 
  - Lorenzo Gallizioli
  - Davide Bonsembiante
  - Thomas Paganelli

- **Week Scrum**: ogni martedì, orario da definirsi sul gruppo whatsapp.

&nbsp;

# 1° Week Scrum 03 Dicembre 2024
| **Titolo**                      | **Compiti**                                                                                       | **Sviluppatore**         |
|----------------------------------|--------------------------------------------------------------------------------------------------|--------------------------|
|  Diagramma delle classi -> Struttura del progetto  | Conversione del diagramma delle classi in codice per implementare la struttura del progetto       | Thomas Paganelli         |
| Aggiornamento diagramma di comunicazione e Controllo degli altri diagrammi   | Aggiornamento del diagramma di comunicazione + controllare se bisogna aggiornare anche gli altri diagrammi (ovvero aggiungere il System Orchestrator e modificare il flusso del diagramma)   | Lorenzo Gallizioli
| Convertire diagramma di attività e diagramma di sequenza in Papyrus  | Convertire il diagramma di attività e diagramma di sequenza, creandolo su papyrus   | Lorenzo Gallizioli
| Aggiornamento diagramma di attività + diagramma di sequenza   | Aggiornamento del diagramma di attività + diagramma di sequenza aggiungendo il System Orchestretor      | Davide Bonsembiante         |
| Aggiornamento Sprint Backlog   | Aggiornamento dello Sprint Backlog con le novità nate dal Week Scrum      | Davide Bonsembiante         |

&nbsp;

# 2° Week Scrum 10 Dicembre 2024
| **Titolo**                      | **Compiti**                                                                                       | **Sviluppatore**         |
|----------------------------------|--------------------------------------------------------------------------------------------------|--------------------------|
|  Implementazione Download Manager | Implementazione del codice per monitorare la cartella download e modifica della classe System Orchestrator per gestirlo  | Davide Bonsembiante |
| Implementazione AntivirusManager, CLAMAV e Virustotal| Implementazione del codice per gestire gli antivirus CLAMAV e Virustotal e implementazione del codice per quest'ultimi, controllando i file scaricati. Infine modifica della classe System Orchestrator per gestirlo | Davide Bonsembiante
| Implementazione Gestione Peer e PeerManager | Implementazione del codice per monitorare e gestire i peer di comunicazione | Thomas Paganelli
| Implementazione File Manager | Implementazione del codice per gestire i file | Thomas Paganelli
| Aggiornamento dei diagrammi | Aggiornamento dei diagrammi con le nuove assunzioni e i cambiamenti decisi durante lo sviluppo del progetto | Thomas Paganelli |
| Implementazione gestione Wireguard | Implementazione del codice per avviare Wireguard e gestire le connessioni VPN | Lorenzo Gallizioli |
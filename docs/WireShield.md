# WireShield

## **Indice**

1. [**🛠 Software Engineering Management**](#1--software-engineering-management)
   - [1.1 Project Plan](#11-project-plan)

2. [**🔄 Software Life Cycle**](#2--software-life-cycle)
   - [Tipo di Processo di Sviluppo](#21-tipo-di-processo-di-sviluppo)
   - [Sprint - Metodologia Scrum](#22-sprint---metodologia-scrum)
   - [Programmazione a coppie](#23-programmazione-a-coppie)

3. [**⚙️ Configuration Management**](#3-️-configuration-management)
   - [GitHub](#31-github)

4. [**👥 People Management**](#4--people-management)
   - [Organizzazione del Lavoro](#41-organizzazione-del-lavoro)

5. [**🔍 Software Quality**](#5--software-quality)
   - [Qualità Fondamentali per il Progetto](#51-qualità-fondamentali-per-il-progetto)

6. [**📋 Requirements Engineering**](#6--requirements-engineering)
   - [Elicitation dei Requisiti](#61-elicitation-dei-requisiti)
   - [Specifica dei Requisiti (IEEE 830)](#62-specifica-dei-requisiti-ieee-830)

7. [**🖋️ Modeling**](#7-️-modeling)
    - [Diagrammi UML](#71-diagrammi-uml)

8. [**🏛️ Software Architecture**](#8-️-software-architecture)

9. [**🖌️ Software Design**](#9-️-software-design)

10. [**🔍 Software Testing**](#10--software-testing)

11. [**🔧 Software Maintenance**](#11--software-maintenance)


&nbsp;

&nbsp;

## 1. 🛠 **Software Engineering Management**

### 1.1 **Project Plan**
Il piano del progetto sarà descritto in questa sezione, evidenziando i 14 punti come indicato in sezione 2.1 del libro. Questo piano sarà sottoposto all'approvazione del professore prima dell'inizio dello sviluppo.

- [**Project Plan**](../ProjectPlan.md)

&nbsp;
## 2. 🔄 **Software Life Cycle**

### 2.1 **Tipo di Processo di Sviluppo**
Il progetto adotta un **processo di sviluppo Iterativo e Incrementale** per garantire una progressione graduale e continua delle funzionalità. Questo approccio prevede lo sviluppo in cicli (sprint), permettendo di incorporare frequenti feedback e adattamenti lungo il percorso.

Questa struttura ci consente di adattarci ai cambiamenti e di migliorare continuamente le funzionalità del software, offrendo la massima flessibilità.

### 2.2 **Sprint - Metodologia Scrum**
Lo sviluppo seguirà la metodologia **Scrum**, strutturato in **sprint settimanali**, opportunamente adattato per soddisfare le esigenze specifiche del team. 
Ogni sprint avrà obiettivi e task specifici, pianificati e rivisti ad ogni ciclo, per rispondere agilmente a nuovi requisiti e migliorare progressivamente il software.

-  **Analisi dei Bisogni degli Utenti Finali**  
   L'obiettivo principale è garantire una connessione VPN sicura e una protezione efficace contro malware. Questo è stato ottenuto tramite un'approfondita analisi delle esigenze tipiche di un utente che richiede:
   - **Sicurezza**: protezione della connessione da minacce esterne e anonimato durante la navigazione.
   - **Affidabilità**: una connessione stabile e costante tramite la tecnologia **WireGuard**.
   - **Facilità d'Uso**: interfacce intuitive per configurare la connessione VPN e gestire i parametri in modo rapido.

-  **Breve panoramica dello Sprint Backlog**

   - **Sprint Totali**: Definiti in base alla complessità e agli obiettivi del progetto.
   - **Durata di uno Sprint**: 2 settimane.
   - **Dettaglio degli Obiettivi**: Per ogni sprint sono indicate le attività principali, come la creazione di diagrammi UML, lo sviluppo delle funzionalità, e l'implementazione dei test.
   - **Ruoli e responsabilità**: Specifica di chi si occupa di cosa (Scrum Master, Product Owner, Development Team).

   Le informazioni dettagliate relative agli sprint sono organizzate nel file [**Sprint Backlog.md**](SprintBacklog.md#), che rappresenta il punto di riferimento per la pianificazione e la gestione iterativa del progetto. 
   Questo documento è accessibile dalla **documentazione principale del progetto** e fornisce un quadro completo delle attività svolte durante ciascun ciclo.

### 2.3 Programmazione a coppie

La **programmazione a coppie** (Pair Programming) sarà integrata nel processo di sviluppo come parte integrante del framework SCRUM. 
La programmazione a coppie sarà applicata principalmente nelle attività legate alla riscrittura del codice, in corrispondenza di bug o funzioni complesse.

&nbsp;
## 3. ⚙️ **Configuration Management**

### 3.1 **GitHub**

- **Versionamento del codice**: Utilizzo di GitHub per il controllo versione, con comandi come:
  - `git add .` per aggiungere modifiche.
  - `git commit -m "messaggio"` per registrare le modifiche.
  - `git push origin nome-ramo` per caricare le modifiche nel repository remoto.

- **Branching**: Saranno utilizzati **branch** per ogni nuova funzionalità o correzione di bug. Il branch principale (**main**) rappresenterà la versione stabile del progetto, mentre le funzionalità in sviluppo saranno contenute in branch separati. 
In questo modo, si garantisce che lo sviluppo non interferisca con la stabilità del progetto principale.
  - `git checkout -b nome-ramo`.

- **Pull Requests (PR)**: Le modifiche al codice saranno sottoposte a **pull request**. Ogni modifica verrà revisionata da almeno un altro membro del team per garantirne la qualità e il rispetto degli **standard di codifica**.

- **Code Review**: Ogni PR viene esaminata da un membro del team per verificarne qualità e funzionalità.

- **Issue Tracking**: Le **issue** saranno utilizzate per tracciare e gestire i bug, le richieste di funzionalità e le attività del progetto. Ogni issue sarà associata a un membro del team, con una priorità definita e una descrizione chiara dell'attività da svolgere. In questo modo, si favorisce la trasparenza nella gestione del lavoro e si permette di monitorare lo stato di avanzamento delle attività.

- **Kanban Board**: Per la gestione delle attività e il monitoraggio dello stato del progetto, verrà utilizzato un **Kanban Board**. Ogni task, rappresentato da un'issue, sarà spostato tra le colonne del board in base al suo stato di avanzamento (ad esempio: **To Do**, **In Progress**, **Code Review**, **Done**). Questo strumento permetterà al team di avere sempre una visione chiara e aggiornata dello stato del progetto, facilitando la collaborazione e l'assegnazione dei task.



- **Statistiche del Repository GitHub**

  Durante il progetto, abbiamo raccolto alcune statistiche chiave che riflettono il progresso e la gestione delle attività:

      Numero di commit totali: ??
      Branch creati: ??
      Pull Request aperte e completate: ??
      Issues create: ??
      Issues risolte: ??
      Code Review effettuate: ??

  Queste metriche dimostrano l'impegno nella gestione strutturata e collaborativa del progetto, garantendo trasparenza e continuità nello sviluppo.


&nbsp;
## 4. 👥 **People Management**

### 4.1 **Organizzazione del Lavoro**

### **Personale**

Il progetto **WireShield** sarà gestito da un team di tre membri, che si occuperanno dell'intero ciclo di vita del progetto, dalle fasi di sviluppo alla gestione dei test e della documentazione. I membri del team collaboreranno in modo sinergico per garantire il successo del progetto. Di seguito sono riportati i dettagli dei membri del team:

- **Davide Bonsembiante** – [GitHub](https://github.com/bnsdavide03)
- **Lorenzo Gallizioli** – [GitHub](https://github.com/LorenzoGallizioli)
- **Thomas Paganelli** – [GitHub](https://github.com/paganello)

Il lavoro sarà distribuito tra i membri del team, con monitoraggio del progresso tramite la **Kanban board**.

La struttura di base del nostro team **Scrum** è la seguente:

- **Scrum Master a Rotazione**
   Il ruolo di **Scrum Master** sarà ricoperto a rotazione da ciascun membro del team. Questo approccio consente a tutti di:
   - Sviluppare competenze nella gestione e facilitazione del processo **SCRUM**.
   - Acquisire una comprensione completa delle dinamiche del team.

- **Product Owner Condiviso**
   Il ruolo di **Product Owner** sarà condiviso tra tutti i membri del team. Ogni membro avrà l'opportunità di:
   - Partecipare attivamente alla definizione delle funzionalità da sviluppare.
   - Contribuire alla prioritizzazione degli obiettivi.
   - Promuovere un approccio collettivo nella gestione del backlog e delle decisioni strategiche.

- **Team di Sviluppo**
   Tutti i membri del team avranno un ruolo attivo nello sviluppo del codice. Non sono previste divisioni rigide nei ruoli di sviluppo, favorendo:
   - Un approccio collaborativo e dinamico.
   - Una maggiore flessibilità nell'assegnazione dei task.

- **Tester**
   I membri del team svolgeranno anche il ruolo di **tester**, eseguendo:
   - Test funzionali, sia manuali che automatizzati.

- **Ruolo di Cliente Interno**
   Poiché il progetto non prevede la presenza di clienti esterni, i membri del team assumeranno il ruolo di **clienti interni**, svolgendo attività come:
   - Testare il prodotto dal punto di vista degli utenti finali.
   - Fornire feedback continuo per migliorare funzionalità e usabilità.
   - Assicurarsi che il prodotto risponda alle aspettative e alle necessità dell'obiettivo principale previsto.

&nbsp;
## 5. 🔍 **Software Quality**

### 5.1 **Qualità Fondamentali per il Progetto**
Ispirandoci alla norma **ISO/IEC 9126** per la qualità del software, abbiamo elaborato le seguenti qualità utili per il successo del nostro progetto:

- **Affidabilità**: Garantire la stabilità dell'applicazione, minimizzando il rischio di crash o comportamenti imprevisti. Un software affidabile è essenziale per assicurare la continuità del servizio e la soddisfazione dell'utente.
  
- **Usabilità**: Creare un'interfaccia utente chiara, intuitiva e facilmente navigabile, in modo da offrire un'esperienza utente fluida. La qualità dell'usabilità è cruciale per assicurare che gli utenti possano utilizzare il sistema in modo semplice e immediato.

- **Performance**: Ottimizzare l'efficienza del sistema per garantire che l'applicazione funzioni in modo rapido e senza rallentamenti significativi. Il nostro obiettivo è realizzare un sistema leggero e performante, in linea con la filosofia del protocollo WireGuard.

- **Manutenibilità**: Strutturare il codice in modo chiaro e modulare, facilitando interventi di modifica o correzione nel tempo. Una buona manutenibilità permette di evolvere il software in modo agile e senza complicazioni.

&nbsp;
## 6. 📋 **Requirements Engineering**

### 6.1 **Elicitation dei Requisiti**

I requisiti sono stati raccolti attraverso un processo strutturato di **elicitation** che ha incluso:

1. **Analisi dei Bisogni degli Utenti Finali**  
   L'obiettivo principale è garantire una connessione VPN sicura e una protezione efficace contro malware. Questo è stato ottenuto tramite un'approfondita analisi delle esigenze tipiche di un utente che richiede:
   - **Sicurezza**: protezione della connessione da minacce esterne e anonimato durante la navigazione.
   - **Affidabilità**: una connessione stabile e costante tramite la tecnologia **WireGuard**.
   - **Facilità d'Uso**: interfacce intuitive per configurare la connessione VPN e gestire i parametri in modo rapido.

2. **Ricerca di Dominio**  
   È stata condotta un’analisi del dominio delle VPN per identificare funzionalità essenziali, tra cui:
   - **Gestione Peer**: possibilità di configurare e gestire i peer VPN.
   - **Monitoraggio Connessione**: strumenti per verificare lo stato della connessione in tempo reale.
   - **Protezione File**: scansione e analisi antivirus dei file scaricati.

3. **Feedback Iterativo**  
   Non avendo clienti esterni, il team ha ricoperto il ruolo di utenti finali, simulando scenari reali di utilizzo per identificare i bisogni più rilevanti. Il feedback raccolto è stato utilizzato per perfezionare i requisiti e definire le priorità.

4. **Analisi dei Rischi**  
   Sono stati considerati i rischi legati a:
   - **Minacce Malware**: implementando strumenti come ClamAV e VirusTotal per garantire protezione proattiva.
   - **Scarsa Usabilità**: concentrandosi sull’ottimizzazione della dashboard e sull’accessibilità delle funzionalità principali.

### 6.2 **Specifica dei Requisiti (IEEE 830)**
La documentazione dei requisiti segue lo standard **IEEE 830**, che fornisce una guida dettagliata per la specifica e la documentazione dei requisiti di sistema. Questo standard aiuta a definire in modo chiaro e strutturato sia i requisiti funzionali, che descrivono le azioni e le operazioni che il sistema deve eseguire, sia i requisiti non funzionali, che stabiliscono le caratteristiche di qualità del sistema, come le prestazioni, la sicurezza e la manutenibilità. Adottando IEEE 830, il nostro obiettivo è garantire che i requisiti siano ben definiti, comprensibili e misurabili, fornendo così una base solida per tutte le fasi del ciclo di vita del software, dallo sviluppo al testing.

&nbsp;
## 7. 🖋️ **Modeling**

### 7.1 **Diagrammi UML**
I seguenti diagrammi UML sono stati utilizzati per progettare il sistema:

#### ***Diagramma dei Casi d’Uso***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/UseCaseDiagram/UseCaseWireShield.png)

#### ***Diagramma delle Classi***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/ClassDiagram/ClassDiagram.png)

#### ***Diagramma delle Macchine a Stati***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/StateMachineDiagram/StateMachineWireShield.png)

#### ***Diagramma di Sequenza***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/sequenceDiagram/sequenceDiagram.png)

#### ***Diagramma di Comunicazione***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/CommunicationDiagram/CommunicationWireShield.png)

#### ***Diagramma di Attività***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/activityDiagram/activityDiagram.png)

#### ***Diagramma dei Componenti***
---
![image](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/ComponentDiagram/ComponentDiagram.png)


&nbsp;
## 8. 🏛️ **Software Architecture**
      
    DEVE contenere la descrizione dell’architettura con almeno un paio di architectural views (per differenti punti di vista)
    DOVREBBE avere almeno una vista con connettori e componenti con la descrizione dello stile architetturale (11.4)
    DEVE utilizzare almeno una libreria esterna con maven.
    Ad esempio l’uso di log4j è molto consigliata.


&nbsp;
## 9. 🖌️ **Software Design**
### Report sui Design Pattern Utilizzati nelle Classi in Wireshield

Diversi design pattern sono stati adottati per migliorare la struttura e la manutenibilità del codice. Di seguito sono descritti i principali pattern utilizzati, le loro applicazioni e i benefici che apportano al progetto.

#### Singleton Pattern

Il Singleton Pattern viene utilizzato per garantire che una classe abbia una sola istanza e per fornire un punto di accesso globale a essa. Questo approccio è adottato in tutte le classi, tranne nella classe `Peer`. Questa decisione è stata presa per una ragione funzionale e dettata dalle caratterische specifiche del nostro sistema= abbiamo spesso avuto la necessità di implmentare classi che fornissero un servizo piu che uno scopo informativo, ad eccezione appunto della classe `Peer`.
In alcuni scenari, il Singleton Pattern può essere combinato con altri pattern per potenziare la funzionalità.

#### Factory Pattern

Il Factory Pattern fornisce un'interfaccia per creare oggetti, delegando alle sottoclassi la logica di definizione del tipo specifico di oggetto da istanziare. In Wireshield, per esempio, la classe `AntivirusManager` sfrutta questo pattern per creare istanze di `ClamAV` o `VirusTotal` in base a condizioni operative, come i parametri di configurazione.

Questo pattern centralizza la logica di creazione degli oggetti, migliorando la flessibilità e la manutenibilità del codice. nel nostro caso viene utilizzato insieme al Singleton Pattern per garantire che ogni tipo di antivirus abbia un'unica istanza gestita centralmente.

#### Strategy Pattern

Il Strategy Pattern permette di definire una famiglia di algoritmi, incapsularli e renderli intercambiabili. La classe `AntivirusManager`, implementando le classi `ClamAV` e `VirusTotal` con interfaccia comune `AVInterface`, consentone di selezionare dinamicamente il metodo di scansione antivirus più appropriato.

Grazie a questo pattern, la logica di selezione dell'antivirus è separata dalla logica di esecuzione, rendendo il codice più modulare e facilmente estendibile. Questo approccio garantisce flessibilità nella gestione delle operazioni di scansione.

#### Template Method Pattern

Il Template Method Pattern definisce la struttura di un algoritmo nella superclasse, lasciando alle sottoclassi l'implementazione di dettagli specifici. In Wireshield, per esempio, la classe `ScanReport` utilizza questo pattern per strutturare il processo di generazione dei report di scansione.

La superclasse `ScanReport` stabilisce i passi comuni per la creazione di un report, mentre le sottoclassi possono personalizzare aspetti come il formato o il contenuto del documento. Questo pattern promuove il riutilizzo del codice e garantisce coerenza nella struttura degli antivirus.

      
    DEVE contenere una descrizione del design (mediante i diagrammi UML va bene)
    POTREBBE contenere un calcolo di complessità (ad esempio con McCabe) di una piccola parte
    DOVREBBE contenere qualche misurazione del codice, (con qualche metrica che abbiamo visto).
    Alcuni tools che vedremo a lezione: stanide, jdepend, struture101, sonarlint, PMD ...
    DEVE applicare un paio di design pattern visti a lezione
  

&nbsp;
## 10. 🔍 **Software Testing**
### Metodologia di Testing del Software

Abbiamo implementato una strategia di testing mirata a garantire un’elevata qualità del codice e una copertura dei test il più ampia possibile. Di seguito, vengono descritte le principali attività svolte:

#### Sviluppo del Codice di Test

- Per ciascun metodo implementato nel progetto, è stato creato un corrispondente codice di test, ad eccezione di alcuni metodi contenenti thread. Questi ultimi si sono rivelati difficili da testare singolarmente senza eseguire l’intero codice del programma, rendendo impraticabile una verifica isolata.

#### Strumenti Utilizzati

- **JUnit**: È stato utilizzato come framework principale per scrivere e organizzare i test unitari.
- **EclEmma**: Lo strumento è stato impiegato per misurare la copertura del codice durante l’esecuzione dei test, con l’obiettivo di raggiungere una copertura del 100%.
- **SonarLint**: Questo strumento è stato utilizzato per identificare i "code smells" all’interno del codice. Le problematiche rilevate sono state analizzate in modo granulare e risolte manualmente.

#### Descrizione dei Casi di Test

- Ogni caso di test è stato accompagnato da una breve descrizione per migliorarne la comprensibilità, fornita attraverso commenti Javadoc o direttamente nel nome del metodo di test. Ciò ha facilitato la tracciabilità e la comprensione dei test stessi.

#### Test Avanzati con Mockito

- Per garantire una verifica più approfondita di alcuni comportamenti, è stato utilizzato **Mockito**, un framework per la simulazione di componenti e dipendenze. Questo ha permesso di isolare i metodi testati e di verificare scenari specifici.

L’approccio adottato ha permesso di mantenere un elevato livello di affidabilità del software, assicurandosi che il codice fosse robusto, privo di anomalie evidenti e ben documentato.

&nbsp;
## 11. 🔧 **Software Maintenance**
### Manutenzione del Progetto

Per garantire la longevità, l'efficienza e la qualità del progetto, intendiamo manutenere il codice mantenendo un monitoraggio continuo per l'individuazione di bug. Continueremo ad utilizzare GitHub per  versioning e segnalazioni basate sulla priorità, in modo da risolvere tempestivamente i problemi più critici. L'obiettivo è realizzare aggiornamenti costanti che introducano maggiore stabilità, una grafica migliorata e funzionalità sempre più avanzate.

Vogliamo adattare il codice alle continue mutazioni delle esigenze degli utenti, raccogliendo feedback e implementando estensioni mirate, con un'attenzione particolare alla sicurezza informatica. Inoltre, applicheremo una manutenzione preventiva, che consisterà in verifiche complete del codice per assicurarci che, durante le continue trasformazioni, non siano stati introdotti difetti. 

Con questa strategia di manutenzione, intendiamo garantire un software affidabile, aggiornato e capace di rispondere alle sfide future, mantenendo al contempo un alto livello di soddisfazione degli utenti.
&nbsp;


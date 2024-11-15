# **Project Plan**

In questo documento illustriamo il **Project Plan** che guiderà lo sviluppo del progetto. Le linee guida fornite serviranno a chiarire il nostro approccio metodologico e le strategie di implementazione, garantendo coerenza e organizzazione durante tutte le fasi del lavoro.


## **1. Introduzione**

*Il progetto **"WireShield"** si propone di sviluppare una soluzione innovativa per l’accesso remoto sicuro, combinando un client VPN basato sul protocollo **WireGuard** con funzionalità avanzate di scansione antivirus e analisi automatica tramite **VirusTotal** e **ClamAV**. L’obiettivo principale è offrire un client VPN che non solo assicuri la riservatezza dei dati e la gestione efficiente delle connessioni, ma che integri anche strumenti per la protezione attiva contro minacce come malware e virus.*

*L’integrazione di **ClamAV** permetterà di eseguire scansioni antivirus a livello locale, mentre l’API di **VirusTotal** offrirà un ulteriore livello di analisi per i file sospetti, riducendo il rischio di falsi positivi e garantendo un ambiente di lavoro remoto più sicuro e resiliente.*

*L’applicazione fornirà agli utenti un’interfaccia grafica (UI) intuitiva per gestire la connessione VPN, visualizzare statistiche in tempo reale e accedere a report dettagliati sull’analisi dei file. Saranno incluse notifiche tempestive per avvisare l’utente in caso di rilevamento di minacce informatiche.*

*Prima di avviare la pianificazione, è stata condotta un’analisi di fattibilità per valutare l’integrazione delle componenti VPN, ClamAV e VirusTotal, verificando la compatibilità tecnica con l’uso del linguaggio **Java**. Questa analisi ha confermato la realizzabilità del progetto, evidenziando i principali rischi e proponendo soluzioni pratiche.*

- [Analisi di fattibilità](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/AnalisiFattibilit%C3%A0.md)


## **2. Modello di Processo**

Per lo sviluppo del progetto, abbiamo adottato un approccio **AGILE** basato sul framework **SCRUM**, opportunamente adattato per soddisfare le esigenze specifiche del team. Questo modello iterativo e incrementale ci consente di mantenere flessibilità durante il processo di sviluppo, rilasciando progressivamente nuove funzionalità e incorporando feedback in tempo reale.

### Modello SCRUM

- **Durata degli sprint**:  
  *Gli sprint avranno una durata di **3 settimane**, un compromesso ideale per bilanciare la produttività con gli impegni accademici del team. Questo intervallo di tempo permette di completare obiettivi significativi senza compromettere la qualità del lavoro.*

- **Riunioni settimanali**:  
  *È prevista una **riunione settimanale di sincronizzazione** della durata di circa un’ora. Questi incontri saranno dedicati a:*
  - *Monitorare l’avanzamento del lavoro.*
  - *Identificare e risolvere eventuali impedimenti.*  
  - *Pianificare e riorganizzare i task in base alle priorità emergenti.*

- **Ruoli del team**:  
  - **Scrum Master**: *Supervisione del processo e facilitazione delle riunioni.* 
  - **Product Owner**: *Definizione delle priorità e gestione del Product Backlog.*
  - **Team di sviluppo**: *Implementazione delle funzionalità e gestione tecnica.*

### Programmazione a coppie

La **programmazione a coppie** (Pair Programming) sarà integrata nel processo di sviluppo come parte integrante del framework SCRUM. Questo approccio verrà utilizzato per:
- *Ridurre gli errori attraverso revisioni in tempo reale.*  
- *Favorire una maggiore condivisione della conoscenza tra i membri del team.*  
- *Migliorare la qualità complessiva del codice.*

La programmazione a coppie sarà applicata principalmente nelle seguenti attività: 
- *Sviluppo di moduli complessi* 
- *Revisione e debugging del codice*


## 3. Organizzazione del progetto

La struttura del team SCRUM è stata pensata per promuovere la collaborazione e l'efficienza, consentendo a ciascun membro di acquisire competenze in diverse aree del processo di sviluppo.

### Ruoli e Responsabilità

- **Scrum Master a rotazione**: *Il ruolo di Scrum Master sarà ricoperto a rotazione da ciascun membro del team. Questo approccio consente a tutti i membri di sviluppare competenze nella gestione e facilitazione del processo SCRUM, oltre a garantire che tutti abbiano una comprensione completa delle dinamiche del team.*

- **Product Owner condiviso**: *Il ruolo di Product Owner non sarà assegnato a una sola persona, ma sarà condiviso tra tutti i membri del team. Ogni membro avrà l'opportunità di partecipare attivamente alla definizione delle funzionalità da sviluppare e alla prioritizzazione degli obiettivi, in modo da favorire un approccio collettivo nella gestione del backlog e delle decisioni strategiche.*

- **Team di sviluppo**: *Ogni membro del team avrà un ruolo attivo nello sviluppo del codice. Non ci sono divisioni rigide nei ruoli di sviluppo, favorendo così un approccio collaborativo e dinamico.*

- **Tester**: *I membri del team svolgeranno anche il ruolo di tester, eseguendo test funzionali per garantire la qualità del software. Saranno previsti test sia manuali che automatizzati, con particolare attenzione alla scansione antivirus e all’analisi dei file.*

Poiché il progetto non prevede la presenza di clienti esterni, i membri del team assumeranno il ruolo di 'clienti', testando il prodotto e valutando le funzionalità come se fossero utenti finali. In questo modo, si garantirà un feedback continuo e un miglioramento costante delle funzionalità, assicurando che il prodotto risponda alle aspettative e alle necessità degli utenti.


## 4. Standard, linee guida, procedure

Ispirandoci alla norma **ISO/IEC 9126** per la qualità del software, abbiamo elaborato le seguenti qualità utili per il successo del nostro progetto:

- **Affidabilità**: *Garantire la stabilità dell'applicazione, minimizzando il rischio di crash o comportamenti imprevisti. Un software affidabile è essenziale per assicurare la continuità del servizio e la soddisfazione dell'utente.*
  
- **Usabilità**: *Creare un'interfaccia utente chiara, intuitiva e facilmente navigabile, in modo da offrire un'esperienza utente fluida e senza frizioni. La qualità dell'usabilità è cruciale per assicurare che gli utenti possano utilizzare il sistema in modo semplice e immediato.*

- **Performance**: *Ottimizzare l'efficienza del sistema per garantire che l'applicazione funzioni in modo rapido e senza rallentamenti significativi. Il nostro obiettivo è realizzare un sistema leggero e performante, in linea con la filosofia del protocollo WireGuard.*

- **Manutenibilità**: *Strutturare il codice in modo chiaro e modulare, facilitando interventi di modifica o correzione nel tempo. Una buona manutenibilità permette di evolvere il software in modo agile e senza complicazioni.*

La documentazione dei requisiti segue lo standard **IEEE 830**, che fornisce una guida dettagliata per la specifica e la documentazione dei requisiti di sistema. Questo standard aiuta a definire in modo chiaro e strutturato sia i requisiti funzionali, che descrivono le azioni e le operazioni che il sistema deve eseguire, sia i requisiti non funzionali, che stabiliscono le caratteristiche di qualità del sistema, come le prestazioni, la sicurezza e la manutenibilità. Adottando IEEE 830, il nostro obiettivo è garantire che i requisiti siano ben definiti, comprensibili e misurabili, fornendo così una base solida per tutte le fasi del ciclo di vita del software, dallo sviluppo al testing.


## 5. Attività di gestione

Per garantire un lavoro completo e dettagliato, il team si impegna costantemente nella creazione di nuova documentazione per le funzionalità aggiunte nelle diverse fasi di sviluppo, senza trascurare l'aggiornamento continuo della documentazione già esistente. Questo approccio assicura che ogni aspetto del progetto sia ben documentato e facilmente comprensibile, facilitando sia lo sviluppo che la manutenzione a lungo termine del software.


## 6. Rischi

Sono stati individuati alcuni rischi, principalmente di natura tecnica, che potrebbero influenzare lo sviluppo del progetto. Questi rischi sono stati analizzati in dettaglio nella [seguente analisi di fattibilità](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/AnalisiFattibilit%C3%A0.md), che fornisce una panoramica completa delle possibili problematiche e delle relative soluzioni proposte per mitigare tali rischi.

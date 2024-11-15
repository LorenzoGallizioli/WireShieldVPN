# Project Plan
In questo documento spieghiamo il ProjectPlan che intenderemo utilizzare  nello sviluppo del progetto, le linee guida qui fornite saranno utili per interprtare il nostro metodo di lavoro e di implementazione.

### Introduzione
Il progetto "WireShield" mira a sviluppare una soluzione di accesso remoto sicuro, combinando un client VPN basato su WireGuard con funzionalità avanzate di scansione antivirus e analisi automatica tramite VirusTotal e ClamAV. L'obiettivo principale è offrire agli utenti un client VPN che non solo gestisca la connessione e garantisca la riservatezza dei dati mediante protocollo WireGuard, ma che includa anche una protezione attiva contro le minacce informatiche, come malware e virus.

L'integrazione di ClamAV permette di eseguire scansioni antivirus locali, mentre l'API di VirusTotal consentirà un'ulteriore analisi dei file sospetti per l'identificazione dei falsi positivi. Questa combinazione crea un ambiente di lavoro remoto sicuro e resiliente, rispondendo alle crescenti esigenze di sicurezza delle organizzazioni moderne.

L'obbiettivo del progetto sara' mettere a disposizione degli utenti una UI che permetta la gestione della connessione VPN con relative statistiche, oltre che un report dettagliato basato sull'analisi dei file e notifiche in tempo reale.

Prima di procedere con la pianificazione, abbiamo condotto un'analisi di fattibilità per valutare la possibilità di integrazione delle componenti VPN, ClamAV e VirusTotal, modellando i processi mediante linguaggio Java. Questa analisi ha considerato aspetti tecnici, confermando la realizzabilità del progetto e identificando i principali rischi e le soluzioni potenziali.

- [Analaisi di fattibilita'](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/AnalisiFattibilit%C3%A0.md)

### Modello di processo
Per lo sviluppo del progetto è stato scelto un approccio AGILE, basato sul framework SCRUM, ma adattato per rispondere alle esigenze specifiche del team. Il modello di sviluppo scelto è iterativo e incrementale, garantendo un processo flessibile e la possibilità di rilasciare progressivamente nuove funzionalità.

Dettagli del Processo:
- **Durata degli sprint**: Gli sprint avranno una durata di 3 settimane, un compromesso ideale per gestire il lavoro senza compromettere la qualità, tenendo conto degli impegni dei membri del team.
- **Riunioni settimanali**: È prevista una riunione di sincronizzazione settimanale, della durata di circa un'ora, per monitorare l'avanzamento del lavoro e risolvere eventuali impedimenti.

Nota: analizzare la possibilita' di implementazione della programmazione a coppie nel processo di sviluppo codice, da affiancare al metodo base SCRUM.

### Organizzazione del progetto

Struttara base del team SCRUM:
- **Scrum Master a rotazione**: Il ruolo di Scrum Master sarà ricoperto a rotazione da ogni membro del team, permettendo a tutti di acquisire competenze di gestione e facilitazione del processo.
- **Product Owner condiviso**: Invece di assegnare un singolo Product Owner, il team ha scelto di condividere questo ruolo tra tutti i membri, in modo che ciascuno possa contribuire attivamente alla definizione e alla priorità delle funzionalità da sviluppare.
- **Team di sviluppo**: Tutti i componenti del team si occupano attivamente dello sviluppo del codice.
- **Tester**: I membri del team svolgeranno anche il ruolo di tester, eseguendo test funzionali per garantire la qualità del software; Saranno previsti test sia manuali che automatizzati, con particolare attenzione alla scansione antivirus e all’analisi dei file. 

il progetto non prevede la presenza di clienti al di furi dei membri stessi del team, i quali saranno chiamati a verificare quindi di volta in volta la presenza e la qualita' delle funzionalita' introdotte, oltre che a fornire feedback per il miglioramento di eventuali fragilita'.

### Standard, linee guida, procedure
Ispiransoci alla norma ISO/IEC 9126 per la qualità del software, abbiamo elaborato le seguanti qualita' importanti per il successo del nostro progetto:

- **Affidabilità**: Garantire la stabilità dell'applicazione, evitando crash e comportamenti inaspettati. La qualità del software in termini di affidabilità è cruciale per garantire la stabilita' e l'efficienza del sistema.
- **Usabilità**: Progettazione di un'interfaccia utente chiara, intuitiva e facilmente navigabile per migliorare l'esperienza dell'utente. La qualità dell’usabilità è centrale per rendere il software accessibile e semplice da usare per tutti gli utenti.
- **Performance**: L’efficienza del sistema deve permettere un utilizzo fluido, senza rallentamenti significativi; Tuttavia puntiamo alla realizzazione di un sistema quanto piu leggero ed efficiente possibile, in linea con la filosofia del protocollo WireGuard.
- **Manutenibilità**: Strutturare il codice in modo chiaro e modulare, facilitando modifiche future e interventi di correzione; La manutenibilità permette al team di effettuare aggiornamenti o miglioramenti con facilità, garantendo un software flessibile nel tempo.

La documentazione dei requisiti segue lo standard IEEE 830 per definire requisiti funzionali e non funzionali.

### Attività di gestione
Allo scopo di persentare un lavoro quanto piu completo e dettagliato, il team si impegna costantemente nella realizzazione di nuova documentazione relativa alle nuove funzionalità aggiunte durante le vari fasi di sviluppo; pur non venendo meno all'aggiornamento della documentazione gia esistente.

### Rischi
abbiamo individuato alcuni rischi, anche se di livello puramente tecnico, all'interno della [seguente analisi di fattibilita'](https://github.com/LorenzoGallizioli/WireShield/blob/main/docs/AnalisiFattibilit%C3%A0.md).


### 
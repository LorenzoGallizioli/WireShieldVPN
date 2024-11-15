# 📝 Linee Guida del Progetto

## 🔧 Ingegneria del Software

**GitHub:** 
- [Garganti](https://github.com/Garganti)
- [silviabonfanti](https://github.com/silviabonfanti)

---

### 1. 🗓 **Project Plan**

- **Tempistiche:**
  - 🕐 **1 mese prima dell’esame**: Definizione del progetto e approvazione del project plan da parte del professore.
  - 🗓 **5 giorni prima**: Completamento del lavoro.

- **Gestione del progetto su GitHub**:
  - Creazione di **📌 Issue** per tracciare i task.
  - Utilizzo di **🌿 Branch** per separare i vari sviluppi e funzionalità.
  - **🔄 Pull request** per integrare le modifiche nel ramo principale.
  - **👀 Code review** per garantire la qualità del codice.
  - Organizzazione delle attività tramite **🗂️ Kanban board**.

---

### 2. 🛠 **Software Engineering Management**

#### 2.1 **Project Plan**
Il piano del progetto sarà descritto in questa sezione, evidenziando i 14 punti come indicato in sezione 2.1 del libro. Questo piano sarà sottoposto all'approvazione del professore prima dell'inizio dello sviluppo.

---

### 3. 🔄 **Software Life Cycle**

#### 3.1 **Tipo di Processo di Sviluppo**
Il progetto adotta un **processo di sviluppo Iterativo e Incrementale** per garantire una progressione graduale e continua delle funzionalità. Questo approccio prevede lo sviluppo in cicli (sprint), permettendo di incorporare frequenti feedback e adattamenti lungo il percorso.

Questa struttura ci consente di adattarci ai cambiamenti e di migliorare continuamente le funzionalità del software, offrendo la massima flessibilità.

#### 3.2 **Sprint - Metodologia Scrum**
Lo sviluppo seguirà la metodologia **Scrum**, strutturato in **sprint settimanali**. Ogni sprint avrà obiettivi e task specifici, pianificati e rivisti ad ogni ciclo, per rispondere agilmente a nuovi requisiti e migliorare progressivamente il software.

---

### 4. ⚙️ **Configuration Management**

#### 4.1 **GitHub**

- **Versionamento del codice**: Utilizzo di GitHub per il controllo versione, con comandi come:
  - `git add .` per aggiungere modifiche.
  - `git commit -m "messaggio"` per registrare le modifiche.
  - `git push origin nome-ramo` per caricare le modifiche nel repository remoto.

- **Branching**: Ogni nuova funzionalità o task viene sviluppata su un ramo separato. Creazione del ramo con:
  - `git checkout -b nome-ramo`.

- **Pull Requests (PR)**: Le modifiche vengono integrate nel ramo principale (`main`) tramite una **Pull Request** su GitHub, seguita da una **code review**.

- **Code Review**: Ogni PR viene esaminata da un membro del team per verificarne qualità e funzionalità.

- **Issue Tracking**: Le **Issue** vengono utilizzate per tracciare task e bug. Le Issue sono collegate ai commit tramite il formato `#numero-issue`.

- **Kanban Board**: Le attività vengono monitorate e organizzate su una Kanban board di GitHub.

#### 4.2 **Kanban**
Gestione delle attività tramite **Kanban board** per visualizzare il progresso e monitorare i task assegnati.

---

### 5. 👥 **People Management**

#### 5.1 **Organizzazione del Lavoro**
Il lavoro sarà distribuito tra i membri del team, con monitoraggio del progresso tramite la **Kanban board**.

La struttura di base del nostro team **Scrum** è la seguente:
- **Scrum Master**: Facilitatore del processo SCRUM, il cui compito è garantire che il team segua le pratiche Scrum correttamente, risolvere eventuali impedimenti che ostacolano il progresso e promuovere una cultura collaborativa e autodisciplinata. Lo Scrum Master sarà ruotato ogni mese, in modo che tutti i membri del team possano ricoprire questo ruolo.
- **Development Team**: Team di professionisti cross-funzionali che lavorano insieme per consegnare le funzionalità del prodotto. Il team ha l’obiettivo di organizzare autonomamente il lavoro durante lo sprint per raggiungere gli obiettivi definiti nello Sprint Backlog.

---

### 6. 🔍 **Software Quality**

#### 6.1 **Qualità Fondamentali per il Progetto**
Le seguenti qualità, ispirate alla norma **ISO/IEC 9126** per la qualità del software, sono utili per il successo del progetto:

- **Affidabilità**: Garantire la stabilità dell'applicazione, evitando crash e comportamenti inaspettati. La qualità del software in termini di affidabilità è cruciale per garantire una connessione VPN robusta e continuativa.
- **Usabilità**: Progettazione di un'interfaccia utente chiara, intuitiva e facilmente navigabile per migliorare l'esperienza dell'utente. La qualità dell’usabilità è centrale per rendere il software accessibile e semplice da usare per tutti gli utenti.
- **Performance**: Ottimizzazione della connessione VPN per garantire velocità e stabilità, riducendo al minimo i tempi di risposta. L’efficienza del sistema deve permettere un utilizzo fluido, senza rallentamenti significativi.
- **Sicurezza**: Implementazione di misure di protezione contro malware e minacce esterne tramite scansioni antivirus. La sicurezza è essenziale per proteggere i dati degli utenti e garantire un ambiente di lavoro sicuro.
- **Manutenibilità**: Strutturare il codice in modo chiaro e modulare, facilitando modifiche future e interventi di correzione. La manutenibilità permette al team di effettuare aggiornamenti o miglioramenti con facilità, garantendo un software flessibile nel tempo.

L’adozione di questi criteri di qualità, in linea con lo standard **ISO/IEC 9126**, assicura che il prodotto finale risponda ai requisiti di stabilità, facilità d'uso, velocità, sicurezza e manutenibilità.

---

### 7. 📋 **Requirements Engineering**

#### 7.1 **Elicitation dei Requisiti**
I requisiti sono stati raccolti tramite un’analisi dei bisogni per la connessione VPN sicura e protezione malware.

#### 7.2 **Specifica dei Requisiti (IEEE 830)**
La documentazione dei requisiti segue lo standard **IEEE 830** per definire requisiti funzionali e non funzionali.

Esempio di specifica dei requisiti: [Mechanical Lung Ventilator - Example Requirements Specification](https://github.com/foselab/abz2024_casestudy_MLV/blob/main/Mechanical_Lung_Ventilator%201_5.pdf)

---

### 8. 🖋️ **Modeling**

#### 8.1 **Diagrammi UML**
I seguenti diagrammi UML sono stati utilizzati per progettare il sistema:
- **Diagramma dei Casi d’Uso**
- **Diagramma delle Classi**
- **Diagramma delle Macchine a Stati**
- **Diagramma di Sequenza**
- **Diagramma di Comunicazione**
- **Diagramma di Attività**
- **Diagramma dei Componenti**

---

### 9. 🏛️ **Software Architecture**

#### 9.1 **Descrizione dell'Architettura**
- **Architettura a strati**: Separazione tra logica di business, interfaccia utente e integrazione con sistemi esterni (VPN e antivirus).
  
- **Principali viste architetturali**:
  - **Vista funzionale**: Descrizione delle funzionalità principali.
  - **Vista strutturale**: Struttura delle componenti del sistema.
  - **Vista comportamentale**: Risposta del sistema agli eventi.

---

### 10. 🖌️ **Software Design**

#### 10.1 **Progettazione Mediante UML**
Progettazione mediante diagrammi UML per rappresentare la struttura e il comportamento del sistema.

#### 10.2 **Metriche di Qualità**
Calcolo della complessità del codice e misurazione delle metriche di qualità per monitorare la manutenibilità e individuare miglioramenti.

#### 10.3 **Calcolo della Complessità**
Utilizzo della **complessità ciclomatica** per garantire che il sistema sia facilmente testabile e manutenibile.

---

### 11. 🔍 **Software Testing**

#### 11.1 **Piano per Attività di Test**
Il piano di test include test **funzionali**, **di integrazione**, e **di regressione** per assicurare il corretto funzionamento delle funzionalità.

#### 11.2 **Casi di Test Implementati**
Test implementati con **JUnit** per verificare la funzionalità principale (connessione VPN, scansione antivirus).

---

### 12. 🔧 **Software Maintenance**

#### 12.1 **Reverse Engineering**
Tecniche di **reverse engineering** saranno utilizzate per comprendere meglio componenti esterni e tecnologie di terze parti.

#### 12.2 **Refactoring**
**Refactoring regolare** del codice per migliorarne leggibilità e performance.

---

> **Nota:** Questo documento deve essere aggiornato regolarmente per riflettere eventuali modifiche o miglioramenti nel processo di sviluppo.

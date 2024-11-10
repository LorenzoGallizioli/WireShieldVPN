# 📝 Linee Guida del Progetto

## Ingegneria del Software

**Github:** Garganti, silviabonfanti

### 1. **Project Plan**

- **Tempistiche:**
  - **1 mese prima dell’esame:** Definizione del progetto e approvazione del piano da parte del professore.
  - **5 giorni prima:** Presentazione del progetto finale.
  
- **Gestione del progetto su GitHub:**
  - Creazione di **Issue** per tracciare i task.
  - Utilizzo di **Branch** per separare i vari sviluppi e funzionalità.
  - **Pull request** per integrare le modifiche nel ramo principale.
  - Revisione del codice tramite **Code review**.
  - Gestione delle attività tramite **Kanban board**.

---

### 2. **Software Engineering Management**

#### 2.1 **Project Plan**
Il piano del progetto sarà descritto in questa sezione, indicando le fasi principali e i tempi di completamento. Il piano dovrà essere approvato dal professore prima dell'inizio dello sviluppo.

---

### 3. **Software Life Cycle**

#### 3.1 **Tipo di Processo di Sviluppo**
- Il processo di sviluppo seguito sarà quello **Iterativo e Incrementale**, dove il progetto sarà sviluppato in piccoli cicli di sviluppo (sprint) con revisioni frequenti e miglioramenti.

#### 3.2 **Approccio MDA (Model-Driven Architecture)**
- L'approccio MDA sarà utilizzato con **Papyrus UML** per la progettazione e generazione di codice da modelli UML.

#### 3.3 **Sprint**
- Il progetto sarà suddiviso in **sprint** di 1 settimana, con il completamento di specifici task e obiettivi definiti prima di ogni sprint.

---

### 4. **Configuration Management**

#### 4.1 **GitHub**
- **Versionamento del codice**: Utilizzo di **GitHub** per il controllo versione e la gestione del repository.
- **Branching**: Creazione di rami separati per ogni nuova funzionalità o task.
- **Pull Requests**: Per integrare le modifiche nel ramo principale (`main`).
  
#### 4.2 **Kanban**
- Gestione delle attività tramite **Kanban board** per visualizzare il progresso e l'assegnazione dei task.

---

### 5. **People Management**

#### 5.1 **Organizzazione del Lavoro**
- Il lavoro sarà distribuito tra i membri del team, assegnando specifici compiti e monitorando il progresso tramite la **Kanban board**.
- I membri del team: **Davide Bonsembiante**, **Lorenzo Gallizioli**, e **Thomas Paganelli** si occuperanno delle seguenti aree:
  - **Davide Bonsembiante**: Responsabile dell'implementazione della connessione VPN.
  - **Lorenzo Gallizioli**: Gestione dell'integrazione della scansione antivirus.
  - **Thomas Paganelli**: Supervisione e gestione dell'interfaccia grafica (UI).

---

### 6. **Software Quality**

#### 6.1 **Qualità Importanti per il Progetto**
Le seguenti qualità sono fondamentali per il successo del progetto:
- **Affidabilità**: Garantire che l'applicazione sia stabile e senza crash.
- **Usabilità**: Interfaccia utente chiara e intuitiva.
- **Performance**: La connessione VPN deve essere veloce e senza interruzioni.
- **Sicurezza**: Protezione dai malware tramite scansioni antivirus.
- **Manutenibilità**: Il codice deve essere ben strutturato per future modifiche e miglioramenti.

---

### 7. **Requirements Engineering**

#### 7.1 **Elicitation dei Requisiti**
I requisiti sono stati ricavati mediante interviste con il committente e analisi delle necessità per la connessione VPN sicura e la protezione contro i malware.

#### 7.2 **Specifica dei Requisiti (IEEE 830)**
La documentazione dei requisiti segue lo standard **IEEE 830** per la definizione dei requisiti funzionali e non funzionali.

Esempio di specifica dei requisiti:
[Mechanical Lung Ventilator - Example Requirements Specification](https://github.com/foselab/abz2024_casestudy_MLV/blob/main/Mechanical_Lung_Ventilator%201_5.pdf)

---

### 8. **Modeling**

#### 8.1 **UML Visti a Lezione**
I seguenti diagrammi UML sono stati utilizzati per progettare il sistema:

- **Use Case Diagram**
- **Class Diagram**
- **State Machine Diagram**
- **Sequence Diagram**
- **Communication Diagram**
- **Activity Diagram**
- **Component Diagram**

---

### 9. **Software Architecture**

#### 9.1 **Descrizione dell'Architettura**
L'architettura del software è basata su un modello a **strati**, con una separazione tra la logica di business, l'interfaccia utente e l'integrazione con i sistemi esterni (WireGuard e il sistema antivirus).

Le principali viste architetturali includono:
- **Vista funzionale**: Descrizione delle funzionalità principali.
- **Vista strutturale**: Come le componenti del sistema sono organizzate.
- **Vista comportamentale**: Come il sistema risponde agli eventi.

---

### 10. **Software Design**

#### 10.1 **Progettazione Mediante UML**
La progettazione è stata realizzata utilizzando diagrammi UML per rappresentare in modo chiaro la struttura e il comportamento del sistema.

#### 10.2 **Misurazione del Codice**
Le metriche di qualità del codice sono state calcolate per misurare la complessità del sistema e identificare aree di miglioramento.

#### 10.3 **Calcolo della Complessità**
La **complessità ciclomatica** del codice è stata analizzata per garantire che il sistema sia facilmente testabile e manutenibile.

---

### 11. **Software Testing**

#### 11.1 **Piano per Attività di Test**
Il piano di test include test funzionali, di integrazione e di regressione per garantire che tutte le funzionalità del sistema siano operative.

#### 11.2 **Casi di Test Implementati**
I test sono stati implementati utilizzando **JUnit**, per verificare il corretto funzionamento delle classi e dei metodi principali, come la connessione VPN e la scansione dei file.

---

### 12. **Software Maintenance**

#### 12.1 **Reverse Engineering**
Nel caso di necessità, verranno utilizzate tecniche di **reverse engineering** per comprendere meglio il funzionamento di componenti esterni o legati a tecnologie di terze parti.

#### 12.2 **Refactoring**
Il codice verrà regolarmente sottoposto a **refactoring** per migliorarne la leggibilità, la manutenibilità e per ottimizzare le performance.

---


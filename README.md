## Wireguard virus scanner

Dato che molte persone utilizzano WireGuard si è pensato di creare un applicativo che si integri con wireguard e che esegua analisi (mediante API rest VirusTotal) per verificare che i file scaricati non siano malevoli.

Componenti:
1. Database interno per tenere traccia dei vari file
2. Luogo dove storare le chiavi wireguard
3. Tramite librerie java fare chiamate a servizi per gestire wireguard
4. ...

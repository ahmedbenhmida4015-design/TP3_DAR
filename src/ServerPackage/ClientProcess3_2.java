
package ServerPackage;

import commonPackage.Operation;

import java.io.*;
import java.net.Socket;

public class ClientProcess3_2 extends Thread {

    private Socket socket;
    private int numero;

    // NOUVEAU EN 3-2 : compteur global partagé par tous les clients.
    private static int compteurOperations = 0;

    public ClientProcess3_2(Socket socket, int numero) {
        this.socket = socket;
        this.numero = numero;
    }

    // NOUVEAU EN 3-2 : synchronisation du compteur.
    private static synchronized int incrementerCompteur() {
        compteurOperations++;
        return compteurOperations;
    }

    @Override
    public void run() {

        try {
            // MODIFICATION 3-2 : créer d'abord le flux de sortie objet.
            ObjectOutputStream oos =
                    new ObjectOutputStream(socket.getOutputStream());
            oos.flush();

            // Puis créer le flux d'entrée objet.
            ObjectInputStream ois =
                    new ObjectInputStream(socket.getInputStream());

            System.out.println(
                    "Bienvenue, vous êtes le client n°" + numero);

            while (true) {

                // Recevoir un objet du client.
                Object recu = ois.readObject();

                // Vérifier si le client veut quitter.
                if (recu instanceof String
                        && recu.equals("FIN")) {
                    System.out.println(
                            "Fin de communication avec le client " + numero);
                    break;
                }

                // Vérifier que l'objet reçu est une Operation.
                if (recu instanceof Operation) {

                    Operation operation = (Operation) recu;

                    double a = operation.getOperande1();
                    char operateur = operation.getOperateur();
                    double b = operation.getOperande2();

                    System.out.println(
                            "Opération reçue : " + a + " "
                                    + operateur + " " + b);

                    String reponse = calculer(a, operateur, b);

                    // NOUVEAU EN 3-2 : compter l'opération traitée.
                    int total = incrementerCompteur();

                    System.out.println(
                            "Nombre total d'opérations : " + total);

                    // Envoyer la réponse au client.
                    oos.writeObject(reponse);
                    oos.flush();

                    System.out.println("Réponse envoyée : " + reponse);

                } else {
                    oos.writeObject("Erreur : objet inconnu");
                    oos.flush();
                }
            }

            ois.close();
            oos.close();

            System.out.println(
                    "Client n°" + numero + " déconnecté.");

        } catch (IOException e) {
            System.out.println(
                    "Erreur client n°" + numero + " : " + e.getMessage());

        } catch (ClassNotFoundException e) {
            System.out.println(
                    "Classe d'objet inconnue : " + e.getMessage());

        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Erreur lors de la fermeture du socket.");
            }
        }
    }

    // Calcul et retour du résultat sous forme de texte.
    static String calculer(double a, char operateur, double b) {

        double resultat;

        switch (operateur) {
            case '+':
                resultat = a + b;
                break;

            case '-':
                resultat = a - b;
                break;

            case '*':
                resultat = a * b;
                break;

            case '/':
                if (b == 0) {
                    return "Erreur : division par zéro impossible";
                }
                resultat = a / b;
                break;

            default:
                return "Erreur : opérateur inconnu";
        }

        return formater(a) + " " + operateur + " "
                + formater(b) + " = " + formater(resultat);
    }

    static String formater(double valeur) {
        if (valeur == Math.rint(valeur)
                && !Double.isInfinite(valeur)
                && !Double.isNaN(valeur)) {
            return String.valueOf((long) valeur);
        }

        return String.valueOf(valeur);
    }
}

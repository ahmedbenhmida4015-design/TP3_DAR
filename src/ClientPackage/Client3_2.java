
package ClientPackage;

import java.io.*;
import java.net.Socket;
import java.util.Locale;
import java.util.Scanner;
import commonPackage.Operation;

public class Client3_2 {

    public static void main(String[] args) {

        System.out.println("Je suis un client pas encore connecte");

        try {
            Socket socket = new Socket("localhost", 1234);

            System.out.println("Je suis un client connecte");

            Scanner sca = new Scanner(System.in);
            sca.useLocale(Locale.US);

            ObjectOutputStream oos =
                    new ObjectOutputStream(socket.getOutputStream());
            oos.flush();

            ObjectInputStream ois =
                    new ObjectInputStream(socket.getInputStream());

            String encore;

            do {
                System.out.println("Premier operande :");
                double x = sca.nextDouble();

                String op;

                do {
                    System.out.println("Operateur (+, -, *, /) :");
                    op = sca.next();
                } while (!op.matches("[+*/-]"));

                System.out.println("Deuxieme operande :");
                double y = sca.nextDouble();

                // Creer et envoyer l'objet Operation
                Operation operation =
                        new Operation(x, op.charAt(0), y);

                oos.writeObject(operation);
                oos.flush();

                // Recevoir la reponse du serveur
                String reponse = (String) ois.readObject();

                System.out.println("Resultat : " + reponse);

                System.out.println("Autre calcul ? (o/n)");
                encore = sca.next();

            } while (encore.equalsIgnoreCase("o"));

            // Signaler la fin de la communication
            oos.writeObject("FIN");
            oos.flush();

            System.out.println("Fin de la communication");

            ois.close();
            oos.close();
            socket.close();
            sca.close();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur cote client : " + e.getMessage());
        }
    }
}

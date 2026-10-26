import ServerPackage.ClientProcess3_2;

static final int PORT = 1234;

public static void main(String[] args) {
    int numero = 0; // numéro d'ordre de connexion
    try {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Je suis un serveur en attente la connexion d'un client ");

        while (true) {
            Socket socket = serverSocket.accept(); // bloque jusqu'au prochain client
            numero++;

            // b) afficher l'adresse IP (et le port) + le numéro d'ordre du client
            System.out.println("Client n°" + numero + " connecté, adresse : "
                    + socket.getRemoteSocketAddress());

            // un thread dédié par client, qui reçoit SA socket et SON numéro
            new ClientProcess3_2(socket,numero).start();
        }
        // pas de serverSocket.close() : la boucle est infinie
    } catch (IOException e) {
        System.out.println("Erreur côté serveur : " + e.getMessage());
    }
}